package com.flowers.flowerymod.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.flowers.flowerymod.Config;
import com.flowers.flowerymod.Flowery;
import com.flowers.flowerymod.FloweryEquipment;
import com.flowers.flowerymod.FlowerySounds;
import com.flowers.flowerymod.network.ModPayloads;
import com.flowers.flowerymod.util.GlidingHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public final class FloweryActions {
    private static final Map<UUID, DashState> ACTIVE_DASHES = new HashMap<>();
    private static final Map<UUID, Long> WIND_RUN_UNTIL = new HashMap<>();
    private static final Map<UUID, Boolean> AIR_DASH_IMMUNITY = new HashMap<>();

    private FloweryActions() {
    }

    public static void handle(ServerPlayer player, ModPayloads.Action action) {
        if (!FloweryEquipment.isWearing(player)) {
            return;
        }

        switch (action) {
            case DASH -> startDash(player);
            case WIND -> castWind(player);
            case WIND_RUN -> startWindRun(player);
        }
    }

    public static boolean isAirDashImmune(ServerPlayer player) {
        return AIR_DASH_IMMUNITY.containsKey(player.getUUID());
    }

    public static void tick(ServerPlayer player) {
        long now = player.level().getGameTime();
        updateWindRun(player, now);
        updateAirDashImmunity(player);

        DashState state = ACTIVE_DASHES.get(player.getUUID());
        if (state == null) {
            return;
        }

        if (now >= state.endTick || player.horizontalCollision || !FloweryEquipment.isWearing(player)) {
            ACTIVE_DASHES.remove(player.getUUID());
            GlidingHelper.setGliding(player, false);
            return;
        }

        player.setDeltaMovement(state.direction.scale(Config.DASH_SPEED.getAsDouble()));
        player.hurtMarked = true;
        GlidingHelper.setGliding(player, true);

        float damage = (float) Config.DASH_DAMAGE.getAsDouble();
        AABB hitbox = player.getBoundingBox().inflate(1.4);
        for (Entity target : player.level().getEntities(player, hitbox,
                entity -> entity instanceof LivingEntity && entity != player && !state.hitEntities.contains(entity.getUUID()))) {
            DamageSource source = player.damageSources().playerAttack(player);
            if (target.hurt(source, damage)) {
                state.hitEntities.add(target.getUUID());
                Vec3 flat = new Vec3(target.getX() - player.getX(), 0.0, target.getZ() - player.getZ());
                Vec3 away = flat.lengthSqr() > 0.0001 ? flat.normalize() : player.getLookAngle();
                target.setDeltaMovement(away.scale(1.2).add(0.0, 0.5, 0.0));
                target.hurtMarked = true;
            }
        }
    }

    private static void playForOthers(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        player.level().playSound(player, player, sound, SoundSource.PLAYERS, volume, pitch);
    }

    private static void updateWindRun(ServerPlayer player, long now) {
        UUID uuid = player.getUUID();
        Long until = WIND_RUN_UNTIL.get(uuid);
        if (until == null) {
            return;
        }
        if (now >= until || !FloweryEquipment.isWearing(player)) {
            WIND_RUN_UNTIL.remove(uuid);
            return;
        }

        Vec3 look = player.getLookAngle();
        if (look.lengthSqr() < 0.0001) {
            look = player.getForward();
        }
        player.setDeltaMovement(look.normalize().scale(Config.WIND_RUN_SPEED.getAsDouble()));
        player.hurtMarked = true;
    }

    private static void updateAirDashImmunity(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (!AIR_DASH_IMMUNITY.containsKey(uuid)) {
            return;
        }
        if (player.onGround() || !FloweryEquipment.isWearing(player)) {
            AIR_DASH_IMMUNITY.remove(uuid);
            return;
        }
        player.invulnerableTime = Math.max(player.invulnerableTime, 20);
    }

    private static void startWindRun(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long now = player.level().getGameTime();
        Long until = WIND_RUN_UNTIL.get(uuid);
        if (until == null || now >= until) {
            playForOthers(player, FlowerySounds.MYSTERIOUS_WIND.get(), 0.75F, 1.15F);
        }
        WIND_RUN_UNTIL.put(uuid, now + Math.max(1, Config.WIND_RUN_REFRESH_TICKS.getAsInt()));
    }

    private static void startDash(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        if (look.lengthSqr() < 0.0001) {
            look = player.getForward();
        }
        Vec3 direction = look.normalize();

        ACTIVE_DASHES.put(player.getUUID(), new DashState(
                direction,
                player.level().getGameTime() + Config.DASH_DURATION_TICKS.getAsInt()
        ));
        if (!player.onGround()) {
            AIR_DASH_IMMUNITY.put(player.getUUID(), Boolean.TRUE);
        }

        player.setDeltaMovement(direction.scale(Config.DASH_SPEED.getAsDouble()));
        player.hurtMarked = true;
        playForOthers(player, FlowerySounds.JARONA.get(), 1.0F, 1.0F);
    }

    private static void castWind(ServerPlayer player) {
        Item chestplate = Flowery.FLOWER_CHESTPLATE.get();
        if (player.getCooldowns().isOnCooldown(chestplate)) {
            return;
        }

        int cooldownTicks = Config.WIND_COOLDOWN_SECONDS.getAsInt() * 20;
        player.getCooldowns().addCooldown(chestplate, cooldownTicks);
        playForOthers(player, FlowerySounds.MYSTERIOUS_WIND.get(), 1.0F, 1.0F);

        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        double radius = Config.WIND_RADIUS.getAsDouble();
        RandomSource random = level.getRandom();

        for (int i = 0; i < 220; i++) {
            Vec3 offset = new Vec3(
                    random.nextDouble() * 2.0 - 1.0,
                    random.nextDouble() * 2.0 - 1.0,
                    random.nextDouble() * 2.0 - 1.0
            );
            if (offset.lengthSqr() < 0.0001) {
                continue;
            }
            Vec3 direction = offset.normalize();
            Vec3 position = center.add(direction.scale(random.nextDouble() * radius));
            level.sendParticles(
                    ParticleTypes.CHERRY_LEAVES,
                    position.x, position.y, position.z,
                    1,
                    -direction.x * 0.25, 0.08 + random.nextDouble() * 0.12, -direction.z * 0.25,
                    0.02
            );
        }

        AABB area = player.getBoundingBox().inflate(radius);
        double strength = Config.WIND_KNOCKBACK.getAsDouble();
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != player)) {
            Vec3 delta = target.position().subtract(center);
            double distance = delta.length();
            Vec3 direction = distance < 0.001
                    ? player.getLookAngle().scale(-1.0).normalize()
                    : delta.scale(1.0 / distance);

            double falloff = 1.0 - Math.min(1.0, distance / radius);
            double power = strength * (0.45 + 0.55 * falloff);
            target.setDeltaMovement(direction.scale(power).add(0.0, 0.9 + 0.4 * falloff, 0.0));
            target.hurtMarked = true;
        }

        level.sendParticles(ParticleTypes.CHERRY_LEAVES, center.x, center.y + 1.0, center.z,
                40, 0.6, 1.2, 0.6, 0.35);
    }

    private static final class DashState {
        private final Vec3 direction;
        private final long endTick;
        private final Set<UUID> hitEntities = new HashSet<>();

        private DashState(Vec3 direction, long endTick) {
            this.direction = direction;
            this.endTick = endTick;
        }
    }
}
