# Gridelyx Code of Conduct

## Purpose

Gridelyx is an experimental, multidisciplinary Minecraft engineering project. Contributors may work across Java, Rust, C/C++, scripting, networking, rendering, world mutation, AI-assisted development, Bedrock integration, build systems and documentation. Technical disagreement is expected; personal hostility is not.

This Code of Conduct defines the minimum standard for participation in Gridelyx repositories, issues, pull requests, reviews, discussions, community spaces and project-associated collaboration.

## Core standard

Participate in a way that is constructive, technically honest and respectful of other people's time, privacy, safety and ownership.

Good participation includes:

- discussing ideas, code, tests and architecture rather than attacking the person presenting them;
- asking basic or advanced questions without ridicule;
- disagreeing clearly and supporting disagreement with reproducible evidence, first-principles reasoning, upstream documentation or an explicitly labelled hypothesis;
- distinguishing facts, assumptions, estimates, hypotheses and unverified claims;
- acknowledging mistakes and correcting the project record when evidence changes;
- reviewing contributions on their technical merits rather than the contributor's experience level, communication style or identity;
- giving appropriate credit for code, research, designs, assets, testing and prior work;
- respecting licensing, authorship and provenance requirements;
- respecting contributor privacy and personal boundaries;
- reporting defects, unsafe behaviour and security problems in good faith;
- helping preserve reproducibility, rollback paths and evidence for consequential changes.

## Unacceptable conduct

The following is not acceptable in Gridelyx project spaces:

- harassment, stalking, threats, intimidation or targeted abuse;
- discriminatory or demeaning conduct directed at a person or group;
- sexual harassment or unwanted sexual attention;
- doxxing, publishing private information or attempting to identify an anonymous contributor without consent;
- exposing passwords, access tokens, private keys, personal data or other credentials;
- impersonation, deceptive attribution or deliberately falsified provenance;
- plagiarism or knowingly presenting another person's work as your own;
- deliberate disruption of discussions, reviews, CI, infrastructure or collaborative work;
- malicious code submissions, backdoors, credential stealers, destructive payloads or intentionally hidden unsafe behaviour;
- knowingly false vulnerability reports intended to harass, extort or consume maintainer time;
- pressuring contributors to bypass security, safety, licensing, provenance, review or evidence controls;
- retaliating against a person for raising a good-faith conduct, safety or security concern;
- repeatedly presenting speculation as established implementation truth after being asked to label or validate it.

## Technical disagreement and review

Strong technical criticism is permitted and often necessary. It must remain specific to the work.

When implementation truth is disputed, prefer evidence in roughly this order when applicable:

1. observed runtime behaviour and reproducible tests;
2. authoritative upstream source or documentation;
3. repository tests, invariants and compatibility evidence;
4. implementation-level reasoning;
5. explicitly labelled assumptions or hypotheses.

A maintainer may require a claim to be downgraded from "works" or "supported" to an appropriate evidence/readiness state until the required validation exists.

Disagreement with an architecture decision is not misconduct. Repeated personal attacks, bad-faith misrepresentation or deliberate obstruction are.

## AI-assisted contributions

AI-assisted work is welcome, but the human contributor submitting the work remains responsible for it.

Contributors must not use AI assistance as a justification for:

- fabricated test results or citations;
- invented upstream APIs or version claims;
- unreviewed privileged code;
- copying material without appropriate licensing or attribution;
- concealing uncertainty about generated implementation;
- submitting secrets or private project material to services without authorisation.

Generated code should be reviewed to the same standard as human-written code, with additional scrutiny for native memory, concurrency, authentication, networking, bytecode transformation, deserialisation, filesystem access and world-state mutation.

## Security and safety reports

Do not publish an exploitable vulnerability, credential, private user information or immediately actionable destructive procedure in a public issue merely to prove that it exists.

Security vulnerabilities should follow `SECURITY.md`. Operational and engineering hazards should follow `SAFETY.md`. If a report contains both, use the more private route first.

Good-faith reporters should provide enough information for maintainers to reproduce and contain the problem without unnecessarily increasing exposure.

## Attribution and project ownership

Contributors must preserve copyright notices, licence obligations and meaningful attribution when required.

Ideas may converge independently. Claims of copying, theft or provenance conflict should be handled with evidence rather than harassment. Maintainers may request commit history, source references, design notes or other provenance evidence when a material dispute affects licensing or project integrity.

## Scope

This Code applies to behaviour in:

- repository issues, pull requests, reviews and discussions;
- Gridelyx-maintained community or collaboration spaces;
- project meetings or real-time collaboration;
- direct project-related communication when it materially affects a contributor's ability to participate safely;
- public representation of the project when a participant is acting in an official project capacity.

It does not give maintainers authority over unrelated private conduct that has no meaningful connection to the project.

## Reporting conduct problems

When possible, report conduct problems privately to a project maintainer or through an available private repository/community reporting mechanism. Do not include unnecessary personal information.

A useful report includes:

- what happened;
- where and approximately when it happened;
- relevant links, screenshots or message references;
- whether there is an immediate safety, privacy or security concern;
- what outcome or protection you are requesting, if any.

Security vulnerabilities should not be sent through a conduct report when the repository's private security reporting mechanism is available.

## Enforcement

Maintainers may take proportionate action including:

1. informal clarification or request to stop;
2. formal warning;
3. editing, hiding or removing harmful content;
4. closing or locking discussions;
5. rejecting or reverting contributions;
6. temporary restriction from project spaces;
7. permanent removal from project-managed spaces for severe or repeated violations.

Immediate stronger action may be taken for credible threats, doxxing, malware, credential theft, deliberate infrastructure attacks, severe harassment or other conduct where a staged warning process would create additional risk.

Enforcement decisions should consider severity, intent, impact, repetition, evidence, remediation and risk to affected people or project systems.

## Confidentiality and retaliation

Maintainers handling private reports should disclose information only as needed to investigate, protect participants, comply with platform/legal obligations or remediate the problem.

Retaliation against a good-faith reporter, witness or reviewer is itself a conduct violation.

## Appeals and corrections

A person subject to a significant moderation action may request reconsideration from maintainers not materially involved in the original dispute when such maintainers are available. New evidence or factual errors should be considered. An appeal is not a licence to harass reporters or repeatedly relitigate a settled matter without new information.

## Relationship to platform rules

This document governs Gridelyx project participation. It does not replace GitHub's Terms of Service, Acceptable Use Policies, Community Guidelines, applicable law or the rules of any third-party service used by the project.
