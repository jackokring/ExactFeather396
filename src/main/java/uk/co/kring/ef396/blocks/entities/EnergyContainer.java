package uk.co.kring.ef396.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.blocks.EnergyBlock;
import uk.co.kring.ef396.utilities.RegistryBlockGroup;

public class EnergyContainer extends AbstractContainerMenu {

    private BlockEntity blockEntity;
    private Player playerEntity;
    private IItemHandler playerInventory;
    private RegistryBlockGroup rbg;

    public EnergyContainer(RegistryBlockGroup rbg, int windowId, Inventory playerInventory, BlockPos pos) {
        super(rbg.getContainer().get(), windowId);
        this.rbg = rbg;
        init(playerInventory, pos);// behavioural overriding
    }

    // ==================== INITIALIZATION INTERFACE ============================

    public final RegistryObject<EnergyBlock> getBlockSingleton() {
        return rbg.get();
    }

    public void init(Inventory playerInventory, BlockPos pos) {
        this.playerEntity = playerInventory.player;
        blockEntity = playerEntity.getCommandSenderWorld().getBlockEntity(pos);
        this.playerInventory = new InvWrapper(playerInventory);
        layoutPlayerInventorySlots();// 0-8, 9-36
        // funny initialization order bug on multiplayer object destroy before GUI construction?
        if (blockEntity != null) {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, 0, 64, 24));// idx 37
            });
        }
        trackPower();
    }

    // Setup syncing of power from server to client so that the GUI can show the amount of power in the block
    private void trackPower() {
        // Unfortunately on a dedicated server ints are actually truncated to short so we need
        // to split our integer here (split our 32-bit integer into two 16-bit integers)
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    ((CustomEnergyStorage)h).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((CustomEnergyStorage)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    public final int getEnergy() {
        return blockEntity.getCapability(CapabilityEnergy.ENERGY)
                .map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    // ============================ INVENTORY INTERFACE =====================

    public boolean quickCrafty[] = { true };// true consumes?

    @Override
    public final ItemStack quickMoveStack(Player playerIn, int index) {
        final int inv = 0;
        final int big = 9;
        final int nrg = big + 3 * 9;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index >= nrg) {
                // player inventory from power slot ++ end notation
                if (!this.moveItemStackTo(stack, 0, nrg, true)) {
                    return ItemStack.EMPTY;
                }
                if(quickCrafty[index - nrg]) slot.onQuickCraft(stack, itemstack);
            } else {
                if (quickCrafty[index - nrg] &&
                        ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0) {//burnable
                    // into the power slot from any other
                    if (!this.moveItemStackTo(stack, nrg,
                            nrg + quickCrafty.length, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // move from inventory to inventory
                    if (!this.moveItemStackTo(stack, 0, nrg, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return itemstack;
    }

    public final int addSlotRange(IItemHandler handler, int index, int x, int y, int amount) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += 18;
            index++;
        }
        return index;
    }

    public final int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int verAmount) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount);
            y += 18;
        }
        return index;
    }

    public final void layoutPlayerInventorySlots() {//10, 70
        // Hot-bar
        // so hot bar happens last inside container
        addSlotRange(playerInventory, 0, 10, 70 + 58, 9);//58 pix down 18*3 + ??
        // Player inventory
        // so places from player inventory indexed into slots
        addSlotBox(playerInventory, 9, 10, 70, 9, 3);
    }

    // ================ DATA VALID CHECK =======================

    @Override
    public final boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(),
                        blockEntity.getBlockPos()), playerEntity,
                getBlockSingleton().get());
    }
}
