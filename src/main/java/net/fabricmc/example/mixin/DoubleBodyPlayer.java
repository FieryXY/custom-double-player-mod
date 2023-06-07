package net.fabricmc.example.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.DoubleBodyPlayerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class DoubleBodyPlayer implements DoubleBodyPlayerAccessor {

    public DoubleBodyPlayerType doubleBodyType = DoubleBodyPlayerType.NORMAL;
    public PlayerEntity partner = null;

    public DoubleBodyPlayerType getDoubleBodyPlayerType() {
        return this.doubleBodyType;
    }

    public PlayerEntity getPartner() {
        return this.partner;
    }

    public void setDoubleBodyPlayerType(DoubleBodyPlayerType type) {
        this.doubleBodyType = type;
    }

    public void setPartner(PlayerEntity player) {
        this.partner = player;
    }


}
