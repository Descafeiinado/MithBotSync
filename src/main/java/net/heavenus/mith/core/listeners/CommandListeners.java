package net.heavenus.mith.core.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.heavenus.mith.BotSync;

import java.util.Objects;

public class CommandListeners extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        String rw = e.getMessage().getContentRaw();

        if (!e.getAuthor().isBot() && !e.getMessage().isWebhookMessage() && rw.startsWith(BotSync.getInstance().getConfiguration().getString("discord.prefix"))) {
            BotSync.getCommandController().handleCommand(e);

        }
    }
}
