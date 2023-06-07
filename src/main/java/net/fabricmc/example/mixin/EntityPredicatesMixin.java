package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class EntityPredicatesMixin {

    @Inject(method="canBePushedBy", at=@At(value="RETURN"), cancellable = true)
    private static void changeCanBePushedBy(Entity entity2, CallbackInfoReturnable<Predicate<Entity>> cir) {
        cir.setReturnValue(cir.getReturnValue().and(ent -> {

            if(ExampleMod.isPlayerTwo(entity2) || ExampleMod.isPlayerTwo(ent)) {
                return false;
            }

            return true;
        }));
    }

}
