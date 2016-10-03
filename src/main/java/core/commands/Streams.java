package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 * Created by keesh on 10/3/2016.
 */
public class Streams implements Command {
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            if (args.equals("help")) { // If the help argument is the only argument that is passed
                return true;
            } else {
                event.getTextChannel().sendMessage(Const.INCORRECT_ARGS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        event.getAuthor().getPrivateChannel().sendMessage("Once my database is set up, this message will return a " +
                "list of streams that are currently live!  Only streams with the criteria set by my managers in your " +
                "Discord server will be displayed.");
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(Const.STREAMS_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
