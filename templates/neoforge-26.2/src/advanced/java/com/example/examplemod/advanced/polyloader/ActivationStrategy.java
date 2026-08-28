package com.example.examplemod.advanced.polyloader;

public enum ActivationStrategy {
    TRANSACTIONAL_RELOAD,
    ASSET_SWAP,
    SCOPED_BEHAVIOR_EPOCH,
    IN_PLACE_REDEFINE,
    CLASSLOADER_EPOCH,
    REGISTRY_VIRTUALIZATION,
    LIFECYCLE_REPLAY,
    RUNTIME_EPOCH_HANDOFF,
    ISOLATED_PROCESS
}
