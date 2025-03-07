package xyz.domza.mystic_gemstones;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.domza.mystic_gemstones.item.MysticGemstonesItem;
import xyz.domza.mystic_gemstones.item.MysticGemstonesItemGroup;
import xyz.domza.mystic_gemstones.block.MysticGemstonesBlocks;
import xyz.domza.mystic_gemstones.recipe.MysticGemstonesRecipe;
import xyz.domza.mystic_gemstones.world.gen.MysticGemstonesWorldGeneration;

public class MysticGemstones implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Constant.MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Mystic Gemstones loading...");
		MysticGemstonesItemGroup.registerItemGroups();
		MysticGemstonesItem.registerItems();
		MysticGemstonesRecipe.registerRecipes();
		MysticGemstonesBlocks.registerBlocks();
		MysticGemstonesWorldGeneration.generateModWorldGen();
	}
}