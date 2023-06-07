package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method="setScreen", at=@At(value="HEAD"), cancellable = true)
    private void preventInventoryScreenForPlayerOne(Screen screen, CallbackInfo ci) {
        PlayerEntity thisEntity = ((MinecraftClient) (Object) this).player;
        if(ExampleMod.isPlayerOne(thisEntity) && (screen instanceof InventoryScreen)) {
            ci.cancel();
        }
    }

}
