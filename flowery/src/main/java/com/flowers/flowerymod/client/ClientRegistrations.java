package com.flowers.flowerymod.client;

import com.flowers.flowerymod.Flowery;
import com.flowers.flowerymod.client.render.AfterimageRenderer;
import com.flowers.flowerymod.entity.FloweryEntities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Flowery.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientRegistrations {
    private ClientRegistrations() {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FloweryEntities.AFTERIMAGE.get(), AfterimageRenderer::new);
    }
}
