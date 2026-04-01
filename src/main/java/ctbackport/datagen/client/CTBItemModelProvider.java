package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBItemModelProvider extends ItemModelProvider {

	public CTBItemModelProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

	@Override
	protected void registerModels() {
		singleTexture(CTBItems.WIND_CHARGE.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/wind_charge"));
	}


}
