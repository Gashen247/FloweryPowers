package com.flowers.flowerymod;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FloweryMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Flowery.MODID);

    public static final Holder<ArmorMaterial> FLOWER_ARMOR_MATERIAL = ARMOR_MATERIALS.register("flower", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.BODY, 3);
            }),
            10,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(Items.POPPY),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Flowery.MODID, "flower"))),
            0.0F,
            0.0F
    ));

    private FloweryMaterials() {
    }
}
