/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package core.commands;

import core.Command;
import langs.LocaleString;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.twitch.controller.TwitchController;
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

    private final String[] options = new String[]{"channel", "filter", "game", "manager", "tag", "help"};
    private String option;
    private String argument;

    public static boolean optionCheck(String args, String option) {
        return args.contains(" ") && args.toLowerCase().substring(0, option.length()).equals(option);
    }

    public static boolean argumentCheck(String args, Integer spaceLocation) {
        return args.indexOf(' ') == spaceLocation && args.length() >= args.indexOf(' ') + 1;
    }

    public static void missingArguments(GuildMessageReceivedEvent event) {

        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
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
                        return false;
                    }
                } else if ("help".equals(args)) {
                    // If the help argument is the only argument that is passed
                    return true;
                }
            } else {
                // If there are no passed arguments
                return false;
            }
        }
        // If all checks fail
        return false;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {

        String guildId = event.getGuild().getId();
        Integer platformId;

        if (getPlatformId(args) > 0) {
            platformId = getPlatformId(args);
        } else {
            platformId = 1;
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
                                    if (!CountManagers.action(guildId, userId)) {

                                        returnStatement(AddManager.action(guildId, userId), event);
                                    } else {
                                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "alreadyManager"));
                                    }
                                } else {
                                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "noBotManager"));
                                }
                            }
                        } catch (NullPointerException npe) {
                            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "discordUserNoExist"));
                        }
                        break;
                    case "channel":
                        switch (platformId) {
                            case 1:
                                TwitchController twitch = new TwitchController();
                                String channelId = twitch.convertNameToId(this.argument);

                                if (CheckTwitchData.action(this.option, guildId, this.argument)) {
                                    sendToChannel(event, LocaleString.getString(guildId, "alreadyExists"));
                                } else {
                                    returnStatement(AddOther.action(this.option, guildId, this.argument, channelId), event);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:

                        if (CheckTwitchData.action(this.option, guildId, this.argument)) {
                            sendToChannel(event, LocaleString.getString(guildId, "alreadyExists"));
                        } else {
                            returnStatement(AddOther.action(this.option, guildId, this.argument, null), event);
                        }
                        break;
                }
            }
        }
    }

    private void returnStatement(Boolean success, GuildMessageReceivedEvent event) {
        if (success) {
            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "added") +
                    "`" + this.option + "` " + this.argument.replaceAll("''", "'"));
        } else {
            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "addFail") +
                    "`" + this.option + "` " + this.argument.replaceAll("''", "'"));
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "addHelp"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");

    }

}