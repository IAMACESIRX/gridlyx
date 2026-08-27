#!/usr/bin/env python3
from pathlib import Path
import argparse, os, subprocess, zipfile
ROOT=Path(__file__).resolve().parents[1]

def run_project(p:Path, gametest=False):
    print(f'\n=== BUILD {p.relative_to(ROOT)} ===',flush=True)
    wrapper=p/('gradlew.bat' if os.name=='nt' else 'gradlew')
    if os.name!='nt' and wrapper.exists(): wrapper.chmod(wrapper.stat().st_mode|0o111)
    gradle = str(wrapper) if wrapper.exists() and ((p/'gradle/wrapper/gradle-wrapper.jar').exists() or os.name=='nt') else 'gradle'
    rc=subprocess.run([gradle,'--no-daemon','build'],cwd=p).returncode
    if rc: return rc
    jars=[x for x in (p/'build/libs').glob('*.jar') if not x.name.endswith(('-sources.jar','-javadoc.jar'))]
    if not jars:
        print('ERROR: no built JAR'); return 3
    for jar in jars:
        with zipfile.ZipFile(jar) as z:
            if 'META-INF/neoforge.mods.toml' not in set(z.namelist()):
                print('ERROR: missing META-INF/neoforge.mods.toml in',jar); return 4
        print('JAR OK',jar.relative_to(ROOT))
    if gametest:
        print('=== GAMETEST ===',flush=True)
        rc=subprocess.run([gradle,'--no-daemon','runGameTestServer'],cwd=p).returncode
        if rc: return rc
    return 0

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--include-template',action='store_true'); ap.add_argument('--gametest',action='store_true'); ns=ap.parse_args()
    projects=[]
    if ns.include_template: projects.append(ROOT/'templates/neoforge-26.2')
    projects += [p for p in sorted((ROOT/'mods').glob('*')) if p.is_dir() and (p/'build.gradle').exists()]
    if not projects: print('No projects to build'); return 0
    for p in projects:
        rc=run_project(p,ns.gametest and (p/'.enable-gametest').exists())
        if rc: return rc
    return 0
if __name__=='__main__': raise SystemExit(main())
