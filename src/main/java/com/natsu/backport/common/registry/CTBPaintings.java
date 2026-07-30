package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.world.entity.decoration.Motive;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** The 1.21 paintings, sizes in pixels. Textures live in textures/painting/. */
public class CTBPaintings {

	public static final DeferredRegister<Motive> PAINTINGS =
			DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, CTBackport.MODID);

	public static final RegistryObject<Motive> BACKYARD = register("backyard", 3, 4);
	public static final RegistryObject<Motive> BAROQUE = register("baroque", 2, 2);
	public static final RegistryObject<Motive> BOUQUET = register("bouquet", 3, 3);
	public static final RegistryObject<Motive> CAVEBIRD = register("cavebird", 3, 3);
	public static final RegistryObject<Motive> CHANGING = register("changing", 4, 2);
	public static final RegistryObject<Motive> COTAN = register("cotan", 3, 3);
	public static final RegistryObject<Motive> DENNIS = register("dennis", 3, 3);
	public static final RegistryObject<Motive> ENDBOSS = register("endboss", 3, 3);
	public static final RegistryObject<Motive> FERN = register("fern", 3, 3);
	public static final RegistryObject<Motive> FINDING = register("finding", 4, 2);
	public static final RegistryObject<Motive> HUMBLE = register("humble", 2, 2);
	public static final RegistryObject<Motive> LOWMIST = register("lowmist", 4, 2);
	public static final RegistryObject<Motive> MEDITATIVE = register("meditative", 1, 1);
	public static final RegistryObject<Motive> ORB = register("orb", 4, 4);
	public static final RegistryObject<Motive> OWLEMONS = register("owlemons", 3, 3);
	public static final RegistryObject<Motive> PASSAGE = register("passage", 4, 2);
	public static final RegistryObject<Motive> POND = register("pond", 3, 4);
	public static final RegistryObject<Motive> PRAIRIE_RIDE = register("prairie_ride", 1, 2);
	public static final RegistryObject<Motive> SUNFLOWERS = register("sunflowers", 3, 3);
	public static final RegistryObject<Motive> TIDES = register("tides", 3, 3);
	public static final RegistryObject<Motive> UNPACKED = register("unpacked", 4, 4);

	private static RegistryObject<Motive> register(String name, int widthBlocks, int heightBlocks) {
		return PAINTINGS.register(name, () -> new Motive(widthBlocks * 16, heightBlocks * 16));
	}
}
