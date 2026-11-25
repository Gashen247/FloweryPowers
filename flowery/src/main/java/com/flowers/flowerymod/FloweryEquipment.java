package com.flowers.flowerymod;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class FloweryEquipment {
    private FloweryEquipment() {
    }

    public static boolean isWearing(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).is(Flowery.FLOWER_CHESTPLATE.get());
    }
}
