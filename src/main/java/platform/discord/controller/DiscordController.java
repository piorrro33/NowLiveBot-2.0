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

import com.mb3364.twitch.api.models.Stream;
import core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger("Discord Controller");
    private static Connection connection;
    private static Connection nlConnection;
    private static Connection mhConnection;
    private static Connection ceConnection;
    private static Connection gciConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement mhStatement;
    private static PreparedStatement ceStatement;
    private static PreparedStatement gciStatement;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet mhResult;
    private static ResultSet ceResult;
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
            }
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

        } catch (PermissionException ex) {
            // Try sending to the default channel
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

    /*public static synchronized void offlineStream(String guildId, Integer platformId, String channelName,
                                                  String channelId) {
        if (checkStreamTable(guildId, platformId, channelName)) {
            try {
                mhConnection = Database.getInstance().getConnection();
                query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";

                mhStatement = mhConnection.prepareStatement(query);

                mhStatement.setString(1, guildId);
                mhResult = mhStatement.executeQuery();

                while (mhResult.next()) {
                    String messageId = getMessageId(guildId, platformId, channelName);

                    // Grab the old message
                    if (messageId != null
                            && Main.getJDA().getTextChannelById(channelId) != null
                            && Main.getJDA().getTextChannelById(channelId).getMessageById(messageId) != null) {

                        RestAction<Message> oldMessage = Main.getJDA().getTextChannelById(channelId)
                                .getMessageById(messageId);

                        switch (mhResult.getInt("cleanup")) {
                            case 1: // Edit old message
                                oldMessage.queue(
                                        success -> {
                                            Message message = editMessage(platformId, channelName);

                                            // Update the old message
                                            try {
                                                success.editMessage(message).queue(
                                                        editSuccess -> {
                                                            // Remove the entry from the stream table
                                                            deleteFromStream(guildId, platformId, channelName);

                                                            new DiscordLogger(" :pencil2: " + channelName + " has gone " +
                                                                    "offline. Message edited in G:" + Main.getJDA
                                                                    ().getGuildById(guildId).getName(), null);
                                                            System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                            "announcement was successfully edited in: %s%n",
                                                                    channelName,
                                                                    Main.getJDA().getGuildById(guildId).getName());
                                                        }
                                                );
                                            } catch (UnsupportedOperationException ose) {
                                                System.out.printf("[~ERROR~] Message has no content.  Cannot " +
                                                                "send the edited message. G:%s:%s C:%s:%s, M:%s%n",
                                                        Main.getJDA().getGuildById(guildId).getName(),
                                                        guildId,
                                                        Main.getJDA().getTextChannelById(channelId).getName(),
                                                        channelId, messageId);
                                            }
                                        },
                                        error -> {
                                            if (error instanceof ErrorResponseException) {
                                                ErrorResponseException ere = (ErrorResponseException) error;
                                                if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                                                    System.out.printf("Discord reported unknown message. " +
                                                            "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                                                            .getTextChannelById(channelId).getId(), messageId);
                                                } else {
                                                    System.out.println("Got unexpected ErrorResponse!");
                                                    System.out.println(ere.getErrorResponse().toString() + " " +
                                                            ": " + ere.getErrorResponse().getMeaning());
                                                }
                                                deleteFromStream(guildId, platformId, channelName);
                                            } else {
                                                System.err.println("got unexpected error");
                                                error.printStackTrace();
                                            }
                                        });
                                break;
                            case 2: // Delete old message
                                oldMessage.queue(
                                        success -> {

                                            // Delete the old message
                                            success.deleteMessage().queue(
                                                    deleteSuccess -> {
                                                        // Remove the entry from the stream table
                                                        deleteFromStream(guildId, platformId, channelName);

                                                        new DiscordLogger(" :x: " + channelName + " has gone " +
                                                                "offline. Message deleted in G:" + Main.getJDA
                                                                ().getGuildById(guildId).getName(), null);
                                                        System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                        "announcement was successfully deleted from: %s%n",
                                                                channelName,
                                                                Main.getJDA().getGuildById(guildId).getName());
                                                    }
                                            );
                                        },
                                        error -> {
                                            if (error instanceof ErrorResponseException) {
                                                ErrorResponseException ere = (ErrorResponseException) error;
                                                if (ere.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                                                    System.out.printf("Discord reported unknown message. " +
                                                            "ChannelId: %s, MessageId: %s.\n", Main.getJDA()
                                                            .getTextChannelById(channelId).getId(), messageId);
                                                } else {
                                                    System.out.println("Got unexpected ErrorResponse!");
                                                    System.out.println(ere.getErrorResponse().toString() + " " +
                                                            ": " + ere.getErrorResponse().getMeaning());
                                                }
                                            } else {
                                                System.err.println("got unexpected error");
                                                error.printStackTrace();
                                            }
                                            new DiscordLogger(" :x: :x: " + channelName + " has gone " +
                                                    "offline. Error deleting message in G:" + Main.getJDA
                                                    ().getGuildById(guildId).getName(), null);
                                            System.out.printf("[~ERROR~] %s has gone offline. There was an " +
                                                            "error deleting it from guild: %s%n",
                                                    channelName,
                                                    Main.getJDA().getGuildById(guildId).getName());
                                            error.printStackTrace();
                                            deleteFromStream(guildId, platformId, channelName);
                                        });
                                break;
                            default:
                                // No action taken, but still need to remove the entry from the stream table
                                deleteFromStream(guildId, platformId, channelName);
                                break;
                        }
                    } else {
                        deleteFromStream(guildId, platformId, channelName);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (PermissionException pe) {
                new DiscordLogger(" :no_entry: Permissions error editing/deleting in G:" + Main.getJDA
                        ().getGuildById(guildId).getName() + ":" + Main.getJDA().getGuildById(guildId).getId(), null);
                System.out.printf("[~ERROR~] There was a permission exception when trying to read message " +
                                "history in G:%s, C:%s, M:%s%n",
                        Main.getJDA().getGuildById(guildId).getName(),
                        Main.getJDA().getTextChannelById(getChannelId(guildId)).getName(),
                        getMessageId(guildId, platformId, channelName));
            } catch (NullPointerException npe) {
                logger.error("There was a NPE when trying to edit/delete a message.",
                        npe.getMessage());
                npe.printStackTrace();
            } finally {
                cleanUp(mhResult, mhStatement, mhConnection);
            }
        }

    }*/

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

    private static synchronized MessageBuilder checkEmoji(String guildId, MessageBuilder message) {
        try {
            query = "SELECT `emoji` FROM `guild` WHERE `guildId` = ?";

            if (ceConnection == null || ceConnection.isClosed()) {
                ceConnection = Database.getInstance().getConnection();
            }

            ceStatement = ceConnection.prepareStatement(query);
            ceStatement.setString(1, guildId);
            ceResult = ceStatement.executeQuery();

            while (ceResult.next()) {
                if (ceResult.getString("emoji") != null) {
                    message.append(" ");
                    message.append(ceResult.getString("emoji"));
                    message.append(" ");
                    message.append(ceResult.getString("emoji"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(ceResult, ceStatement, ceConnection);
        }
        message.append("");
        return message;
    }

    public static synchronized String getChannelId(String guildId) {
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

            while (nlResult.next()) {
                switch (nlResult.getInt("level")) {
                    case 1: // User wants a @User mention
                        String userId = nlResult.getString("userId");
                        User user = Main.getJDA().getUserById(userId);
                        message.append("Hey ");
                        message.appendMention(user);
                        message.append("! Check out this streamer that just went live!");
                        break;
                    case 2: // User wants @here mention
                        message.append("Hey ");
                        message.appendHereMention();
                        message.append("! Check out this streamer that just went live!");
                        break;
                    case 3: // User wants @everyone mention
                        message.append("Hey ");
                        message.appendEveryoneMention();
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

    private synchronized Message editMessage(Map<String, String> stream) {
        return buildEmbed(stream, "edit");
    }

    private synchronized Message buildEmbed(Map<String, String> streamData, String action) {
        Integer platformId = 1;
        String guildId = streamData.get("guildId");
        String channelName = streamData.get("channelName");
        String displayName = streamData.get("channelDisplayName");
        String streamTitle = streamData.get("channelStatus");
        String url = streamData.get("channelUrl");
        String logo = streamData.get("channelLogo");
        String profileBanner = streamData.get("channelProfileBanner");
        String game = streamData.get("streamsGame");
        String followers = streamData.get("channelFollowers");
        String views = streamData.get("channelViews");

        EmbedBuilder eBuilder = new EmbedBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        notifyLevel(guildId, mBuilder);
        // TODO: re-enable once emoji command is enabled
        //checkEmoji(guildId, message);

        float[] rgb;

        switch (platformId) {
            case 1:
                rgb = Color.RGBtoHSB(100, 65, 165, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            case 2:
                rgb = Color.RGBtoHSB(83, 109, 254, null);
                eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));
                break;
            default:
                // Never should hit
                break;
        }

        switch (action) {
            case "new":
                eBuilder.setAuthor(displayName + " is now streaming!", url, Const.BOT_LOGO);
                break;
            case "edit":
                eBuilder.setAuthor(displayName + " has gone offline!", url, Const.BOT_LOGO);
                break;
            default:
                break;
        }

        eBuilder.setTitle(url);

        eBuilder.addField("Now Playing", game, false);
        eBuilder.addField("Stream Title", streamTitle, false);

        if (logo != null) {
            eBuilder.setThumbnail(logo);
        }

        if (checkCompact(guildId).equals(0)) {
            if (profileBanner != null) {
                eBuilder.setImage(profileBanner);
            }
            eBuilder.addField("Followers", followers, true);
            eBuilder.addField("Total Views", views, true);
        }


        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    public synchronized void offlineStream(Map<String, String> offline) {

        GetCleanUp clean = new GetCleanUp();
        Integer cleanup = clean.getCleanup(offline.get("guildId"));

        CheckStreamTable checkStreamTable = new CheckStreamTable();
        switch (cleanup) {
            case 1: // Edit
                if (checkStreamTable.check(offline.get("guildId"), Integer.parseInt(offline.get("platformId")), offline.get("channelName"))) {

                    DeleteFromStream deleteStream = new DeleteFromStream();
                    deleteStream.process(offline.get("guildId"), 1, offline.get("channelName"));

                    Main.getJDA()
                            .getTextChannelById(offline.get("textChannelId"))
                            .editMessageById(offline.get("messageId"), buildEmbed(offline, "edit"))
                            .queue(success -> {


                                new DiscordLogger(" :pencil2: " + offline.get("channelName") + " has gone " +
                                        "offline. Message edited in G:" + Main.getJDA().getGuildById(offline.get("guildId")).getName(), null);
                                System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                "announcement was successfully edited in: %s%n",
                                        offline.get("channelName"),
                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());
                            });
                }
                break;
            case 2: // Delete
                if (checkStreamTable.check(offline.get("guildId"), Integer.valueOf(offline.get("platformId")), offline.get("channelName"))) {

                    DeleteFromStream deleteStream = new DeleteFromStream();
                    deleteStream.process(offline.get("guildId"), 1, offline.get("channelName"));

                    Main.getJDA()
                            .getTextChannelById(offline.get("textChannelId"))
                            .deleteMessageById(offline.get("messageId"))
                            .queue(success -> {

                                new DiscordLogger(" :x: " + offline.get("channelName") + " has gone " +
                                        "offline. Message deleted in G:" + Main.getJDA
                                        ().getGuildById(offline.get("guildId")).getName(), null);
                                System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                "announcement was successfully deleted in: %s%n",
                                        offline.get("channelName"),
                                        Main.getJDA().getGuildById(offline.get("guildId")).getName());
                            });
                }
                break;
            default: // Do nothing

                break;
        }
    }

    public synchronized void announceStream(String guildId, String textChannelId, Integer platformId, Stream stream) {

        HashMap<String, String> streamData = new HashMap<>();
        streamData.put("guildId", guildId);
        streamData.put("textChannelId", textChannelId);
        streamData.put("streamsGame", stream.getGame());
        streamData.put("streamsViewers", String.valueOf(stream.getViewers()));
        streamData.put("channelStatus", stream.getChannel().getStatus());
        streamData.put("channelDisplayName", stream.getChannel().getDisplayName());
        streamData.put("channelLanguage", stream.getChannel().getBroadcasterLanguage());
        streamData.put("channelId", String.valueOf(stream.getChannel().getId()));
        streamData.put("channelName", stream.getChannel().getName());
        streamData.put("channelLogo", stream.getChannel().getLogo());
        streamData.put("channelProfileBanner", stream.getChannel().getProfileBanner());
        streamData.put("channelUrl", stream.getChannel().getUrl());
        streamData.put("channelViews", String.valueOf(stream.getChannel().getViews()));
        streamData.put("channelFollowers", String.valueOf(stream.getChannel().getFollowers()));

        Message message = buildEmbed(streamData, "new");

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
                        logger.error("There was an SQL Exception", e);
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                }
            } catch (NullPointerException npe) {
                logger.error("There was a NPE in Discord Controller");
            }
        }

        CheckStreamTable checkStreamTable = new CheckStreamTable();
        if (!checkStreamTable.check(guildId, platformId, streamData.get("channelName"))) {
            AddToStream addLiveStream = new AddToStream();
            addLiveStream.process(guildId, textChannelId, platformId, stream);
            try {
                Main.getJDA().getTextChannelById(textChannelId).sendMessage(message).queue(
                        sentMessage -> {
                            UpdateMessageId updateMessageId = new UpdateMessageId();
                            updateMessageId.executeUpdate(guildId, platformId, streamData.get("channelName"), sentMessage.getId());

                            // TODO: Fix this ugly mess!!
                            MessageBuilder discord = new MessageBuilder();

                            discord.append(" :tada: ");
                            discord.append("[G:");
                            discord.append(Main.getJDA().getGuildById(guildId).getName());
                            discord.append("][C:");
                            discord.append(Main.getJDA().getTextChannelById(textChannelId).getName());
                            discord.append("]");
                            discord.append(stream.getChannel().getName());
                            discord.append(" is streaming ");
                            discord.append(stream.getGame());

                            Message dMessage = discord.build();

                            new DiscordLogger(dMessage.getRawContent(), null);
                            System.out.printf("[STREAM ANNOUNCE] [%s:%s] [%s:%s] [%s]: %s%n",
                                    Main.getJDA().getGuildById(guildId).getName(),
                                    Main.getJDA().getGuildById(guildId).getId(),
                                    Main.getJDA().getTextChannelById(getChannelId(guildId)).getName(),
                                    Main.getJDA().getTextChannelById(getChannelId(guildId)).getId(),
                                    sentMessage.getId(),
                                    stream.getChannel().getName() + " is streaming " + stream.getGame());

                            new Tracker("Streams Announced");
                        }
                );
            } catch (PermissionException pe) {
                new DiscordLogger(" :no_entry: Permission error sending in G:" + Main.getJDA
                        ().getGuildById(guildId).getName() + ":" + Main.getJDA().getGuildById(guildId).getId(), null);
                System.out.printf("[~ERROR~] Permission Exception! G:%s:%s C:%s:%s%n",
                        Main.getJDA().getGuildById(guildId).getName(),
                        guildId,
                        Main.getJDA().getTextChannelById(textChannelId).getName(),
                        textChannelId);
            }
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
