# Interactive Gridelyx API documentation

The development bridge API is documented from an OpenAPI 3.1 contract. This surface is **capability-gated and evidence-bound**: the specification defines a neutral integration contract, while actual target support is discovered at runtime and remains subject to server authority, version fingerprints and R0-R6 evidence.

<swagger-ui src="gridelyx-development-api.openapi.yaml"/>

## Contract rules

- Default development endpoints are loopback-first.
- Connection does not imply authorization.
- World mutations are transactions and remain authoritative on the target server/runtime.
- Hotload operations may be rejected or escalated to a broader restart scope when runtime structure cannot be safely changed live.
- Capability state and target fingerprints must be checked before use.
- API expansion must update the OpenAPI contract, security model, capability manifest and tests together.
