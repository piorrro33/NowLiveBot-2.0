package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 * Created by keesh on 10/2/2016.
 */
public class Compact implements Command {
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        event.getTextChannel().sendMessage("Compact command works");
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(Const.COMPACT_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
