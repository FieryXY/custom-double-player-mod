package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.DoubleBodyPlayerType;
import net.fabricmc.example.ExampleMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class CollisionDisableMixin {

    @Inject(method="canHit", at=@At(value="HEAD"), cancellable = true)
    private void removeProjectileCollision(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if(ExampleMod.isPlayerTwo(entity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method="changeLookDirection", at=@At(value="HEAD"), cancellable = true)
    private void preventLookChangesForPlayerTwo(double x, double y, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if(ExampleMod.isPlayerTwo(entity)) {
            ci.cancel();
        }
    }

}
