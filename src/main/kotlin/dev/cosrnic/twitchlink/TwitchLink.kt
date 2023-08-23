package dev.cosrnic.twitchlink

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import dev.cosrnic.twitchlink.commands.TwitchLinkCommand
import dev.cosrnic.twitchlink.twitchevents.twitchMessageEvent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient

object TwitchLink : ModInitializer {

    lateinit var twitchClient: TwitchClient
    var mc: MinecraftClient = MinecraftClient.getInstance();

    var chatList: List<String> = listOf();

    override fun onInitialize() {

        twitchClient = TwitchClientBuilder.builder().withEnableChat(true).build()

        val cre = ClientCommandRegistrationCallback.EVENT
        cre.register(TwitchLinkCommand::register)

        println("EPIC POG")

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            println("Stopping TwitchClient Chat connection")
            twitchClient.chat.disconnect();
        }

        twitchClient.eventManager.onEvent(ChannelMessageEvent::class.java) { event -> twitchMessageEvent(event) }


    }
}
