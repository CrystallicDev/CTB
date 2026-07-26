package com.natsu.backport.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

/** The 26.2 geyser particle payload : how many water blocks the plume crosses. */
public record GeyserParticleOptions(ParticleType<GeyserParticleOptions> type, int waterBlocks) implements ParticleOptions {

	public static final Codec<GeyserParticleOptions> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("water_blocks").forGetter(GeyserParticleOptions::waterBlocks))
			.apply(i, waterBlocks -> new GeyserParticleOptions(
					com.natsu.backport.common.registry.CTBParticles.GEYSER.get(), waterBlocks)));

	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<GeyserParticleOptions> DESERIALIZER =
			new ParticleOptions.Deserializer<>() {
				@Override
				public GeyserParticleOptions fromCommand(ParticleType<GeyserParticleOptions> type, StringReader reader)
						throws CommandSyntaxException {
					reader.expect(' ');
					return new GeyserParticleOptions(type, reader.readInt());
				}

				@Override
				public GeyserParticleOptions fromNetwork(ParticleType<GeyserParticleOptions> type, FriendlyByteBuf buffer) {
					return new GeyserParticleOptions(type, buffer.readVarInt());
				}
			};

	@Override
	public ParticleType<GeyserParticleOptions> getType() {
		return this.type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.waterBlocks);
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(this.type) + " " + this.waterBlocks;
	}
}
