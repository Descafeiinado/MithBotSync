package net.heavenus.mith.core.role;

import net.heavenus.mith.BotSync;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Role {

    public int id;
    public String name;

    public String roleLong;

    public boolean member, booster;

    public Role(String name, String roleLong, boolean booster, boolean member) {
        this.id = ROLES.size();
        this.name = name;
        this.roleLong = roleLong;
        this.member = member;
        this.booster = booster;
    }

    public String getName() {
        return name;
    }

    public String getRoleLong() {
        return roleLong;
    }

    public boolean isBooster() {
        return booster;
    }

    public boolean isMember() {
        return member;
    }

    private static final List<Role> ROLES = new ArrayList<>();

    public Group getLuckPermsNode() {
        return LuckPermsProvider.get().getGroupManager().getLoadedGroups().stream().filter(predicate -> {
            return predicate.getName().equals(this.name);
        }).findFirst().orElse(null);
    }

    public static Role getRoleByBooster() {
        return ROLES.stream().filter(Role::isBooster).findAny().orElse(null);
    }

    public static Role getRoleByDefault() {
        return ROLES.stream().filter(Role::isMember).findAny().orElse(null);
    }

    public static Role getRoleByName(String name) {
        return ROLES.stream().filter(predicate -> {
            return predicate.getName().equals(name.toLowerCase());
        }).findAny().orElse(null);
    }

    public static Role getRoleByDiscord(String value) {
        return ROLES.stream().filter(predicate -> {
            return predicate.getRoleLong().equals(value);
        }).findAny().orElse(null);
    }

    public static Role getLastRole() {
        return ROLES.get(ROLES.size() - 1);
    }

    public static List<Role> listRoles() {
        return ROLES;
    }

    public static void setupRoles() {
        Configuration config = BotSync.getInstance().getConfiguration();
        for (String key : config.getSection("roles").getKeys()) {
            String name = config.getString("roles." + key + ".name");
            String roleLong = config.getString("roles." + key + ".role");
            boolean booster = config.getBoolean("roles." + key + ".booster", false);
            boolean member = config.getBoolean("roles." + key + ".member", false);
            Role.listRoles().add(new Role(name.toLowerCase(), roleLong, booster, member));
        }

        if (Role.listRoles().isEmpty()) {
            BotSync.getInstance().getLogger().severe("Nao foi possivel carregar nenhum cargo da config.yml, desligando servidor.");
            System.exit(404);
        }
    }

}
