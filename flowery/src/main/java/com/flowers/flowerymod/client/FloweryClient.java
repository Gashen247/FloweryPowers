package com.flowers.flowerymod.client;

import com.flowers.flowerymod.Flowery;
import com.flowers.flowerymod.FloweryEquipment;
import com.flowers.flowerymod.FlowerySounds;
import com.flowers.flowerymod.network.ModPayloads;
import com.flowers.flowerymod.util.GlidingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;

import org.lwjgl.glfw.GLFW;

@Mod(value = Flowery.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Flowery.MODID, value = Dist.CLIENT)
public final class FloweryClient {
    private static boolean rightButtonDown = false;
    private static boolean windRunActive = false;
    private static int windRunKeepAlive = 0;

    public FloweryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onMouseButton(InputEvent.MouseButton.Post event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            rightButtonDown = event.getAction() == GLFW.GLFW_PRESS;
            if (event.getAction() == GLFW.GLFW_RELEASE) {
                stopWindRun();
            }
        }

        if (event.getAction() != GLFW.GLFW_PRESS || Minecraft.getInstance().screen != null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !FloweryEquipment.isWearing(player)) {
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            PacketDistributor.sendToServer(new ModPayloads.AbilityActionPayload(ModPayloads.Action.DASH));
            playAttachedSound(player, FlowerySounds.JARONA.get(), 1.0F, 1.0F);
            GlidingHelper.setGliding(player, true);
            TrailSpawner.notifyDash();
        } else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (isForwardDown(minecraft)) {
                startWindRun(player);
            } else {
                PacketDistributor.sendToServer(new ModPayloads.AbilityActionPayload(ModPayloads.Action.WIND));
                playAttachedSound(player, FlowerySounds.MYSTERIOUS_WIND.get(), 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (minecraft.level == null || player == null || minecraft.screen != null) {
            if (minecraft.screen != null) {
                rightButtonDown = false;
                stopWindRun();
            }
            TrailSpawner.reset();
            return;
        }

        if (!FloweryEquipment.isWearing(player)) {
            rightButtonDown = false;
            stopWindRun();
            TrailSpawner.reset();
            return;
        }

        boolean forwardDown = isForwardDown(minecraft);
        if (rightButtonDown && forwardDown && !windRunActive) {
            startWindRun(player);
        }

        if (windRunActive) {
            if (!rightButtonDown || !forwardDown) {
                stopWindRun();
            } else if (--windRunKeepAlive <= 0) {
                PacketDistributor.sendToServer(new ModPayloads.AbilityActionPayload(ModPayloads.Action.WIND_RUN));
                windRunKeepAlive = 5;
            }
        }

        TrailSpawner.tick(minecraft.level, player);
    }

    private static boolean isForwardDown(Minecraft minecraft) {
        return minecraft.options.keyUp.isDown();
    }

    private static void playAttachedSound(LocalPlayer player, SoundEvent sound, float volume, float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getSoundManager().play(new EntityBoundSoundInstance(
                sound,
                SoundSource.PLAYERS,
                volume,
                pitch,
                player,
                minecraft.level.random.nextLong()
        ));
    }

    private static void startWindRun(LocalPlayer player) {
        windRunActive = true;
        windRunKeepAlive = 5;
        playAttachedSound(player, FlowerySounds.MYSTERIOUS_WIND.get(), 0.75F, 1.15F);
        TrailSpawner.notifyWindRun();
        PacketDistributor.sendToServer(new ModPayloads.AbilityActionPayload(ModPayloads.Action.WIND_RUN));
    }

    private static void stopWindRun() {
        windRunActive = false;
        windRunKeepAlive = 0;
        TrailSpawner.clearWindRun();
    }
}
