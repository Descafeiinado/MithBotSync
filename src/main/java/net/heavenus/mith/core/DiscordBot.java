package net.heavenus.mith.core;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.listeners.CommandListeners;
import net.heavenus.mith.core.thread.JavaBotChangeStatusThread;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class DiscordBot {
    private static JDA jda;

    public static JDA getJda() {
        return jda;
    }

    public static void startBot() {
        try {
            jda = JDABuilder.
                    createLight(BotSync.getInstance().getConfiguration().getString("discord.token", "null")).
                    addEventListeners(new CommandListeners()).
                    build();
            jda.awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaBotChangeStatusThread.setupPresences();

        BotSync.setStatusThread(new JavaBotChangeStatusThread(jda));
        BotSync.getScheduledExecutorService().scheduleAtFixedRate(BotSync.getStatusThread(), 0, 15, TimeUnit.SECONDS);
    }

}
