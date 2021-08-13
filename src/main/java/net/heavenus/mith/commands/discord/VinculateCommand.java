package net.heavenus.mith.commands.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.commands.interfaces.CommandInterface;
import net.heavenus.mith.commands.proxy.DiscordCommand;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.core.role.Role;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;

public class VinculateCommand implements CommandInterface {

    @Override
    public void handle(List<String> args, PrivateMessageReceivedEvent e) {

        if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", e.getAuthor().getId()) != null) {
            e.getChannel().sendMessage(Embeds.ACCOUNT_ALREADY_REGISTERED).queue();
            return;
        }

        if (args.size() != 2) {
            e.getChannel().sendMessage(Embeds.REGISTRY_ARGUMENTS_NOT_SUFFICIENT).queue();
            return;
        }

        if (DiscordCommand.hashMap.containsKey(args.get(1))) {
            BotSync.getHikariDatabase().execute("INSERT INTO `MithBotSync` VALUES (?, ?)", DiscordCommand.hashMap.get(args.get(1)), e.getAuthor().getId());
            e.getChannel().sendMessage(Embeds.REGISTERED_SUCCESS(DiscordCommand.hashMap.get(args.get(1)))).queue();

            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(DiscordCommand.hashMap.remove(args.get(1)));
            if (proxiedPlayer != null && proxiedPlayer.isConnected()) {
                proxiedPlayer.sendMessage(TextComponent.fromLegacyText("\n" +
                        " §aSucesso!\n" +
                        " §aVocê teve sua conta do servidor vinculada com a conta do Discord §f" + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + "§a.\n" +
                        " §7* Você obteve acesso à todos os canais do nosso Discord.\n"));
            }
        }

    }

    @Override
    public String[] getInvoke() {
        return new String[]{"vincular", "vinculate"};
    }

}
