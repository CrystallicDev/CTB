package com.natsu.backport.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.render.BoggedRenderer;
import com.natsu.backport.client.render.CopperChestRenderer;
import com.natsu.backport.client.render.SulphurCubeRenderer;
import com.natsu.backport.client.render.CopperGolemRenderer;
import com.natsu.backport.client.render.CopperGolemStatueRenderer;
import com.natsu.backport.client.render.BreezeRenderer;
import com.natsu.backport.client.render.CreakingRenderer;
import com.natsu.backport.client.render.DecoratedPotRenderer;
import com.natsu.backport.client.render.NautilusRenderer;
import com.natsu.backport.client.render.OminousItemSpawnerRenderer;
import com.natsu.backport.client.render.ParchedRenderer;
import com.natsu.backport.client.render.TrialSpawnerRenderer;
import com.natsu.backport.client.render.VaultRenderer;
import com.natsu.backport.client.render.WindChargeRenderer;
import com.natsu.backport.common.particles.CherryParticle;
import com.natsu.backport.common.particles.GustEmitterParticle;
import com.natsu.backport.common.particles.GustParticle;
import com.natsu.backport.common.particles.TrailParticle;
import com.natsu.backport.common.particles.TrialSpawnerDetectionParticle;
import com.natsu.backport.common.particles.VaultConnectionParticle;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CTBackport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(CTBEntities.WIND_CHARGE_ENTITY.get(), WindChargeRenderer::new);
		event.registerEntityRenderer(CTBEntities.CREAKING.get(), CreakingRenderer::new);
		event.registerEntityRenderer(CTBEntities.SULPHUR_CUBE.get(), SulphurCubeRenderer::new);
		event.registerEntityRenderer(CTBEntities.BREEZE.get(), BreezeRenderer::new);
		event.registerEntityRenderer(CTBEntities.BOGGED.get(), BoggedRenderer::new);
		event.registerEntityRenderer(CTBEntities.PARCHED.get(), ParchedRenderer::new);
		event.registerEntityRenderer(CTBEntities.COPPER_GOLEM.get(), CopperGolemRenderer::new);
		event.registerEntityRenderer(CTBEntities.CAMEL.get(), com.natsu.backport.client.render.CamelRenderer::new);
		event.registerEntityRenderer(CTBEntities.SNIFFER.get(), com.natsu.backport.client.render.SnifferRenderer::new);
		event.registerEntityRenderer(CTBEntities.HAPPY_GHAST.get(), com.natsu.backport.client.render.HappyGhastRenderer::new);
		event.registerEntityRenderer(CTBEntities.ARMADILLO.get(), com.natsu.backport.client.render.ArmadilloRenderer::new);
		event.registerEntityRenderer(net.minecraft.world.entity.EntityType.WOLF,
				com.natsu.backport.client.render.VariantWolfRenderer::new);
		event.registerEntityRenderer(CTBEntities.CAMEL_HUSK.get(), com.natsu.backport.client.render.CamelRenderer::new);
		net.minecraft.client.gui.screens.MenuScreens.register(
				com.natsu.backport.common.registry.CTBMenus.NAUTILUS_INVENTORY.get(),
				com.natsu.backport.client.screen.NautilusInventoryScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(
				com.natsu.backport.common.registry.CTBMenus.CRAFTER.get(),
				com.natsu.backport.client.screen.CrafterScreen::new);
		event.registerEntityRenderer(CTBEntities.VARIANT_EGG.get(),
				net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
		event.registerEntityRenderer(net.minecraft.world.entity.EntityType.PIG,
				com.natsu.backport.client.render.VariantPigRenderer::new);
		event.registerEntityRenderer(net.minecraft.world.entity.EntityType.COW,
				com.natsu.backport.client.render.VariantCowRenderer::new);
		event.registerEntityRenderer(net.minecraft.world.entity.EntityType.CHICKEN,
				com.natsu.backport.client.render.VariantChickenRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.COPPER_CHEST.get(), CopperChestRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.SHELF.get(), com.natsu.backport.client.render.ShelfRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.COPPER_GOLEM_STATUE.get(), CopperGolemStatueRenderer::new);
		event.registerEntityRenderer(CTBEntities.NAUTILUS.get(), NautilusRenderer::new);
		event.registerEntityRenderer(CTBEntities.ZOMBIE_NAUTILUS.get(), NautilusRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.VAULT.get(), VaultRenderer::new);
		event.registerEntityRenderer(CTBEntities.OMINOUS_ITEM_SPAWNER.get(), OminousItemSpawnerRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.DECORATED_POT.get(), DecoratedPotRenderer::new);
	}

	@net.minecraftforge.eventbus.api.SubscribeEvent
	public static void registerBlockColors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
				// the 1.21.5 dry foliage tint, flattened to its colormap average
		event.getBlockColors().register((state, level, pos, tintIndex) -> 0x96693E,
				CTBBlocks.LEAF_LITTER.get());
		event.getBlockColors().register((state, level, pos, tintIndex) ->
				level != null && pos != null
						? net.minecraft.client.renderer.BiomeColors.getAverageGrassColor(level, pos)
						: net.minecraft.world.level.GrassColor.get(0.5, 1.0),
				CTBBlocks.BUSH.get());
	}

	@net.minecraftforge.eventbus.api.SubscribeEvent
	public static void registerItemColors(net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
				event.getItemColors().register((stack, tintIndex) -> 0x96693E,
				CTBBlocks.LEAF_LITTER.get().asItem());
		event.getItemColors().register((stack, tintIndex) -> net.minecraft.world.level.GrassColor.get(0.5, 1.0),
				CTBBlocks.BUSH.get().asItem());
	}

	@SubscribeEvent
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST.get(), GustParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_LARGE.get(), GustEmitterParticle.LargeProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_SMALL.get(), GustEmitterParticle.SmallProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.CHERRY.get(), CherryParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.COPPER_FIRE_FLAME.get(), net.minecraft.client.particle.FlameParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.FIREFLY.get(), com.natsu.backport.client.render.FireflyParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.SULFUR_CUBE_GOO.get(), com.natsu.backport.client.render.GooParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GEYSER.get(), com.natsu.backport.client.render.GeyserParticles.Plume.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GEYSER_BASE.get(), com.natsu.backport.client.render.GeyserParticles.Base.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.SULFUR_BUBBLES.get(), com.natsu.backport.client.render.GeyserParticles.SulfurBubble.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.NOXIOUS_GAS.get(), com.natsu.backport.client.render.GeyserParticles.NoxiousGas.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.PALE_OAK_LEAVES.get(), CherryParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.TRAIL.get(), TrailParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.TRIAL_SPAWNER_DETECTION.get(), TrialSpawnerDetectionParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.TRIAL_SPAWNER_DETECTION_OMINOUS.get(), TrialSpawnerDetectionParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.VAULT_CONNECTION.get(), VaultConnectionParticle.Provider::new);
		// same fly-along-vector behavior as the vault connection
		Minecraft.getInstance().particleEngine.register(CTBParticles.OMINOUS_SPAWNING.get(), VaultConnectionParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.TRIAL_OMEN.get(), TrialSpawnerDetectionParticle.Provider::new);
	}

	// the spear only slows its wielder to 38% instead of the usual 20% while couched
	@Mod.EventBusSubscriber(modid = CTBackport.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class SpearMovement {
		@SubscribeEvent
		public static void onMovementInput(net.minecraftforge.client.event.MovementInputUpdateEvent event) {
			net.minecraft.world.entity.player.Player player = event.getPlayer();
			if (player.isUsingItem() && !player.isPassenger()
					&& player.getUseItem().getItem() instanceof com.natsu.backport.common.item.SpearItem) {
				event.getInput().forwardImpulse *= 1.9F;
				event.getInput().leftImpulse *= 1.9F;
			}
		}

		// couched lance pose, the 1.18.2 use animations have nothing close to the 1.21 one
		@SubscribeEvent
		public static void onRenderHand(net.minecraftforge.client.event.RenderHandEvent event) {
			net.minecraft.client.player.LocalPlayer player = Minecraft.getInstance().player;
			if (player == null || !player.isUsingItem()
					|| event.getHand() != player.getUsedItemHand()
					|| !(event.getItemStack().getItem() instanceof com.natsu.backport.common.item.SpearItem)) {
				return;
			}

			boolean rightSide = (event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
					? player.getMainArm() : player.getMainArm().getOpposite()) == net.minecraft.world.entity.HumanoidArm.RIGHT;
			float side = rightSide ? 1.0F : -1.0F;
			com.mojang.blaze3d.vertex.PoseStack pose = event.getPoseStack();
			// tucked against the hip, tip levelled at the crosshair
			pose.translate(side * -0.22, -0.12, -0.16);
			pose.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-32.0F));
			pose.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(side * 8.0F));
		}
	}

	@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
        	// the happy ghast platform collision needs to see the local player
        	com.natsu.backport.common.entity.HappyGhast.CLIENT_PLAYER =
        			() -> net.minecraft.client.Minecraft.getInstance().player;
        	CTBBlocks.BAMBOO_WOOD.setRenderTypes();
        	CTBBlocks.CHERRY_WOOD.setRenderTypes();
        	CTBBlocks.PALE_OAK_WOOD.setRenderTypes();
        	CTBBlocks.PALE_OAK_LEAVES.setRenderTypes();
        	// transparent textures render as solid gray squares without cutout
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.RESIN.clump.get(), RenderType.cutout());
        	for (com.natsu.backport.utils.sets.WeatherableCopperSet<?, ?> set : java.util.List.of(
        			CTBBlocks.COPPER_BARS, CTBBlocks.COPPER_CHAIN, CTBBlocks.COPPER_LANTERN)) {
        		for (net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.Block> b : java.util.List.of(
        				set.block, set.exposedBlock, set.weatheredBlock, set.oxidizedBlock,
        				set.blockWaxed, set.exposedBlockWaxed, set.weatheredBlockWaxed, set.oxidizedBlockWaxed)) {
        			ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutout());
        		}
        	}
        	for (net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.Block> b : java.util.List.of(
        			CTBBlocks.PINK_PETALS, CTBBlocks.BUSH, CTBBlocks.FIREFLY_BUSH, CTBBlocks.CACTUS_FLOWER, CTBBlocks.SHORT_DRY_GRASS,
        			CTBBlocks.TALL_DRY_GRASS, CTBBlocks.WILDFLOWERS, CTBBlocks.LEAF_LITTER,
        			CTBBlocks.POTTED_PALE_OAK_SAPLING, CTBBlocks.POTTED_OPEN_EYEBLOSSOM, CTBBlocks.POTTED_CLOSED_EYEBLOSSOM)) {
        		ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutout());
        	}
        	for (net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.Block> b : java.util.List.of(
        			CTBBlocks.TORCHFLOWER, CTBBlocks.POTTED_TORCHFLOWER, CTBBlocks.TORCHFLOWER_CROP,
        			CTBBlocks.PITCHER_CROP, CTBBlocks.PITCHER_PLANT, CTBBlocks.SNIFFER_EGG, CTBBlocks.DRIED_GHAST)) {
        		ItemBlockRenderTypes.setRenderLayer(b.get(), RenderType.cutout());
        	}
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.GOLDEN_DANDELION.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.POTTED_GOLDEN_DANDELION.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.PALE_MOSS.mossLayer.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.SULFUR_SPIKE.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.COPPER_TORCH.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.COPPER_WALL_TORCH.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.OPEN_EYEBLOSSOM.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.CLOSED_EYEBLOSSOM.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.PALE_HANGING_MOSS.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.PALE_OAK_SAPLING.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.TRIAL_SPAWNER.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.VAULT.get(), RenderType.cutout());
        });
    }

}
