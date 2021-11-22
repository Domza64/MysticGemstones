package mysticgemstones.mysticgemstones.item;

import mysticgemstones.mysticgemstones.block.MysticGemstonesBlock;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StarstoneOreBlockItem extends BlockItem {
    public StarstoneOreBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    private boolean canShine;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.getTimeOfDay() > 22350 || world.getTimeOfDay() < 21980) {
            System.out.println(1);
            FabricModelPredicateProviderRegistry.register(MysticGemstonesBlock.STARSTONE_ORE.asItem(),
                    new Identifier("shining"), (itemStack, clientWorld, livingEntity, hmmm) -> 0.0F);
            canShine = false;
        }
        else {
            System.out.println(0);
            FabricModelPredicateProviderRegistry.register(MysticGemstonesBlock.STARSTONE_ORE.asItem(),
                    new Identifier("shining"), (itemStack, clientWorld, livingEntity, hmmm) -> 1.0F);
            canShine = true;
        }
    }

    public boolean hasGlint(ItemStack stack) {
        return canShine;
    }
}
