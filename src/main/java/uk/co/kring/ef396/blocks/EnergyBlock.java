package uk.co.kring.ef396.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.Loaded;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;
import uk.co.kring.ef396.utilities.RegistryBlockGroup;

import java.util.List;

public class EnergyBlock extends Block implements EntityBlock /* , ModelledBlock */ {// for variants

    private static RegistryBlockGroup rbg;

    static {
        // alter for accessed extending classes
        // just place another static section in the overriding class
        setRegister(Loaded.energy);
    }

    protected static final void setRegister(RegistryBlockGroup blockGroup) {
        rbg = blockGroup;
    }

    // ========================== CUSTOMIZATION ===========================

    public EnergyBlock() {
        super(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2.0f)
                .lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockStateProperties.POWERED, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader,
                                List<Component> list, TooltipFlag flags) {
        list.add(new TranslatableComponent(nameScreen()).withStyle(ChatFormatting.BLUE));
    }

    // =================== FIXED FINALS ======================

    @Nullable
    @Override
    public final BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return rbg.getEntity(blockPos, blockState);
    }

    public final AbstractContainerMenu newContainer(int windowId, Inventory playerInventory, BlockPos pos) {
        return rbg.getFunky(windowId, playerInventory, pos);
    }

    public final String nameScreen() {
        // in lang file as "block.ef396.screen.energy" for example
        return "block." + ExactFeather.MOD_ID
                + ".screen." + this.getRegistryName().getPath();
    }

    @Nullable
    @Override
    public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                        BlockState state,
                                                                        BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof EnergyEntity tile) {
                tile.tickServer();
            }
        };
    }

    @Override
    public final @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EnergyEntity && rbg.getContainer() != null) {// has GUI?
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        // a name
                        return new TranslatableComponent(nameScreen());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory,
                                                            Player playerEntity) {
                        return newContainer(windowId, playerInventory, pos);
                    }
                };
                NetworkHooks.openGui((ServerPlayer) player, containerProvider, be.getBlockPos());
            } else if(rbg.getContainer() != null) {
                throw new IllegalStateException(be.getClass().getCanonicalName()
                        + " entity container provider is missing in "
                + this.getClass().getCanonicalName());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
