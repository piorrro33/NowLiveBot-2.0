package core.commands;

import core.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.SetCompact;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Compact implements Command {

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        String[] options = new String[]{"on", "off", "help"};

        for (String s : options) {
            // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
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
    public final void action(String args, GuildMessageReceivedEvent event) {
        Integer intArg = -1;
        // Make sure that on/off are transposed properly for DB insertion
        switch (args) {
            case "on":
                intArg = 1;
                break;
            case "off":
                intArg = 0;
                break;
            default:
                sendToChannel(event, Const.INCORRECT_ARGS);
                break;
        }

        if (intArg.equals(1) || intArg.equals(0) && SetCompact.action(event.getGuild().getId(), intArg)) {
            switch (args) {
                case "off":
                    sendToChannel(event, Const.COMPACT_MODE_OFF);

                    break;
                case "on":
                    sendToChannel(event, Const.COMPACT_MODE_ON);
                    break;
                default:
                    System.out.println("[~ERROR~] This statement should never be reached.");
                    break;
            }
        } else {
            sendToChannel(event, Const.COMPACT_FAILURE);
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.COMPACT_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Compact");
    }

    private boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
