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
        val visibleLineCount = getVisibleLineCount()
        val visibleMessages = visibleMessages.size
        if (visibleMessages > 0) {
            val chatScale = getChatScale().toFloat()
            val something = MathHelper.ceil(this.getWidth().toFloat() / chatScale)
            context.matrices.push()
            context.matrices.scale(chatScale, chatScale, 1.0f)
            context.matrices.translate(4.0f, 0.0f, 0.0f)
            val something2 = MathHelper.floor((getHeight()).toFloat() / chatScale)
            val chatOpacity = client.options.chatOpacity.value as Double * 0.8999999761581421 + 0.10000000149011612
            val textBackgroundOpacity = client.options.textBackgroundOpacity.value as Double
            val chatLineSpacing = client.options.chatLineSpacing.value as Double
            val lineHeight = getLineHeight()
            val centerSpacing = (-8.0 * (chatLineSpacing + 1.0) + 4.0 * chatLineSpacing).roundToInt()
            var age: Int
            var chatColour: Int
            var textBackgroundColour: Int
            var x: Int
            var counter = 0
            while (counter + scrolledLines < this.visibleMessages.size && visibleLineCount > counter) {
                val countedLines = scrolledLines + counter
                val visible = this.visibleMessages[countedLines]
                age = currentTick - visible.addedTime()
                if (age < 300) {
                    val opacityMultiplier = getMessageOpacityMultiplier(age)
                    chatColour = (255.0 * opacityMultiplier * chatOpacity).toInt()
                    textBackgroundColour = (255.0 * opacityMultiplier * textBackgroundOpacity).toInt()
                    if (chatColour > 3) {
                        x = something2 - counter * lineHeight
                        val y = x + centerSpacing
                        context.matrices.push()
                        context.matrices.translate(0.0f, 0.0f, 50.0f)
                        context.fill(-4, x - lineHeight, 0 + something + 4 + 4, x, textBackgroundColour shl 24)
                        context.matrices.translate(0.0f, 0.0f, 50.0f)
                        context.drawTextWithShadow(
                            client.textRenderer,
                            visible.content(),
                            0,
                            y,
                            16777215 + (chatColour shl 24)
                        )
                        context.matrices.pop()
                    }
                }
                ++counter
            }
            val unprocessedMessagesCount = client.messageHandler.unprocessedMessageCount
            val something3: Int
            if (unprocessedMessagesCount > 0L) {
                something3 = (128.0 * chatOpacity).toInt()
                age = (255.0 * textBackgroundOpacity).toInt()
                context.matrices.push()
                context.matrices.translate(0.0f, something2.toFloat(), 50.0f)
                context.fill(-2, 0, something + 4, 9, age shl 24)
                context.matrices.translate(0.0f, 0.0f, 50.0f)
                context.drawTextWithShadow(
                    client.textRenderer,
                    Text.translatable("chat.queue", *arrayOf<Any>(unprocessedMessagesCount)),
                    0,
                    1,
                    16777215 + (something3 shl 24)
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