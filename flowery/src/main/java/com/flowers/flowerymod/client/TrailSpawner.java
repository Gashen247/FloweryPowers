package com.flowers.flowerymod.client;

import java.util.UUID;

import com.flowers.flowerymod.Config;
import com.flowers.flowerymod.entity.AfterimageEntity;
import com.flowers.flowerymod.entity.FloweryEntities;
import com.flowers.flowerymod.util.GlidingHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public final class TrailSpawner {
    private static Vec3 lastPosition = null;
    private static int colorIndex = 0;
    private static int dashTicks = 0;
    private static boolean windRunActive = false;

    private TrailSpawner() {
    }

    public static void notifyDash() {
        dashTicks = Math.max(1, Config.DASH_DURATION_TICKS.getAsInt()) + 2;
    }

    public static void notifyWindRun() {
        windRunActive = true;
    }

    public static void clearWindRun() {
        windRunActive = false;
    }

    public static void reset() {
        lastPosition = null;
        dashTicks = 0;
        windRunActive = false;
    }

    public static void tick(ClientLevel level, LocalPlayer player) {
        if (dashTicks > 0) {
            dashTicks--;
            if (dashTicks == 0 && player.getPose() == Pose.FALL_FLYING) {
                GlidingHelper.setGliding(player, false);
            }
        }

        double speed = player.getDeltaMovement().length();
        boolean flyingMoving = player.getAbilities().flying && speed > 0.08;
        boolean active = dashTicks > 0 || windRunActive || flyingMoving;
        if (!active || speed < 0.08) {
            lastPosition = null;
            return;
        }

        Vec3 current = player.position();
        if (lastPosition == null) {
            lastPosition = current;
            return;
        }

        double spacing = Math.max(0.1, Config.TRAIL_SPACING_BLOCKS.getAsDouble());
        if (current.distanceToSqr(lastPosition) < spacing * spacing) {
            return;
        }

        int max = Math.max(1, Config.TRAIL_MAX_AFTERIMAGES.getAsInt());
        if (countLiveAfterimages(level, player.getUUID()) >= max) {
            lastPosition = current;
            return;
        }

        spawn(level, player, lastPosition.lerp(current, 0.5));
        lastPosition = current;
    }

    private static int countLiveAfterimages(ClientLevel level, UUID ownerUUID) {
        int count = 0;
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof AfterimageEntity afterimage && afterimage.getOwnerUUID().equals(ownerUUID)) {
                count++;
            }
        }
        return count;
    }

    private static void spawn(ClientLevel level, LocalPlayer player, Vec3 position) {
        PlayerSkin playerSkin = player.getSkin();
        ResourceLocation skin = playerSkin.texture();
        boolean slim = playerSkin.model() == PlayerSkin.Model.SLIM;
        int color = rainbow(colorIndex++);

        AfterimageEntity ghost = new AfterimageEntity(
                FloweryEntities.AFTERIMAGE.get(),
                level,
                player.getUUID(),
                skin,
                slim,
                color,
                player.getYRot(),
                player.getYHeadRot(),
                player.getXRot(),
                player.isCrouching(),
                player.getPose()
        );
        ghost.setPos(position);
        level.addEntity(ghost);
    }

    private static int rainbow(int index) {
        float hue = ((index * 11) % 360) / 360.0F;
        float saturation = 0.65F;
        float value = 1.0F;

        int sector = (int) (hue * 6.0F);
        float fraction = hue * 6.0F - sector;
        float p = value * (1.0F - saturation);
        float q = value * (1.0F - fraction * saturation);
        float t = value * (1.0F - (1.0F - fraction) * saturation);

        float red;
        float green;
        float blue;
        switch (sector % 6) {
            case 0 -> {
                red = value;
                green = t;
                blue = p;
            }
            case 1 -> {
                red = q;
                green = value;
                blue = p;
            }
            case 2 -> {
                red = p;
                green = value;
                blue = t;
            }
            case 3 -> {
                red = p;
                green = q;
                blue = value;
            }
            case 4 -> {
                red = t;
                green = p;
                blue = value;
            }
            default -> {
                red = value;
                green = p;
                blue = q;
            }
        }

        int r = Math.round(red * 255.0F) & 0xFF;
        int g = Math.round(green * 255.0F) & 0xFF;
        int b = Math.round(blue * 255.0F) & 0xFF;
        return (r << 16) | (g << 8) | b;
    }
}
