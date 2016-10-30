package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Status implements Command {

    private String option;

    @Override
    public final boolean called(String args, MessageReceivedEvent event) {
        String[] options = new String[]{"discord", "database", "twitch", "all"};

        for (String s : options) {
            // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    option = s;
                    return true;
                } else if ("help".equals(args)) {
                    // If the help argument is the only argument that is passed
                    return true;
                }
            } else {
                // If there are no passed arguments
                sendToChannel(event, Const.EMPTY_ARGS);
                return false;
            }
        }
        // If all checks fail
        return false;
    }

    @Override
    public final void action(String args, MessageReceivedEvent event) {
        switch (option) {
            case "discord":

                break;

            case "database":
                //String query = "SHOW SESSION STATUS LIKE %Uptime%";
                break;

            case "twitch":

                break;

            case "all":

                break;

            default:

                break;
        }
    }

    @Override
    public final void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.STATUS_HELP);

    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Status");
    }

    private boolean optionCheck(String args, String passedOption) {
        return args.toLowerCase().substring(0, passedOption.length()).equals(passedOption);
    }
}
