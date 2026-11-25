package com.flowers.flowerymod.client.render;

import com.flowers.flowerymod.entity.AfterimageEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public final class AfterimageRenderer extends EntityRenderer<AfterimageEntity> {
    private final PlayerModel<AbstractClientPlayer> defaultModel;
    private final PlayerModel<AbstractClientPlayer> slimModel;

    public AfterimageRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.defaultModel = new PlayerModel<>(context.getModelSet().bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel<>(context.getModelSet().bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(AfterimageEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float fade = entity.getFade(partialTick);
        if (fade <= 0.001F) {
            return;
        }

        AbstractClientPlayer player = null;
        if (Minecraft.getInstance().level != null) {
            var found = Minecraft.getInstance().level.getPlayerByUUID(entity.getOwnerUUID());
            if (found instanceof AbstractClientPlayer clientPlayer) {
                player = clientPlayer;
            }
        }
        if (player == null) {
            return;
        }

        int alpha = (int) (fade * 110.0F) & 0xFF;
        int color = (alpha << 24) | (entity.getColor() & 0xFFFFFF);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(entity.getSkin()));

        boolean gliding = entity.getPose() == Pose.FALL_FLYING;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        if (gliding) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getPitch()));
            poseStack.translate(0.0F, -0.15F, 0.0F);
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        PlayerModel<AbstractClientPlayer> model = entity.isSlim() ? this.slimModel : this.defaultModel;
        model.setAllVisible(true);
        model.crouching = entity.isCrouching() && !gliding;
        model.young = false;
        model.attackTime = 0.0F;
        model.head.yRot = (entity.getHeadYaw() - entity.getBodyYaw()) * Mth.DEG_TO_RAD;
        model.head.xRot = gliding ? -0.7853982F : entity.getPitch() * Mth.DEG_TO_RAD;
        model.body.yRot = 0.0F;
        model.rightArm.xRot = 0.0F;
        model.leftArm.xRot = 0.0F;
        model.rightArm.zRot = gliding ? -1.15F : 0.0F;
        model.leftArm.zRot = gliding ? 1.15F : 0.0F;
        model.rightArm.yRot = 0.0F;
        model.leftArm.yRot = 0.0F;
        model.rightLeg.xRot = gliding ? -0.2F : 0.0F;
        model.leftLeg.xRot = gliding ? -0.2F : 0.0F;
        model.rightLeg.yRot = 0.0F;
        model.leftLeg.yRot = 0.0F;
        model.rightLeg.zRot = 0.0F;
        model.leftLeg.zRot = 0.0F;
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AfterimageEntity entity) {
        return entity.getSkin();
    }
}
