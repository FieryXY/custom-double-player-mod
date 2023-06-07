package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method="openHandledScreen", at=@At(value="HEAD"), cancellable = true)
    private void preventPlayer1HandledScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
        ServerPlayerEntity thisServerPlayerEntity = (ServerPlayerEntity) (Object) this;
        if(ExampleMod.isPlayerOne(thisServerPlayerEntity)) {
            cir.cancel();
        }
    }

}
