package net.heavenus.mith.handlers;

import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.role.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.user.track.UserTrackEvent;

public class LuckPermsEventHandler {

    public static void loadHandlers(){
        LuckPerms luckPerms = LuckPermsProvider.get();
        EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(BotSync.getInstance(), NodeMutateEvent.class, consumer -> {

        });
    }
}
