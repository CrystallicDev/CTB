package com.natsu.backport.common.particles;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

/**
 * Backport of the 1.21 trail particle data : a target position, a color and a
 * travel duration in ticks.
 */
public class TrailParticleOption implements ParticleOptions {

	public static final Codec<TrailParticleOption> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.DOUBLE.fieldOf("target_x").forGetter(o -> o.target.x),
			Codec.DOUBLE.fieldOf("target_y").forGetter(o -> o.target.y),
			Codec.DOUBLE.fieldOf("target_z").forGetter(o -> o.target.z),
			Codec.INT.fieldOf("color").forGetter(o -> o.color),
			Codec.INT.fieldOf("duration").forGetter(o -> o.duration))
			.apply(i, (x, y, z, color, duration) -> new TrailParticleOption(new Vec3(x, y, z), color, duration)));

	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<TrailParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Override
		public TrailParticleOption fromCommand(ParticleType<TrailParticleOption> type, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			double x = reader.readDouble();
			reader.expect(' ');
			double y = reader.readDouble();
			reader.expect(' ');
			double z = reader.readDouble();
			reader.expect(' ');
			int color = reader.readInt();
			reader.expect(' ');
			int duration = reader.readInt();
			return new TrailParticleOption(new Vec3(x, y, z), color, duration);
		}

		@Override
		public TrailParticleOption fromNetwork(ParticleType<TrailParticleOption> type, FriendlyByteBuf buf) {
			return new TrailParticleOption(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
					buf.readInt(), buf.readVarInt());
		}
	};

	private final Vec3 target;
	private final int color;
	private final int duration;

	public TrailParticleOption(Vec3 target, int color, int duration) {
		this.target = target;
		this.color = color;
		this.duration = duration;
	}

	public Vec3 getTarget() {
		return target;
	}

	public int getColor() {
		return color;
	}

	public int getDuration() {
		return duration;
	}

	@Override
	public ParticleType<?> getType() {
		return CTBParticles.TRAIL.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeDouble(target.x);
		buf.writeDouble(target.y);
		buf.writeDouble(target.z);
		buf.writeInt(color);
		buf.writeVarInt(duration);
	}

	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d %d",
				getType().getRegistryName(), target.x, target.y, target.z, color, duration);
	}
}
