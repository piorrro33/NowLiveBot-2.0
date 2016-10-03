package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

import java.util.logging.Logger;

/**
 * Created by keesh on 10/3/2016.
 */
public class Remove implements Command {

    private static final Logger LOG = Logger.getLogger(Remove.class.getName());
    public String help;
    private String option;
    private String argument;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {

        String[] options = new String[]{"channel", "game", "manager", "tag", "team", "help"};

        for (String s : options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    if (argumentCheck(args, s.length())) {
                        // Sets the class scope variables that will be used by action()
                        this.option = s;
                        this.argument = args.substring(this.option.length() + 1);
                        return true;
                    } else {
                        // If the required arguments for the option are missing
                        missingArguments(event);
                        return false;
                    }
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

        event.getTextChannel().sendMessage("Removed `" + this.option + "` " + this.argument);
    }

    @Override
    public void help(MessageReceivedEvent event) {

        event.getTextChannel().sendMessage(Const.REMOVE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        // TODO: Database command count + other post-script
    }

    private boolean optionCheck(String args, String option) {
        return args.contains(" ") && args.toLowerCase().substring(0, option.length()).equals(option);
    }

    private boolean argumentCheck(String args, Integer spaceLocation) {

        return args.indexOf(" ") == spaceLocation && args.length() >= args.indexOf(" ") + 1;
    }

    private void missingArguments(MessageReceivedEvent event) {

        event.getTextChannel().sendMessage(Const.INCORRECT_ARGS);
    }

}
