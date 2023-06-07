package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow @Final @Deprecated private Item item;

    @Shadow public abstract boolean itemMatches(RegistryEntry<Item> itemEntry);

    @Inject(method="use", at=@At(value="HEAD"), cancellable = true)
    private void useInject(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        boolean playerOneItems = (this.item instanceof BlockItem) || (this.item instanceof FluidModificationItem) || this.item.isFood() || (this.item instanceof ShieldItem) || (this.item instanceof FireworkRocketItem);

        if(ExampleMod.isPlayerOne(user) && !playerOneItems) {
            cir.setReturnValue(TypedActionResult.fail((ItemStack) (Object) this));
        }
        if(ExampleMod.isPlayerTwo(user) && playerOneItems) {
            cir.setReturnValue(TypedActionResult.fail((ItemStack) (Object) this));
        }

    }

    @Inject(method="useOnBlock", at=@At(value="HEAD"), cancellable = true)
    private void useOnBlockInject(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        boolean playerOneItems = (this.item instanceof BlockItem) || (this.item instanceof FluidModificationItem) || this.item.isFood() || (this.item instanceof ShieldItem) || (this.item instanceof FireworkRocketItem);

        if(ExampleMod.isPlayerOne(context.getPlayer()) && !playerOneItems) {
            cir.setReturnValue(ActionResult.FAIL);
        }
        if(ExampleMod.isPlayerTwo(context.getPlayer()) && playerOneItems) {
            cir.setReturnValue(ActionResult.FAIL);
        }

    }

}
