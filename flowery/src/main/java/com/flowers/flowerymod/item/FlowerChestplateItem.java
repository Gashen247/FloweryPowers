package com.flowers.flowerymod.item;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;

public final class FlowerChestplateItem extends ArmorItem {
    public FlowerChestplateItem(Holder<ArmorMaterial> material, Properties properties) {
        super(material, Type.CHESTPLATE, properties);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.flowery.flower_chestplate.tooltip.lmb"));
        tooltip.add(Component.translatable("item.flowery.flower_chestplate.tooltip.rmb"));
        tooltip.add(Component.translatable("item.flowery.flower_chestplate.tooltip.windrun"));
        tooltip.add(Component.translatable("item.flowery.flower_chestplate.tooltip.flight"));
    }
}
