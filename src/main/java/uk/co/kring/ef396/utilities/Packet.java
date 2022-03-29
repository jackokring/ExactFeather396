package uk.co.kring.ef396.utilities;

import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public interface Packet {
    boolean handle(Supplier<NetworkEvent.Context> supplier);
}
