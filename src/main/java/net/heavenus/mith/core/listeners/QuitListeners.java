package net.heavenus.mith.core.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.types.InheritanceNode;

public class QuitListeners extends ListenerAdapter {

    public void onGuildLeaveEvent(GuildMemberRemoveEvent e) {
        Guild guild = e.getGuild();
        if (!guild.getId().equalsIgnoreCase(BotSync.getInstance().getConfiguration().getString("discord.token", "null"))) return;

        if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", e.getUser().getId()) == null) {
            return;
        }

        IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, e.getUser().getId());
        if(iMithAccount instanceof EmptyMithAccountBucket) return;
        AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
        abstractMithAccount.remove();

        BotSync.getHikariDatabase().execute("DELETE FROM `MithBotSync` WHERE `discord_id` = ?", e.getUser().getId());
    }
}
