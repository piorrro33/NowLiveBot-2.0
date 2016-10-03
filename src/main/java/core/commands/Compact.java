package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 * Created by keesh on 10/2/2016.
 */
public class Compact implements Command {

    private String option;
    private String argument;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        String[] options = new String[]{"on", "off", "help"};

        for (String s : options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    this.option = s;
                    return true;
                } else if (args.equals("help")) { // If the help argument is the only argument that is passed
                    return true;
                }
            } else {
                // If there are no passed arguments
                event.getTextChannel().sendMessage(Const.EMPTY_ARGS);
                return false;
            }
        }
        // If all checks fail
        return false;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {

        event.getTextChannel().sendMessage("Compact mode has been turned " + this.option + ".");
    }

    @Override
    public void help(MessageReceivedEvent event) {

        event.getTextChannel().sendMessage(Const.COMPACT_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    private boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
