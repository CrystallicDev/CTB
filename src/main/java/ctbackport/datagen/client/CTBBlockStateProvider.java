package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBBlockStateProvider extends BlockStateProvider {

	public CTBBlockStateProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

	@Override
	protected void registerStatesAndModels() {
		// TODO Auto-generated method stub
		
	}

}