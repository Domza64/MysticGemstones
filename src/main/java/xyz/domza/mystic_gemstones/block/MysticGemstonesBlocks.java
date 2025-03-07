package xyz.domza.mystic_gemstones.block;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import xyz.domza.mystic_gemstones.Constant;

import java.util.HashMap;
import java.util.Map;

public class MysticGemstonesBlocks {
    public static final Map<Identifier, Block> BLOCKS = new HashMap<>();

    public static final Block AQUAMARINE_ORE =
            register(Constant.AQUAMARINE_ORE, new ExperienceDroppingBlock(
                    UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.STONE_GRAY)
                            .instrument(Instrument.BASEDRUM)
                            .requiresTool()
                            .strength(3.0f, 3.0f)));

    public static final Block GEM_GRINDSTONE = register(Constant.GEM_GRINDSTONE, new GemGrindstone(AbstractBlock.Settings.create()));
    //public static final Block GEM_COMPRESSOR = register(Constant.GEM_COMPRESSOR, new Block(AbstractBlock.Settings.create()));

    private static <T extends Block> T register(String id, T block) {
        BLOCKS.put(new Identifier(Constant.MOD_ID, id), block);
        return block;
    }

    public static void registerBlocks() {
        BLOCKS.forEach((identifier, block) -> {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());

            Registry.register(Registries.BLOCK, identifier, block);
            Registry.register(Registries.ITEM, identifier, blockItem);
        });
     }
}
