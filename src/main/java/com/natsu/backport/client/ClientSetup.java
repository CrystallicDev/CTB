package com.natsu.backport.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.render.BoggedRenderer;
import com.natsu.backport.client.render.BreezeRenderer;
import com.natsu.backport.client.render.CreakingRenderer;
import com.natsu.backport.client.render.DecoratedPotRenderer;
import com.natsu.backport.client.render.NautilusRenderer;
import com.natsu.backport.client.render.OminousItemSpawnerRenderer;
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
		//event.registerEntityRenderer(CTBEntities.SULPHUR_CUBE.get(), SulphurCubeRenderer::new);
		event.registerEntityRenderer(CTBEntities.BREEZE.get(), BreezeRenderer::new);
		event.registerEntityRenderer(CTBEntities.BOGGED.get(), BoggedRenderer::new);
		event.registerEntityRenderer(CTBEntities.NAUTILUS.get(), NautilusRenderer::new);
		event.registerEntityRenderer(CTBEntities.ZOMBIE_NAUTILUS.get(), NautilusRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.VAULT.get(), VaultRenderer::new);
		event.registerEntityRenderer(CTBEntities.OMINOUS_ITEM_SPAWNER.get(), OminousItemSpawnerRenderer::new);
		event.registerBlockEntityRenderer(CTBBlockEntities.DECORATED_POT.get(), DecoratedPotRenderer::new);
	}

	@SubscribeEvent
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST.get(), GustParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_LARGE.get(), GustEmitterParticle.LargeProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_SMALL.get(), GustEmitterParticle.SmallProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.CHERRY.get(), CherryParticle.Provider::new);
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
	}

	@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
        	CTBBlocks.BAMBOO_WOOD.setRenderTypes();
        	CTBBlocks.CHERRY_WOOD.setRenderTypes();
        	CTBBlocks.PALE_OAK_WOOD.setRenderTypes();
        	CTBBlocks.PALE_OAK_LEAVES.setRenderTypes();
        	// transparent textures render as solid gray squares without cutout
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.RESIN.clump.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.OPEN_EYEBLOSSOM.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.CLOSED_EYEBLOSSOM.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.PALE_HANGING_MOSS.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.PALE_OAK_SAPLING.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.TRIAL_SPAWNER.get(), RenderType.cutout());
        	ItemBlockRenderTypes.setRenderLayer(CTBBlocks.VAULT.get(), RenderType.cutout());
        });
    }

}
