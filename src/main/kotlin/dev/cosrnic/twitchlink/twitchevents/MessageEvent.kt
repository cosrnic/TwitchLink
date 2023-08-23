package dev.cosrnic.twitchlink.twitchevents

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dev.cosrnic.twitchlink.TwitchLink
import dev.cosrnic.twitchlink.utils.Utils
import net.minecraft.text.*
import net.minecraft.util.Formatting

fun twitchMessageEvent(event: ChannelMessageEvent) {
    val text = Text.empty()
        .append(
            Text.literal("[TL] ").setStyle(
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

    val badges = event.messageEvent.badges;
    if (badges.isNotEmpty()) {
        badges.forEach {
            if (it.key.equals("broadcaster")) {
                text.append(
                    Text.literal("\uD83D\uDCF9 ").setStyle(
                        Style.EMPTY.withFormatting(Formatting.RED).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Broadcaster"))))
                )
            }
            if (it.key.equals("moderator")) {
                text.append(
                    Text.literal("\uD83D\uDDE1 ").setStyle(
                        Style.EMPTY.withFormatting(Formatting.GREEN).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Moderator"))))
                )
            }
            if (it.key.equals("vip")) {
                text.append(
                    Text.literal("\uD83D\uDC8E ").setStyle(
                        Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("VIP"))))
                )
            }
            if (it.key.equals("subscriber")) {
                text.append(
                    Text.literal("\uD83C\uDF1F ").setStyle(
                        Style.EMPTY.withFormatting(Formatting.AQUA).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Subscriber - ${event.messageEvent.subscriberMonths.asInt} month${if (event.messageEvent.subscriberMonths.asInt != 1) "s" else ""}"))))
                )
            }
            if (it.key.equals("bits")) {
                text.append(Text.literal("\uD83D\uDCB0 ").setStyle(
                    Style.EMPTY.withFormatting(Formatting.GOLD).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Bits - ${event.messageEvent.cheererTier.asInt}+"))))
                )
            }
            if (it.key.equals("partner")) {
                text.append(
                    Text.literal("✔ ").setStyle(
                        Style.EMPTY.withFormatting(Formatting.DARK_PURPLE)
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Twitch Partner")))
                    )
                )
            }
        }
    }
    if (event.messageEvent.userName == "cosrnic") {
        text.append(Text.literal("\uD83D\uDCBB ").setStyle(
            Style.EMPTY.withFormatting(Formatting.DARK_AQUA)
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append(Text.literal("TwitchLink ").formatted(Formatting.LIGHT_PURPLE)).append(Text.literal("Developer"))))
                .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://cosrnic.dev"))
        ))
    }
    text.append(
        Text.literal(event.messageEvent.userDisplayName.orElse(event.messageEvent.userName)).setStyle(
            Style.EMPTY.withColor(
                TextColor.fromRgb(Utils.hexColourToColourInt(event.messageEvent.userChatColor.orElse("#FFFFFF"))))))
    text.append(Text.literal(": ").formatted(Formatting.GRAY))
    text.append(Text.literal(event.message).formatted(Formatting.WHITE).setStyle(
        if (event.replyInfo != null) {
            Style.EMPTY.withHoverEvent(
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Text.literal("Replying to @${event.replyInfo!!.displayName}: ${event.replyInfo!!.messageBody}")
                )
            )
        } else {
            Style.EMPTY
        }
    ))

    TwitchLink.mc.inGameHud.chatHud.addMessage(text)

}