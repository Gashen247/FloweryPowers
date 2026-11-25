package com.flowers.flowerymod.entity;

import java.util.UUID;

import com.flowers.flowerymod.Config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

/**
 * A client-only "ghost" of the player's skin. It is spawned locally by the trail renderer,
 * so it never needs to be saved or synchronized with the server.
 */
public final class AfterimageEntity extends Entity {
    private final UUID ownerUUID;
    private final ResourceLocation skin;
    private final boolean slim;
    private final int color;
    private final float bodyYaw;
    private final float headYaw;
    private final float pitch;
    private final boolean crouching;
    private final Pose pose;
    private int age;

    public AfterimageEntity(EntityType<AfterimageEntity> type, Level level) {
        this(type, level, new UUID(0L, 0L), ResourceLocation.parse("minecraft:textures/entity/player/steve.png"),
                false, 0xFFFFFF, 0.0F, 0.0F, 0.0F, false, Pose.STANDING);
    }

    public AfterimageEntity(EntityType<AfterimageEntity> type, Level level, UUID ownerUUID, ResourceLocation skin, boolean slim,
                            int color, float bodyYaw, float headYaw, float pitch, boolean crouching, Pose pose) {
        super(type, level);
        this.ownerUUID = ownerUUID;
        this.skin = skin;
        this.slim = slim;
        this.color = color;
        this.bodyYaw = bodyYaw;
        this.headYaw = headYaw;
        this.pitch = pitch;
        this.crouching = crouching;
        this.pose = pose;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;
        if (this.age >= Config.TRAIL_LIFETIME_TICKS.getAsInt()) {
            this.discard();
        }
    }

    public float getFade(float partialTick) {
        float life = Math.max(1, Config.TRAIL_LIFETIME_TICKS.getAsInt());
        return Math.max(0.0F, 1.0F - (this.age + partialTick) / life);
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public ResourceLocation getSkin() {
        return this.skin;
    }

    public boolean isSlim() {
        return this.slim;
    }

    public int getColor() {
        return this.color;
    }

    public float getBodyYaw() {
        return this.bodyYaw;
    }

    public float getHeadYaw() {
        return this.headYaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isCrouching() {
        return this.crouching;
    }

    public Pose getPose() {
        return this.pose;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
