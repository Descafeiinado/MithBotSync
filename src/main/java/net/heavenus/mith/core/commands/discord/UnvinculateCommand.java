package net.heavenus.mith.core.commands.discord;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.heavenus.mith.BotSync;
import net.heavenus.mith.core.commands.interfaces.CommandInterface;
import net.heavenus.mith.core.embed.Embeds;
import net.heavenus.mith.models.AbstractMithAccount;
import net.heavenus.mith.models.EmptyMithAccountBucket;
import net.heavenus.mith.models.IMithAccount;
import net.heavenus.mith.models.enums.DatabaseFindType;

import java.util.List;

public class UnvinculateCommand implements CommandInterface {

    @Override
    public void handle(List<String> args, PrivateMessageReceivedEvent e) {

        if (BotSync.getHikariDatabase().query("SELECT * FROM `MithBotSync` WHERE `discord_id` = ?", e.getAuthor().getId()) == null) {
            e.getChannel().sendMessage(Embeds.DESVINCULATE_NOT_VINCULATED()).queue();
            return;
        }

        if (args.size() != 1) {
            e.getChannel().sendMessage(Embeds.REGISTRY_ARGUMENTS_NOT_SUFFICIENT).queue();
            return;
        }

        IMithAccount iMithAccount = AbstractMithAccount.getFrom(DatabaseFindType.USERNAME, e.getAuthor().getId());
        if(iMithAccount instanceof EmptyMithAccountBucket) return;
        AbstractMithAccount abstractMithAccount = (AbstractMithAccount) iMithAccount;
        abstractMithAccount.remove();

        BotSync.getHikariDatabase().execute("DELETE FROM `MithBotSync` WHERE `discord_id` = ?", e.getAuthor().getId());
        e.getChannel().sendMessage(Embeds.DESVINCULATE_SUCCESS()).queue();

    }


    @Override
    public String[] getInvoke() {
        return new String[]{"desvincular", "unvinculate"};
    }

}
