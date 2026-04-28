package com.natsu.backport.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.render.BreezeRenderer;
import com.natsu.backport.client.render.CreakingRenderer;
import com.natsu.backport.client.render.SulphurCubeRenderer;
import com.natsu.backport.client.render.WindChargeRenderer;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.SulphurCube;
import com.natsu.backport.common.particles.CherryParticle;
import com.natsu.backport.common.particles.GustEmitterParticle;
import com.natsu.backport.common.particles.GustParticle;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
	}
	
	@SubscribeEvent
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST.get(), GustParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_LARGE.get(), GustEmitterParticle.LargeProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.GUST_EMITTER_SMALL.get(), GustEmitterParticle.SmallProvider::new);
		Minecraft.getInstance().particleEngine.register(CTBParticles.CHERRY.get(), CherryParticle.Provider::new);
	}
	
	@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
        	CTBBlocks.BAMBOO_WOOD.setRenderTypes();
        	CTBBlocks.CHERRY_WOOD.setRenderTypes();
        	
        });
    }
	
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
	    event.put(CTBEntities.CREAKING.get(), Creaking.createAttributes().build());
	    event.put(CTBEntities.BREEZE.get(), Breeze.createAttributes().build());
	    event.put(CTBEntities.SULPHUR_CUBE.get(), SulphurCube.createAttributes().build());
	}
	
}
