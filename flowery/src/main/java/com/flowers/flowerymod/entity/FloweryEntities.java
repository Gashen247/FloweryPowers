package com.flowers.flowerymod.entity;

import com.flowers.flowerymod.Flowery;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FloweryEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Flowery.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AfterimageEntity>> AFTERIMAGE = ENTITIES.register(
            "afterimage",
            () -> EntityType.Builder.<AfterimageEntity>of(AfterimageEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(32)
                    .build("afterimage")
    );

    private FloweryEntities() {
    }
}
