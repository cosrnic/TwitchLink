package dev.cosrnic.twitchlink.chat

import dev.cosrnic.twitchlink.TwitchLink.chatList
import dev.cosrnic.twitchlink.TwitchLink.mc
import dev.cosrnic.twitchlink.TwitchLink.twitchClient
import dev.cosrnic.twitchlink.utils.Utils
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun leaveChannel(channel: String) {

    if (!twitchClient.chat.isChannelJoined(channel)) {
        mc.inGameHud.chatHud.addMessage(Utils.prefix.copy().append(Text.literal("Not connected to ${channel}'s chat!").formatted(Formatting.RED)))
        return;
    }
    twitchClient.chat.leaveChannel(channel)

    chatList -= channel

    mc.inGameHud.chatHud.addMessage(Utils.prefix.copy().append(Text.literal("Successfully disconnected from ${channel}'s chat!").formatted(Formatting.GREEN)))

}