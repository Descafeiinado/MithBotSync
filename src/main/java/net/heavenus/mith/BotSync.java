package net.heavenus.mith;

import me.bristermitten.pdm.BungeeDependencyManager;
import me.bristermitten.pdm.PluginDependencyManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.heavenus.mith.commands.Commands;
import net.heavenus.mith.controllers.CommandController;
import net.heavenus.mith.core.DiscordBot;
import net.heavenus.mith.core.implementation.PersonalizedPresence;
import net.heavenus.mith.core.role.Role;
import net.heavenus.mith.core.thread.JavaBotChangeStatusThread;
import net.heavenus.mith.database.HikariDatabase;
import net.heavenus.mith.exceptions.DependencyLoadingException;
import net.heavenus.mith.handlers.LuckPermsEventHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BotSync extends Plugin {

    private static BotSync instance;
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static final ArrayList<String> debugLogs = new ArrayList<>();

    private static JavaBotChangeStatusThread statusThread;

    public static BotSync getInstance() {
        return instance;
    }

    public static JavaBotChangeStatusThread getStatusThread() {
        return statusThread;
    }

    public static void setStatusThread(JavaBotChangeStatusThread statusThread) {
        BotSync.statusThread = statusThread;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    private static HikariDatabase hikariDatabase;


    private static CommandController commandController;

    public static HikariDatabase getHikariDatabase() {
        return hikariDatabase;
    }

    public static CommandController getCommandController() {
        return commandController;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        hikariDatabase = new HikariDatabase(
                configuration.getString("database.mysql.host"),
                configuration.getString("database.mysql.porta"),
                configuration.getString("database.mysql.nome"),
                configuration.getString("database.mysql.usuario"),
                configuration.getString("database.mysql.senha"),
                configuration.getBoolean("database.mysql.mariadb", false)
        );
        commandController = new CommandController();
        Commands.setupCommands();
        Role.setupRoles();
        DiscordBot.startBot();
        LuckPermsEventHandler.loadHandlers();
        getLogger().info("O plugin foi ativado.");
        /*getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (!hikariDatabase.isConnected()) {
                    return;
                }

                try (CachedRowSet rowSet = hikariDatabase.query("SELECT * FROM `MithBotSync`")) {

                    if (rowSet == null) {
                        return;
                    }

                    BotSync.getInstance().getLogger().info("Name: " + rowSet.getString("name"));
                    BotSync.getInstance().getLogger().info("Discord_ID: " + rowSet.getString("discord_id"));
                    BotSync.getInstance().getLogger().info("Size: " + rowSet.size());
                    BotSync.getInstance().getLogger().info("Last: " + rowSet.last());
                    BotSync.getInstance().getLogger().info("First: " + rowSet.first());
                    BotSync.getInstance().getLogger().info("First & Last: " + (rowSet.first() && rowSet.last()));


                } catch (SQLException e) {
                    BotSync.getInstance().getLogger().severe("Couldn't load CachedRowSet for automatic role synchronization.");
                    e.printStackTrace();
                }

            }
        }, 0L, 1L, TimeUnit.MINUTES);
*/
    }

    private Configuration configuration;

    public void saveDefaultConfig() {
        for (String fileName : new String[]{"config"}) {
            File file = new File("plugins/MithBotSync/" + fileName + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copyFile(BotSync.getInstance().getResourceAsStream(fileName + ".yml"), file);
            }

            try {
                if (fileName.equals("config")) {
                    this.configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                }
            } catch (IOException ex) {
                this.getLogger().log(Level.WARNING, "Cannot load " + fileName + ".yml: ", ex);
            }
        }
    }

    public static void copyFile(InputStream input, File out) {
        FileOutputStream ou = null;
        try {
            ou = new FileOutputStream(out);
            byte[] buff = new byte[1024];
            int len;
            while ((len = input.read(buff)) > 0) {
                ou.write(buff, 0, len);
            }
        } catch (IOException ex) {
            getInstance().getLogger().log(Level.WARNING, "Failed at copy file " + out.getName() + "!", ex);
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("O plugin foi desativado.");
    }

}
