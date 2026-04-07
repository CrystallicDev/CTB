package com.natsu.backport.common.registry;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class CTBSurfaceRules {

	private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource RED_TERRACOTTA = makeStateRule(Blocks.RED_TERRACOTTA);
    private static final SurfaceRules.RuleSource BLUE_TERRACOTTA = makeStateRule(Blocks.BLUE_TERRACOTTA);

    public static SurfaceRules.RuleSource makeRules()
    {
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

        SurfaceRules.RuleSource vanillaCaves = SurfaceRules.sequence(
        	    SurfaceRules.ifTrue(
        	        SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
        	        SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())
        	    ),
        	    SurfaceRules.ifTrue(
        	        SurfaceRules.abovePreliminarySurface(),
        	        SurfaceRules.sequence(
        	            SurfaceRules.ifTrue(
        	                SurfaceRules.ON_FLOOR,
        	                SurfaceRules.ifTrue(
        	                    SurfaceRules.not(SurfaceRules.steep()),
        	                    SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState())
        	                )
        	            ),
        	            SurfaceRules.ifTrue(
        	                SurfaceRules.UNDER_FLOOR,
        	                SurfaceRules.state(Blocks.DIRT.defaultBlockState())
        	            )
        	        )
        	    )
        	);
        
        return SurfaceRules.sequence(
            //SurfaceRules.ifTrue(SurfaceRules.isBiome(CristalliteBiomes.HOT_RED), RED_TERRACOTTA),
            //SurfaceRules.ifTrue(SurfaceRules.isBiome(CristalliteBiomes.COLD_BLUE), BLUE_TERRACOTTA),
            //SurfaceRules.ifTrue(SurfaceRules.isBiome(CTBBiomes.TEST_FOREST), BLUE_TERRACOTTA),

            // Default to a grass and dirt surface
            SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, vanillaCaves)
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }
	
}
