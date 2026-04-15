package ctbackport.datagen;

import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;

public interface DataGenBlockItemHandler {

	public abstract void handleWoodSet(WoodSet set);
	public abstract void handleLeavesSet(LeavesSet set);
	public abstract void handleStoneDecorationSet(StoneDecorationSet set);
	public abstract void handleDirtDecorationSet(DirtDecorationSet set);
	public abstract void handleMossSet(MossSet set);
	public abstract void handleResinSet(ResinSet set);
	public abstract void handleCopperSet(WeatherableCopperSet<?, ?> set);
	public abstract void handleCopperDoorSet(WeatherableCopperSet<?, ?> set);
	public abstract void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set);
	public abstract void handleCopperBulbSet(WeatherableCopperSet<?, ?> set);

}
