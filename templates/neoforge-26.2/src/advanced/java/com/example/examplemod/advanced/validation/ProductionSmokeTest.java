package com.example.examplemod.advanced.validation;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/production/CameraKeyframe.java
import com.example.examplemod.advanced.production.CameraKeyframe;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/production/CameraPose.java
import com.example.examplemod.advanced.production.CameraPose;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/production/CameraTrack.java
import com.example.examplemod.advanced.production.CameraTrack;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/production/RationalTime.java
import com.example.examplemod.advanced.production.RationalTime;
import java.util.List;

public final class ProductionSmokeTest {
    private ProductionSmokeTest() {}

    public static void run() {
        CameraPose start = new CameraPose(0, 64, 0, 350, 0, 0, 70, 8);
        CameraPose end = new CameraPose(10, 70, 20, 10, -20, 5, 50, 4);
        CameraTrack track = new CameraTrack(List.of(
                new CameraKeyframe(RationalTime.frames(0, 60), start),
                new CameraKeyframe(RationalTime.frames(60, 60), end)));
        CameraPose middle = track.sample(RationalTime.frames(30, 60));
        if (Math.abs(middle.x() - 5.0) > 1.0e-9 || Math.abs(middle.z() - 10.0) > 1.0e-9) {
            throw new IllegalStateException("production camera interpolation failed");
        }
        if (Math.abs(middle.yawDegrees() - 360.0) > 1.0e-9) {
            throw new IllegalStateException("production camera shortest-angle interpolation failed");
        }
    }
}
