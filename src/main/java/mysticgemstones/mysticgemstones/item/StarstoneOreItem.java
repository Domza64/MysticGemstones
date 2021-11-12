package mysticgemstones.mysticgemstones.item;

import mysticgemstones.mysticgemstones.block.MysticGemstonesBlock;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;


public class StarstoneOreItem extends Item {


    @Deprecated
    private final Block block;

    public StarstoneOreItem(Settings settings) {
        super(settings);
        this.block = MysticGemstonesBlock.STARSTONE_ORE;
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (world.getTimeOfDay() > 22350 || world.getTimeOfDay() < 21980) {
            FabricModelPredicateProviderRegistry.register(MysticGemstonesItem.STARSTONE_ORE_ITEM,
                    new Identifier("shining"), (itemStack, clientWorld, livingEntity, hmmm) -> 0.0F);
        }
        else {
            FabricModelPredicateProviderRegistry.register(MysticGemstonesItem.STARSTONE_ORE_ITEM,
                    new Identifier("shining"), (itemStack, clientWorld, livingEntity, hmmm) -> 1.0F);
        }
    }



    /* Stuff from BlockItem class... Makes it so that you can place this item like block and it places Raw Starstone Ore
       Probably worst possible way to do this but I don't know how to make class for BlockItem, only for Item and I need
       inventoryTick method from Item class. */

    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (!actionResult.isAccepted() && this.isFood()) {
            ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2;
        } else {
            return actionResult;
        }
    }

    public ActionResult place(ItemPlacementContext context) {
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        } else {
            ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
            if (itemPlacementContext == null) {
                return ActionResult.FAIL;
            } else {
                BlockState blockState = this.getPlacementState(itemPlacementContext);
                if (blockState == null) {
                    return ActionResult.FAIL;
                } else if (!this.place(itemPlacementContext, blockState)) {
                    return ActionResult.FAIL;
                } else {
                    BlockPos blockPos = itemPlacementContext.getBlockPos();
                    World world = itemPlacementContext.getWorld();
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    ItemStack itemStack = itemPlacementContext.getStack();
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.isOf(blockState.getBlock())) {
                        blockState2 = this.placeFromTag(blockPos, world, itemStack, blockState2);
                        this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
                        blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
                        if (playerEntity instanceof ServerPlayerEntity) {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                        }
                    }

                    BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                    world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
                    world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
                    if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    return ActionResult.success(world.isClient);
                }
            }
        }
    }

    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundGroup().getPlaceSound();
    }

    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        return context;
    }

    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return writeTagToBlockEntity(world, player, pos, stack);
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState = this.getBlock().getPlacementState(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    private BlockState placeFromTag(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
            StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
            Iterator var9 = nbtCompound2.getKeys().iterator();

            while(var9.hasNext()) {
                String string = (String)var9.next();
                Property<?> property = stateManager.getProperty(string);
                if (property != null) {
                    String string2 = nbtCompound2.get(string).asString();
                    blockState = with(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return (BlockState)property.parse(name).map((value) -> {
            return (BlockState)state.with(property, value);
        }).orElse(state);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
    }

    public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        } else {
            NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
            if (nbtCompound != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                        return false;
                    }

                    NbtCompound nbtCompound2 = blockEntity.writeNbt(new NbtCompound());
                    NbtCompound nbtCompound3 = nbtCompound2.copy();
                    nbtCompound2.copyFrom(nbtCompound);
                    nbtCompound2.putInt("x", pos.getX());
                    nbtCompound2.putInt("y", pos.getY());
                    nbtCompound2.putInt("z", pos.getZ());
                    if (!nbtCompound2.equals(nbtCompound3)) {
                        blockEntity.readNbt(nbtCompound2);
                        blockEntity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public String getTranslationKey() {
        return this.getBlock().getTranslationKey();
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        this.getBlock().appendTooltip(stack, world, tooltip, context);
    }

    public Block getBlock() {
        return this.block;
    }
}
