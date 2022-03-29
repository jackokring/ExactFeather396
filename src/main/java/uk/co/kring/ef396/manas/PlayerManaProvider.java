package uk.co.kring.ef396.manas;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class PlayerManaProvider implements ICapabilityProvider,
        INBTSerializable<CompoundTag> {//OK it's the capability provider

    public static Capability<Mana> PLAYER_MANA
            = CapabilityManager.get(new CapabilityToken<>(){});

    private Mana playerMana = null;
    private final LazyOptional<Mana> opt = LazyOptional.of(this::createPlayerMana);

    @Nonnull
    private Mana createPlayerMana() {
        if (playerMana == null) {
            playerMana = new Mana(0);// start with none
        }
        return playerMana;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == PLAYER_MANA) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerMana().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerMana().loadNBTData(nbt);
    }
}
