/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import util.Const;
import util.database.calls.*;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.generic.controller.PlatformController.getPlatformId;

/**
 * Add Command.
 * TODO: Move SQL calls to separate class.
 *
 * @author keesh
 */
public class Add implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Add.class);
    private String option;
    private String argument;
    private DiscordController dController;
    private Integer platformId;
    private String[] options = new String[]{"channel", "filter", "game", "manager", "tag", "team", "help"};

    public static boolean optionCheck(String args, String option) {
        return args.contains(" ") && args.toLowerCase().substring(0, option.length()).equals(option);
    }

    public static boolean argumentCheck(String args, Integer spaceLocation) {
        return args.indexOf(' ') == spaceLocation && args.length() >= args.indexOf(' ') + 1;
    }

    public static void missingArguments(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.INCORRECT_ARGS);
    }

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        for (String s : this.options) { // Iterate through the available options for this command
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

        dController = new DiscordController(event);

        String guildId = dController.getGuildId();

        if (getPlatformId(args) > 0) {
            platformId = getPlatformId(args);
        } else {
            platformId = 1;
        }

        if (platformId > 0) {
            args = args.substring(args.indexOf("~") + 1);
        }

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {

                this.argument = this.argument.replace("'", "''");

                switch (this.option) {
                    case "manager":
                        // Check to make sure the user is not a bot
                        try {
                            for (User u : event.getMessage().getMentionedUsers()) {
                                String userId = u.getId();
                                if (!event.getJDA().getUserById(userId).isBot()) {
                                    if (!CountManagers.action(this.option, guildId, userId)) {

                                        returnStatement(AddManager.action(this.option, guildId, userId), guildId,
                                                event);
                                    } else {
                                        sendToChannel(event, "It seems I've already hired that user as a manager.  Find moar " +
                                                "humanz!");
                                    }
                                } else {
                                    sendToChannel(event, Const.NO_BOT_MANAGER);
                                }
                            }
                        } catch (NullPointerException npe) {
                            sendToChannel(event, "That person isn't a Discord user!  Try again!");
                        }
                        break;
                    default:

                        if (CheckTableData.action(this.option, guildId, platformId, this.argument)) {
                            sendToChannel(event, Const.ALREADY_EXISTS);
                        } else {
                            returnStatement(AddOther.action(this.option, guildId, platformId, this.argument),
                                    guildId, event);
                        }
                        break;
                }
            }
        }
    }

    private void returnStatement(Boolean success, String guildId, GuildMessageReceivedEvent event) {
        if (success) {
            sendToChannel(event, "Added `" + this.option + "` " + this.argument);
            logger.info("Successfully added " + this.argument + " to the database for guildId: " +
                    guildId + ".");
        } else {
            sendToChannel(event, "Failed to add `" + this.option + "` " + this.argument);
            logger.info("Failed to add " + this.option + " " + this.argument + " to the database for " +
                    "guildId: " + guildId + ".");
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.ADD_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Add");

    }

}