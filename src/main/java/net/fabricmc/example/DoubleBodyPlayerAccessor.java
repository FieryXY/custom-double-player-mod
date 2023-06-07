package net.fabricmc.example;

import net.fabricmc.example.DoubleBodyPlayerType;
import net.minecraft.entity.player.PlayerEntity;

public interface DoubleBodyPlayerAccessor {

     DoubleBodyPlayerType getDoubleBodyPlayerType();

     PlayerEntity getPartner();

     void setDoubleBodyPlayerType(DoubleBodyPlayerType type);

     void setPartner(PlayerEntity player);

}
