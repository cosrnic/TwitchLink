package dev.cosrnic.twitchlink.utils

import net.minecraft.text.*
import net.minecraft.util.Formatting
import java.lang.NumberFormatException

object Utils {

    val prefix: MutableText = Text.literal("[TL] ").setStyle(
        Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE)
            .withClickEvent(
                ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    "https://github.com/cosrnic/TwitchLink"
                )
            ).withHoverEvent(
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, Text.literal("TwitchLink").formatted(Formatting.LIGHT_PURPLE)
                )
            )
    )

    fun hexColourToColourInt(hexColour: String): Int {
        val cleanHexColor = if (hexColour.startsWith("#")) hexColour.substring(1) else hexColour

        return try {
            Integer.parseInt(cleanHexColor, 16)
        } catch (e: NumberFormatException) {
            0xFFFFFF
        }

    }
}