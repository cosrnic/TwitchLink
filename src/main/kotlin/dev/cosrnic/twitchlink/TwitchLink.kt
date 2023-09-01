package dev.cosrnic.twitchlink

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.chat.events.channel.CheerEvent
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent
import com.github.twitch4j.chat.events.channel.SubscriptionEvent
import dev.cosrnic.twitchlink.commands.TwitchLinkCommand
import dev.cosrnic.twitchlink.hud.TwitchHud
import dev.cosrnic.twitchlink.twitchevents.twitchCheerEvent
import dev.cosrnic.twitchlink.twitchevents.twitchGiftSubscribeEvent
import dev.cosrnic.twitchlink.twitchevents.twitchMessageEvent
import dev.cosrnic.twitchlink.twitchevents.twitchSubscribeEvent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient

object TwitchLink : ModInitializer {

    lateinit var twitchClient: TwitchClient
    var mc: MinecraftClient = MinecraftClient.getInstance()

    var chatList: List<String> = listOf()

    @JvmStatic val twitchHud = TwitchHud()

    override fun onInitialize() {

        twitchClient = TwitchClientBuilder.builder().withEnableChat(true).withEnableHelix(true).build()

        val cre = ClientCommandRegistrationCallback.EVENT
        cre.register(TwitchLinkCommand::register)

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            println("Disconnecting Twitch Chat!")
            twitchClient.chat.disconnect()
        }

        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event -> twitchMessageEvent(event) }
        twitchClient.eventManager.onEvent(SubscriptionEvent::class.java) { event -> twitchSubscribeEvent(event) }
        twitchClient.eventManager.onEvent(GiftSubscriptionsEvent::class.java) { event -> twitchGiftSubscribeEvent(event) }
        twitchClient.eventManager.onEvent(CheerEvent::class.java) { event -> twitchCheerEvent(event) }


    }

}
