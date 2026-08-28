#!/usr/bin/env python3
from __future__ import annotations

import argparse
import subprocess
from collections import defaultdict
from pathlib import Path

SECTIONS = {
    "feat": "Added",
    "fix": "Fixed",
    "docs": "Documentation",
    "refactor": "Changed",
    "perf": "Performance",
    "test": "Testing",
    "build": "Build / Tooling",
    "ci": "Build / Tooling",
    "security": "Security",
    "chore": "Maintenance",
}


def git(*args: str) -> str:
    result = subprocess.run(["git", *args], check=True, capture_output=True, text=True)
    return result.stdout.strip()


def default_base() -> str:
    try:
        return git("describe", "--tags", "--abbrev=0")
    except subprocess.CalledProcessError:
        return git("rev-list", "--max-parents=0", "HEAD").splitlines()[0]


def classify(subject: str) -> str:
    prefix = subject.split(":", 1)[0].split("(", 1)[0].strip().lower()
    return SECTIONS.get(prefix, "Other")


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate evidence-preserving Gridelyx changelog Markdown from Git history")
    parser.add_argument("--base", default=None)
    parser.add_argument("--head", default="HEAD")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()

    base = args.base or default_base()
    lines = git("log", "--no-merges", "--format=%H%x09%s%x09%an", f"{base}..{args.head}").splitlines()
    groups: dict[str, list[tuple[str, str, str]]] = defaultdict(list)
    for line in lines:
        if not line.strip():
            continue
        sha, subject, author = line.split("\t", 2)
        groups[classify(subject)].append((sha, subject, author))

    order = ["Added", "Fixed", "Changed", "Performance", "Security", "Documentation", "Testing", "Build / Tooling", "Maintenance", "Other"]
    output = ["# Generated changelog candidate", "", f"Range: `{base}..{args.head}`", ""]
    if not lines:
        output += ["No non-merge commits found in this range.", ""]
    else:
        for section in order:
            entries = groups.get(section)
            if not entries:
                continue
            output += [f"## {section}", ""]
            for sha, subject, author in entries:
                output.append(f"- {subject} (`{sha[:10]}`, {author})")
            output.append("")

    text = "\n".join(output).rstrip() + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(text, encoding="utf-8")
        print(f"WROTE: {args.output}")
    else:
        print(text, end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
