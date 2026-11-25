package com.flowers.flowerymod.event;

import com.flowers.flowerymod.FloweryEquipment;
import com.flowers.flowerymod.action.FloweryActions;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public final class AbilityEvents {
    private static final String WAS_WEARING_KEY = "flowery:wearing_flower";

    private AbilityEvents() {
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player && FloweryActions.isAirDashImmune(player)) {
            event.setNewDamage(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        FloweryActions.tick(player);
        updateFlight(player);
    }

    private static void updateFlight(ServerPlayer player) {
        boolean wearing = FloweryEquipment.isWearing(player);
        boolean wasWearing = player.getPersistentData().getBoolean(WAS_WEARING_KEY);
        boolean specialMode = player.isCreative() || player.isSpectator();

        if (wearing == wasWearing) {
            if (wearing && !specialMode && !player.getAbilities().mayfly) {
                applyFlight(player, true);
            }
            return;
        }

        player.getPersistentData().putBoolean(WAS_WEARING_KEY, wearing);

        if (specialMode) {
            return;
        }

        applyFlight(player, wearing);
    }

    private static void applyFlight(ServerPlayer player, boolean enabled) {
        player.getAbilities().mayfly = enabled;
        player.getAbilities().flying = enabled;
        player.onUpdateAbilities();
    }
}
