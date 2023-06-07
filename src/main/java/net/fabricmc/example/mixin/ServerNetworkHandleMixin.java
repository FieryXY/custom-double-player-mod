package net.fabricmc.example.mixin;

import net.fabricmc.example.DoubleBodyPlayerAccessor;
import net.fabricmc.example.DoubleBodyPlayerType;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerNetworkHandleMixin {

    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Shadow public ServerPlayerEntity player;

    @Inject(method="onPlayerMove", at=@At(value="HEAD"), cancellable = true)
    private void cancelPlayerMoveUpdateForPlayer2(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        PlayerEntity player = this.getPlayer();
        if(ExampleMod.isPlayerTwo(player)) {
            DoubleBodyPlayerAccessor doubleBodyPlayer = (DoubleBodyPlayerAccessor) player;
            ci.cancel();
        }
    }

    @Redirect(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V"))
    private void overridePlayer2Movement(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch) {
        if(!player.getWorld().isClient) {
            DoubleBodyPlayerAccessor doubleBodyPlayer = (DoubleBodyPlayerAccessor) (PlayerEntity) player;
            if(ExampleMod.isPlayerTwo(player)) {

                PlayerEntity partner = doubleBodyPlayer.getPartner();
                player.updatePositionAndAngles(partner.getX(), partner.getY(), partner.getZ(), partner.getHeadYaw(), MathHelper.clamp(partner.getPitch(), -90.0F, 90.0F));

                //Send New Position and Velocity to Client
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeDouble(partner.getX());
                buf.writeDouble(partner.getY());
                buf.writeDouble(partner.getZ());
                buf.writeFloat(partner.getYaw());
                buf.writeFloat(MathHelper.clamp(partner.getPitch(), -90.0F, 90.0F));

                //Velocity
                buf.writeDouble(partner.getVelocity().getX());
                buf.writeDouble(partner.getVelocity().getY());
                buf.writeDouble(partner.getVelocity().getZ());

                //Send Packet
                ServerPlayNetworking.send(player, ExampleMod.PLAYER_POS_VELOCITY_OVERRIDE_PACKET_ID, buf);

                //Update Chunk Manager with new Player Position (so it can provide the client with the chunks)
                //We need to do this manually since this step is usually tied up with the player's autonomous movement
                //being synced with the server (which we don't want)
                player.getWorld().getChunkManager().updatePosition(player);
                return;
            }
        }

        player.updatePositionAndAngles(x,y,z,yaw,pitch);

    }

}
