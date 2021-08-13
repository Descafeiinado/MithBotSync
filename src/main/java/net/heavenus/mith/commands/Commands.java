package net.heavenus.mith.commands;

import net.heavenus.mith.BotSync;
import net.heavenus.mith.commands.proxy.DiscordCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Maxter
 */
public abstract class Commands extends Command {

    public Commands(String name, String... aliases) {
        super(name, null, aliases);
        ProxyServer.getInstance().getPluginManager().registerCommand(BotSync.getInstance(), this);
    }

    public abstract void perform(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.perform(sender, args);
    }

    public static void setupCommands() {
        new DiscordCommand();
    }
}
