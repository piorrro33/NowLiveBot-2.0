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
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.generic.controller.PlatformController.*;
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
        if (!event.getAuthor().isBot()) { // Prevents errors if a bot auto-sends PM's on
            // join
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


    public static synchronized void offlineStream(String guildId, Integer platformId, String channelName,
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

    }

    private static synchronized Message editMessage(Integer platformId, String channelName) {
        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder msgDesc = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

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

        eBuilder.setAuthor(Const.OFFLINE, Const.DISCORD_URL, Const.BOT_LOGO);

        eBuilder.setTitle(channelName + " has gone offline!");

        msgDesc.append("Make sure to watch for my next announcement of when they go live!");
        //msgDesc.append(url);

        eBuilder.setDescription(msgDesc.toString());

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    //public static synchronized void announceStream(String guildId, String channelId, Integer platformId, String
    //        channelName, String streamTitle, String gameName, String url, String thumbnail, String banner) {
    public static synchronized void announceStream(String guildId, String channelId, Integer platformId, Stream stream) {
        //System.out.println("Made into the announcement method...");

        EmbedBuilder eBuilder = new EmbedBuilder();
        StringBuilder msgDesc = new StringBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        notifyLevel(guildId, mBuilder);
        //System.out.println("Notify level checked...");
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

        eBuilder.setAuthor(stream.getChannel().getDisplayName() + " is now streaming!",
                stream.getChannel().getUrl(), Const.BOT_LOGO);

        eBuilder.setTitle(stream.getChannel().getUrl());

        eBuilder.setDescription(msgDesc.toString());
        if (stream.getChannel().getLogo() != null) {
            eBuilder.setThumbnail(stream.getChannel().getLogo());
        }

        if (checkCompact(guildId).equals(0)) {
            if (stream.getPreview().getLarge() != null) {
                eBuilder.setImage(stream.getPreview().getLarge());
            }
        }

        eBuilder.addField("Now Playing", stream.getGame(), false);
        eBuilder.addField("Stream Title", stream.getChannel().getStatus(), false);
        eBuilder.addField("Followers", String.valueOf(stream.getChannel().getFollowers()), true);
        eBuilder.addField("Total Views", String.valueOf(stream.getChannel().getViews()), true);

        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        Message message = mBuilder.build();

        // If the channel doesn't exist, reset it to the default public channel which is the guildId
        if (Main.getJDA().getTextChannelById(channelId) == null) {
            try {
                if (Main.getJDA().getTextChannelById(guildId) != null) {
                    try {
                        connection = Database.getInstance().getConnection();
                        query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
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

        if (!checkStreamTable(guildId, platformId, stream.getChannel().getName())) {
            try {
                Main.getJDA().getTextChannelById(channelId).sendMessage(message).queue(
                        sentMessage -> {

                            addToStream(guildId, channelId, platformId, stream.getChannel().getName(), stream.getChannel().getStatus(), stream.getGame(), sentMessage
                                    .getId());

                            // TODO: Fix this ugly mess!!
                            MessageBuilder discord = new MessageBuilder();

                            discord.append(" :tada: ");
                            discord.append("[G:");
                            discord.append(Main.getJDA().getGuildById(guildId).getName());
                            discord.append("][C:");
                            discord.append(Main.getJDA().getTextChannelById(channelId).getName());
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
                        Main.getJDA().getTextChannelById(channelId).getName(),
                        channelId);
            }
        }
    }

    private static synchronized Integer checkCompact(String guildId) {
        try {
            ccConnection = Database.getInstance().getConnection();
            query = "SELECT `isCompact` FROM `guild` WHERE `guildId` = ?";
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
            ceConnection = Database.getInstance().getConnection();
            query = "SELECT `emoji` FROM `guild` WHERE `guildId` = ?";
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
            gciConnection = Database.getInstance().getConnection();
            query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";
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
            nlConnection = Database.getInstance().getConnection();
            query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
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
