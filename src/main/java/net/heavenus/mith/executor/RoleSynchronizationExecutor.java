package net.heavenus.mith.executor;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.DiscordBot;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.UUID;

public class RoleSynchronizationExecutor {

    public static IMithAccount getAccount(String username) {
        try (CachedRowSet cachedRowSet = BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `name` = ?", username)) {
            return new AbstractMithAccount(cachedRowSet.getString("name"), cachedRowSet.getString("discord_id"));
        } catch (SQLException e) {
            return new EmptyMithAccountBucket();
        }
    }

    public static IMithAccount getAccountFromDiscord(String username) {
        try (CachedRowSet cachedRowSet = BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", username)) {
            return new AbstractMithAccount(cachedRowSet.getString("name"), cachedRowSet.getString("discord_id"));
        } catch (SQLException e) {
            return new EmptyMithAccountBucket();
        }
    }

    public static void sync(IMithAccount abstractAccount) {
        sync(abstractAccount, false, true);
    }

    public static void sync(IMithAccount abstractAccount, boolean remove, boolean ismember) {
        if (abstractAccount instanceof EmptyMithAccountBucket) {
            BotSync.getInstance().getLogger().info("No account was found.");
            return;
        }

        AbstractMithAccount realAccount = (AbstractMithAccount) abstractAccount;
        Guild guild = DiscordBot.getJda().getGuildById(BotSync.getInstance().getConfiguration().getString("discord.guild"));
        User user = DiscordBot.getJda().getUserById(realAccount.getDiscordId());
        if (guild == null) return;
        if (user == null) return;
        if (guild.getMemberById(user.getId()) == null) return;

        Member member = guild.getMemberById(user.getId());
        if(member == null) return;

        if (remove) {
            if (member.getRoles().isEmpty()) return;
            member.getRoles().forEach(role -> {
                guild.removeRoleFromMember(member, role).complete();
            });
            return;
        }
        LuckPerms luckPerms = LuckPermsProvider.get();
        net.luckperms.api.model.user.User luckPermsUser = luckPerms.getUserManager().loadUser(UUID.randomUUID(), realAccount.getUsername()).join();

        if(ismember){
            net.heavenus.mith.core.role.Role.listRoles().forEach(localRole -> {
                if(localRole.isMember()){
                    Role role = guild.getRoleById(localRole.getRoleLong());
                    if (role == null) return;
                    guild.addRoleToMember(member, role).complete();
                }
            });
            return;
        }

        net.heavenus.mith.core.role.Role.listRoles().forEach(localRole -> {

            if (localRole.getLuckPermsNode() == null) return;
            if (luckPermsUser == null) return;
            InheritanceNode inheritanceNode = InheritanceNode.builder(localRole.getName()).build();
            if(luckPermsUser.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()){
                Role role = guild.getRoleById(localRole.getRoleLong());
                if (role == null) return;
                guild.addRoleToMember(member, role).complete();
            } else {
                Role role = guild.getRoleById(localRole.getRoleLong());
                if (role == null) return;
                guild.removeRoleFromMember(member, role).complete();
            }

        });

    }

}
