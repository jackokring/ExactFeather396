package uk.co.kring.ef396.utilities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import uk.co.kring.ef396.ExactFeather;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class KeyBinding {

    public static final String KEYS_FOR_MOD = "key.categories." + ExactFeather.MOD_ID;

    private static List<KeyBinding> list = new LinkedList<>();
    private Supplier<Packet> supplier;
    private KeyMapping km;

    public KeyBinding(String name, String key, Supplier<Packet> supplier) {
        km = new KeyMapping("key." + name,
                KeyConflictContext.IN_GAME,
                InputConstants.getKey("key.keyboard." + key),//E.G "period"
                KEYS_FOR_MOD);
        ClientRegistry.registerKeyBinding(km);
        list.add(this);
        this.supplier = supplier;
    }

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        list.forEach((keyBinding) -> {
            if (keyBinding.km.consumeClick()) {
                Messages.sendToServer(keyBinding.supplier.get());
            }
        });
    }
}
