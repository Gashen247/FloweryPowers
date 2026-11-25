package com.flowers.flowerymod;

import com.flowers.flowerymod.entity.FloweryEntities;
import com.flowers.flowerymod.item.FlowerChestplateItem;
import com.flowers.flowerymod.network.ModPayloads;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Flowery.MODID)
public final class Flowery {
    public static final String MODID = "flowery";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<FlowerChestplateItem> FLOWER_CHESTPLATE = ITEMS.register(
            "flower_chestplate",
            () -> new FlowerChestplateItem(
                    FloweryMaterials.FLOWER_ARMOR_MATERIAL,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(16))
            )
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FLOWERY_TAB = CREATIVE_MODE_TABS.register(
            "flowery",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.flowery"))
                    .icon(() -> FLOWER_CHESTPLATE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> output.accept(FLOWER_CHESTPLATE.get()))
                    .build()
    );

    public Flowery(IEventBus modEventBus, ModContainer modContainer) {
        FloweryMaterials.ARMOR_MATERIALS.register(modEventBus);
        FlowerySounds.SOUNDS.register(modEventBus);
        FloweryEntities.ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(ModPayloads::register);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
