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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.database.calls.Tracker;

import java.util.ArrayList;

import static core.CommandParser.getCommands;
import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Twitch implements Command {

    private ArrayList<String> commands = new ArrayList<>();
    private Boolean valid = false;
    private Boolean goodCommand = true;
    private String channel = null;
    private String gameFilter = null;
    private String discordChannelId = null;
    private StringBuilder message = new StringBuilder();

    public Twitch() {
        addCommands();
    }

    private void addCommands() {
        commands.add("channel");
        commands.add("community");
        commands.add("gamefilter");
        commands.add("game");
        commands.add("team");
        commands.add("titlefilter");
        commands.add("help");
    }

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
            /*commands.forEach(
                    command -> {
                        if (args.toLowerCase().startsWith(command)) {
                            valid = true;
                        }
                    });

            if (valid) {
                return true;
            }*/


            String calledArgs = args.trim().substring(args.lastIndexOf(' ') + 1);

            if (calledArgs.matches("^[a-zA-Z0-9_]{4,25}$")) {
                if ("help".equals(args)) {
                    return true;
                }

                String secondaryCommand;
                try {
                    secondaryCommand = args.trim().substring(0, args.indexOf(' '));
                } catch (StringIndexOutOfBoundsException ex) {
                    return false;
                }

                switch (secondaryCommand) {
                    case "add":
                    case "remove":
                        return true;
                    default:
                        return false;
                }
            }
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

        /*commands.forEach(
                command -> {
                    if (args.toLowerCase().startsWith(command)) {
                        switch (command) {
                            case "channel":
                                System.out.println(args);// Print all args
                                channelHandler(event, args.replaceFirst("channel ", ""));
                                if (channel != null) {
                                    String channelString = String.format("Found channel(s): %s. ", channel.replaceAll(",", ", "));
                                    message.append(channelString);
                                }
                                if (discordChannelId != null) {
                                    String discordChannelString = String.format("They will announce on : #%s. ",
                                            event.getGuild().getTextChannelById(discordChannelId).getName());
                                    message.append(discordChannelString);
                                }
                                if (gameFilter != null) {
                                    String gameFilterString = String.format("They will only be announced when they are playing: %s.",
                                            gameFilter);
                                    message.append(gameFilterString);
                                }
                                break;
                            case "community":

                                break;
                            case "gamefilter":

                                break;
                            case "game":

                                break;
                            case "team":

                                break;
                            case "titlefilter":

                                break;
                            default:
                                help(event);
                                break;
                        }
                    }
                });
        if (message.length() > 0 && goodCommand) {
            sendToChannel(event, message.toString());
        } else if (!goodCommand) {
            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
        }*/

        // Grab the secondary command (add and remove)
        String secondaryCommand = args.trim().substring(0, args.indexOf(' '));
        // the args to be passed to the secondaryCommand#called()
        String calledArgs = args.trim().substring(args.indexOf(' ') + 1).trim();
        // the args to be passed along with the platform identifier
        String secondaryArgs = "twitch~" + args.trim().substring(args.indexOf(' ') + 1);
        switch (secondaryCommand) {
            case "add":
            case "remove":
                if (calledArgs.startsWith("channel")) {
                    if (getCommands().get(secondaryCommand).called(calledArgs, event)) {
                        getCommands().get(secondaryCommand).action(secondaryArgs, event);
                    }
                } else {
                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
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

    private void channelHandler(GuildMessageReceivedEvent event, String args) {
        // Extract the channel name from the args
        if (args.indexOf(' ') > 0 && args.indexOf("|") > 0) {// Check for adding multiple channels at once with other options
            this.channel = args.substring(0, args.indexOf(' '));
            this.channel = channel.replaceAll("\\|", ",");
        } else if (args.indexOf("|") > 0) {
            this.channel = args.replaceAll("\\|", ",");
        } else if (args.indexOf(' ') > 0) {
            this.channel = args.substring(0, args.indexOf(' '));
        } else {
            this.channel = args;
        }
        System.out.println(channel);//Print just the channel name

        // Check for specific channel to announce in
        String discordChannels;
        if (args.indexOf("#") > 0) {
            if (args.indexOf(' ', args.indexOf("#")) > 0) {// Check if there are more things after the channel
                discordChannels = args.substring(args.indexOf("#") + 1, args.indexOf(' ', args.indexOf("#")));
            } else {// this is the last argument in the string
                discordChannels = args.substring(args.indexOf("#") + 1);
            }
            if (!checkValidDiscordChannel(event, discordChannels)) {
                this.goodCommand = false;
            } else {
                event.getGuild().getTextChannelsByName(discordChannels, true).forEach(
                        discordChannel -> this.discordChannelId = discordChannel.getId());
            }
        } else {
            this.discordChannelId = null;
        }
        System.out.println(discordChannelId);

        // Check for specific channel filter
        if (args.indexOf("{") > 0) {
            if (args.indexOf("}", args.indexOf("{")) > 0) {// Check if there are more things after the filter
                gameFilter = args.substring(args.indexOf("{") + 1, args.indexOf("}", args.indexOf("{")));
            }
        } else {
            gameFilter = null;
        }
        System.out.println(gameFilter);
    }

    private Boolean checkValidDiscordChannel(GuildMessageReceivedEvent event, String channelName) {
        if (!event.getGuild().getTextChannelsByName(channelName, true).isEmpty()) {
            return true;
        }
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "discordChannelNoExist"));
        return false;
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "twitchHelp"));
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }
}
