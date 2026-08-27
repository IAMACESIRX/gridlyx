#!/usr/bin/env python3
from __future__ import annotations
from pathlib import Path
import argparse, hashlib, json, shutil, sys, zipfile

ROOT=Path(__file__).resolve().parents[1]
MANIFEST=ROOT/'vault/manifest.json'

def digest(path: Path) -> str:
    h=hashlib.sha256()
    with path.open('rb') as f:
        for b in iter(lambda:f.read(1024*1024),b''): h.update(b)
    return h.hexdigest()

def load(): return json.loads(MANIFEST.read_text(encoding='utf-8'))

def selected(m, ids, all_):
    arts=m['artifacts']
    if all_: return arts
    if not ids: raise SystemExit('Specify artifact id(s) or --all')
    by={a['id']:a for a in arts}
    bad=[x for x in ids if x not in by]
    if bad: raise SystemExit('Unknown artifact id(s): '+', '.join(bad))
    return [by[x] for x in ids]

def verify_artifact(a):
    ok=True
    if a['storage']=='whole':
        p=ROOT/a['path']
        if not p.exists() or p.stat().st_size!=a['size_bytes'] or digest(p)!=a['sha256']:
            print(f"FAIL {a['id']} whole artifact mismatch"); return False
    else:
        total=0
        for part in a['parts']:
            p=ROOT/part['path']
            if not p.exists(): print('FAIL missing',part['path']); ok=False; continue
            total+=p.stat().st_size
            if p.stat().st_size!=part['size_bytes'] or digest(p)!=part['sha256']:
                print('FAIL part mismatch',part['path']); ok=False
        if total!=a['size_bytes']:
            print(f"FAIL {a['id']} total size {total} != {a['size_bytes']}"); ok=False
    if ok: print('OK  ',a['id'],a['sha256'])
    return ok

def reconstruct(a,outdir:Path)->Path:
    outdir.mkdir(parents=True,exist_ok=True)
    dest=outdir/a['original_filename']
    if a['storage']=='whole': shutil.copy2(ROOT/a['path'],dest)
    else:
        with dest.open('wb') as o:
            for part in a['parts']:
                with (ROOT/part['path']).open('rb') as f: shutil.copyfileobj(f,o,1024*1024)
    if dest.stat().st_size!=a['size_bytes'] or digest(dest)!=a['sha256']:
        dest.unlink(missing_ok=True); raise RuntimeError(f"Reconstruction verification failed: {a['id']}")
    print('RECONSTRUCTED',a['id'],'->',dest)
    return dest

def main():
    ap=argparse.ArgumentParser(description='Verify, reconstruct and extract supplied reference artifacts.')
    sub=ap.add_subparsers(dest='cmd',required=True)
    for name in ('verify','reconstruct','extract'):
        p=sub.add_parser(name); p.add_argument('ids',nargs='*'); p.add_argument('--all',action='store_true')
        if name!='verify': p.add_argument('--output',type=Path,required=True)
    ns=ap.parse_args(); m=load(); arts=selected(m,ns.ids,ns.all)
    if ns.cmd=='verify':
        sys.exit(0 if all(verify_artifact(a) for a in arts) else 2)
    raw=ns.output if ns.cmd=='reconstruct' else ROOT/'.reference-cache/raw'
    rebuilt=[reconstruct(a,raw) for a in arts]
    if ns.cmd=='extract':
        ns.output.mkdir(parents=True,exist_ok=True)
        for a,p in zip(arts,rebuilt):
            dest=ns.output/a['id']; dest.mkdir(parents=True,exist_ok=True)
            if zipfile.is_zipfile(p):
                with zipfile.ZipFile(p) as z: z.extractall(dest)
                print('EXTRACTED',a['id'],'->',dest)
            else: print('SKIP non-zip',p)
if __name__=='__main__': main()
