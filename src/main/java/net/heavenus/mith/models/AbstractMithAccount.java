package net.heavenus.mith.models;

public class AbstractMithAccount implements IMithAccount {

    public String username, discordId;

    public AbstractMithAccount(String username, String discordId) {
        this.username = username;
        this.discordId = discordId;
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
}
