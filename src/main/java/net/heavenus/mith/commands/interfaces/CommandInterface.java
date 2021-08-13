package net.heavenus.mith.commands.interfaces;


import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.List;

public interface CommandInterface {

    void handle(List<String> args, PrivateMessageReceivedEvent event);
    Object getInvoke();

}