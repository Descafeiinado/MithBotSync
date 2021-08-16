package net.heavenus.mith.core.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.core.role.Role;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordBoosterListeners extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        // Ativou um boost
        if (event.getOldTimeBoosted() == null && event.getNewTimeBoosted() != null) {
            IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.DISCORD_ID, event.getMember().getId());
            if (iMithAccount instanceof EmptyMithAccountBucket) return;
            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;

            if (Role.getRoleByBooster() == null) return;

            LuckPerms luckPerms = LuckPermsProvider.get();
            net.luckperms.api.model.user.User luckPermsUser = luckPerms.getUserManager().loadUser(UUID.randomUUID(), abstractMithAccount.getUsername()).join();

            luckPerms.getGroupManager().getLoadedGroups().forEach(group -> {
                net.heavenus.mith.core.role.Role localRole = net.heavenus.mith.core.role.Role.getRoleByName(group.getName());

                if (localRole == null) {
                    return;
                }
                if (localRole.isBooster()) {
                    luckPermsUser.data().add(InheritanceNode.builder(group).build());
                    luckPerms.getUserManager().saveUser(luckPermsUser);
                    abstractMithAccount.sync();
                }
            });
            return;
        }

        // Desativou um boost
        if (event.getNewTimeBoosted() != null && event.getOldTimeBoosted() != null) {
            IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.DISCORD_ID, event.getMember().getId());
            if(iMithAccount instanceof EmptyMithAccountBucket) return;
            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            if (Role.getRoleByBooster() == null) return;

            LuckPerms luckPerms = LuckPermsProvider.get();
            net.luckperms.api.model.user.User luckPermsUser = luckPerms.getUserManager().loadUser(UUID.randomUUID(), abstractMithAccount.getUsername()).join();

            luckPermsUser.getInheritedGroups(luckPermsUser.getQueryOptions()).forEach(group -> {

                net.heavenus.mith.core.role.Role localRole = net.heavenus.mith.core.role.Role.getRoleByName(group.getName());

                if (localRole == null) {
                    return;
                }
                if (localRole.isBooster()) {
                    luckPermsUser.data().remove(InheritanceNode.builder(group).build());
                    luckPerms.getUserManager().saveUser(luckPermsUser);
                    abstractMithAccount.sync();
                }
            });
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.DISCORD_ID, e.getUser().getId());
        if(iMithAccount instanceof EmptyMithAccountBucket) return;
        AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
        abstractMithAccount.sync();
    }
}
