package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Shadow protected abstract void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f);

    @Shadow protected abstract void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h);

    @Inject(method="render", at=@At(value="HEAD"), cancellable = true)
    private void cancelPartnerRender(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        ClientPlayerEntity self = MinecraftClient.getInstance().player;
        if(ExampleMod.isFromDoubleBody(self) && ((DoubleBodyPlayerAccessor) self).getPartner().equals(abstractClientPlayerEntity)) {
            ci.cancel();
        }
    }

}
