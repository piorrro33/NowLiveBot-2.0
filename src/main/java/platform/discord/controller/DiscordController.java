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
import net.dv8tion.jda.client.exceptions.VerificationLevelException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
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
import util.ExceptionHandlerNoRestart;
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
    private String mentionedUsersId;
    private StringBuilder announced = new StringBuilder();
    private StringBuilder edited = new StringBuilder();
    private StringBuilder deleted = new StringBuilder();
    private StringBuilder permsRead = new StringBuilder();
    private StringBuilder permsWrite = new StringBuilder();
    private StringBuilder permsManageMessages = new StringBuilder();
    private StringBuilder permsEmbeds = new StringBuilder();
    private StringBuilder permsEveryone = new StringBuilder();

    public DiscordController(GuildMessageReceivedEvent event) {
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandlerNoRestart());
        mentionedUsersID(event);
    }

    public DiscordController() {
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandlerNoRestart());
    }

    public static void sendToChannel(GuildMessageReceivedEvent event, String message) {
        // if the bot doesn't have permissions to post in the channel (which shouldn't happen at this point, but is)
        if (event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) {
            event.getMessage().getChannel().sendMessage(message).queue(success -> {
                        new DiscordLogger(message, event);
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
                                sentMessage -> System.out.printf("[BOT -> PM] [%s:%s] [%s:%s]%n",
                                        event.getGuild().getName(),
                                        event.getGuild().getId(),
                                        event.getAuthor().getName(),
                                        event.getAuthor().getId())
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
                                    sentPM -> System.out.printf("[BOT -> PM] [%s:%s]%n",
                                            event.getAuthor().getName(),
                                            event.getAuthor().getId())
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

    private synchronized MessageBuilder notifyLevel(String textChannel, Map<String, String> data, MessageBuilder message) {
        try {
            query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
            if (nlConnection == null || nlConnection.isClosed()) {
                nlConnection = Database.getInstance().getConnection();
            }
            nlStatement = nlConnection.prepareStatement(query);

            nlStatement.setString(1, data.get("guildId"));
            nlResult = nlStatement.executeQuery();

            // Not going to add these to the Lang files because they will eventually be tokenized for customization
            while (nlResult.next()) {
                switch (nlResult.getInt("level")) {
                    case 1: // User wants a @User mention
                        String userId = nlResult.getString("userId");
                        User user = Main.getJDA().getUserById(userId);
                        message.append(user.getAsMention());
                        message.append(String.format(" " + LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                data.get("channelDisplayName"),
                                data.get("channelUrl")));
                        break;
                    case 2: // User wants @here mention
                        if (Main.getJDA().getGuildById(data.get("guildId")).getSelfMember().hasPermission(Main.getJDA().getTextChannelById(textChannel), Permission.MESSAGE_MENTION_EVERYONE)) {
                            message.append(MessageBuilder.HERE_MENTION);
                            message.append(String.format(" " + LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                    data.get("channelDisplayName"),
                                    data.get("channelUrl")));
                        } else {
                            permsEveryoneBuilder(data, Main.getJDA());
                        }
                        break;
                    case 3: // User wants @everyone mention
                        if (Main.getJDA().getGuildById(data.get("guildId")).getSelfMember().hasPermission(Main.getJDA().getTextChannelById(textChannel), Permission.MESSAGE_MENTION_EVERYONE)) {
                            message.append(MessageBuilder.EVERYONE_MENTION);
                            message.append(String.format(" " + LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                    data.get("channelDisplayName"),
                                    data.get("channelUrl")));
                        } else {
                            permsEveryoneBuilder(data, Main.getJDA());
                        }
                        break;
                    default:
                        message.append(String.format(LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                data.get("channelDisplayName"),
                                data.get("channelUrl")));
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

    public StringBuilder getPermsEveryone() {
        return permsEveryone;
    }

    public void setPermsEveryone(StringBuilder permsEveryone) {
        this.permsEveryone = permsEveryone;
    }

    public StringBuilder getPermsRead() {
        return permsRead;
    }

    public void setPermsRead(StringBuilder permsRead) {
        this.permsRead = permsRead;
    }

    public StringBuilder getPermsWrite() {
        return permsWrite;
    }

    public void setPermsWrite(StringBuilder permsWrite) {
        this.permsWrite = permsWrite;
    }

    public StringBuilder getPermsManageMessages() {
        return permsManageMessages;
    }

    public void setPermsManageMessages(StringBuilder permsManageMessages) {
        this.permsManageMessages = permsManageMessages;
    }

    public StringBuilder getPermsEmbeds() {
        return permsEmbeds;
    }

    public void setPermsEmbeds(StringBuilder permsEmbeds) {
        this.permsEmbeds = permsEmbeds;
    }

    public StringBuilder getAnnounced() {
        return announced;
    }

    public void setAnnounced(StringBuilder announced) {
        this.announced = announced;
    }

    public StringBuilder getEdited() {
        return edited;
    }

    public void setEdited(StringBuilder edited) {
        this.edited = edited;
    }

    public StringBuilder getDeleted() {
        return deleted;
    }

    public void setDeleted(StringBuilder deleted) {
        this.deleted = deleted;
    }

    private synchronized Message buildEmbed(String textChannel, Map<String, String> streamData, String platform, String action) {
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

        if (streamTitle.length() > 300) {
            streamTitle = streamTitle.substring(0, 299);
        }
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

        notifyLevel(textChannel, streamData, mBuilder);

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    private synchronized void unknownMessageHandler(ErrorResponseException ere, Map<String, String> data) {

        if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
            System.out.printf("Discord reported unknown message. " +
                    "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                    .getTextChannelById(data.get("textChannelId")).getId(), data.get("messageId"));
        } else if (ere.getErrorResponse() == ErrorResponse.MISSING_ACCESS) {
            badPermsHandler(data.get("textChannelId"), data);
        } else {
            System.out.println("Got unexpected ErrorResponse!");
            System.out.println(ere.getErrorResponse().toString() + " " +
                    ": " + ere.getErrorResponse().getMeaning());
        }
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

    private synchronized void permsReadBuilder(Map<String, String> data, JDA jda) {
        StringBuilder permsReadBuilder = new StringBuilder();
        permsReadBuilder.append(jda.getGuildById(data.get("guildId")).getName());

        if (permsRead.indexOf(permsReadBuilder.toString()) == -1) {
            if (permsRead.length() > 0) {
                permsRead.append(", ");
            }
            permsRead.append(permsReadBuilder.toString());
        }
    }

    private synchronized void permsEveryoneBuilder(Map<String, String> data, JDA jda) {
        StringBuilder permsEveryoneBuilder = new StringBuilder();
        permsEveryoneBuilder.append(jda.getGuildById(data.get("guildId")).getName());

        if (permsEveryone.indexOf(permsEveryoneBuilder.toString()) == -1) {
            if (permsEveryone.length() > 0) {
                permsEveryone.append(", ");
            }
            permsEveryone.append(permsEveryoneBuilder.toString());
        }
    }

    private synchronized void permsManageBuilder(Map<String, String> data, JDA jda) {
        StringBuilder permsManageBuilder = new StringBuilder();
        permsManageBuilder.append(jda.getGuildById(data.get("guildId")).getName());

        if (permsManageMessages.indexOf(permsManageBuilder.toString()) == -1) {
            if (permsManageMessages.length() > 0) {
                permsManageMessages.append(", ");
            }
            permsManageMessages.append(permsManageBuilder.toString());
        }
    }

    private synchronized void permsWriteBuilder(Map<String, String> data, JDA jda) {
        StringBuilder permsWriteBuilder = new StringBuilder();
        permsWriteBuilder.append(jda.getGuildById(data.get("guildId")).getName());

        if (permsWrite.indexOf(permsWriteBuilder.toString()) == -1) {
            if (permsWrite.length() > 0) {
                permsWrite.append(", ");
            }
            permsWrite.append(permsWriteBuilder.toString());
        }
    }

    private synchronized void permsEmbedBuilder(Map<String, String> data, JDA jda) {
        StringBuilder permsEmbedBuilder = new StringBuilder();
        permsEmbedBuilder.append(jda.getGuildById(data.get("guildId")).getName());

        if (permsEmbeds.indexOf(permsEmbedBuilder.toString()) == -1) {
            if (permsEmbeds.length() > 0) {
                permsEmbeds.append(", ");
            }
            permsEmbeds.append(permsEmbedBuilder.toString());
        }
    }

    public synchronized void offlineStream() {
        JDA jda = Main.getJDA();

        GetTwitchStreams getTwitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> offlineStreams = getTwitchStreams.offline();

        if (offlineStreams != null && offlineStreams.size() > 0) {

            offlineStreams.forEach((String id, Map<String, String> offline) -> {

                String guildId = offline.get("guildId");
                Member member = jda.getGuildById(guildId).getSelfMember();

                String textChannelId = offline.get("textChannelId");

                if (offline.get("messageId") != null && !offline.get("messageId").isEmpty() && !"".equals(offline.get("messageId"))) {
                    if (textChannelId != null && !textChannelId.isEmpty() && !"".equals(textChannelId)) {
                        if (jda.getTextChannelById(textChannelId) != null) {

                            GetCleanUp clean = new GetCleanUp();
                            Integer cleanup = clean.doStuff(guildId);

                            switch (cleanup) {
                                case 1: // Edit
                                    if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_READ)) {
                                        if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_MANAGE)) {

                                            Message newEmbed = buildEmbed(textChannelId, offline, "twitch", "edit");

                                            try {
                                                jda.getTextChannelById(textChannelId).editMessageById(offline.get("messageId"), newEmbed).complete();

                                                StringBuilder editStream = new StringBuilder();
                                                editStream.append(jda.getGuildById(offline.get("guildId")).getName()).append(":");
                                                editStream.append(offline.get("channelName")).append(":");
                                                editStream.append(offline.get("streamsGame"));

                                                if (edited.indexOf(editStream.toString()) == -1) {
                                                    if (edited.length() > 0) {
                                                        edited.append(", ");
                                                    }
                                                    edited.append(editStream);

                                                    if (edited.length() > 1800) {
                                                        String loggerBuilder = "```Markdown\n# Announcements Edited\n " + edited + "```";
                                                        new DiscordLogger(loggerBuilder, null);
                                                        edited = new StringBuilder();
                                                    }
                                                }

                                                System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                "announcement was successfully edited in: %s%n",
                                                        offline.get("channelName"),
                                                        jda.getGuildById(guildId).getName());
                                            } catch (ErrorResponseException ere) {
                                                unknownMessageHandler(ere, offline);
                                            } finally {
                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                deleteStream.process(guildId, offline.get("channelId"));
                                            }
                                        } else {
                                            permsManageBuilder(offline, jda);
                                        }
                                    } else {
                                        permsReadBuilder(offline, jda);
                                    }
                                    break;
                                case 2: // Delete
                                    if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_READ)) {
                                        if (member.hasPermission(jda.getTextChannelById(textChannelId), Permission.MESSAGE_MANAGE)) {

                                            try {
                                                jda.getTextChannelById(textChannelId).deleteMessageById(offline.get("messageId")).complete();

                                                StringBuilder deleteStreamBuilder = new StringBuilder();
                                                deleteStreamBuilder.append(jda.getGuildById(offline.get("guildId")).getName()).append(":");
                                                deleteStreamBuilder.append(offline.get("channelName")).append(":");
                                                deleteStreamBuilder.append(offline.get("streamsGame"));

                                                if (deleted.indexOf(deleteStreamBuilder.toString()) == -1) {
                                                    if (deleted.length() > 0) {
                                                        deleted.append(", ");
                                                    }
                                                    deleted.append(deleteStreamBuilder);

                                                    if (deleted.length() > 1800) {
                                                        String loggerBuilder = "```Markdown\n# Announcements Deleted\n " + deleted + "```";
                                                        new DiscordLogger(loggerBuilder, null);
                                                        deleted = new StringBuilder();
                                                    }
                                                }

                                                System.out.printf(
                                                        "[OFFLINE STREAM] %s has gone offline. The announcement " +
                                                                "was successfully deleted in: %s%n",
                                                        offline.get("channelName"),
                                                        jda.getGuildById(guildId).getName());

                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                deleteStream.process(guildId, offline.get("channelId"));
                                            } catch (ErrorResponseException ere) {
                                                unknownMessageHandler(ere, offline);
                                            } finally {
                                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                                deleteStream.process(guildId, offline.get("channelId"));
                                            }
                                        } else {
                                            permsManageBuilder(offline, jda);
                                        }
                                    } else {
                                        permsReadBuilder(offline, jda);
                                    }
                                    break;
                                default:
                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                    deleteStream.process(offline.get("guildId"), offline.get("channelId"));
                                    break;
                            }
                        } else {
                            System.out.println("[~ERROR~] Text Channel doesn't exist. Deleting stream from the database.");
                        }
                    } else {
                        System.out.println("[~ERROR~] Text Channel ID was null. Deleting stream from the database.");
                    }
                } else {
                    System.out.println("[~ERROR~] Message ID was null. Deleting stream from the database.");
                }

                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                deleteStream.process(guildId, offline.get("channelId"));
            });
        }
    }

    public synchronized void announceChannel(String platform) {
        JDA jda = Main.getJDA();

        GetTwitchStreams twitchStreams = new GetTwitchStreams();
        ConcurrentHashMap<String, Map<String, String>> newStreams = twitchStreams.onlineStreams();

        if (newStreams != null && newStreams.size() > 0) {
            newStreams.values().forEach(streamData -> {

                String announceChannel = streamData.get("textChannelId");

                Message message = buildEmbed(announceChannel, streamData, platform, "new");

                if (announceChannel != null && !announceChannel.isEmpty() && !"".equals(announceChannel)) {
                    // Check to ensure the bot is connected to the websocket
                    if (jda.getStatus() == JDA.Status.CONNECTED) {
                        if (jda.getTextChannelById(announceChannel) != null && message != null) {

                            if (jda.getGuildById(streamData.get("guildId")).getSelfMember().hasPermission(jda.getTextChannelById(announceChannel), Permission.MESSAGE_READ)) {
                                if (jda.getGuildById(streamData.get("guildId")).getSelfMember().hasPermission(jda.getTextChannelById(announceChannel), Permission.MESSAGE_WRITE)) {
                                    if (jda.getGuildById(streamData.get("guildId")).getSelfMember().hasPermission(jda.getTextChannelById(announceChannel), Permission.MESSAGE_EMBED_LINKS)) {
                                        if (streamData.get("messageId") == null && streamData.get("online").equals("1")) {
                                            // Send the message with blocking to ensure completion before moving on
                                            String messageId = null;

                                            try {
                                                messageId = jda.getTextChannelById(announceChannel).sendMessage(message).complete().getId();
                                            } catch (PermissionException pe) {
                                                System.out.println("Permissions Exception");
                                                pe.printStackTrace();
                                            } catch (VerificationLevelException vle) {
                                                System.out.println("Verification Level Exception Exception");
                                                vle.printStackTrace();
                                            } catch (IllegalArgumentException iae) {
                                                System.out.println("Runtime Exception");
                                                iae.printStackTrace();
                                            } catch (RuntimeException re) {
                                                System.out.println("Illegal Arguement Exception");
                                                re.printStackTrace();
                                            } finally {
                                                System.out.println(streamData);
                                            }

                                            if (messageId != null) {

                                                UpdateMessageId updateMessageId = new UpdateMessageId();
                                                updateMessageId.executeUpdate(streamData.get("guildId"), streamData.get("channelId"), messageId);

                                                StringBuilder currentStream = new StringBuilder();
                                                currentStream.append(jda.getGuildById(streamData.get("guildId")).getName()).append(":");
                                                currentStream.append(streamData.get("channelName")).append(":");
                                                currentStream.append(streamData.get("streamsGame"));

                                                if (announced.indexOf(currentStream.toString()) == -1) {
                                                    if (announced.length() > 0) {
                                                        announced.append(", ");
                                                    }
                                                    announced.append(currentStream);

                                                    if (announced.length() > 1800) {
                                                        String loggerBuilder = "```Markdown\n# Streams Announced\n " + announced.toString() + "```";
                                                        new DiscordLogger(loggerBuilder, null);
                                                        announced = new StringBuilder();
                                                    }
                                                }

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
                                        } else {
                                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                            deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                                        }
                                    } else {
                                        permsEmbedBuilder(streamData, jda);
                                        DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                        deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                                    }
                                } else {
                                    permsWriteBuilder(streamData, jda);
                                    DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                    deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                                }
                            } else {
                                permsReadBuilder(streamData, jda);
                                DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                                deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                            }
                        } else {
                            DeleteTwitchStream deleteStream = new DeleteTwitchStream();
                            deleteStream.process(streamData.get("guildId"), streamData.get("channelId"));
                        }
                    } else {
                        System.out.println("JDA is not connected.  Deleting stream and will try later.");
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
}
