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
