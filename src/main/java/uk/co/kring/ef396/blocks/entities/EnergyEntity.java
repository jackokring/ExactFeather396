package uk.co.kring.ef396.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import uk.co.kring.ef396.blocks.EnergyBlock;
import uk.co.kring.ef396.utilities.RegistryBlockGroup;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class EnergyEntity extends BlockEntity {

    // Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
    private final CraftyStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    private int counter;

    private RegistryBlockGroup rbg;

    public EnergyEntity(RegistryBlockGroup rbg, BlockPos pos, BlockState state) {
        super(rbg.getEntity().get(), pos, state);
    }

    // ===================== TICK SERVICE =======================

    public void tickServer() {
        if (counter > 0) {
            energyStorage.addEnergy(rbg.get().get().chargeRate());
            counter--;
            setChanged();
        }
        if (counter <= 0) {
            ItemStack stack = itemHandler.getStackInSlot(0);
            int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
            if (burnTime > 0) {
                itemHandler.extractItem(0, 1, false);
                counter = burnTime;
                setChanged();
            }
        }
        BlockState blockState = level.getBlockState(worldPosition);
        Block b = blockState.getBlock();
        if(b instanceof EnergyBlock eb) {
            int red = eb.scaleRedstone(counter);
            if (blockState.getValue(BlockStateProperties.POWER) != red) {
                level.setBlock(worldPosition,
                        blockState.setValue(BlockStateProperties.POWER, red),
                        Block.UPDATE_ALL);
            }
        }
        sendOutPower();
    }

    public final void kryptonite(int range, Predicate<Entity> kill) {
        BlockPos topCorner = this.worldPosition.offset(range, range, range);
        BlockPos bottomCorner = this.worldPosition.offset(-range, -range, -range);
        AABB box = new AABB(topCorner, bottomCorner);

        List<Entity> entities = this.level.getEntities(null, box);
        for (Entity target : entities){
            if (!(target instanceof Player) && kill.test(target)){
                target.hurt(DamageSource.GENERIC, 2);
            }
        }
    }

    // ==================== ENERGY EXPORT ==========================

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {//directional out
                BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
                if (be != null) {
                    boolean doContinue = be.getCapability(CapabilityEnergy.ENERGY,
                            direction.getOpposite()).map(handler -> {// get relative direction wall
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(),
                                            rbg.get().get().dischargeRate()), false);
                                    capacity.addAndGet(-received);
                                    energyStorage.consumeEnergy(received);
                                    setChanged();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }
                    ).orElse(true);
                    if (!doContinue) {// fast empty baulk
                        return;
                    }
                }
            }
        }
    }

    // ==================== TAGS ====================

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }
        if (tag.contains("Info")) {
            counter = tag.getCompound("Info").getInt("Counter");
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.put("Energy", energyStorage.serializeNBT());

        CompoundTag infoTag = new CompoundTag();
        infoTag.putInt("Counter", counter);
        tag.put("Info", infoTag);
    }

    // ================ CAPS HANDLERS ===================

    private CraftyStackHandler createHandler() {
        return new CraftyStackHandler(1) {// SLOT 0

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NonNull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            }

            @NonNull
            @Override
            public ItemStack insertItem(int slot, @NonNull ItemStack stack, boolean simulate) {
                if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) <= 0) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(rbg.get().get().charge(), 0) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    // ================ CAPS MAKE DESTROY ===============

    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) return handler.cast();
            if(side.compareTo(Direction.UP) == 0) {//TOP LOADER
                // TODO
            }
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
        energy.invalidate();
    }
}
