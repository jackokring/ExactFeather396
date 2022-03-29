package uk.co.kring.ef396.manas;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.NotNull;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.capabilities.ManaProvider;
import uk.co.kring.ef396.manas.packets.PacketSyncManaToClient;
import uk.co.kring.ef396.utilities.Messages;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ManaManager extends SavedData {

    // For every chunk that we visited already we store the mana currently available.
    // Note that this is done in a lazy way.
    // Chunks that we didn't visit will not have mana yet
    private final Map<ChunkPos, Mana> manaMap = new HashMap<>();
    private final Random random = new Random();

    // Keep a counter so that we don't send mana back to the client every tick
    private int counter = 0;

    // This function can be used to get access to the mana manager for a given level.
    // It can only be called server-side!
    @Nonnull
    public static ManaManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        // Get the mana manager if it already exists. Otherwise, create a new one. Note that both
        // invocations of ManaManager::new actually refer to a different constructor. One without parameters
        // and the other with a CompoundTag parameter
        return storage.computeIfAbsent(ManaManager::new, ManaManager::new, "manamanager");
    }

    @NotNull
    private Mana getManaInternal(BlockPos pos) {
        // Get the mana at a certain chunk.
        // If this is the first time then we fill in the manaMap using computeIfAbsent
        ChunkPos chunkPos = new ChunkPos(pos);
        return manaMap.computeIfAbsent(chunkPos,
                cp -> new Mana(random.nextInt(ManaConfig.CHUNK_MAX_MANA.get())
                        + ManaConfig.CHUNK_MIN_MANA.get()));
    }

    public int getMana(BlockPos pos) {
        Mana mana = getManaInternal(pos);
        return mana.getMana();
    }

    public int extractMana(BlockPos pos) {
        Mana mana = getManaInternal(pos);
        int present = mana.getMana();
        if (present > 0) {
            mana.setMana(present-1);
            // Do not forget to call setDirty() whenever making changes that need to be persisted!
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    // This tick is called from a tick event (see later)
    public void tick(Level level) {
        counter--;
        // Every 10 ticks this code will synchronize the mana of each player and the mana of the current
        // chunk of that player to the client so that it can be displayed on screen
        if (counter <= 0) {
            counter = 10;
            // Synchronize the mana to the players in this world
            // todo expansion: keep the previous data that was sent to the player and only send if changed
            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    int playerMana = serverPlayer.getCapability(ManaProvider.PLAYER_MANA)
                            .map(Mana::getMana)
                            .orElse(-1);
                    int chunkMana = getMana(serverPlayer.blockPosition());
                    Messages.sendToPlayer(new PacketSyncManaToClient(playerMana, chunkMana), serverPlayer);
                }
            });

            // todo expansion: here it would be possible to slowly regenerate mana in chunks
        }
    }

    // This constructor is called for a new mana manager
    public ManaManager() {
    }

    // This constructor is called when loading the mana manager from disk
    public ManaManager(CompoundTag tag) {
        ListTag list = tag.getList("mana", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag manaTag = (CompoundTag) t;
            Mana mana = new Mana(manaTag.getInt("mana"));
            ChunkPos pos = new ChunkPos(manaTag.getInt("x"), manaTag.getInt("z"));
            manaMap.put(pos, mana);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        manaMap.forEach((chunkPos, mana) -> {
            CompoundTag manaTag = new CompoundTag();
            manaTag.putInt("x", chunkPos.x);
            manaTag.putInt("z", chunkPos.z);
            manaTag.putInt("mana", mana.getMana());
            list.add(manaTag);
        });
        tag.put("mana", list);
        return tag;
    }

    // ============================= EVENT INTERFACE =======================
    public static void init(IEventBus bus) {
        bus.addGenericListener(Entity.class, ManaManager::onAttachCapabilitiesPlayer);
        bus.addListener(ManaManager::onPlayerCloned);
        bus.addListener(ManaManager::onRegisterCapabilities);
        bus.addListener(ManaManager::onWorldTick);
    }


    // Whenever a new object of some type is created the AttachCapabilitiesEvent will fire.
    // In our case we want to know
    // when a new player arrives so that we can attach our capability here
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(ManaProvider.PLAYER_MANA).isPresent()) {
                // The player does not already have this capability.
                // So we need to add the capability provider here
                event.addCapability(new ResourceLocation(ExactFeather.MOD_ID,
                        "playermana"), new ManaProvider());
            }
        }
    }

    // When a player dies or teleports from the end capabilities are cleared. Using the PlayerEvent.
    // Clone event, we can detect this and copy our capability from the old player to the new one
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            event.getOriginal().getCapability(ManaProvider.PLAYER_MANA).ifPresent(oldStore -> {
                event.getPlayer().getCapability(ManaProvider.PLAYER_MANA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // Finally, we need to register our capability in a RegisterCapabilitiesEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Mana.class);
    }

    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        // Don't do anything client side
        if (event.world.isClientSide) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        // Get the mana manager for this level
        ManaManager manager = get(event.world);
        manager.tick(event.world);
    }
}
