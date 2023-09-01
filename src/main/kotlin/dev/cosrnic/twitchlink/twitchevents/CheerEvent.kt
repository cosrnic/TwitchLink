package dev.cosrnic.twitchlink.twitchevents

import com.github.twitch4j.chat.events.channel.CheerEvent
import dev.cosrnic.twitchlink.TwitchLink
import dev.cosrnic.twitchlink.config.Config
import dev.cosrnic.twitchlink.utils.Utils
import net.minecraft.text.*
import net.minecraft.util.Formatting

fun twitchCheerEvent(event: CheerEvent) {
    val text = Text.empty()
        .append(
            Text.literal(if (Config.separateChatHud && TwitchLink.chatList.size > 1) "[${event.messageEvent.channelName.orElse(event.channel.name)}] " else "[TL] ").setStyle(
                Style.EMPTY.withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, Text.literal("TwitchLink").formatted(Formatting.LIGHT_PURPLE).append(
                            Text.literal(" - ${event.channel.name}'s Chat").formatted(Formatting.DARK_GRAY))
                    )
                ).withClickEvent(
                    ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "https://github.com/cosrnic/TwitchLink"
                    )
                )
            ).formatted(Formatting.LIGHT_PURPLE)
        )

    text.append(
        Text.literal(event.messageEvent.userDisplayName.orElse(event.messageEvent.userName)).setStyle(
            Style.EMPTY.withColor(
                TextColor.fromRgb(Utils.hexColourToColourInt(event.messageEvent.userChatColor.orElse("#FFFFFF"))))))
    text.append(" cheered ")
    text.append(Text.literal("${event.bits} ").formatted(Formatting.LIGHT_PURPLE))

    if (event.message.isNotEmpty()) {
        text.append(Text.literal(": ").formatted(Formatting.GRAY))
        text.append(Text.literal(event.message).formatted(Formatting.WHITE))
    }

    if (!Config.separateChatHud) {
        TwitchLink.mc.inGameHud.chatHud.addMessage(text)
    } else {
        TwitchLink.twitchHud.addMessage(text)
    }

}