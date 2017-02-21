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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordController {

    private static Connection nlConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement pStatement;
    private static String query;
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

    private synchronized void unknownMessageHandler(Throwable error, Map<String, String> data) {
        GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
        String textChannelId = getAnnounceChannel.action(data.get("guildId"));

        if (error instanceof ErrorResponseException) {
            ErrorResponseException ere = (ErrorResponseException) error;
            if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                System.out.printf("Discord reported unknown message. " +
                        "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                        .getTextChannelById(textChannelId).getId(), data.get("messageId"));
                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                deleteStream.process(data.get("guildId"), data.get("channelId"));
            } else if (ere.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
                badPermsHandler(textChannelId, data);
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

    private synchronized Boolean checkPerms(Map<String, String> data) {
        String guildId = data.get("guildId");

        GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
        String textChannelId = getAnnounceChannel.action(guildId);

        TextChannel channel = Main.getJDA().getTextChannelById(textChannelId);
        Guild guild = Main.getJDA().getGuildById(guildId);
        Member selfMember = guild.getSelfMember();

        if (channel != null) {
            if (selfMember.hasPermission(channel, Permission.ADMINISTRATOR)) {
                return true;
            } else if (selfMember.hasPermission(channel, Permission.MESSAGE_READ)
                    && selfMember.hasPermission(channel, Permission.MESSAGE_WRITE)
                    && selfMember.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void badPermsHandler(String textChannel, Map<String, String> data) {
        if (textChannel != null && Main.getJDA().getTextChannelById(textChannel) != null) {
            String guildId = data.get("guildId");

            new DiscordLogger(" :no_entry: Permission error in G:"
                    + Main.getJDA().getGuildById(guildId).getName() + ":" + guildId, null);

            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s GO:%s#%s:%s%n",
                    Main.getJDA().getGuildById(guildId).getName(),
                    guildId,
                    Main.getJDA().getTextChannelById(textChannel).getName(),
                    textChannel,
                    Main.getJDA().getGuildById(guildId).getOwner().getUser().getName(),
                    Main.getJDA().getGuildById(guildId).getOwner().getUser().getDiscriminator(),
                    Main.getJDA().getGuildById(guildId).getOwner().getUser().getId());

            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
            deleteStream.process(guildId, data.get("channelId"));
        }
    }

    public synchronized void offlineStream() {
        GetTwitchStreams getTwitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> offlineStreams = getTwitchStreams.offline();

        if (offlineStreams != null && offlineStreams.size() > 0) {
            offlineStreams.forEach(
                    (String channelId, Map<String, String> offline) -> {
                        GetCleanUp clean = new GetCleanUp();
                        Integer cleanup = clean.doStuff(offline.get("guildId"));

                        GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
                        String textChannelId = getAnnounceChannel.action(offline.get("guildId"));

                        // Check to make sure the bot has all the necessary permissions in the channel it's going to output to
                        if (offline.get("messageId") != null) {
                            if (checkPerms(offline)) {

                                switch (cleanup) {
                                    case 1: // Edit
                                        if (Main.getJDA().getTextChannelById(textChannelId) != null) {

                                            Main.getJDA().getTextChannelById(textChannelId)
                                                    .editMessageById(offline.get("messageId"), buildEmbed(offline, "twitch", "edit"))
                                                    .queue(
                                                            success -> {
                                                                String loggerMessage = String.format(
                                                                        " :pencil2: %s has gone offline. Message edited in G:%s",
                                                                        offline.get("channelName"),
                                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName()
                                                                );
                                                                new DiscordLogger(loggerMessage, null);

                                                                System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                                "announcement was successfully edited in: %s%n",
                                                                        offline.get("channelName"),
                                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());

                                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                                deleteStream.process(offline.get("guildId"), channelId);
                                                            },
                                                            error -> unknownMessageHandler(error, offline));
                                        } else {
                                            System.out.println("[~ERROR~] Text Channel doesn't exist. Deleting stream from the database.");
                                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                            deleteStream.process(offline.get("guildId"), channelId);
                                        }
                                        break;
                                    case 2: // Delete
                                        if (Main.getJDA().getTextChannelById(textChannelId) != null) {
                                            Main.getJDA().getTextChannelById(textChannelId).deleteMessageById(offline.get("messageId"))
                                                    .queue(
                                                            success -> {
                                                                String loggerMessage = String.format(
                                                                        " :x: %s has gone offline. Message deleted in G:%s",
                                                                        offline.get("channelName"),
                                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());
                                                                new DiscordLogger(loggerMessage, null);

                                                                System.out.printf(
                                                                        "[OFFLINE STREAM] %s has gone offline. The announcement " +
                                                                                "was successfully deleted in: %s%n",
                                                                        offline.get("channelName"),
                                                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());

                                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                                deleteStream.process(offline.get("guildId"), channelId);
                                                            },
                                                            error -> unknownMessageHandler(error, offline));
                                        } else {
                                            System.out.println("[~ERROR~] Text Channel doesn't exist. Deleting stream from the database.");
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
                                System.out.println("[~ERROR~] Permissions error. Deleting stream from the database.");
                                badPermsHandler(textChannelId, offline);

                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                deleteStream.process(offline.get("guildId"), channelId);
                            }
                        } else {
                            System.out.println("[~ERROR~] Message ID was null. Deleting stream from the database.");
                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                            deleteStream.process(offline.get("guildId"), channelId);
                        }
                    });
        }
    }

    public synchronized void announceChannel(String platform, String flag) {
        GetTwitchStreams twitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> newStreams = twitchStreams.onlineStreams(flag);

        if (newStreams != null) {
            newStreams.values().forEach(
                    streamData -> {
                        GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
                        String announceChannel = getAnnounceChannel.action(streamData.get("guildId"));

                        if (checkPerms(streamData)) {
                            Message message = buildEmbed(streamData, platform, "new");

                            if (announceChannel != null && !announceChannel.isEmpty() && !"".equals(announceChannel)) {
                                CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                                if (!checkTwitchStreams.check(streamData.get("channelId"), streamData.get("guildId"))) {

                                    if (Main.getJDA().getTextChannelById(announceChannel) != null && message != null) {
                                        Main.getJDA().getTextChannelById(announceChannel).sendMessage(message).queue(
                                                sentMessage -> {
                                                    UpdateMessageId updateMessageId = new UpdateMessageId();
                                                    updateMessageId.executeUpdate(streamData.get("guildId"), streamData.get("channelId"), sentMessage.getId());

                                                    String loggerMessage = String.format(
                                                            " :tada: [G:%s][TC:%s] %s is streaming %s.",
                                                            Main.getJDA().getGuildById(streamData.get("guildId")).getName(),
                                                            Main.getJDA().getTextChannelById(announceChannel).getName(),
                                                            streamData.get("channelName"),
                                                            streamData.get("streamsGame")
                                                    );
                                                    new DiscordLogger(loggerMessage, null);

                                                    System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                                            Main.getJDA().getGuildById(streamData.get("guildId")).getName(),
                                                            Main.getJDA().getGuildById(streamData.get("guildId")).getId(),
                                                            Main.getJDA().getTextChannelById(announceChannel).getName(),
                                                            Main.getJDA().getTextChannelById(announceChannel).getId(),
                                                            sentMessage.getId(),
                                                            streamData.get("channelName") + " is streaming " + streamData.get("streamsGame"));

                                                    switch (platform) {
                                                        case "twitch":
                                                            new Tracker("Twitch Streams");
                                                            break;
                                                    }
                                                },
                                                error -> unknownMessageHandler(error, streamData)
                                        );
                                    }
                                } else {
                                    UpdateOffline updateOffline = new UpdateOffline();
                                    updateOffline.executeUpdate(streamData.get("channelId"));
                                }
                            } else {
                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                            }
                        } else {
                            badPermsHandler(announceChannel, streamData);

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
}
