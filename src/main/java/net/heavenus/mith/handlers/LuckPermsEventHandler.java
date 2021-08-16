package net.heavenus.mith.handlers;

import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.role.Role;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.event.user.track.UserTrackEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.md_5.bungee.api.chat.TextComponent;

import javax.sql.rowset.CachedRowSet;

public class LuckPermsEventHandler {

    public static void loadHandlers() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(BotSync.getInstance(), NodeAddEvent.class, consumer -> {
            if (!consumer.isUser()) {
                return;
            }

            User user = (User) consumer.getTarget();
            Node node = consumer.getNode();
            if (node.getType() != NodeType.INHERITANCE) return;

            IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, user.getUsername());
            if(iMithAccount instanceof EmptyMithAccountBucket) return;
            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            abstractMithAccount.sync();
        });

        eventBus.subscribe(BotSync.getInstance(), NodeRemoveEvent.class, consumer -> {
            if (!consumer.isUser()) {
                return;
            }

            User user = (User) consumer.getTarget();
            Node node = consumer.getNode();

            if (node.getType() != NodeType.INHERITANCE) return;

            IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, user.getUsername());
            if(iMithAccount instanceof EmptyMithAccountBucket) return;
            AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
            abstractMithAccount.sync();
        });

    }
}
