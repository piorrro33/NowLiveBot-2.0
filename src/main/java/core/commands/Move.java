package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 * Created by keesh on 10/2/2016.
 */
public class Move implements Command {

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            if (args.substring(0, 1).equals("#") && !args.contains(" ")) {
                return true;
            } else if (!args.equals("help")) {
                event.getTextChannel().sendMessage(Const.INCORRECT_ARGS);
                return false;
            } else {
                return true;
            }
        } else {
            event.getTextChannel().sendMessage(Const.EMPTY_ARGS);
            return false;
        }
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        // Get the channelID from the guild and insert into the DB
        event.getTextChannel().sendMessage("You moved me to " + args + ".");
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(Const.MOVE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
