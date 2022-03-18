package uk.co.kring.ef396.utilities;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

import java.util.function.Consumer;

public class Side {

    public static boolean server(Consumer<ServerLevel> code, LevelAccessor access) {
        if(access instanceof ServerLevel level) {
            code.accept(level);
            return true;
        }
        return false;
    }

    public static boolean client(Consumer<ClientLevel> code, LevelAccessor access) {
        if(access instanceof ClientLevel level) {
            code.accept(level);
            return true;
        }
        return false;
    }
}
