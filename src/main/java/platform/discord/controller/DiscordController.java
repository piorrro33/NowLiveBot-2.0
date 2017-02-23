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
import net.dv8tion.jda.core.JDA;
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
import java.util.ArrayList;
import java.util.List;
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
    private String mentionedUsersId;

    public DiscordController(GuildMessageReceivedEvent event) {
        mentionedUsersID(event);
    }

    public DiscordController() {

    }

    public static void sendToChannel(GuildMessageReceivedEvent event, String message) {
        // if the bot doesn't have permissions to post in the channel (which shouldn't happen at this point, but is)
        if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
            event.getMessage().getChannel().sendMessage(message).queue(success -> {
                        new DiscordLogger(message, event);
                        System.out.printf("[BOT -> GUILD] [%s:%s] [%s:%s] %s%n",
                                event.getGuild().getName(),
                                event.getGuild().getId(),
                                event.getChannel().getName(),
                                event.getChannel().getId(),
                                success.getContent());
                    }, failure -> System.out.printf("[~ERROR~] Unable to send message to %s:%s %s:%s.  Trying public channel.%n",
                    event.getGuild().getName(),
                    event.getGuild().getId(),
                    event.getChannel().getName(),
                    event.getChannel().getId())

            );
        } else if (event.getGuild().getSelfMember().hasPermission(event.getGuild().getPublicChannel(), Permission.MESSAGE_WRITE)) {
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
        } else {
            MessageBuilder critFailure = new MessageBuilder();
            critFailure.append("Hi there!  It would seem as though I don't have permission to send messages in your Discord.  " +
                    "Please make sure that I can at the bare minimum send messages in your server if you want me to do my announcements.");
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

    private synchronized void unknownMessageHandler(ErrorResponseException ere, Map<String, String> data) {
        GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
        String textChannelId = getAnnounceChannel.action(data.get("guildId"));

        if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
            System.out.printf("Discord reported unknown message. " +
                    "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                    .getTextChannelById(textChannelId).getId(), data.get("messageId"));
        } else if (ere.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
            badPermsHandler(textChannelId, data);
        } else {
            System.out.println("Got unexpected ErrorResponse!");
            System.out.println(ere.getErrorResponse().toString() + " " +
                    ": " + ere.getErrorResponse().getMeaning());
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
        JDA jda = Main.getJDA();
        String guildId = data.get("guildId");

        if (textChannel != null && jda.getTextChannelById(textChannel) != null) {

            new DiscordLogger(" :no_entry: Permission error in G:"
                    + jda.getGuildById(guildId).getName() + ":" + guildId, null);

            System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s GO:%s#%s:%s%n",
                    jda.getGuildById(guildId).getName(),
                    guildId,
                    jda.getTextChannelById(textChannel).getName(),
                    textChannel,
                    jda.getGuildById(guildId).getOwner().getUser().getName(),
                    jda.getGuildById(guildId).getOwner().getUser().getDiscriminator(),
                    jda.getGuildById(guildId).getOwner().getUser().getId());
        }
    }

    public synchronized void offlineStream() {
        JDA jda = Main.getJDA();

        GetTwitchStreams getTwitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> offlineStreams = getTwitchStreams.offline();

        if (offlineStreams != null && offlineStreams.size() > 0) {
            System.out.println(offlineStreams.size());

            offlineStreams.values().forEach(offline -> {

                String guildId = offline.get("guildId");
                Member member = jda.getGuildById(guildId).getSelfMember();

                GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
                String textChannelId = getAnnounceChannel.action(guildId);

                if (checkPerms(offline)) {
                    if (offline.get("messageId") != null && !offline.get("messageId").isEmpty() && !"".equals(offline.get("messageId"))) {
                        if (textChannelId != null && !textChannelId.isEmpty() && !"".equals(textChannelId)) {
                            if (jda.getTextChannelById(textChannelId) != null) {
                                if (jda.getTextChannelById(textChannelId).getMessageById(offline.get("messageId")) != null) {

                                    GetCleanUp clean = new GetCleanUp();
                                    Integer cleanup = clean.doStuff(guildId);

                                    switch (cleanup) {
                                        case 1: // Edit
                                            Message newEmbed = buildEmbed(offline, "twitch", "edit");

                                            if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_READ)
                                                    && member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_MANAGE)) {

                                                try {
                                                    jda.getTextChannelById(textChannelId).editMessageById(offline.get("messageId"), newEmbed).complete();

                                                    String loggerMessage = String.format(
                                                            " :pencil2: %s has gone offline. Message edited in G:%s",
                                                            offline.get("channelName"),
                                                            jda.getGuildById(guildId).getName()
                                                    );
                                                    new DiscordLogger(loggerMessage, null);

                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                    "announcement was successfully edited in: %s%n",
                                                            offline.get("channelName"),
                                                            jda.getGuildById(guildId).getName());
                                                } catch (PermissionException pe) {
                                                    new DiscordLogger(" :no_entry: Missing Permission: MESSAGE_READ in G:"
                                                            + jda.getGuildById(guildId).getName() + ":" + guildId, null);
                                                } catch (ErrorResponseException ere) {
                                                    unknownMessageHandler(ere, offline);
                                                } finally {
                                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                    deleteStream.process(guildId, offline.get("channelId"));
                                                }
                                            } else {
                                                new DiscordLogger(" :no_entry: Missing Permissions: MESSAGE_READ & MESSAGE_MANAGE in G:"
                                                        + jda.getGuildById(guildId).getName() + ":" + guildId, null);
                                            }
                                            break;
                                        case 2: // Delete
                                            if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_READ)
                                                    && member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_MANAGE)) {

                                                try {
                                                    jda.getTextChannelById(textChannelId).deleteMessageById(offline.get("messageId")).complete();

                                                    String loggerMessage = String.format(
                                                            " :x: %s has gone offline. Message deleted in G:%s",
                                                            offline.get("channelName"),
                                                            jda.getGuildById(guildId).getName());
                                                    new DiscordLogger(loggerMessage, null);

                                                    System.out.printf(
                                                            "[OFFLINE STREAM] %s has gone offline. The announcement " +
                                                                    "was successfully deleted in: %s%n",
                                                            offline.get("channelName"),
                                                            jda.getGuildById(guildId).getName());

                                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                    deleteStream.process(guildId, offline.get("channelId"));
                                                } catch (PermissionException pe) {
                                                    new DiscordLogger(" :no_entry: Missing Permissions: MESSAGE_READ & MESSAGE_MANAGE in G:"
                                                            + jda.getGuildById(guildId).getName() + ":" + guildId, null);
                                                } catch (ErrorResponseException ere) {
                                                    unknownMessageHandler(ere, offline);
                                                } finally {
                                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                    deleteStream.process(guildId, offline.get("channelId"));
                                                }
                                            } else {
                                                new DiscordLogger(" :no_entry: Missing Permissions: MESSAGE_READ & MESSAGE_MANAGE in G:"
                                                        + jda.getGuildById(guildId).getName() + ":" + guildId, null);
                                            }
                                            break;
                                        default:
                                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                            deleteStream.process(offline.get("guildId"), offline.get("channelId"));
                                            break;
                                    }
                                } else {
                                    System.out.println("[~ERROR~] Message doesn't exist. Deleting stream from the database.");
                                }
                            } else {
                                System.out.println("[~ERROR~] Text Channel doesn't exist. Deleting stream from the database.");
                            }
                        }
                    } else {
                        System.out.println("[~ERROR~] Message ID was null. Deleting stream from the database.");
                    }
                } else {
                    System.out.println("[~ERROR~] Permissions error. Deleting stream from the database.");
                    badPermsHandler(textChannelId, offline);
                }
                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                deleteStream.process(guildId, offline.get("channelId"));
            });
        }
    }

    public synchronized void announceChannel(String platform, String flag) {
        JDA jda = Main.getJDA();

        GetTwitchStreams twitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> newStreams = twitchStreams.onlineStreams(flag);

        if (newStreams != null && newStreams.size() > 0) {
            newStreams.values().forEach(streamData -> {

                GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();
                String announceChannel = getAnnounceChannel.action(streamData.get("guildId"));

                if (checkPerms(streamData)) {
                    Message message = buildEmbed(streamData, platform, "new");

                    if (announceChannel != null && !announceChannel.isEmpty() && !"".equals(announceChannel)) {

                        CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                        if (!checkTwitchStreams.check(streamData.get("channelId"), streamData.get("guildId"))) {

                            // Check to ensure the bot is connected to the websocket
                            if (jda.getStatus() == JDA.Status.CONNECTED) {
                                if (jda.getTextChannelById(announceChannel) != null && message != null) {

                                    // Send the message with blocking to ensure completion before moving on
                                    String messageId = jda.getTextChannelById(announceChannel).sendMessage(message).complete().getId();

                                    if (messageId != null) {
                                        System.out.println(messageId);

                                        UpdateMessageId updateMessageId = new UpdateMessageId();
                                        updateMessageId.executeUpdate(streamData.get("guildId"), streamData.get("channelId"), messageId);

                                        String loggerMessage = String.format(
                                                " :tada: [G:%s][TC:%s] %s is streaming %s.",
                                                jda.getGuildById(streamData.get("guildId")).getName(),
                                                jda.getTextChannelById(announceChannel).getName(),
                                                streamData.get("channelName"),
                                                streamData.get("streamsGame")
                                        );
                                        new DiscordLogger(loggerMessage, null);

                                        System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                                jda.getGuildById(streamData.get("guildId")).getName(),
                                                jda.getGuildById(streamData.get("guildId")).getId(),
                                                jda.getTextChannelById(announceChannel).getName(),
                                                jda.getTextChannelById(announceChannel).getId(),
                                                messageId,
                                                streamData.get("channelName") + " is streaming " + streamData.get("streamsGame"));

                                        switch (platform) {
                                            case "twitch":
                                                new Tracker("Twitch Streams");
                                                break;
                                        }
                                    }
                                }
                            } else {
                                System.out.println("JDA is not connected.  Deleting stream and will try later.");
                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                            }
                        } else {
                            List<String> channelId = new ArrayList<>();
                            channelId.add(streamData.get("channelId"));

                            UpdateOffline updateOffline = new UpdateOffline();
                            updateOffline.executeUpdate(channelId);
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
