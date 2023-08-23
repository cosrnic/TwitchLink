package dev.cosrnic.twitchlink.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.cosrnic.twitchlink.TwitchLink
import dev.cosrnic.twitchlink.chat.joinChannel
import dev.cosrnic.twitchlink.chat.leaveChannel
import dev.cosrnic.twitchlink.utils.Utils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TwitchLinkCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, registryAccess: CommandRegistryAccess) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>("twitchlink")
                .then(
                    literal("connect")
                        .then(
                            argument("channel", StringArgumentType.string())
                                .executes {
                                    joinChannel(StringArgumentType.getString(it, "channel"))
                                    return@executes 0
                                }
                        )
                    )
                .then(literal("disconnect")
                    .then(
                        argument("channel", StringArgumentType.string())
                            .executes {
                                leaveChannel(StringArgumentType.getString(it, "channel"))
                                return@executes 0
                            }
                    )
                )
                .then(literal("list")
                    .executes {
                        TwitchLink.mc.inGameHud.chatHud.addMessage(Text.literal("Connected Twitch chats:").formatted(Formatting.BLUE))
                        TwitchLink.chatList.forEach {
                            TwitchLink.mc.inGameHud.chatHud.addMessage(Text.empty().append(Text.literal("- ").formatted(Formatting.DARK_GRAY)).append(Text.literal(it)).setStyle(Style.EMPTY.withClickEvent(
                                ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, it)
                            ).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy!")))))
                        }
                        return@executes 0
                    }
                )
                .then(literal("enable")
                    .executes {
                        TwitchLink.twitchClient.chat.connect()
                        TwitchLink.mc.inGameHud.chatHud.addMessage(Utils.prefix.copy().append(Text.literal("Enabled Twitch Chat Link!").formatted(Formatting.GREEN)))
                        TwitchLink.mc.inGameHud.chatHud.addMessage(Utils.prefix.copy().append(Text.literal("Apologies for any lag :(").formatted(Formatting.DARK_GRAY)))
                        return@executes 0
                    }
                )
                .then(literal("disable")
                    .executes {
                        TwitchLink.twitchClient.chat.disconnect()
                        TwitchLink.mc.inGameHud.chatHud.addMessage(Utils.prefix.copy().append(Text.literal("Disabled Twitch Chat Link!").formatted(Formatting.RED)))
                        return@executes 0
                    }
                )
        )
    }
}