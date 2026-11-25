package com.flowers.flowerymod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlowerySounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, Flowery.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> JARONA = SOUNDS.register("jarona",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Flowery.MODID, "jarona")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MYSTERIOUS_WIND = SOUNDS.register("mysterious_wind",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Flowery.MODID, "mysterious_wind")));

    private FlowerySounds() {
    }
}
