package net.heavenus.mith.core.listeners;

import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.core.role.Role;
import net.heavenus.mith.executor.RoleSynchronizationExecutor;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordBoosterListeners extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        // Ativou um boost
        if (event.getOldTimeBoosted() == null && event.getNewTimeBoosted() != null) {
            IMithAccount iMithAccount = RoleSynchronizationExecutor.getAccountFromDiscord(event.getUser().getId());
            if (iMithAccount instanceof EmptyMithAccountBucket){
                event.getUser().openPrivateChannel().complete().sendMessage(Embeds.BOOST(event.getGuild(), event.getMember(), iMithAccount)).queue();
                return;
            }

            if(Role.getRoleByBooster() == null || Role.getRoleByBooster().getLuckPermsNode() == null) return;

            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            LuckPerms luckPerms = LuckPermsProvider.get();
            net.luckperms.api.model.user.User luckPermsUser = luckPerms.getUserManager().loadUser(UUID.randomUUID(), abstractMithAccount.getUsername()).join();
            if(luckPermsUser != null) {
                InheritanceNode inheritanceNode = InheritanceNode.builder(Role.getRoleByBooster().getName()).build();
                if(luckPermsUser.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()){
                    luckPermsUser.data().add(inheritanceNode);
                    luckPerms.getUserManager().saveUser(luckPermsUser);
                }
            }

            return;
        }

        // Desativou um boost
        if (event.getNewTimeBoosted() != null && event.getOldTimeBoosted() != null) {
            IMithAccount iMithAccount = RoleSynchronizationExecutor.getAccountFromDiscord(event.getUser().getId());
            if (iMithAccount instanceof EmptyMithAccountBucket){
                return;
            }
            if(Role.getRoleByBooster() == null || Role.getRoleByBooster().getLuckPermsNode() == null) return;

            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            LuckPerms luckPerms = LuckPermsProvider.get();
            net.luckperms.api.model.user.User luckPermsUser = luckPerms.getUserManager().loadUser(UUID.randomUUID(), abstractMithAccount.getUsername()).join();
            if(luckPermsUser != null) {
                InheritanceNode inheritanceNode = InheritanceNode.builder(Role.getRoleByBooster().getName()).build();
                if(luckPermsUser.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()){
                    luckPermsUser.data().remove(inheritanceNode);
                    luckPerms.getUserManager().saveUser(luckPermsUser);
                }
            }
        }
    }
}
