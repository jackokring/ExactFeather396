package uk.co.kring.ef396.manas.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import uk.co.kring.ef396.manas.ManaOverlay;
import uk.co.kring.ef396.utilities.Packet;

import java.util.function.Supplier;

public class PacketSyncManaToClient implements Packet {//OK a data container to client

    private final int playerMana;
    private final int chunkMana;

    public PacketSyncManaToClient(int playerMana, int chunkMana) {
        this.playerMana = playerMana;
        this.chunkMana = chunkMana;
    }

    public PacketSyncManaToClient(FriendlyByteBuf buf) {
        playerMana = buf.readInt();
        chunkMana = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerMana);
        buf.writeInt(chunkMana);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            // Be very careful not to access client-only classes here! (like Minecraft) because
            // this packet needs to be available server-side too
            ManaOverlay.set(playerMana, chunkMana);
        });
        return true;
    }
}