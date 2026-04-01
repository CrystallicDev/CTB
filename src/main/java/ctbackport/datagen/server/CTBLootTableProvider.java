package ctbackport.datagen.server;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class CTBLootTableProvider extends LootTableProvider {

	public CTBLootTableProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(
            // Enregistre les block loot tables
            Pair.of(CTBBlockLoot::new, LootContextParamSets.BLOCK)
            // Tu peux ajouter d'autres types ici plus tard :
            // Pair.of(CristalliteEntityLoot::new, LootContextParamSets.ENTITY),
            // Pair.of(CristalliteChestLoot::new, LootContextParamSets.CHEST)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
        // Validation des loot tables
        map.forEach((resourceLocation, lootTable) ->
            LootTables.validate(validationContext, resourceLocation, lootTable)
        );
    }
}
