package net.heavenus.mith.core.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;

import java.awt.*;
import java.util.Date;

public class Embeds {

    public static MessageEmbed CHANNEL_NOT_PRIVATE = new EmbedBuilder().setColor(Color.RED).setTitle(":x: | **Erro:**").setTimestamp(new Date().toInstant()).
            setDescription("Seu código de autenticação é inválido.").build();
    public static MessageEmbed ACCOUNT_ALREADY_REGISTERED = new EmbedBuilder().setColor(Color.RED).setTitle(":x: | **Erro:**").setTimestamp(new Date().toInstant()).
            setDescription("Seu discord já está vinculado à uma conta.").build();
    public static MessageEmbed REGISTRY_ARGUMENTS_NOT_SUFFICIENT = new EmbedBuilder().setColor(Color.RED).setTitle(":x: | **Erro:**").setTimestamp(new Date().toInstant()).
            setDescription("Argumentos insuficientes.\nUtilize " + BotSync.getInstance().getConfiguration().getString("discord.prefix") + "vincular <codigo>").build();

    public static MessageEmbed BOOST(Guild guild, Member member, IMithAccount iMithAccount) {
        return new EmbedBuilder().setColor(Color.PINK).setTitle(":bookmark:  | **Boost:**").setTimestamp(new Date().toInstant()).
                setDescription("Olá, " + member.getUser().getName() + ".\nAgradecemos por ter impulsionado o servidor " + guild.getName() + "."
                        +
                        (iMithAccount instanceof EmptyMithAccountBucket ? "\n\n * Verificamos que você não tem conta vinculada em nossa rede, entre em nosso servidor e vincule utilizando /discord vincular!" : "")).build();
    }

    public static MessageEmbed DESVINCULATE_NOT_VINCULATED() {
        return new EmbedBuilder().setColor(Color.RED).setTitle(":x: | **Erro:**").setTimestamp(new Date().toInstant()).
                setDescription("Você não possui nenhuma conta vinculada.").build();
    }

    public static MessageEmbed DESVINCULATE_SUCCESS() {
        return new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_small_square: | **Sucesso:**").setTimestamp(new Date().toInstant()).
                setDescription("Sua conta foi desvinculada com sucesso.").build();
    }

    public static MessageEmbed REGISTERED_SUCCESS(String nickname) {
        return new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_small_square: | **Sucesso:**").setTimestamp(new Date().toInstant()).
                setDescription("Sua conta foi vinculada com sucesso ao usuário `" + nickname + "`").build();
    }

}
