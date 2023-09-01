package dev.cosrnic.twitchlink.twitchevents

import com.github.twitch4j.chat.events.channel.SubscriptionEvent
import dev.cosrnic.twitchlink.TwitchLink
import dev.cosrnic.twitchlink.config.Config
import dev.cosrnic.twitchlink.utils.Utils
import net.minecraft.text.*
import net.minecraft.util.Formatting

fun twitchSubscribeEvent(event: SubscriptionEvent) {
    println(event)
    val text = Text.empty()
        .append(
            Text.literal(if (Config.separateChatHud) "[${event.messageEvent.channelName.orElse(event.channel.name)}] " else "[TL] ").setStyle(
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
            ).formatted(if (Config.separateChatHud) Formatting.DARK_GRAY else Formatting.LIGHT_PURPLE)
        )

    text.append(
        Text.literal(event.messageEvent.userDisplayName.orElse(event.messageEvent.userName)).setStyle(
            Style.EMPTY.withColor(
                TextColor.fromRgb(Utils.hexColourToColourInt(event.messageEvent.userChatColor.orElse("#FFFFFF"))))))
    text.append(" ")
    text.append(Text.literal("Subscribed with ").formatted(Formatting.GRAY))
    text.append(Text.literal("${if (event.subscriptionPlan == "Prime") { "Prime" } else "Tier " + event.subscriptionPlan.toInt() / 1000} ").formatted(Formatting.WHITE))
    text.append(Text.literal("for ").formatted(Formatting.GRAY))
    text.append(Text.literal("${event.months} month${if (event.months != 1) "s" else ""}").formatted(Formatting.WHITE))
    if (!event.message.isEmpty) {
        text.append(Text.literal(": ").formatted(Formatting.GRAY))
        text.append(Text.literal(event.message.get()).formatted(Formatting.WHITE))
    }

    if (!Config.separateChatHud) {
        TwitchLink.mc.inGameHud.chatHud.addMessage(text)
    } else {
        TwitchLink.twitchHud.addMessage(text)
    }

}