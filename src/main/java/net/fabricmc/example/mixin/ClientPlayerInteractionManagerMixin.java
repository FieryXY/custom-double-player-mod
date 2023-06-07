package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {


    @Shadow @Final private MinecraftClient client;

    @Inject(method="attackBlock", at=@At(value="HEAD"), cancellable = true)
    private void cancelAttackBlockForPlayerOne(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity thisPlayer = this.client.player;

        if(ExampleMod.isPlayerOne(thisPlayer)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method="attackEntity", at=@At(value="HEAD"), cancellable = true)
    private void cancelAttackEntityForPlayerOne(PlayerEntity player, Entity target, CallbackInfo ci) {
        ClientPlayerEntity thisPlayer = this.client.player;

        if(ExampleMod.isPlayerOne(thisPlayer)) {
            ci.cancel();
        }
    }

}
