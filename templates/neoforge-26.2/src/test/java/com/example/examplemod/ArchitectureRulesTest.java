package com.example.examplemod;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class ArchitectureRulesTest {
    @Test
    void normalModClassesMustNotDependOnAdvancedPackages() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.example.examplemod");
        classes.forEach(javaClass -> javaClass.getDirectDependenciesFromSelf().forEach(dependency -> {
            String targetPackage = dependency.getTargetClass().getPackageName();
            assertFalse(
                    targetPackage.contains(".advanced."),
                    () -> javaClass.getName() + " depends on isolated advanced code " + dependency);
        }));
    }
}
