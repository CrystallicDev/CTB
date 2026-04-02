package ctbackport.datagen;

import com.natsu.backport.CTBackport;

import ctbackport.datagen.client.CTBBlockStateProvider;
import ctbackport.datagen.client.CTBItemModelProvider;
import ctbackport.datagen.lang.CTBLangEN;
import ctbackport.datagen.lang.CTBLangFR;
import ctbackport.datagen.server.CTBBlockTags;
import ctbackport.datagen.server.CTBLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = CTBackport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CTBDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeClient()) {
            gen.addProvider(new CTBBlockStateProvider(gen, helper));
            gen.addProvider(new CTBItemModelProvider(gen, helper));
            gen.addProvider(new CTBLangEN(gen));
            gen.addProvider(new CTBLangFR(gen));
        }

        if (event.includeServer()) {
            gen.addProvider(new CTBLootTableProvider(gen));
            gen.addProvider(new CTBBlockTags(gen, helper));
        }
    }
}
