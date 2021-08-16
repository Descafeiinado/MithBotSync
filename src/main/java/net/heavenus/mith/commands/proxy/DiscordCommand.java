package net.heavenus.mith.commands.proxy;

import net.dv8tion.jda.api.entities.Member;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.commands.Commands;
import net.heavenus.mith.core.DiscordBot;
import net.heavenus.mith.logger.LoggerExecutor;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.sql.rowset.CachedRowSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DiscordCommand extends Commands {

    public static HashMap<String, String> hashMap = new HashMap<>();

    public DiscordCommand() {
        super("discord");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 0) {
            player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Ajuda 1/1 \n \n " +
                    " §3/discord vincular §f- §7Vincular um discord à sua conta." +
                    " \n §3/discord desvincular §f- §7Desvincular um discord da sua conta.\n" +
                    " §3/discord info §f- §7Verificar informações sobre a sua conta vinculada.\n"));
            return;
        }

        String action = args[0];
        if (action.equalsIgnoreCase("vincular")) {

            if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `name` = ?", player.getName()) != null) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê já possui uma conta vinculada, desvincule utilizando /discord desvincular"));
                return;
            }

            String code = "authsync-" + UUID.randomUUID().toString().split("-")[0];

            hashMap.put(code, player.getName());

            BaseComponent component = new TextComponent("");
            for (BaseComponent components : TextComponent.fromLegacyText(" \n §aOlá! Para vincular o seu Discord com esta conta do Minecraft, basta ir nas mensagens diretas de nosso bot que se encontra em nosso discord oficial, o §7" + DiscordBot.getJda().getSelfUser().getName() + "#" + DiscordBot.getJda().getSelfUser().getDiscriminator() + " §ae digitar o comando abaixo:" +
                    "\n\n §f" + BotSync.getInstance().getConfiguration().getString("discord.prefix") + "vincular " + code + " ")) {
                component.addExtra(components);
            }
            BaseComponent accept = new TextComponent("(copiar)");
            accept.setColor(ChatColor.GRAY);
            accept.setBold(false);
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, BotSync.getInstance().getConfiguration().getString("discord.prefix") + "vincular " + code));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique para copiar o seu código de vínculo.\n§7Código: §f" + code)));
            component.addExtra(accept);

            for (BaseComponent components : TextComponent.fromLegacyText("\n ")) {
                component.addExtra(components);
            }

            player.sendMessage(component);
        } else if (action.equalsIgnoreCase("desvincular")) {
            if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `name` = ?", player.getName()) == null) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê não possui uma conta vinculada, vincule utilizando /discord vincular"));
                return;
            }
            IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, player.getName());
            if(iMithAccount instanceof EmptyMithAccountBucket) return;
            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            abstractMithAccount.remove();
            BotSync.getHikariDatabase().execute("DELETE FROM `MithBotSync` WHERE `name` = ?", player.getName());
            player.sendMessage(TextComponent.fromLegacyText("§aConta desvinculada com sucesso."));

        } else if (action.equalsIgnoreCase("info")) {
            BotSync.getInstance().getLogger().info("Length: " + args.length);
            if(args.length == 2){
                if(player.hasPermission("mithbotsync.cmd.info")){
                    IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, args[1]);

                    if(iMithAccount instanceof EmptyMithAccountBucket){
                        player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações (" + args[1] + ") \n \n " +
                                "§3Vinculado: §cNão." + "\n"));
                        return;
                    }

                    AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
                    Member member = abstractMithAccount.getMember();

                    if(member == null){
                        player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações (" + args[1] + ") \n \n " +
                                "§3Vinculado: §cNão." + "\n"));
                        return;
                    }
                    SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss", new Locale("pt", "br"));
                    Timestamp timestamp = new Timestamp(abstractMithAccount.getTime());
                    player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações (" + args[1] + ") \n \n " +
                            "§3Vinculado: §aSim." + "\n" +
                            " §3Conta vinculada: §f" + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + "\n" +
                            " §3Horário da vinculação: §f" + format.format(new Date(timestamp.getTime())) + "\n"));

                } else {
                    player.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para executar este comando."));
                }
            } else if (args.length == 1){
                IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, player.getName());
                if(iMithAccount instanceof EmptyMithAccountBucket){
                    player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações \n \n " +
                            "§3Vinculado: §cNão." + "\n"));
                    return;
                }

                AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
                Member member = abstractMithAccount.getMember();

                if(member == null){
                    player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações \n \n " +
                            "§3Vinculado: §cNão." + "\n"));
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
                Timestamp timestamp = new Timestamp(abstractMithAccount.getTime());
                player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Informações \n \n " +
                        "§3Vinculado: §aSim." + "\n" +
                        " §3Conta vinculada: §f" + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + "\n" +
                        " §3Horário da vinculação: §f" + format.format(new Date(timestamp.getTime())) + "\n"));

            }
        } else if(action.equalsIgnoreCase("generatelogs")){
            if(player.hasPermission("mithbotsync.cmd.logs")){
                LoggerExecutor loggerExecutor = new LoggerExecutor();
                loggerExecutor.createFile();
                loggerExecutor.writeToFile();
                player.sendMessage(TextComponent.fromLegacyText("§aArquivo de logs gerado: " + loggerExecutor.getName()));
            }

            }


            else if (action.equalsIgnoreCase("ajuda") || action.equalsIgnoreCase("help")) {
            player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Ajuda 1/1 \n \n " +
                    " §3/discord vincular §f- §7Vincular um discord à sua conta." +
                    " \n §3/discord desvincular §f- §7Desvincular um discord da sua conta.\n" +
                    " §3/discord info §f- §7Verificar informações sobre a sua conta vinculada.\n"));
        } else {
            player.sendMessage(TextComponent.fromLegacyText(" \n §eDiscord - Ajuda 1/1 \n \n " +
                    " §3/discord vincular §f- §7Vincular um discord à sua conta." +
                    " \n §3/discord desvincular §f- §7Desvincular um discord da sua conta.\n" +
                    " §3/discord info §f- §7Verificar informações sobre a sua conta vinculada.\n"));
        }
    }
}