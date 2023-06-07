package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ClientLivingEntity {

    @Inject(method="canHit", at=@At(value="HEAD"), cancellable = true)
    private void avoidInterDoubleBodyHits(CallbackInfoReturnable<Boolean> ci) {
//        System.out.println("Raycast Mixin Running");


        Entity thisEntity = (Entity) (Object) this;

        if(!thisEntity.getWorld().isClient()) {
            return;
        }

        if(ExampleMod.isFromDoubleBody(MinecraftClient.getInstance().player) &&
                ((DoubleBodyPlayerAccessor) MinecraftClient.getInstance().player).getPartner().equals(thisEntity)) {
//            System.out.println("Avoided Hit!");
            ci.setReturnValue(false);
        }
    }

}
