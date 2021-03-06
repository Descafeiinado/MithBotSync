package net.heavenus.mith.core.commands.discord;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.commands.interfaces.CommandInterface;
import net.heavenus.mith.core.commands.proxy.DiscordCommand;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(DiscordCommand.hashMap.get(args.get(1)));
            if (proxiedPlayer != null && proxiedPlayer.isConnected()) {
                BotSync.getHikariDatabase().execute("INSERT INTO `MithBotSync` VALUES (?, ?, ?, ?)", DiscordCommand.hashMap.get(args.get(1)), e.getAuthor().getId(), proxiedPlayer.getUniqueId().toString(), System.currentTimeMillis()).thenRun(new Runnable() {
                    @Override
                    public void run() {
                        e.getChannel().sendMessage(Embeds.REGISTERED_SUCCESS(DiscordCommand.hashMap.get(args.get(1)))).queue();
                        IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.DISCORD_ID, e.getAuthor().getId());
                        if(iMithAccount instanceof EmptyMithAccountBucket) return;
                        AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
                        abstractMithAccount.register();


                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText("\n" +
                                " ??aSucesso!\n" +
                                " ??aVoc?? teve sua conta do servidor vinculada com a conta do Discord ??f" + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + "??a.\n" +
                                " ??7* Voc?? obteve acesso ?? todos os canais do nosso Discord.\n"));
                        DiscordCommand.hashMap.remove(args.get(1));
                    }
                });

            } else {
                e.getChannel().sendMessage(Embeds.PLAYER_NOT_ONLINE).queue();
            }
        }

    }

    @Override
    public String[] getInvoke() {
        return new String[]{"vincular", "vinculate"};
    }

}
