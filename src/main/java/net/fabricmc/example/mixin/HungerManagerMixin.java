package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(method="update", at=@At(value="HEAD"), cancellable = true)
    private void overridePlayer2Hunger(PlayerEntity player, CallbackInfo ci) {
        if(ExampleMod.isPlayerTwo(player)) {
            PlayerEntity partner = ((DoubleBodyPlayerAccessor) player).getPartner();
            player.getHungerManager().setExhaustion(partner.getHungerManager().getExhaustion());
            player.getHungerManager().setFoodLevel(partner.getHungerManager().getFoodLevel());
            player.getHungerManager().setSaturationLevel(partner.getHungerManager().getSaturationLevel());
            ci.cancel();
        }
    }

}
