package uk.co.kring.ef396;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import uk.co.kring.ef396.manas.PacketGatherMana;
import uk.co.kring.ef396.utilities.Messages;

public class KeyBindings {

    public static final String KEYS_FOR_MOD = "key.categories." + ExactFeather.MOD_ID;
    public static final String KEY_GATHER_MANA = "key.gatherMana";

    public static KeyMapping gatherManaKeyMapping;

    public static void init() {
        // Use KeyConflictContext.IN_GAME to indicate this key is meant for usage in-game
        ClientRegistry.registerKeyBinding(
                new KeyMapping(KEY_GATHER_MANA,
                KeyConflictContext.IN_GAME,
                InputConstants.getKey("key.keyboard.period"),
                KEYS_FOR_MOD));
    }

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.gatherManaKeyMapping.consumeClick()) {
            Messages.sendToServer(new PacketGatherMana());
        }
    }
}
