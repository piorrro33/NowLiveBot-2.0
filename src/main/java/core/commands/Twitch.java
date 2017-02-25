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
import platform.twitch.controller.TwitchController;
import util.database.calls.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static core.CommandParser.getCommands;
import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Twitch implements Command {

    private ArrayList<String> commands = new ArrayList<>();
    private Boolean valid = false;
    private Boolean goodCommand = true;
    private ConcurrentHashMap<String, String> channel = new ConcurrentHashMap<>();
    private List<String> notFoundChannel = new CopyOnWriteArrayList<>();
    private String discordChannelId = null;
    private List<String> gameFilter = new CopyOnWriteArrayList<>();
    private List<String> titleFilter = new CopyOnWriteArrayList<>();
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
        /*StringBuilder columnValues = new StringBuilder();
        StringBuilder columnNames = new StringBuilder();

        commands.forEach(command -> {
            if (args.toLowerCase().startsWith(command)) {
                // Make sure everything is empty to prevent excess API checks
                if (channel.size() > 0) {
                    channel.clear();
                }
                if (notFoundChannel.size() > 0) {
                    notFoundChannel.clear();
                }
                if (gameFilter.size() > 0) {
                    gameFilter.clear();
                }
                if (titleFilter.size() > 0) {
                    titleFilter.clear();
                }

                switch (command) {
                    case "channel":
                        System.out.println(args);// Print all args
                        channelHandler(event, args.replaceFirst("channel ", ""));
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

                if (channel != null && channel.size() > 0) {
                    String flattened = channel.values().toString();
                    String stripped = flattened.replaceAll("[\\[\\]]", "");
                    message.append(String.format("\n# Found channel(s): %s. ", stripped));
                }

                if (notFoundChannel != null && notFoundChannel.size() > 0) {
                    String flattened = notFoundChannel.toString();
                    String stripped = flattened.replaceAll("[\\[\\]]", "");
                    message.append(String.format("\n# Channel(s) not found: %s. ", stripped));
                }

                if (discordChannelId != null && event.getGuild().getTextChannelById(discordChannelId) != null) {
                    String discordChannelString = String.format("\n# They will announce on : #%s. ",
                            event.getGuild().getTextChannelById(discordChannelId).getName());
                    if (columnValues.length() > 0) {
                        columnNames.append(",");
                        columnValues.append(",");
                    }
                    columnNames.append("`announceChannel`");
                    columnValues.append("'");
                    columnValues.append(discordChannelId);
                    columnValues.append("'");
                    message.append(discordChannelString);
                }

                if (gameFilter != null && gameFilter.size() > 0) {
                    String flattened = gameFilter.toString();
                    String stripped = flattened.replaceAll("[\\[\\]]", "");
                    String gameFilterString = String.format("\n# They will only be announced when they are playing: %s.",
                            stripped);

                    if (columnValues.length() > 0) {
                        columnValues.append(",");
                        columnNames.append(",");
                    }
                    columnNames.append("`gameFilter`");
                    columnValues.append("'");
                    columnValues.append(stripped);
                    columnValues.append("'");
                    message.append(gameFilterString);
                }

                if (titleFilter != null && titleFilter.size() > 0) {
                    String flattened = titleFilter.toString();
                    String stripped = flattened.replaceAll("[\\[\\]]", "");
                    String titleFilterString = String.format("\n# They will only be announced when these words are in the title: %s.",
                            stripped);
                    if (columnValues.length() > 0) {
                        columnValues.append(",");
                        columnNames.append(",");
                    }
                    columnNames.append("`titleFilter`");
                    columnValues.append("'");
                    columnValues.append(stripped);
                    columnValues.append("'");
                    message.append(titleFilterString);
                }
                if (channel != null && channel.size() > 0) {
                    channel.forEach((chanId, chan) -> {
                        String query = null;

                        if (chanId != null && columnNames.length() > 0) {
                            query = String.format("INSERT INTO `twitch` (`channelName`, `channelId`,%s) VALUES ('%s','%s',%s)",
                                    columnNames, chan, chanId, columnValues);
                        } else if (chanId != null) {
                            query = String.format("INSERT INTO `twitch` (`channelName`, `channelId`) VALUES ('%s','%s')",
                                    chan, chanId);
                        }
                        System.out.println(query);
                    });
                }
            }
        });
        if (message.length() > 0 && goodCommand) {
            sendToChannel(event, "```Markdown" + message.toString() + "```");
            message.setLength(0);
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
        findTwitchChannels(args);
        findDiscordChannel(args, event);
        findGameFilters(args);
        findTitleFilters(args);
    }

    private synchronized void findTwitchChannels(String args) {
        // Extract the channel name from the args
        TwitchController twitch = new TwitchController();
        channel.clear();

        if (args.indexOf(' ') > 0 && args.indexOf("|") > 0) {// Check for adding multiple channels at once with other options
            List<String> channelList = Arrays.stream(args.substring(0, args.indexOf(' ')).split("\\|")).collect(Collectors.toList());
            channelList.forEach(chan -> {
                String chanId = twitch.convertNameToId(chan);
                if (chanId != null) {
                    channel.put(chanId, chan);
                } else {
                    notFoundChannel.add(chan);
                }
            });
        } else if (args.indexOf("|") > 0) {
            List<String> channelList = Arrays.stream(args.split("\\|")).collect(Collectors.toList());
            channelList.forEach(chann -> {
                String chanId = twitch.convertNameToId(chann);
                if (chanId == null) {
                    notFoundChannel.add(chann);
                } else {
                    channel.put(chanId, chann);
                }
            });
        } else if (args.indexOf(' ') > 0) {
            String chanId = twitch.convertNameToId(args.substring(0, args.indexOf(' ')));
            if (chanId != null) {
                channel.put(chanId, args.substring(0, args.indexOf(' ')));
            } else {
                notFoundChannel.add(args.substring(0, args.indexOf(' ')));
            }
        } else {
            String chanId = twitch.convertNameToId(args);
            if (chanId != null) {
                channel.put(chanId, args);
            } else {
                notFoundChannel.add(args);
            }
        }
        System.out.println(channel);//Print just the channel name
    }

    private synchronized void findDiscordChannel(String args, GuildMessageReceivedEvent event) {
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
    }

    private synchronized void findGameFilters(String args) {
        // Check for specific channel filter(s)
        if (args.indexOf("{") > 0 && args.indexOf("}") > args.indexOf("{")) {
            String gameFilters = args.substring(args.indexOf("{") + 1, args.indexOf("}", args.indexOf("{")));
            if (gameFilters.indexOf("|") > 0) {
                this.gameFilter = Arrays.stream(gameFilters.split("\\|")).collect(Collectors.toList());
            } else {
                this.gameFilter.add(gameFilters);
            }
        } else {
            this.gameFilter = null;
        }
        System.out.println(gameFilter);
    }

    private synchronized void findTitleFilters(String args) {
        // Check for specific title filters
        if (args.indexOf("[") > 0 && args.indexOf("]") > args.indexOf("[")) {
            String titleFilters = args.substring(args.indexOf("[") + 1, args.indexOf("]", args.indexOf("[")));
            if (titleFilters.indexOf("|") > 0) {
                this.titleFilter = Arrays.stream(titleFilters.split("\\|")).collect(Collectors.toList());
            } else {
                this.titleFilter.add(titleFilters);
            }
        } else {
            this.titleFilter = null;
        }
        System.out.println(titleFilter);
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
