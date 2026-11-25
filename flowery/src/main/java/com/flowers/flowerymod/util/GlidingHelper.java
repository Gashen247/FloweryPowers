package com.flowers.flowerymod.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

/**
 * Forces the vanilla elytra pose without needing an elytra in the chest slot.
 * Used for the Jarona! dash so the hitbox, client animation and afterimages match.
 */
public final class GlidingHelper {
    private static final Method SET_SHARED_FLAG = findSetSharedFlag();
    private static final Field FALL_FLY_TICKS = findFallFlyTicks();

    private GlidingHelper() {
    }

    public static void setGliding(LivingEntity entity, boolean gliding) {
        entity.setPose(gliding ? Pose.FALL_FLYING : entity.isCrouching() ? Pose.CROUCHING : Pose.STANDING);

        if (SET_SHARED_FLAG != null) {
            try {
                SET_SHARED_FLAG.invoke(entity, 7, gliding);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
        if (FALL_FLY_TICKS != null) {
            try {
                FALL_FLY_TICKS.setInt(entity, gliding ? 20 : 0);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
    }

    private static Method findSetSharedFlag() {
        try {
            Method method = Entity.class.getDeclaredMethod("setSharedFlag", int.class, boolean.class);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static Field findFallFlyTicks() {
        try {
            Field field = LivingEntity.class.getDeclaredField("fallFlyTicks");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }
}
