package net.heavenus.mith.controllers;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.commands.discord.UnvinculateCommand;
import net.heavenus.mith.commands.discord.VinculateCommand;
import net.heavenus.mith.commands.interfaces.CommandInterface;
import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandController {

    private final Map<String, CommandInterface> commands = new HashMap<>();

    public CommandController() {

        addCommand(new VinculateCommand());
        addCommand(new UnvinculateCommand());
    }

    private void addCommand(CommandInterface command) {
        if (command.getInvoke() instanceof String) {
            if (!commands.containsKey((String) command.getInvoke())) {
                commands.put((String) command.getInvoke(), command);
            }
        } else {
            for (String aliase : (String[]) command.getInvoke()) {
                if (!commands.containsKey(aliase)) {
                    commands.put(aliase, command);
                }
            }
        }

    }

    public Collection<CommandInterface> getCommands() {
        return commands.values();
    }

    public CommandInterface getCommand(@NotNull String name) {
        return commands.get(name);
    }

    public void handleCommand(PrivateMessageReceivedEvent event) {
        final String prefix = BotSync.getInstance().getConfiguration().getString("discord.prefix");


        final String[] split = event.getMessage().getContentRaw().replaceFirst(
                "(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final String invoke = split[0].toLowerCase();

        if (commands.containsKey(invoke)) {
            final List<String> args = Arrays.asList(split);
            commands.get(invoke
            ).handle(args, event);


        } else {
            EmbedBuilder help = new EmbedBuilder();
            String[] subCommand = {
                    " ⠀`" + prefix + "vincular <codigo> | Vincula uma conta do Minecraft à uma conta do discord." + "`"};
            help.setTitle("Confira os comandos disponíveis para você!");
            help.setColor(new Color(54, 57, 63));
            help.setDescription(
                    "Comandos: \n\n" + String.join("\n", subCommand));
            event.getChannel().sendMessage(help.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));

        }
    }
}
