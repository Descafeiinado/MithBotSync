package net.heavenus.mith.core.thread;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.implementation.PersonalizedPresence;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class JavaBotChangeStatusThread implements Runnable {

    public JDA jda;
    public int timer;

    public JDA getJda() {
        return jda;
    }

    public JavaBotChangeStatusThread(JDA jda) {
        this.jda = jda;
        this.timer = 0;
    }

    @Override
    public void run() {
        timer++;
        Presence presence = this.jda.getPresence();

        PersonalizedPresence personalizedPresence = presences.get(ThreadLocalRandom.current().nextInt(presences.size()));

        presence.setPresence(personalizedPresence.getOnlineStatus(), personalizedPresence.getActivity());
    }

    @Getter
    private static final ArrayList<PersonalizedPresence> presences = new ArrayList<>();

    public static void setupPresences() {
        for (String serialized : BotSync.getInstance().getConfiguration().getStringList("presences")) {
            String[] parts = serialized.split("; ");
            presences.add(new PersonalizedPresence(OnlineStatus.valueOf(parts[0]), Activity.of(Activity.ActivityType.valueOf(parts[1]), parts[2])));
        }
    }

}
