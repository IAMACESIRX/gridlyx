#!/usr/bin/env python3
from __future__ import annotations
from pathlib import Path
import argparse, io, sys, zipfile

ROOT = Path(__file__).resolve().parents[1]
INDEX = ROOT / 'references' / 'index'
CACHE = ROOT / '.reference-cache' / 'raw'
sys.path.insert(0, str(ROOT / 'tools'))
import vault as vault_tool


def ensure_artifact(artifact_id: str) -> Path:
    m = vault_tool.load()
    by_id = {a['id']: a for a in m['artifacts']}
    if artifact_id not in by_id:
        raise SystemExit(f'Unknown artifact: {artifact_id}')
    a = by_id[artifact_id]
    dest = CACHE / a['original_filename']
    if dest.exists() and dest.stat().st_size == a['size_bytes'] and vault_tool.digest(dest) == a['sha256']:
        return dest
    return vault_tool.reconstruct(a, CACHE)


def search(term: str, limit: int):
    needle = term.casefold()
    count = 0
    for p in sorted(INDEX.rglob('*.tsv')):
        try:
            lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
        except OSError:
            continue
        for n, line in enumerate(lines, 1):
            if needle in line.casefold():
                print(f'{p.relative_to(ROOT)}:{n}: {line}')
                count += 1
                if count >= limit:
                    return
    if not count:
        print('No indexed matches.')


def write_bytes(data: bytes, output: Path | None, label: str):
    if output:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_bytes(data)
        print(output)
    else:
        try:
            sys.stdout.write(data.decode('utf-8'))
        except UnicodeDecodeError:
            raise SystemExit(f'{label} is binary; use --output')


def extract_jdk(inner: str, output: Path | None):
    jdk = ensure_artifact('jdk')
    with zipfile.ZipFile(jdk) as outer:
        src_name = next((n for n in outer.namelist() if n.endswith('/lib/src.zip')), None)
        if not src_name:
            raise SystemExit('JDK lib/src.zip not found')
        with zipfile.ZipFile(io.BytesIO(outer.read(src_name))) as src:
            if inner not in src.namelist():
                raise SystemExit(f'JDK source path not found: {inner}')
            write_bytes(src.read(inner), output, inner)


def extract_lwjgl(source_jar: str, inner: str, output: Path | None):
    bundle = ensure_artifact('lwjgl')
    with zipfile.ZipFile(bundle) as outer:
        if source_jar not in outer.namelist():
            candidates = [n for n in outer.namelist() if n.endswith(source_jar)]
            if len(candidates) == 1:
                source_jar = candidates[0]
            else:
                raise SystemExit(f'LWJGL source JAR not found or ambiguous: {source_jar}')
        with zipfile.ZipFile(io.BytesIO(outer.read(source_jar))) as src:
            if inner not in src.namelist():
                raise SystemExit(f'Path not found in {source_jar}: {inner}')
            write_bytes(src.read(inner), output, inner)


def extract_archive(artifact_id: str, inner: str, output: Path | None):
    artifact = ensure_artifact(artifact_id)
    with zipfile.ZipFile(artifact) as z:
        if inner not in z.namelist():
            raise SystemExit(f'Path not found: {inner}')
        write_bytes(z.read(inner), output, inner)


def main():
    ap = argparse.ArgumentParser(description='Search and retrieve exact supplied reference source on demand.')
    sub = ap.add_subparsers(dest='cmd', required=True)
    s = sub.add_parser('search'); s.add_argument('term'); s.add_argument('--limit', type=int, default=50)
    j = sub.add_parser('jdk'); j.add_argument('path'); j.add_argument('--output', type=Path)
    l = sub.add_parser('lwjgl'); l.add_argument('source_jar'); l.add_argument('path'); l.add_argument('--output', type=Path)
    a = sub.add_parser('archive'); a.add_argument('artifact', choices=['mdk','neoforge_installer','jdk','lwjgl']); a.add_argument('path'); a.add_argument('--output', type=Path)
    ns = ap.parse_args()
    if ns.cmd == 'search': search(ns.term, ns.limit)
    elif ns.cmd == 'jdk': extract_jdk(ns.path, ns.output)
    elif ns.cmd == 'lwjgl': extract_lwjgl(ns.source_jar, ns.path, ns.output)
    else: extract_archive(ns.artifact, ns.path, ns.output)

if __name__ == '__main__': main()
