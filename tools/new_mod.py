#!/usr/bin/env python3
from pathlib import Path
import argparse, re, shutil

ROOT=Path(__file__).resolve().parents[1]
TEMPLATE=ROOT/'templates/neoforge-26.2'

def main():
    p=argparse.ArgumentParser(description='Create an isolated NeoForge mod workspace.')
    p.add_argument('mod_id'); p.add_argument('mod_name'); p.add_argument('group')
    ns=p.parse_args()
    if not re.fullmatch(r'[a-z][a-z0-9_]{1,63}',ns.mod_id): raise SystemExit('Invalid NeoForge mod_id')
    if not re.fullmatch(r'[A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)+',ns.group): raise SystemExit('Use a Java package/group such as com.example.mod')
    dest=ROOT/'mods'/ns.mod_id
    if dest.exists(): raise SystemExit(f'Workspace already exists: {dest}')
    shutil.copytree(TEMPLATE,dest)
    shutil.rmtree(dest/'.github',ignore_errors=True)
    repl={'examplemod':ns.mod_id,'Example Mod':ns.mod_name,'com.example.examplemod':ns.group}
    for path in dest.rglob('*'):
        if not path.is_file(): continue
        try: text=path.read_text(encoding='utf-8')
        except UnicodeDecodeError: continue
        for a,b in repl.items(): text=text.replace(a,b)
        path.write_text(text,encoding='utf-8',newline='\n')
    old=dest/'src/main/java/com/example/examplemod'
    if old.exists():
        target=dest/'src/main/java'/Path(*ns.group.split('.'))
        target.parent.mkdir(parents=True,exist_ok=True)
        shutil.move(str(old),str(target))
        for d in [dest/'src/main/java/com/example',dest/'src/main/java/com']:
            try: d.rmdir()
            except OSError: pass
    print(dest)
    print('Next: edit the starter code, then run:')
    print(f'  python tools/validate_platform.py --mod {ns.mod_id}')
    print(f'  cd mods/{ns.mod_id} && ./gradlew build')
if __name__=='__main__': main()
