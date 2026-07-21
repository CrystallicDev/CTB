package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Nautilus;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class NautilusGameTests {

	@GameTest(template = "empty", timeoutTicks = 200)
	public static void nautilusGivesBreathToRider(GameTestHelper helper) {
		Nautilus nautilus = helper.spawn(CTBEntities.NAUTILUS.get(), new BlockPos(3, 2, 3));
		nautilus.setTame(true);
		nautilus.equipSaddle(SoundSource.NEUTRAL);

		Player player = helper.makeMockPlayer();
		player.setPos(nautilus.getX(), nautilus.getY(), nautilus.getZ());
		if (!player.startRiding(nautilus, true)) {
			throw new GameTestAssertException("player could not mount the nautilus");
		}

		helper.succeedWhen(() -> {
			if (!player.hasEffect(CTBEffects.BREATH_OF_THE_NAUTILUS.get())) {
				throw new GameTestAssertException("rider should have breath of the nautilus");
			}
		});
	}
}
