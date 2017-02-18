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

package platform.discord.controller;

import core.Main;
import langs.LocaleString;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordController {

    private static Connection connection;
    private static Connection nlConnection;
    private static Connection gciConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement gciStatement;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet gciResult;
    private static ResultSet nlResult;
    private static ResultSet result;
    private String guildIdMessageEvent;
    private String mentionedUsersId;

    public DiscordController(GuildMessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public DiscordController() {

    }

    public static void sendToChannel(GuildMessageReceivedEvent event, String message) {
        // if the bot doesn't have permissions to post in the channel (which shouldn't happen at this point, but is)
        try {
            if (message == null || "".equals(message)) {

                System.out.printf("[DEBUG:sendToChannel] [EMPTY MESSAGE] G:%s:%s TC:%s:%s M:%s%n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getChannel().getName(),
                        event.getChannel().getId(),
                        message);
            } else {
                // Try sending to the channel it was moved to
                event.getMessage().getChannel().sendMessage(message).queue(
                        success -> {
                            new DiscordLogger(message, event);
                            System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s] %s%n",
                                    event.getGuild().getName(),
                                    event.getGuild().getId(),
                                    event.getChannel().getName(),
                                    event.getChannel().getId(),
                                    success.getContent());
                        },
                        failure -> System.out.printf("[~ERROR~] Unable to send message to %s:%s %s:%s.  Trying public " +
                                        "channel.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId())

                );
            }

        } catch (PermissionException ex) {
            // Try sending to the default channel
            if (message != null) {
                event.getGuild().getPublicChannel().sendMessage(message).queue(
                        success -> {
                            new DiscordLogger(message, event);
                            System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s]: %s%n",
                                    event.getGuild().getName(),
                                    event.getGuild().getId(),
                                    event.getGuild().getPublicChannel().getName(),
                                    event.getGuild().getPublicChannel().getId(),
                                    success.getContent());
                        },
                        failure -> System.out.printf("[~ERROR~] Unable to send message to %s:%s, Public Channel: %s:%s.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId())
                );
            }
        }
    }

    public static void sendToPm(GuildMessageReceivedEvent event, Message message) {
        event.getAuthor().openPrivateChannel().queue(
                success ->
                        event.getAuthor().getPrivateChannel().sendMessage(message).queue(
                                sentMessage -> System.out.printf("[BOT -> PM] [%s:%s] [%s:%s]: %s%n",
                                        event.getGuild().getName(),
                                        event.getGuild().getId(),
                                        event.getAuthor().getName(),
                                        event.getAuthor().getId(),
                                        sentMessage.getContent())
                        ),
                failure ->
                        System.out.printf("[~ERROR~] Unable to send PM to %s:%s, Author: %s:%s.%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getAuthor().getName(),
                                event.getAuthor().getId())
        );
    }

    public static void sendToPm(PrivateMessageReceivedEvent event, Message message) {
        if (!event.getAuthor().isBot()) { // Prevents errors if a bot auto-sends PM's on join
            event.getAuthor().openPrivateChannel().queue(
                    success ->
                            event.getAuthor().getPrivateChannel().sendMessage(message).queue(
                                    sentPM -> System.out.printf("[BOT -> PM] [%s:%s]: %s%n",
                                            event.getAuthor().getName(),
                                            event.getAuthor().getId(),
                                            sentPM.getContent())
                            ),
                    failure ->
                            System.out.printf("[~ERROR~] Unable to send PM to author: %s:%s.%n",
                                    event.getAuthor().getName(),
                                    event.getAuthor().getId())
            );
        }
    }

    private static synchronized Integer checkCompact(String guildId) {
        try {
            query = "SELECT `isCompact` FROM `guild` WHERE `guildId` = ?";
            if (ccConnection == null || ccConnection.isClosed()) {
                ccConnection = Database.getInstance().getConnection();
            }
            pStatement = ccConnection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            if (result.next()) {
                return result.getInt("isCompact");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, ccConnection);
        }
        return -1;
    }

    private static synchronized String getChannelId(String guildId) {
        try {
            query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";

            if (gciConnection == null || gciConnection.isClosed()) {
                gciConnection = Database.getInstance().getConnection();
            }

            gciStatement = gciConnection.prepareStatement(query);
            gciStatement.setString(1, guildId);

            gciResult = gciStatement.executeQuery();
            while (gciResult.next()) {
                String channelId = gciResult.getString("channelId");
                if (!"".equals(channelId)) {
                    return channelId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(gciResult, gciStatement, gciConnection);
        }
        return "";
    }

    private static synchronized MessageBuilder notifyLevel(String guildId, MessageBuilder message) {
        try {
            query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
            if (nlConnection == null || nlConnection.isClosed()) {
                nlConnection = Database.getInstance().getConnection();
            }
            nlStatement = nlConnection.prepareStatement(query);

            nlStatement.setString(1, guildId);
            nlResult = nlStatement.executeQuery();

            // Not going to add these to the Lang files because they will eventually be tokenized for customization
            while (nlResult.next()) {
                switch (nlResult.getInt("level")) {
                    case 1: // User wants a @User mention
                        String userId = nlResult.getString("userId");
                        User user = Main.getJDA().getUserById(userId);
                        message.append("Hey ");
                        message.append(user);
                        message.append("! Check out this streamer that just went live!");
                        break;
                    case 2: // User wants @here mention
                        message.append("Hey ");
                        message.append(MessageBuilder.HERE_MENTION);
                        message.append("! Check out this streamer that just went live!");
                        break;
                    case 3: // User wants @everyone mention
                        message.append("Hey ");
                        message.append(MessageBuilder.EVERYONE_MENTION);
                        message.append("! Check out this streamer that just went live!");
                        break;
                    default:
                        // No mention
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(nlResult, nlStatement, nlConnection);
        }
        return message;
    }

    private synchronized Message buildEmbed(Map<String, String> streamData, String platform, String action) {
        String guildId = streamData.get("guildId");
        String displayName = streamData.get("channelDisplayName");
        String streamTitle = streamData.get("channelStatus");
        String url = streamData.get("channelUrl");
        String logo = streamData.get("channelLogo");
        String profileBanner = streamData.get("channelProfileBanner");
        String game = streamData.get("streamsGame");
        String followers = streamData.get("channelFollowers");
        String views = streamData.get("channelViews");

        EmbedBuilder eBuilder = new EmbedBuilder();

        float[] rgb;

        switch (platform) {
            case "twitch":
                rgb = Color.RGBtoHSB(100, 65, 165, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            case "beam":
                rgb = Color.RGBtoHSB(83, 109, 254, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            default:
                // Never should hit
                break;
        }

        switch (action) {
            case "new":
                eBuilder.setAuthor(displayName + LocaleString.getString(guildId, "nowStreamingEmbed"),
                        url, Const.BOT_LOGO);
                break;
            case "edit":
                eBuilder.setAuthor(displayName + LocaleString.getString(guildId, "offlineEmbed"),
                        url, Const.BOT_LOGO);
                break;
            default:
                break;
        }

        eBuilder.setTitle(url, url);

        eBuilder.addField(LocaleString.getString(guildId, "nowPlayingEmbed"), game, false);
        eBuilder.addField(LocaleString.getString(guildId, "streamTitleEmbed"), streamTitle, false);

        if (logo != null) {
            eBuilder.setThumbnail(logo);
        }

        if (checkCompact(guildId).equals(0)) {
            if (profileBanner != null) {
                eBuilder.setImage(profileBanner);
            }
            eBuilder.addField(LocaleString.getString(guildId, "followersEmbed"), followers, true);
            eBuilder.addField(LocaleString.getString(guildId, "totalViewsEmbed"), views, true);
        }

        MessageEmbed embed = eBuilder.build();
        MessageBuilder mBuilder = new MessageBuilder();

        notifyLevel(guildId, mBuilder);

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    private synchronized void unknownMessageHandler(Throwable error, Map<String, String> offline) {
        if (error instanceof ErrorResponseException) {
            ErrorResponseException ere = (ErrorResponseException) error;
            if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                System.out.printf("Discord reported unknown message. " +
                        "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                        .getTextChannelById(getChannelId(offline.get("guildId"))).getId(), offline.get("messageId"));
                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                deleteStream.process(offline.get("guildId"), offline.get("channelId"));
            } else {
                System.out.println("Got unexpected ErrorResponse!");
                System.out.println(ere.getErrorResponse().toString() + " " +
                        ": " + ere.getErrorResponse().getMeaning());
            }
        } else {
            System.err.println("got unexpected error");
            error.printStackTrace();
        }
    }

    public synchronized void offlineStream() {
        GetTwitchStreams getTwitchStreams = new GetTwitchStreams();
        HashMap<String, Map<String, String>> offlineStreams = getTwitchStreams.offline();

        offlineStreams.forEach((String channelId, Map<String, String> offline) -> {
            GetCleanUp clean = new GetCleanUp();
            Integer cleanup = clean.doStuff(offline.get("guildId"));
            if (offline.get("messageId") != null) {

                switch (cleanup) {
                    case 1: // Edit
                        if (offline.get("messageId") != null && Main.getJDA().getTextChannelById(getChannelId(offline.get("guildId"))) != null) {

                            Main.getJDA().getTextChannelById(getChannelId(offline.get("guildId")))
                                    .editMessageById(offline.get("messageId"), buildEmbed(offline, "twitch", "edit"))
                                    .queue(success -> {
                                                new DiscordLogger(" :pencil2: " +
                                                        offline.get("channelName") +
                                                        " has gone offline. Message edited in G:" +
                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName(), null);

                                                System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                "announcement was successfully edited in: %s%n",
                                                        offline.get("channelName"),
                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());

                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                deleteStream.process(offline.get("guildId"), channelId);
                                            },
                                            error -> unknownMessageHandler(error, offline));
                        } else {
                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                            deleteStream.process(offline.get("guildId"), channelId);
                        }
                        break;
                    case 2: // Delete
                        if (offline.get("messageId") != null) {
                            try {
                                Main.getJDA().getTextChannelById(getChannelId(offline.get("guildId"))).deleteMessageById(offline.get("messageId"))
                                        .queue(success -> {
                                                    new DiscordLogger(" :x: " + offline.get("channelName") + " has gone " +
                                                            "offline. Message deleted in G:" + Main.getJDA
                                                            ().getGuildById(offline.get("guildId")).getName(), null);
                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                    "announcement was successfully deleted in: %s%n",
                                                            offline.get("channelName"),
                                                            Main.getJDA().getGuildById(offline.get("guildId")).getName());

                                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                    deleteStream.process(offline.get("guildId"), channelId);
                                                },
                                                error -> unknownMessageHandler(error, offline));
                            } catch (NullPointerException npe) {
                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                deleteStream.process(offline.get("guildId"), channelId);
                            }
                        } else {
                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                            deleteStream.process(offline.get("guildId"), channelId);
                        }
                        break;
                    default:
                        DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                        deleteStream.process(offline.get("guildId"), channelId);
                        break;
                }
            } else {
                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                deleteStream.process(offline.get("guildId"), channelId);
            }
        });
    }

    private synchronized Boolean checkChannelExists(String textChannelId, String guildId) {
        // If the channel doesn't exist, reset it to the default public channel which is the guildId
        if (Main.getJDA().getTextChannelById(textChannelId) == null) {
            try {
                if (Main.getJDA().getTextChannelById(guildId) != null) {
                    try {
                        query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";

                        if (connection == null || connection.isClosed()) {
                            connection = Database.getInstance().getConnection();
                        }
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setString(2, guildId);
                        pStatement.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("~[ERROR] There was an SQL Exception");
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                    return true;
                } else {
                    System.out.println("~[ERROR] Unable to find a suitable channel to announce in");
                    return false;
                }
            } catch (NullPointerException npe) {
                System.out.println("~[ERROR] There was a NPE in DiscordController#checkChannelExists");
            }
        }
        return true;
    }

    public synchronized void announceChannel(String platform, String flag) {
        GetTwitchStreams twitchStreams = new GetTwitchStreams();
        HashMap<String, Map<String, String>> newStreams = twitchStreams.onlineStreams(flag);

        if (newStreams != null) {
            newStreams.values().forEach(streamData -> {

                Message message = buildEmbed(streamData, platform, "new");

                String searchColumn = "";
                switch (flag) {
                    case "channel":
                        searchColumn = streamData.get("channelId");
                        break;
                    case "game":
                        searchColumn = streamData.get("streamsGame");
                        break;
                    default:
                        break;
                }

                GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
                String announceChannel = getAnnounceChannel.action(streamData.get("guildId"), searchColumn, "twitch", flag);
                System.out.println("Announce Channel: " + announceChannel);

                if (announceChannel != null && !announceChannel.isEmpty() || !"".equals(announceChannel)) {
                    if (checkChannelExists(announceChannel, streamData.get("guildId"))) {
                        try {
                            CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                            if (!checkTwitchStreams.check(streamData.get("channelId"), streamData.get("guildId"))) {

                                Main.getJDA().getTextChannelById(announceChannel).sendMessage(message).queue(
                                        sentMessage -> {
                                            UpdateMessageId updateMessageId = new UpdateMessageId();
                                            updateMessageId.executeUpdate(streamData.get("guildId"), streamData.get("channelId"), sentMessage.getId());

                                            MessageBuilder discord = new MessageBuilder();

                                            discord.append(" :tada: ");
                                            discord.append("[G:");
                                            discord.append(Main.getJDA().getGuildById(streamData.get("guildId")).getName());
                                            discord.append("][C:");
                                            discord.append(Main.getJDA().getTextChannelById(announceChannel).getName());
                                            discord.append("]");
                                            discord.append(streamData.get("channelName"));
                                            discord.append(" is streaming ");
                                            discord.append(streamData.get("streamsGame"));

                                            Message dMessage = discord.build();

                                            new DiscordLogger(dMessage.getRawContent(), null);
                                            System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                                    Main.getJDA().getGuildById(streamData.get("guildId")).getName(),
                                                    Main.getJDA().getGuildById(streamData.get("guildId")).getId(),
                                                    Main.getJDA().getTextChannelById(getChannelId(streamData.get("guildId"))).getName(),
                                                    Main.getJDA().getTextChannelById(getChannelId(streamData.get("guildId"))).getId(),
                                                    sentMessage.getId(),
                                                    streamData.get("channelName") + " is streaming " + streamData.get("streamsGame"));

                                            new Tracker("Streams Announced");
                                        }
                                );
                            } else {
                                UpdateOffline updateOffline = new UpdateOffline();
                                updateOffline.executeUpdate(streamData.get("streamsId"));
                            }
                        } catch (PermissionException pe) {
                            new DiscordLogger(" :no_entry: Permission error sending in G:" + Main.getJDA
                                    ().getGuildById(streamData.get("guildId")).getName() + ":" + streamData.get("guildId"), null);
                            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                                    Main.getJDA().getGuildById(streamData.get("guildId")).getName(),
                                    streamData.get("guildId"),
                                    Main.getJDA().getTextChannelById(announceChannel).getName(),
                                    announceChannel);
                        }
                    } else {
                        DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                        deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                    }
                } else {
                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                    deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                }
            });
        }
    }


    private void mentionedUsersID(GuildMessageReceivedEvent event) {
        for (User u : event.getMessage().getMentionedUsers()) {
            this.mentionedUsersId = u.getId();
        }
    }

    public final String getMentionedUsersId() {
        return this.mentionedUsersId;
    }

    public final String getGuildId() {
        return this.guildIdMessageEvent;
    }
}
