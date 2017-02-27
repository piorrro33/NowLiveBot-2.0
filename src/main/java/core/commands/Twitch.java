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
import core.Main;
import langs.LocaleString;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.twitch.controller.TwitchController;
import util.database.calls.CheckTwitchData;
import util.database.calls.GetGlobalAnnounceChannel;
import util.database.calls.Tracker;
import util.database.calls.TwitchData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Twitch implements Command {

    private ArrayList<String> commands = new ArrayList<>();
    private Boolean valid = false;
    private ConcurrentHashMap<String, String> channel = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<String> games = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> teams = new CopyOnWriteArrayList<>();
    private List<String> notFoundChannel = new CopyOnWriteArrayList<>();
    private String discordChannelId = null;
    private List<String> gameFilter = new CopyOnWriteArrayList<>();
    private List<String> titleFilter = new CopyOnWriteArrayList<>();
    private StringBuilder message = new StringBuilder();
    private CopyOnWriteArrayList<String> addedChannels = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> deletedChannels = new CopyOnWriteArrayList<>();
    private StringBuilder updateAnnounceChannel = new StringBuilder();
    private String discordChannelName;
    private StringBuilder insertChannelData = new StringBuilder();
    private StringBuilder deleteChannelData = new StringBuilder();
    private String globalAnnounceChannelId = null;

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
            commands.forEach(command -> {
                if (args.toLowerCase().startsWith(command)) {
                    valid = true;
                }
            });

            if (valid) {
                return true;
            }
/*
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
            }*/
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
        commands.forEach(command -> {
            if (args.toLowerCase().startsWith(command)) {
                if (message.length() > 0) {
                    message.setLength(0);
                }
                switch (command) {
                    case "channel":
                        if (gameFilter != null) {
                            gameFilter.clear();
                        }
                        channelHandler(event, args.toLowerCase().replaceFirst("\\b(channel )\\b", ""));
                        channelQueryCalls();

                        sendToChannel(event, "```Markdown" + message.toString() + "```");
                        break;
                    case "community":

                        break;
                    case "gamefilter":
                        if (gameFilter != null) {
                            gameFilter.clear();
                        }
                        gameFilterHandler(event, args.toLowerCase().replaceFirst("\\b(gamefilter )\\b", ""));
                        sendToChannel(event, "```Markdown" + message.toString() + "```");
                        break;
                    case "game":
                        gameHandler(event, args.toLowerCase().replaceFirst("\\b(game )\\b", ""));
                        sendToChannel(event, "```Markdown" + message.toString() + "```");
                        break;
                    case "team":
                        teamHandler(event, args.toLowerCase().replaceFirst("\\b(team )\\b", ""));
                        break;
                    case "titlefilter":
                        if (titleFilter != null) {
                            titleFilter.clear();
                        }
                        titleFilterHandler(event, args.toLowerCase().replaceFirst("\\b(titlefilter )\\b", ""));
                        sendToChannel(event, "```Markdown" + message.toString() + "```");
                        break;
                    default:
                        help(event);
                        break;
                }
            }
        });
        message = new StringBuilder();
/*
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
        }*/
    }

    private synchronized void teamHandler(GuildMessageReceivedEvent event, String args) {
        findDiscordChannel(args, event);

        StringBuilder teamAddList = new StringBuilder();
        StringBuilder teamDeleteList = new StringBuilder();
        StringBuilder teamAddNameList = new StringBuilder();
        StringBuilder teamDeleteNameList = new StringBuilder();

        if (args.indexOf("|") > 0 && args.indexOf("#") > 0) {
            List<String> teamList = Arrays.stream(args.substring(0, args.indexOf("#") - 1).split("\\|")).collect(Collectors.toList());
            teamList.forEach(team -> teams.addIfAbsent(team.trim()));
        } else if (args.indexOf("|") > 0) {
            List<String> teamList = Arrays.stream(args.split("\\|")).collect(Collectors.toList());
            teamList.forEach(team -> teams.addIfAbsent(team.trim()));
        } else if (args.indexOf("#") > 0) {
            teams.add(args.substring(0, args.indexOf("#") - 1).trim());
        } else {
            teams.add(args.trim());
        }

        if (teams != null && teams.size() > 0) {
            teams.forEach(teamName -> {
                if (!CheckTwitchData.action("team", event.getGuild().getId(), teamName)) {

                }
            });
        }
    }

    private synchronized void gameHandler(GuildMessageReceivedEvent event, String args) {
        findDiscordChannel(args, event);

        StringBuilder gameAddList = new StringBuilder();
        StringBuilder gameDeleteList = new StringBuilder();
        StringBuilder gameAddNameList = new StringBuilder();
        StringBuilder gameDeleteNameList = new StringBuilder();

        if (args.indexOf("#") > 0 && args.indexOf("|") > 0) {
            List<String> gameList = Arrays.stream(args.substring(0, args.indexOf("#") - 1).split("\\|")).collect(Collectors.toList());
            gameList.forEach(game -> games.addIfAbsent(game.trim()));
        } else if (args.indexOf("|") > 0) {
            List<String> gameList = Arrays.stream(args.split("\\|")).collect(Collectors.toList());
            gameList.forEach(game -> games.addIfAbsent(game.trim()));
        } else if (args.indexOf("#") > 0) {
            games.addIfAbsent(args.substring(0, args.indexOf("#") - 1).trim());
        } else {
            games.addIfAbsent(args.trim());
        }

        if (games != null && games.size() > 0) {
            games.forEach(gameName -> {
                if (!CheckTwitchData.action("game", event.getGuild().getId(), gameName)) {
                    if (gameAddList.length() > 0) {
                        gameAddList.append(",");
                        gameAddNameList.append(", ");
                    }
                    if (discordChannelId != null) {
                        gameAddList.append(String.format("('%s','%s','%s')",
                                event.getGuild().getId(),
                                gameName,
                                discordChannelId));
                        gameAddNameList.append(gameName);
                    } else {
                        gameAddList.append(String.format("('%s','%s','%s')",
                                event.getGuild().getId(),
                                gameName,
                                globalAnnounceChannelId));
                        gameAddNameList.append(gameName);
                    }
                } else {
                    if (gameDeleteList.length() > 0) {
                        gameDeleteList.append(",");
                        gameDeleteNameList.append(", ");
                    }
                    gameDeleteList.append(String.format("('%s','%s')",
                            event.getGuild().getId(),
                            gameName));
                    gameDeleteNameList.append(gameName);
                }
            });
        }

        if (gameAddList.length() > 0) {
            TwitchData twitchData = new TwitchData();
            String query;

            if (discordChannelId != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `gameName`, `announceChannel`) VALUES %s",
                        gameAddList.toString());
            } else {
                query = String.format("INSERT INTO `twitch` (`guildId`, `gameName`, `announceChannel`) VALUES %s",
                        gameAddList.toString());
            }

            if (twitchData.action(query)) {
                message.append(String.format("\n# Added game(s): %s.",
                        gameAddNameList.toString().replaceAll("[\\[\\]]", "")));
                if (discordChannelId != null) {
                    message.append(String.format("\n# The game will announce in: #%s.",
                            event.getGuild().getTextChannelById(discordChannelId).getName()));
                } else {
                    message.append(String.format("\n# The game will announce in: #%s.",
                            event.getGuild().getTextChannelById(globalAnnounceChannelId).getName()));
                }
            } else {
                message.append(String.format("\n# Failed to add game(s): %s.",
                        gameAddNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }

        if (gameDeleteList.length() > 0) {
            TwitchData twitchData = new TwitchData();

            String query = String.format("DELETE FROM `twitch` WHERE (`guildId`, `gameName`) IN (%s)",
                    gameDeleteList.toString());

            if (twitchData.action(query)) {
                message.append(String.format("\n# Removed game(s): %s.",
                        gameDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n# Failed to remove game(s): %s.",
                        gameDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }
        games = new CopyOnWriteArrayList<>();
        discordChannelId = null;
        globalAnnounceChannelId = null;
    }

    private synchronized void channelHandler(GuildMessageReceivedEvent event, String args) {
        findTwitchChannels(args);
        findDiscordChannel(args, event);
        findGameFilters(args);
        findTitleFilters(args);

        if (channel != null && channel.size() > 0) {
            channel.forEach((chanId, chan) -> {
                if (!CheckTwitchData.action("channel", event.getGuild().getId(), chan)) {
                    if (chanId != null) {
                        if (discordChannelId != null && gameFilter != null && titleFilter != null) {
                            gameFilter.forEach(gFilter -> titleFilter.forEach(tFilter -> {
                                if (insertChannelData.length() > 0) {
                                    insertChannelData.append(",");
                                }
                                insertChannelData.append(String.format("('%s','%s','%s','%s','%s','%s')",
                                        event.getGuild().getId(),
                                        chan,
                                        chanId,
                                        discordChannelId,
                                        gFilter,
                                        tFilter));
                            }));

                        } else if (discordChannelId != null && gameFilter != null) {
                            gameFilter.forEach(gFilter -> {
                                if (insertChannelData.length() > 0) {
                                    insertChannelData.append(",");
                                }
                                insertChannelData.append(String.format("('%s','%s','%s','%s','%s')",
                                        event.getGuild().getId(),
                                        chan,
                                        chanId,
                                        discordChannelId,
                                        gFilter));
                            });
                        } else if (discordChannelId != null && titleFilter != null) {
                            titleFilter.forEach(tFilter -> {
                                if (insertChannelData.length() > 0) {
                                    insertChannelData.append(",");
                                }
                                insertChannelData.append(String.format("('%s','%s','%s','%s','%s')",
                                        event.getGuild().getId(),
                                        chan,
                                        chanId,
                                        discordChannelId,
                                        tFilter));
                            });
                        } else if (discordChannelId != null) {
                            if (insertChannelData.length() > 0) {
                                insertChannelData.append(",");
                            }
                            insertChannelData.append(String.format("('%s','%s','%s','%s')",
                                    event.getGuild().getId(),
                                    chan,
                                    chanId,
                                    discordChannelId));
                        } else if (gameFilter != null && titleFilter != null) {
                            gameFilter.forEach(gFilter ->
                                    titleFilter.forEach(tFilter -> {
                                        if (insertChannelData.length() > 0) {
                                            insertChannelData.append(",");
                                        }
                                        insertChannelData.append(String.format("('%s','%s','%s','%s','%s','%s')",
                                                event.getGuild().getId(),
                                                chan,
                                                chanId,
                                                globalAnnounceChannelId,
                                                gFilter,
                                                tFilter));
                                    }));
                        } else if (gameFilter != null) {
                            gameFilter.forEach(gFilter -> {
                                if (insertChannelData.length() > 0) {
                                    insertChannelData.append(",");
                                }
                                insertChannelData.append(String.format("('%s','%s','%s','%s','%s')",
                                        event.getGuild().getId(),
                                        chan,
                                        chanId,
                                        globalAnnounceChannelId,
                                        gFilter));
                            });
                        } else if (titleFilter != null) {
                            titleFilter.forEach(tFilter -> {
                                if (insertChannelData.length() > 0) {
                                    insertChannelData.append(",");
                                }
                                insertChannelData.append(String.format("('%s','%s','%s','%s','%s')",
                                        event.getGuild().getId(),
                                        chan,
                                        chanId,
                                        globalAnnounceChannelId,
                                        tFilter));
                            });
                        } else {
                            if (insertChannelData.length() > 0) {
                                insertChannelData.append(",");
                            }
                            insertChannelData.append(String.format("('%s','%s','%s','%s')",
                                    event.getGuild().getId(),
                                    chan,
                                    chanId,
                                    globalAnnounceChannelId));
                        }
                        addedChannels.addIfAbsent(chan);
                    }
                } else {
                    if (discordChannelId != null) {
                        if (updateAnnounceChannel.length() > 0) {
                            updateAnnounceChannel.append(",");
                        }
                        updateAnnounceChannel.append(String.format("('%s','%s')",
                                event.getGuild().getId(),
                                chanId));
                    } else {
                        if (deleteChannelData.length() > 0) {
                            deleteChannelData.append(",");
                        }
                        deleteChannelData.append(String.format("('%s','%s')",
                                event.getGuild().getId(),
                                chanId));
                        deletedChannels.addIfAbsent(chan);
                    }
                }
            });
        }
    }

    private synchronized void channelQueryCalls() {
        String query;
        TwitchData twitchData = new TwitchData();

        if (insertChannelData.length() > 0) {
            if (discordChannelId != null && gameFilter != null && titleFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `gameFilter`, `titleFilter`) VALUES %s",
                        insertChannelData);
            } else if (discordChannelId != null && gameFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `gameFilter`) VALUES %s",
                        insertChannelData);
            } else if (discordChannelId != null && titleFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `titleFilter`) VALUES %s",
                        insertChannelData);
            } else if (discordChannelId != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`) VALUES %s",
                        insertChannelData);
            } else if (gameFilter != null && titleFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `gameFilter`, `titleFilter`) VALUES %s",
                        insertChannelData);
            } else if (gameFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `gameFilter`) VALUES %s",
                        insertChannelData);
            } else if (titleFilter != null) {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`, `titleFilter`) VALUES %s",
                        insertChannelData);
            } else {
                query = String.format("INSERT INTO `twitch` (`guildId`, `channelName`, `channelId`, `announceChannel`) VALUES %s",
                        insertChannelData);
            }

            if (twitchData.action(query)) {
                message.append(String.format("\n# Added channels: %s.",
                        addedChannels.toString().replaceAll("[\\[\\]]", "")));

                if (discordChannelId != null) {
                    message.append(String.format("\n# They will be announced in: #%s.",
                            Main.getJDA().getTextChannelById(discordChannelId).getName()));
                } else {
                    message.append(String.format("\n# They will be announced in: #%s.",
                            Main.getJDA().getTextChannelById(globalAnnounceChannelId).getName()));
                }

                if (gameFilter != null && gameFilter.size() > 0) {
                    message.append(String.format("\n# They will only be announced when they are playing: %s.",
                            gameFilter.toString().replaceAll("[\\[\\]]", "")));
                }

                if (titleFilter != null && titleFilter != null) {
                    message.append(String.format("\n# They will only be announced when these words are in the title: %s.",
                            titleFilter.toString().replaceAll("[\\[\\]]", "")));
                }
            } else {
                message.append(String.format("\n# Failed to add channels: %s.",
                        addedChannels.toString().replaceAll("[\\[\\]]", "")));
            }
            insertChannelData.setLength(0);
            addedChannels.clear();
        }

        if (updateAnnounceChannel != null && updateAnnounceChannel.length() > 0) {
            query = String.format("UPDATE `twitch` SET `announceChannel` = '%s' WHERE (`guildId`,`channelId`) IN (%s)",
                    discordChannelId,
                    updateAnnounceChannel);

            if (twitchData.action(query)) {
                message.append(String.format("\n# Updated the Twitch announcement channel for %s to: %s.",
                        channel.values().toString().replaceAll("[\\[\\]]", ""),
                        discordChannelName));
            } else {
                message.append(String.format("\n! Failed to change the announce channel for %s to: %s.",
                        channel.values().toString().replaceAll("[\\[\\]]", ""),
                        discordChannelName));
            }
            updateAnnounceChannel = new StringBuilder();
        }

        if (deleteChannelData.length() > 0) {
            query = String.format("DELETE FROM `twitch` WHERE (`guildId`,`channelId`) IN (%s)",
                    deleteChannelData);

            if (twitchData.action(query)) {
                message.append(String.format("\n# Removed channels: %s.",
                        deletedChannels.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n! Failed to delete channels: %s.",
                        deletedChannels.toString().replaceAll("[\\[\\]]", "")));
            }
            deleteChannelData.setLength(0);
            deletedChannels.clear();
        }
        // Reset everything
        channel = new ConcurrentHashMap<>();
        discordChannelId = null;
        globalAnnounceChannelId = null;
        gameFilter = new CopyOnWriteArrayList<>();
        titleFilter = new CopyOnWriteArrayList<>();
        insertChannelData = new StringBuilder();
        deleteChannelData = new StringBuilder();
    }

    private synchronized void gameFilterHandler(GuildMessageReceivedEvent event, String args) {
        findGameFilters(args);

        StringBuilder gFilterAddList = new StringBuilder();
        StringBuilder gFilterDeleteList = new StringBuilder();
        StringBuilder gFilterAddNameList = new StringBuilder();
        StringBuilder gFilterDeleteNameList = new StringBuilder();

        if (gameFilter != null && gameFilter.size() > 0) {
            gameFilter.forEach(gFilter -> {
                if (!CheckTwitchData.action("gameFilter", event.getGuild().getId(), gFilter)) {
                    if (gFilterAddList.length() > 0) {
                        gFilterAddList.append(",");
                        gFilterAddNameList.append(", ");
                    }
                    gFilterAddList.append(String.format("('%s','%s')",
                            event.getGuild().getId(),
                            gFilter));
                    gFilterAddNameList.append(gFilter);
                } else {
                    if (gFilterDeleteList.length() > 0) {
                        gFilterDeleteList.append(",");
                        gFilterDeleteNameList.append(", ");
                    }
                    gFilterDeleteList.append(String.format("('%s','%s')",
                            event.getGuild().getId(),
                            gFilter));
                    gFilterDeleteNameList.append(gFilter);
                }
            });
        }

        if (gFilterAddList.length() > 0) {
            TwitchData twitchData = new TwitchData();

            String query = String.format("INSERT INTO `twitch` (`guildId`, `gameFilter`) VALUES %s",
                    gFilterAddList.toString());

            if (twitchData.action(query)) {
                message.append(String.format("\n# Added game filter(s): %s.",
                        gFilterAddNameList.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n# Failed to add game filter(s): %s.",
                        gFilterAddNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }

        if (gFilterDeleteList.length() > 0) {
            TwitchData twitchData = new TwitchData();

            String query = String.format("DELETE FROM `twitch` WHERE (`guildId`, `gameFilter`) IN (%s)",
                    gFilterDeleteList.toString());

            if (twitchData.action(query)) {
                message.append(String.format("\n# Removed game filter(s): %s.",
                        gFilterDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n# Failed to remove game filter(s): %s.",
                        gFilterDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }
    }

    private synchronized void titleFilterHandler(GuildMessageReceivedEvent event, String args) {
        findTitleFilters(args);

        StringBuilder tFilterAddList = new StringBuilder();
        StringBuilder tFilterDeleteList = new StringBuilder();
        StringBuilder tFilterAddNameList = new StringBuilder();
        StringBuilder tFilterDeleteNameList = new StringBuilder();

        if (titleFilter != null && titleFilter.size() > 0) {
            titleFilter.forEach(tFilter -> {
                if (!CheckTwitchData.action("titleFilter", event.getGuild().getId(), tFilter)) {
                    if (tFilterAddList.length() > 0) {
                        tFilterAddList.append(",");
                        tFilterAddNameList.append(", ");
                    }
                    tFilterAddList.append(String.format("('%s','%s')",
                            event.getGuild().getId(),
                            tFilter));
                    tFilterAddNameList.append(tFilter);
                } else {
                    if (tFilterDeleteList.length() > 0) {
                        tFilterDeleteList.append(",");
                        tFilterDeleteNameList.append(", ");
                    }
                    tFilterDeleteList.append(String.format("('%s','%s')",
                            event.getGuild().getId(),
                            tFilter));
                    tFilterDeleteNameList.append(tFilter);
                }
            });
        }
        System.out.println(tFilterAddList);
        System.out.println(tFilterDeleteList);
        if (tFilterAddList.length() > 0) {
            TwitchData twitchData = new TwitchData();

            String query = String.format("INSERT INTO `twitch` (`guildId`, `titleFilter`) VALUES %s",
                    tFilterAddList.toString());

            if (twitchData.action(query)) {
                message.append(String.format("\n# Added title filter(s): %s.",
                        tFilterAddNameList.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n# Failed to add title filter(s): %s.",
                        tFilterAddNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }

        if (tFilterDeleteList.length() > 0) {
            TwitchData twitchData = new TwitchData();

            String query = String.format("DELETE FROM `twitch` WHERE (`guildId`, `titleFilter`) IN (%s)",
                    tFilterDeleteList.toString());

            if (twitchData.action(query)) {
                message.append(String.format("\n# Removed title filter(s): %s.",
                        tFilterDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            } else {
                message.append(String.format("\n# Failed to remove title filter(s): %s.",
                        tFilterDeleteNameList.toString().replaceAll("[\\[\\]]", "")));
            }
        }
    }

    private synchronized void findTwitchChannels(String args) {
        // Extract the channel name from the args
        TwitchController twitch = new TwitchController();
        channel = new ConcurrentHashMap<>();

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

        if (notFoundChannel != null && notFoundChannel.size() > 0) {
            String flattened = notFoundChannel.toString();
            String stripped = flattened.replaceAll("[\\[\\]]", "");
            message.append(String.format("\n# Channel(s) not found: %s.",
                    stripped));
            notFoundChannel = new CopyOnWriteArrayList<>();
        }
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
            if (checkValidDiscordChannel(event, discordChannels)) {
                event.getGuild().getTextChannelsByName(discordChannels, true).forEach(discordChannel -> {
                    this.discordChannelId = discordChannel.getId();
                    this.discordChannelName = discordChannels;
                });
            } else {
                message.append(String.format("\n* %s",
                        LocaleString.getString(event.getMessage().getGuild().getId(), "discordChannelNoExist")));
            }
        } else {
            GetGlobalAnnounceChannel globalAnnounceChannel = new GetGlobalAnnounceChannel();
            this.globalAnnounceChannelId = globalAnnounceChannel.fetch(event.getGuild().getId());
        }
    }

    private synchronized void findGameFilters(String args) {
        // Check for specific channel filter(s)
        if (args.indexOf("{") > 0 && args.indexOf("}") > args.indexOf("{")) {
            String gameFilters = args.substring(args.indexOf("{") + 1, args.indexOf("}", args.indexOf("{")));

            if (gameFilters.indexOf("|") > 0) {
                this.gameFilter = Arrays.stream(gameFilters.split("\\|")).collect(Collectors.toList());
            } else {
                gameFilter.add(gameFilters);
            }
        } else {
            this.gameFilter = null;
        }
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
