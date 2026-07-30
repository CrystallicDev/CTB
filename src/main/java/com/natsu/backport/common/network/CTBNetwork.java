package com.natsu.backport.common.network;

import java.util.function.Supplier;

import com.natsu.backport.CTBackport;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CTBNetwork {

	private static final String VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CTBackport.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

	public static void register() {
		CHANNEL.registerMessage(0, AnimalVariantPacket.class,
				AnimalVariantPacket::encode, AnimalVariantPacket::decode, AnimalVariantPacket::handle);
		CHANNEL.registerMessage(1, CrafterSlotStatePacket.class,
				CrafterSlotStatePacket::encode, CrafterSlotStatePacket::decode, CrafterSlotStatePacket::handle);
		CHANNEL.registerMessage(2, WolfArmorPacket.class,
				WolfArmorPacket::encode, WolfArmorPacket::decode, WolfArmorPacket::handle);
	}

	/** Tells the client the wolf armor durability, -1 for none. */
	public record WolfArmorPacket(int entityId, int durability) {

		public static void encode(WolfArmorPacket packet, FriendlyByteBuf buffer) {
			buffer.writeVarInt(packet.entityId);
			buffer.writeVarInt(packet.durability + 1);
		}

		public static WolfArmorPacket decode(FriendlyByteBuf buffer) {
			return new WolfArmorPacket(buffer.readVarInt(), buffer.readVarInt() - 1);
		}

		public static void handle(WolfArmorPacket packet, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() ->
					com.natsu.backport.client.ClientVariantCache.putWolfArmor(packet.entityId, packet.durability));
			context.get().setPacketHandled(true);
		}
	}

	/** Client to server : the player toggled a crafter grid slot. */
	public record CrafterSlotStatePacket(int containerId, int slotId, boolean enabled) {

		public static void encode(CrafterSlotStatePacket packet, FriendlyByteBuf buffer) {
			buffer.writeVarInt(packet.containerId);
			buffer.writeVarInt(packet.slotId);
			buffer.writeBoolean(packet.enabled);
		}

		public static CrafterSlotStatePacket decode(FriendlyByteBuf buffer) {
			return new CrafterSlotStatePacket(buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean());
		}

		public static void handle(CrafterSlotStatePacket packet, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				net.minecraft.server.level.ServerPlayer sender = context.get().getSender();
				if (sender != null
						&& sender.containerMenu instanceof com.natsu.backport.common.inventory.CrafterMenu menu
						&& menu.containerId == packet.containerId
						&& packet.slotId >= 0 && packet.slotId < 9) {
					menu.setSlotState(packet.slotId, packet.enabled);
				}
			});
			context.get().setPacketHandled(true);
		}
	}

	/** Tells the client which farm animal variant an entity carries. */
	public record AnimalVariantPacket(int entityId, byte variant) {

		public static void encode(AnimalVariantPacket packet, FriendlyByteBuf buffer) {
			buffer.writeVarInt(packet.entityId);
			buffer.writeByte(packet.variant);
		}

		public static AnimalVariantPacket decode(FriendlyByteBuf buffer) {
			return new AnimalVariantPacket(buffer.readVarInt(), buffer.readByte());
		}

		public static void handle(AnimalVariantPacket packet, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() ->
					com.natsu.backport.client.ClientVariantCache.put(packet.entityId, packet.variant));
			context.get().setPacketHandled(true);
		}
	}
}
