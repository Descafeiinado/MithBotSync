package net.heavenus.mith.models;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.DiscordBot;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.InheritanceNode;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.UUID;

public class AbstractMithAccount implements IMithAccount {

    public String username, discordId;
    public UUID uniqueId;
    public Long time;

    public AbstractMithAccount(String username, String discordId, UUID uniqueId, Long time) {
        this.username = username;
        this.discordId = discordId;
        this.uniqueId = uniqueId;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Member getMember() {
        Guild guild = DiscordBot.getJda().getGuildById(BotSync.getInstance().getConfiguration().getString("discord.guildid"));
        if (guild == null) return null;
        return guild.getMemberById(this.getDiscordId());
    }

    public void register() {
        Guild guild = DiscordBot.getJda().getGuildById(BotSync.getInstance().getConfiguration().getString("discord.guildid"));
        if (guild == null) return;
        Member member = this.getMember();
        if (member == null) return;
        LuckPerms luckPerms = LuckPermsProvider.get();
        net.luckperms.api.model.user.User luckPermsUser;

        try {
            luckPermsUser = luckPerms.getUserManager().loadUser(this.getUniqueId(), this.getUsername()).get();
        } catch (Exception exception) {
            BotSync.debugLogs.add("Couldn't load user.");
            return;
        }

        net.heavenus.mith.core.role.Role.listRoles().forEach(localRole -> {
            if (localRole.isMember()) {
                Role role = guild.getRoleById(localRole.getRoleLong());
                if (role == null) return;
                guild.addRoleToMember(member, role).queue();
            }
        });
        this.sync();
    }

    public void remove() {
        Guild guild = DiscordBot.getJda().getGuildById(BotSync.getInstance().getConfiguration().getString("discord.guildid"));
        if (guild == null) return;
        Member member = this.getMember();
        if (member == null) return;
        if (member.getRoles().isEmpty()) return;
        member.getRoles().forEach(role -> {
            if (net.heavenus.mith.core.role.Role.getRoleByBooster().getRoleLong().equals(role.getId())) return;
            guild.removeRoleFromMember(member, role).queue();
        });
        member.modifyNickname("").complete();
    }

    public void sync() {
        Guild guild = DiscordBot.getJda().getGuildById(BotSync.getInstance().getConfiguration().getString("discord.guildid"));
        if (guild == null) return;
        Member member = this.getMember();
        if (member == null) return;
        LuckPerms luckPerms = LuckPermsProvider.get();
        net.luckperms.api.model.user.User luckPermsUser;

        try {
            luckPermsUser = luckPerms.getUserManager().loadUser(this.getUniqueId(), this.getUsername()).get();
        } catch (Exception exception) {
            BotSync.debugLogs.add("Couldn't load user.");
            return;
        }

        member.modifyNickname(this.getUsername() + " ✯").complete();

        if (guild.getBoosters().contains(member)) {
            luckPerms.getGroupManager().getLoadedGroups().forEach(group -> {
                net.heavenus.mith.core.role.Role localRole = net.heavenus.mith.core.role.Role.getRoleByName(group.getName());

                if (localRole == null) {
                    return;
                }
                if (localRole.isBooster()) {
                    luckPermsUser.data().add(InheritanceNode.builder(group).build());
                    luckPerms.getUserManager().saveUser(luckPermsUser);
                }
            });
        }

        member.getRoles().forEach(role -> {
            if (net.heavenus.mith.core.role.Role.getRoleByDefault().getRoleLong().equals(role.getId())) {
                return;
            }

            if (net.heavenus.mith.core.role.Role.getRoleByBooster().getRoleLong().equals(role.getId())) {
                return;
            }
            net.heavenus.mith.core.role.Role roleLocal = net.heavenus.mith.core.role.Role.getRoleByDiscord(role.getId());

            if (roleLocal == null) {
                return;
            }
            if(!roleLocal.getName().equals(luckPermsUser.getPrimaryGroup())) {
                guild.removeRoleFromMember(member, role).queue();
            }
        });

        Group group = luckPerms.getGroupManager().getGroup(luckPermsUser.getPrimaryGroup());

        net.heavenus.mith.core.role.Role localRole = net.heavenus.mith.core.role.Role.getRoleByName(group.getName());

        if (localRole == null) {
            return;
        }
        Role role = guild.getRoleById(localRole.getRoleLong());
        if (role == null) return;
        guild.addRoleToMember(member, role).queue();


        net.heavenus.mith.core.role.Role inheritedLocalRole = net.heavenus.mith.core.role.Role.getRoleByName(group.getName());

        if (inheritedLocalRole == null) {
            return;
        }

        Role inheritedRole = guild.getRoleById(inheritedLocalRole.getRoleLong());
        if (inheritedRole == null) return;
        BotSync.debugLogs.add("Member: " + member.getUser().getId());
        BotSync.debugLogs.add("Username: " + member.getUser().getName());
        BotSync.debugLogs.add("group: " + group.getName());
        BotSync.debugLogs.add("Role name: " + inheritedRole.getName());
        BotSync.debugLogs.add("LocalRole: " + localRole.getName());
        guild.addRoleToMember(member, inheritedRole).queue();
    }

    public static IMithAccount getFrom(DatabaseFindType findType, String value) {
        switch (findType) {
            case USERNAME:
                try (CachedRowSet cachedRowSet = BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `name` = ?", value)) {
                    if (cachedRowSet == null || cachedRowSet.getString("name") == null) {
                        return new EmptyMithAccountBucket();
                    }

                    return new AbstractMithAccount(cachedRowSet.getString("name"), cachedRowSet.getString("discord_id"), UUID.fromString(cachedRowSet.getString("uuid")), cachedRowSet.getLong("time"));
                } catch (SQLException e) {
                    return new EmptyMithAccountBucket();
                }
            case DISCORD_ID:
                try (CachedRowSet cachedRowSet = BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", value)) {
                    if (cachedRowSet == null || cachedRowSet.getString("name") == null) {
                        return new EmptyMithAccountBucket();
                    }

                    return new AbstractMithAccount(cachedRowSet.getString("name"), cachedRowSet.getString("discord_id"), UUID.fromString(cachedRowSet.getString("uuid")), cachedRowSet.getLong("time"));
                } catch (SQLException e) {
                    return new EmptyMithAccountBucket();
                }
            default:
                return new EmptyMithAccountBucket();
        }

    }

}
