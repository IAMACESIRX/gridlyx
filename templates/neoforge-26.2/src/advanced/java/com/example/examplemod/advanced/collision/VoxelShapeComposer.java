package com.example.examplemod.advanced.collision;

import com.example.examplemod.advanced.collision.DynamicCollisionShape.Box;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VoxelShapeComposer {
    private VoxelShapeComposer() {}

    public static VoxelShape compose(DynamicCollisionShape collision) {
        VoxelShape result = Shapes.empty();
        for (Box box : collision.boxes()) {
            AABB bounds = new AABB(
                    box.minX(), box.minY(), box.minZ(),
                    box.maxX(), box.maxY(), box.maxZ());
            result = Shapes.or(result, Shapes.create(bounds));
        }
        return result;
    }
}
