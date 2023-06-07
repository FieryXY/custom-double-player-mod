package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.PLAYER_POS_VELOCITY_OVERRIDE_PACKET_ID, (client, handler, buf, responseSender) -> {

            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float yaw = buf.readFloat();
            float pitch = buf.readFloat();

            double vx = buf.readDouble();
            double vy = buf.readDouble();
            double vz = buf.readDouble();

            client.execute(() -> {
                PlayerEntity player = client.player;
                player.updatePositionAndAngles(x,y,z,yaw,pitch);
                player.setVelocity(vx, vy, vz);
            });

        });

        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.PLAYER_DOUBLE_BODY_STATUS_UPDATE_ID, (client, handler, buf, responseSender) -> {
            int doubleBodyTypeIndex = buf.readInt();
            int entityId = buf.readInt();
            DoubleBodyPlayerType doubleBodyType = DoubleBodyPlayerType.getTypeByIndex(doubleBodyTypeIndex);
            Entity partnerEntity = (entityId >= 0) ? client.world.getEntityById(entityId) : null;
            PlayerEntity partner = (partnerEntity instanceof PlayerEntity) ? (PlayerEntity) partnerEntity : null;


            System.out.println("Client: Setting " + client.player.getName() + " to " + doubleBodyType);

            if(client.player instanceof DoubleBodyPlayerAccessor) {
                ((DoubleBodyPlayerAccessor) client.player).setDoubleBodyPlayerType(doubleBodyType);
                ((DoubleBodyPlayerAccessor) client.player).setPartner(partner);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.PLAYER_DOUBLE_UPDATE_SLOT, ((client, handler, buf, responseSender) -> {
            int newSelectedSlot = buf.readInt();
            if(newSelectedSlot >= 0) {
                client.player.getInventory().selectedSlot = newSelectedSlot;
            }
        }));
    }
}
