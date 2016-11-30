/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.beam.controller.BeamController;
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

    private String option;
    private String argument;
    private DiscordController dController;
    private Integer platformId;
    private String[] options = new String[]{"channel", "filter", "game", "manager", "help"};

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
                        try {
                            for (User u : event.getMessage().getMentionedUsers()) {
                                String userId = u.getId();
                                // Check to make sure the user is not a bot
                                if (!event.getJDA().getUserById(userId).isBot()) {
                                    if (!CountManagers.action(this.option, guildId, userId)) {

                                        returnStatement(AddManager.action(this.option, guildId, userId), event);
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
                    case "channel":
                        switch (platformId) {
                            case 1:
                                if (CheckTableData.action(this.option, guildId, platformId, this.argument)) {
                                    sendToChannel(event, Const.ALREADY_EXISTS);
                                } else {
                                    returnStatement(AddOther.action(this.option, guildId, platformId, this.argument), event);
                                }
                                break;
                            case 2:
                                if (BeamController.channelExists(this.argument)) {
                                    if (CheckTableData.action(this.option, guildId, platformId, this.argument)) {
                                        sendToChannel(event, Const.ALREADY_EXISTS);
                                    } else {
                                        returnStatement(AddOther.action(this.option, guildId, platformId, this.argument), event);
                                    }
                                } else {
                                    sendToChannel(event, "That Beam user does not exist! Check your spelling and try" +
                                            " again!");
                                }
                        }
                        break;
                    default:

                        if (CheckTableData.action(this.option, guildId, platformId, this.argument)) {
                            sendToChannel(event, Const.ALREADY_EXISTS);
                        } else {
                            returnStatement(AddOther.action(this.option, guildId, platformId, this.argument), event);
                        }
                        break;
                }
            }
        }
    }

    private void returnStatement(Boolean success, GuildMessageReceivedEvent event) {
        if (success) {
            sendToChannel(event, "Added `" + this.option + "` " + this.argument);
        } else {
            sendToChannel(event, "Failed to add `" + this.option + "` " + this.argument);
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