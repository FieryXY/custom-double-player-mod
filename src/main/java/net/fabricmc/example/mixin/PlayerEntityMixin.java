package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract PlayerInventory getInventory();

    @Inject(method="collideWithEntity", at=@At(value="HEAD"), cancellable = true)
    private void handleEntityCollision(Entity entity, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(ExampleMod.isPlayerTwo(player) && entity.getType() != EntityType.ITEM && entity.getType() != EntityType.EXPERIENCE_ORB) {
            ci.cancel();
        }
        else if(ExampleMod.isPlayerOne(player) &&
                (entity.getType() == EntityType.ITEM || entity.getType() == EntityType.EXPERIENCE_ORB)) {
            ci.cancel();
        }
     }

     @Inject(method="damage", at=@At(value="HEAD"), cancellable = true)
     private void damageModifications(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(ExampleMod.isPlayerOne(player)) {
            ((DoubleBodyPlayerAccessor) player).getPartner().damage(source, amount);
        }
        else if(ExampleMod.isPlayerTwo(player)) {
            cir.setReturnValue(false);
        }
     }

    @Inject(method="tick", at=@At(value="TAIL"))
    private void tickMain(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if(ExampleMod.isPlayerTwo(player)) {
            PlayerEntity partner = ((DoubleBodyPlayerAccessor) player).getPartner();
            player.setHealth(partner.getHealth());
            player.setAir(partner.getAir());


            player.clearStatusEffects();
            partner.getStatusEffects().forEach(effect -> {
                player.addStatusEffect(effect);
            });

            //TODO CHECK IF THIS WORKS
//            if(partner.getVehicle() != null) {
//                player.startRiding(partner.getVehicle());
//            }
//            else {
//                player.stopRiding();
//            }

        }
        else if(ExampleMod.isPlayerOne(player)) {
            PlayerEntity partner = ((DoubleBodyPlayerAccessor) player).getPartner();
            //TODO Make Experience Sync Work :(
//            player.experienceLevel = partner.experienceLevel;
//            player.experienceProgress = partner.experienceProgress;
//            player.totalExperience = partner.totalExperience;

            if(!player.world.isClient()) {
                player.getInventory().main.clear();
                for(int i = 0; i < player.getInventory().main.size(); i++) {
                    player.getInventory().main.set(i, partner.getInventory().main.get(i));
                }
                player.getInventory().armor.clear();
                for(int i = 0; i < player.getInventory().armor.size(); i++) {
                    player.getInventory().armor.set(i, partner.getInventory().armor.get(i));
                }
                player.getInventory().offHand.clear();
                for(int i = 0; i < player.getInventory().offHand.size(); i++) {
                    player.getInventory().offHand.set(i, partner.getInventory().offHand.get(i));
                }


                player.getInventory().selectedSlot = partner.getInventory().selectedSlot;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(partner.getInventory().selectedSlot);
                ServerPlayNetworking.send((ServerPlayerEntity) player, ExampleMod.PLAYER_DOUBLE_UPDATE_SLOT, buf);
            }

            //System.out.println("Player 1 Is Blocking: " + player.isBlocking());

        }
    }

    @Inject(method="attack", at=@At(value="HEAD"), cancellable = true)
    private void disablePlayerOneAttack(Entity target, CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        if(ExampleMod.isPlayerOne(thisEntity)) {
            ci.cancel();
        }
    }

}
