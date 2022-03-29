package uk.co.kring.ef396.manas;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import uk.co.kring.ef396.utilities.Packet;

import java.util.function.Supplier;

public class PacketGatherMana implements Packet {//OK basic packet and handler class
    public static final String MESSAGE_NO_MANA = "message.nomana";

    public PacketGatherMana() {
    }

    public PacketGatherMana(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are server side
            ServerPlayer player = ctx.getSender();
            // First try to extract mana from the current chunk
            int extracted = ManaManager.get(player.level).extractMana(player.blockPosition());
            if (extracted <= 0) {
                player.sendMessage(new TranslatableComponent(MESSAGE_NO_MANA)
                        .withStyle(ChatFormatting.RED), Util.NIL_UUID);
            } else {
                // Get the capability from the player and use it to add mana
                player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(playerMana -> {
                    playerMana.addMana(extracted);
                });
            }
        });
        return true;
    }
}
