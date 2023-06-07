package net.fabricmc.example.mixin;

import com.google.common.base.Predicates;
import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow private @Nullable LivingEntity attacker;

    @Inject(method="setAttacker", at=@At(value="HEAD"))
    private void setPlayer2AttackertoPlayer1(LivingEntity attacker, CallbackInfo ci) {

        LivingEntity thisEntity = (LivingEntity) (Object) this;

        if(attacker == null) {
            return;
        }

        if(ExampleMod.isPlayerTwo(thisEntity)) {
            DoubleBodyPlayerAccessor doubleBodyPlayer = (DoubleBodyPlayerAccessor) thisEntity;
            attacker.setAttacking(doubleBodyPlayer.getPartner());
            doubleBodyPlayer.getPartner().setAttacker(attacker);
            this.attacker = null;
         }
    }

}