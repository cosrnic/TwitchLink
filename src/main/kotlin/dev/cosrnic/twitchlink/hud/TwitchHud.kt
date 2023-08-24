package dev.cosrnic.twitchlink.hud

import com.google.common.collect.Lists
import com.mojang.logging.LogUtils
import dev.cosrnic.twitchlink.TwitchLink.mc
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.ChatHudLine.Visible
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Nullables
import net.minecraft.util.math.MathHelper
import java.util.*
import kotlin.math.roundToInt

class TwitchHud {
    private val logger = LogUtils.getLogger()
    private val client: MinecraftClient = mc
    private val visibleMessages: MutableList<Visible> = Lists.newArrayList()
    private var scrolledLines = 0
    private var hasUnreadNewMessages = false

    fun render(context: DrawContext, currentTick: Int) {
        val i = getVisibleLineCount()
        val j = visibleMessages.size
        if (j > 0) {
            val f = getChatScale().toFloat()
            val k = MathHelper.ceil(this.getWidth().toFloat() / f)
            context.matrices.push()
            context.matrices.scale(f, f, 1.0f)
            context.matrices.translate(4.0f, 0.0f, 0.0f)
            val m = MathHelper.floor((getHeight()).toFloat() / f)
            val d = client.options.chatOpacity.value as Double * 0.8999999761581421 + 0.10000000149011612
            val e = client.options.textBackgroundOpacity.value as Double
            val g = client.options.chatLineSpacing.value as Double
            val o = getLineHeight()
            val p = (-8.0 * (g + 1.0) + 4.0 * g).roundToInt()
            var q = 0
            var t: Int
            var u: Int
            var v: Int
            var x: Int
            var r = 0
            while (r + scrolledLines < visibleMessages.size && r < i) {
                val s = r + scrolledLines
                val visible = visibleMessages[s]
                t = currentTick - visible.addedTime()
                if (t < 200) {
                    val h = getMessageOpacityMultiplier(t)
                    u = (255.0 * h * d).toInt()
                    v = (255.0 * h * e).toInt()
                    ++q
                    if (u > 3) {
                        x = m - r * o
                        val y = x + p
                        context.matrices.push()
                        context.matrices.translate(0.0f, 0.0f, 50.0f)
                        context.fill(-4, x - o, 0 + k + 4 + 4, x, v shl 24)
                        context.matrices.translate(0.0f, 0.0f, 50.0f)
                        context.drawTextWithShadow(
                            client.textRenderer,
                            visible.content(),
                            0,
                            y,
                            16777215 + (u shl 24)
                        )
                        context.matrices.pop()
                    }
                }
                ++r
            }
            val ac = client.messageHandler.unprocessedMessageCount
            val ad: Int
            if (ac > 0L) {
                ad = (128.0 * d).toInt()
                t = (255.0 * e).toInt()
                context.matrices.push()
                context.matrices.translate(0.0f, m.toFloat(), 50.0f)
                context.fill(-2, 0, k + 4, 9, t shl 24)
                context.matrices.translate(0.0f, 0.0f, 50.0f)
                context.drawTextWithShadow(
                    client.textRenderer,
                    Text.translatable("chat.queue", *arrayOf<Any>(ac)),
                    0,
                    1,
                    16777215 + (ad shl 24)
                )
                context.matrices.pop()
            }
            context.matrices.pop()
        }
    }


    private fun getMessageOpacityMultiplier(age: Int): Double {
        var d = age.toDouble() / 200.0
        d = 1.0 - d
        d *= 10.0
        d = MathHelper.clamp(d, 0.0, 1.0)
        d *= d
        return d
    }

    fun addMessage(message: Text) {
        this.addMessage(
            message,
            if (client.isConnectedToLocalServer) MessageIndicator.singlePlayer() else MessageIndicator.system()
        )
    }

    private fun addMessage(message: Text, indicator: MessageIndicator?) {
        logChatMessage(message, indicator)
        this.addMessage(message, client.inGameHud.ticks, indicator)
    }

    private fun logChatMessage(message: Text, indicator: MessageIndicator?) {
        val string = message.string.replace("\r".toRegex(), "\\\\r").replace("\n".toRegex(), "\\\\n")
        val string2 = Nullables.map(
            indicator
        ) { obj: MessageIndicator? -> obj!!.loggedName() }
        if (string2 != null) {
            logger.info("[{}] [CHAT] {}", string2, string)
        } else {
            logger.info("[CHAT] {}", string)
        }
    }

    private fun addMessage(
        message: Text,
        ticks: Int,
        indicator: MessageIndicator?
    ) {
        var i = MathHelper.floor(this.getWidth().toDouble() / getChatScale())
        if (indicator?.icon() != null) {
            i -= indicator.icon()!!.width + 4 + 2
        }
        val list = ChatMessages.breakRenderedChatMessageLines(message, i, client.textRenderer)
        for (j in list.indices) {
            val orderedText = list[j] as OrderedText
            if (scrolledLines > 0) {
                hasUnreadNewMessages = true
                scroll()
            }
            val bl2 = j == list.size - 1
            visibleMessages.add(0, Visible(ticks, orderedText, indicator, bl2))
        }
        while (visibleMessages.size > 100) {
            visibleMessages.removeAt(visibleMessages.size - 1)
        }
    }

    private fun scroll() {
        scrolledLines += 1
        val i = visibleMessages.size
        if (scrolledLines > i - getVisibleLineCount()) {
            scrolledLines = i - getVisibleLineCount()
        }
        if (scrolledLines <= 0) {
            scrolledLines = 0
            hasUnreadNewMessages = false
        }
    }

    private fun getWidth(): Int {
        return getWidth(client.options.chatWidth.value as Double)
    }

    private fun getHeight(): Int {
        return getHeight(client.options.chatHeightFocused.value as Double)
    }

    private fun getChatScale(): Double {
        return client.options.chatScale.value as Double
    }

    private fun getWidth(widthOption: Double): Int {
        return MathHelper.floor(widthOption * 280.0 + 40.0)
    }

    private fun getHeight(heightOption: Double): Int {
        return MathHelper.floor(heightOption * 160.0 + 20.0)
    }

    private fun getVisibleLineCount(): Int {
        return this.getHeight() / getLineHeight()
    }

    private fun getLineHeight(): Int {
        Objects.requireNonNull(client.textRenderer)
        return (9.0 * (client.options.chatLineSpacing.value as Double + 1.0)).toInt()
    }

}