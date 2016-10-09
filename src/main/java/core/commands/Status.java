package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Status implements Command {

    private static Logger logger = LoggerFactory.getLogger(Status.class);

    private String option;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        String[] options = new String[]{"discord", "database", "twitch", "all"};

        for (String s : options) {
            // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    this.option = s;
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
    public void action(String args, MessageReceivedEvent event) {
        switch (this.option) {
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
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.STATUS_HELP);

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Status");
    }

    private boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
