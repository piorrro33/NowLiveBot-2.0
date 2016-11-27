package core.commands;

import core.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static core.CommandParser.getCommands;
import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Twitch implements Command {
    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !"".equals(args)) {
            if ("help".equals(args)) {
                return true;
            }
            String secondaryCommand = args.substring(0, args.indexOf(' '));
            switch (secondaryCommand) {
                case "add":
                case "remove":
                    return true;
            }
            System.out.println(secondaryCommand);
        }
        return false;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void action(String args, GuildMessageReceivedEvent event) {
        // Grab the secondary command (add and remove)
        String secondaryCommand = args.substring(0, args.indexOf(' '));
        // the args to be passed to the secondaryCommand#called()
        String calledArgs = args.substring(args.indexOf(' ') + 1);
        // the args to be passed along with the platform identifier
        String secondaryArgs = "twitch~" + args.substring(args.indexOf(' ') + 1);
        switch (secondaryCommand) {
            case "add":
            case "remove":
                if (calledArgs.startsWith("channel")) {
                    if (getCommands().get(secondaryCommand).called(calledArgs, event)) {
                        getCommands().get(secondaryCommand).action(secondaryArgs, event);
                    }
                }
                break;
            case "help":
                if (getCommands().get(secondaryCommand).called(calledArgs, event)) {
                    getCommands().get(secondaryCommand).help(event);
                }
                break;
            default:
                // This should never be used
                break;
        }
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.TWITCH_HELP);
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Twitch");
    }
}
