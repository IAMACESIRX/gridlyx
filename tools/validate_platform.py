#!/usr/bin/env python3
from pathlib import Path
import argparse, json, re

ROOT=Path(__file__).resolve().parents[1]
LOCK=json.loads((ROOT/'platform/versions.json').read_text())
REQUIRED={'build.gradle','gradle.properties','settings.gradle','gradlew','gradlew.bat','gradle/wrapper/gradle-wrapper.properties','src/main/templates/META-INF/neoforge.mods.toml'}

def err(errors,msg): errors.append(msg); print('ERROR:',msg)
def validate_project(project:Path, errors:list[str]):
    label=str(project.relative_to(ROOT))
    for rel in REQUIRED:
        if not (project/rel).exists(): err(errors,f'{label}: missing {rel}')
    gp=project/'gradle.properties'
    if not gp.exists(): return
    props={}
    for line in gp.read_text(encoding='utf-8').splitlines():
        line=line.strip()
        if line and not line.startswith('#') and '=' in line:
            k,v=line.split('=',1); props[k.strip()]=v.strip()
    mid=props.get('mod_id','')
    if not re.fullmatch(r'[a-z][a-z0-9_]{1,63}',mid): err(errors,f'{label}: invalid mod_id {mid!r}')
    if props.get('minecraft_version')!=LOCK['minecraft']: err(errors,f'{label}: minecraft_version drift')
    if props.get('neo_version')!=LOCK['neoforge']: err(errors,f'{label}: neo_version drift')
    bg=(project/'build.gradle').read_text(encoding='utf-8',errors='replace') if (project/'build.gradle').exists() else ''
    if f"net.neoforged.moddev' version '{LOCK['moddevgradle']}'" not in bg and f'net.neoforged.moddev" version "{LOCK["moddevgradle"]}"' not in bg:
        err(errors,f'{label}: ModDevGradle is not locked to {LOCK["moddevgradle"]}')
    if not re.search(r'JavaLanguageVersion\.of\(25\)',bg): err(errors,f'{label}: Java 25 toolchain not declared')
    suspicious=[r'implementation\s+["\']org\.lwjgl:',r'implementation\s+["\']net\.neoforged:neoforge:']
    for pat in suspicious:
        if re.search(pat,bg): err(errors,f'{label}: direct vendor/runtime dependency found; reference vault must stay isolated unless explicitly reviewed')
    wp=(project/'gradle/wrapper/gradle-wrapper.properties')
    if wp.exists() and f'gradle-{LOCK["gradle"]}-bin.zip' not in wp.read_text(encoding='utf-8'):
        err(errors,f'{label}: Gradle wrapper drift')

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--mod'); ns=ap.parse_args(); errors=[]
    if ns.mod:
        p=ROOT/'mods'/ns.mod
        if not p.is_dir(): raise SystemExit(f'No mod workspace: {p}')
        validate_project(p,errors)
    else:
        validate_project(ROOT/LOCK['template'],errors)
        for p in sorted((ROOT/'mods').glob('*')):
            if p.is_dir(): validate_project(p,errors)
        vm=json.loads((ROOT/'vault/manifest.json').read_text())
        ids={a['id'] for a in vm['artifacts']}
        expected={'mdk','neoforge_installer','jdk','lwjgl'}
        if ids!=expected: err(errors,f'vault manifest ids {ids} != {expected}')
    if errors:
        print(f'FAILED: {len(errors)} validation error(s)'); return 2
    print('PASS: platform static validation')
    return 0
if __name__=='__main__': raise SystemExit(main())
