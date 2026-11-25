package com.flowers.flowerymod.network;

import com.flowers.flowerymod.Flowery;
import com.flowers.flowerymod.action.FloweryActions;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPayloads {
    private ModPayloads() {
    }

    public enum Action {
        DASH,
        WIND,
        WIND_RUN
    }

    public record AbilityActionPayload(Action action) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<AbilityActionPayload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Flowery.MODID, "ability_action"));

        public static final StreamCodec<ByteBuf, AbilityActionPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                payload -> payload.action.ordinal(),
                AbilityActionPayload::fromOrdinal
        );

        private static AbilityActionPayload fromOrdinal(int ordinal) {
            Action[] actions = Action.values();
            if (ordinal < 0 || ordinal >= actions.length) {
                return new AbilityActionPayload(Action.DASH);
            }
            return new AbilityActionPayload(actions[ordinal]);
        }

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(AbilityActionPayload.TYPE, AbilityActionPayload.STREAM_CODEC,
                (payload, context) -> handle(payload, context));
    }

    private static void handle(AbilityActionPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            FloweryActions.handle(serverPlayer, payload.action());
        }
    }
}
