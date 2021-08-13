package net.heavenus.mith.commands.discord;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.commands.interfaces.CommandInterface;
import net.heavenus.mith.commands.proxy.DiscordCommand;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.executor.RoleSynchronizationExecutor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class UnvinculateCommand implements CommandInterface {

    @Override
    public void handle(List<String> args, PrivateMessageReceivedEvent e) {

        if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", e.getAuthor().getId()) == null) {
            e.getChannel().sendMessage(Embeds.DESVINCULATE_NOT_VINCULATED()).queue();
            return;
        }

        if (args.size() != 1) {
            e.getChannel().sendMessage(Embeds.REGISTRY_ARGUMENTS_NOT_SUFFICIENT).queue();
            return;
        }
        RoleSynchronizationExecutor.sync(RoleSynchronizationExecutor.getAccountFromDiscord(e.getAuthor().getId()), true, false);
        BotSync.getHikariDatabase().execute("DELETE FROM `MithBotSync` WHERE `discord_id` = ?", e.getAuthor());
        e.getChannel().sendMessage(Embeds.DESVINCULATE_SUCCESS()).queue();

    }


    @Override
    public String[] getInvoke() {
        return new String[]{"desvincular", "unvinculate"};
    }

}
