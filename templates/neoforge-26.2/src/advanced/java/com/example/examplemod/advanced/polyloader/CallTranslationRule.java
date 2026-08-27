package com.example.examplemod.advanced.polyloader;

import java.util.Objects;

public record CallTranslationRule(
        int opcode,
        String owner,
        String name,
        String descriptor,
        int replacementOpcode,
        String replacementOwner,
        String replacementName,
        String replacementDescriptor,
        boolean replacementInterface) {
    public CallTranslationRule {
        owner = Objects.requireNonNull(owner);
        name = Objects.requireNonNull(name);
        descriptor = Objects.requireNonNull(descriptor);
        replacementOwner = Objects.requireNonNull(replacementOwner);
        replacementName = Objects.requireNonNull(replacementName);
        replacementDescriptor = Objects.requireNonNull(replacementDescriptor);
        if (!descriptor.equals(replacementDescriptor)) {
            throw new IllegalArgumentException("Safe call translation currently requires an identical descriptor");
        }
    }

    public boolean matches(int candidateOpcode, String candidateOwner, String candidateName, String candidateDescriptor) {
        return opcode == candidateOpcode
                && owner.equals(candidateOwner)
                && name.equals(candidateName)
                && descriptor.equals(candidateDescriptor);
    }
}
