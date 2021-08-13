package net.heavenus.mith.core.implementation;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class PersonalizedPresence {

    private OnlineStatus onlineStatus;
    private Activity activity;

    public PersonalizedPresence(OnlineStatus onlineStatus, Activity activity) {
        this.onlineStatus = onlineStatus;
        this.activity = activity;
    }

    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
