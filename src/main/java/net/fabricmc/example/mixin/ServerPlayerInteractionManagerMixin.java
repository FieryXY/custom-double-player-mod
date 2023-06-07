package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.ExampleMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Redirect(method="interactItem", at=@At(value="INVOKE", target="Lnet/minecraft/server/network/ServerPlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    private void redirectItemUseInventoryUpdateToPlayer2(ServerPlayerEntity instance, Hand hand, ItemStack itemStack) {

        if(ExampleMod.isPlayerOne(instance)) {
            ((DoubleBodyPlayerAccessor) instance).getPartner().setStackInHand(hand, itemStack);
        }

        instance.setStackInHand(hand, itemStack);
    }

    @Redirect(method="interactItem", at=@At(value="INVOKE", target="Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"))
    private TypedActionResult<ItemStack> redirectItemUseToPlayer1(ItemStack instance, World world, PlayerEntity user, Hand hand) {

        System.out.println("Redirect Item Use");

        if(ExampleMod.isPlayerTwo(user)) {
            System.out.println("Use To Player 1 Instead");
            return instance.use(world, ((DoubleBodyPlayerAccessor) user).getPartner(), hand);
        }

        return instance.use(world, user, hand);
    }

}
