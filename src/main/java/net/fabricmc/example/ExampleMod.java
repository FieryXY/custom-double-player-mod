package net.fabricmc.example;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.mixin.DoubleBodyPlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("doublebody");

	public static final Identifier PLAYER_POS_VELOCITY_OVERRIDE_PACKET_ID = new Identifier("doublebody", "pos_vel_override");
	public static final Identifier PLAYER_DOUBLE_BODY_STATUS_UPDATE_ID = new Identifier("doublebody", "double_body_update");

	public static final Identifier PLAYER_DOUBLE_UPDATE_SLOT = new Identifier("doublebody", "update_selected_slot");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("unpairbody")
						.requires(source -> source.hasPermissionLevel(3))
						.then(argument("player", EntityArgumentType.player())
								.executes(ctx -> {

									PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

									if(isFromDoubleBody(player)) {
										DoubleBodyPlayerAccessor double1 = (DoubleBodyPlayerAccessor) player;
										double1.setDoubleBodyPlayerType(DoubleBodyPlayerType.NORMAL);
										double1.setPartner(null);
										sendDoubleBodyPlayerUpdate(player, DoubleBodyPlayerType.NORMAL, null);
									}

									return 1;

								})))));

		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
			dispatcher.register(literal("pairbody")
					.requires(source -> source.hasPermissionLevel(3))
					.then(argument("player1", EntityArgumentType.player())
							.then(argument("player2", EntityArgumentType.player())
									.executes(ctx -> {

										PlayerEntity player1 = EntityArgumentType.getPlayer(ctx, "player1");
										PlayerEntity player2 = EntityArgumentType.getPlayer(ctx, "player2");

										if(player1 == null || player2 == null) {
											throw new SimpleCommandExceptionType(Text.literal("One or more of the players don't exist")).create();
										}

										if(!(player1 instanceof DoubleBodyPlayerAccessor && player2 instanceof DoubleBodyPlayerAccessor)) {
											throw new SimpleCommandExceptionType(Text.literal("Something went wrong with the player mixin :(")).create();
										}

										DoubleBodyPlayerAccessor double1 = (DoubleBodyPlayerAccessor) ((Object) player1);
										DoubleBodyPlayerAccessor double2 = (DoubleBodyPlayerAccessor) ((Object) player2);

										double1.setDoubleBodyPlayerType(DoubleBodyPlayerType.ONE);
										double2.setDoubleBodyPlayerType(DoubleBodyPlayerType.TWO);

										double1.setPartner(player2);
										double2.setPartner(player1);


										//Send Double Body Status Updates to Client
										sendDoubleBodyPlayerUpdate(player1, DoubleBodyPlayerType.ONE, player2);
										sendDoubleBodyPlayerUpdate(player2, DoubleBodyPlayerType.TWO, player1);


										return 1;
									})))



			)
		));

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			PlayerEntity player = handler.getPlayer();
			if(player instanceof DoubleBodyPlayerAccessor) {
				DoubleBodyPlayerAccessor doubleBodyPlayer = (DoubleBodyPlayerAccessor) ((Object) player);
				if(doubleBodyPlayer.getDoubleBodyPlayerType() != DoubleBodyPlayerType.NORMAL
						&& doubleBodyPlayer.getPartner() instanceof DoubleBodyPlayerAccessor) {
					System.out.println("Updating Partner...");
					DoubleBodyPlayerAccessor doubleBodyPartner = (DoubleBodyPlayerAccessor) ((Object) doubleBodyPlayer.getPartner());
					doubleBodyPartner.setPartner(null);
					doubleBodyPartner.setDoubleBodyPlayerType(DoubleBodyPlayerType.NORMAL);
					sendDoubleBodyPlayerUpdate(doubleBodyPartner.getPartner(), DoubleBodyPlayerType.NORMAL, null);
				}

				doubleBodyPlayer.setPartner(null);
				doubleBodyPlayer.setDoubleBodyPlayerType(DoubleBodyPlayerType.NORMAL);
			}
		});

	}

	public static boolean isPlayerOne(Entity entity) {
		return (entity instanceof DoubleBodyPlayerAccessor) &&
				((DoubleBodyPlayerAccessor) entity).getDoubleBodyPlayerType() == DoubleBodyPlayerType.ONE &&
				((DoubleBodyPlayerAccessor) entity).getPartner() != null;

	}

	public static boolean isPlayerTwo(Entity entity) {
		return (entity instanceof DoubleBodyPlayerAccessor) &&
				((DoubleBodyPlayerAccessor) entity).getDoubleBodyPlayerType() == DoubleBodyPlayerType.TWO &&
				((DoubleBodyPlayerAccessor) entity).getPartner() != null;

	}

	public static boolean isFromDoubleBody(Entity entity) {
		return isPlayerOne(entity) || isPlayerTwo(entity);
	}

	public static void sendDoubleBodyPlayerUpdate(PlayerEntity target, DoubleBodyPlayerType type, PlayerEntity partner) {

		if(!(target instanceof ServerPlayerEntity)) {
			throw new RuntimeException("Cannot send Double Body Player Status update since targeted player is not a ServerPlayerEntity");
		}

		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(type.getIndex());
		//buf.writeUuid((partner != null) ? partner.getUuid() : UUID.randomUUID());
		buf.writeInt((partner != null) ? partner.getId() : -1);
		ServerPlayNetworking.send((ServerPlayerEntity) target, PLAYER_DOUBLE_BODY_STATUS_UPDATE_ID, buf);
	}
}
