/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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
import util.database.Database;
import util.database.calls.Tracker;

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
    private static Connection gplConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement mhStatement;
    private static PreparedStatement ceStatement;
    private static PreparedStatement gciStatement;
    private static PreparedStatement gplStatement;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet gplResult;
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
            // Try sending to the channel it was moved to
            event.getMessage().getChannel().sendMessage(message).queue(
                    success -> System.out.printf("[BOT -> GUILD][%s:%s][%s:%s]: %s%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId(),
                            success.getContent()),
                    failure -> System.out.printf("[ERROR] Unable to send message to %s:%s %s:%s.  Trying public " +
                                    "channel.%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getChannel().getName(),
                            event.getChannel().getId())
            );

        } catch (PermissionException ex) {
            // Try sending to the default channel
            event.getGuild().getPublicChannel().sendMessage(message).queue(
                    success -> System.out.printf("[BOT -> GUILD][%s:%s][%s:%s]: %s%n",
                            event.getGuild().getName(),
                            event.getGuild().getId(),
                            event.getGuild().getPublicChannel().getName(),
                            event.getGuild().getPublicChannel().getId(),
                            success.getContent()),
                    failure -> System.out.printf("[ERROR] Unable to send message to %s:%s, Public Channel: %s:%s.%n",
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
                                sentMessage -> System.out.printf("[BOT -> PM][%s:%s][%s:%s]: %s%n",
                                        event.getGuild().getName(),
                                        event.getGuild().getId(),
                                        event.getAuthor().getName(),
                                        event.getAuthor().getId(),
                                        sentMessage.getContent())
                        ),
                failure -> System.out.printf("[ERROR] Unable to send PM to %s:%s, Author: %s:%s.%n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getAuthor().getName(),
                        event.getAuthor().getId())
        );
    }

    public static void sendToPm(PrivateMessageReceivedEvent event, Message message) {
        if (!event.getAuthor().isBot()) { // Prevents errors if a bot auto-sends PM's on join
            event.getAuthor().openPrivateChannel().queue(
                    success -> {
                        event.getAuthor().getPrivateChannel().sendMessage(message).queue(
                                sentPM -> System.out.printf("[BOT -> PM][%s:%s]: %s%n",
                                        event.getAuthor().getName(),
                                        event.getAuthor().getId(),
                                        sentPM.getContent())
                        );
                    },
                    failure -> System.out.printf("[ERROR] Unable to send PM to author: %s:%s.%n",
                            event.getAuthor().getName(),
                            event.getAuthor().getId())
            );
        }
    }

    public static synchronized void messageHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName, Integer online) {

        switch (online) {
            case 1: // Stream is online
                if (!checkStreamTable(guildId, platformId, channelName)) {
                    try {

                        announceStream(guildId, platformId, channelName, streamTitle, gameName);
                    } catch (Exception e) {
                        logger.error("There was an error announcing the stream.");
                        e.printStackTrace();
                    }
                }
                break;

            default: // Stream is offline
                if (checkStreamTable(guildId, platformId, channelName)) {
                    try {
                        mhConnection = Database.getInstance().getConnection();
                        query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";

                        mhStatement = mhConnection.prepareStatement(query);

                        mhStatement.setString(1, guildId);
                        mhResult = mhStatement.executeQuery();

                        while (mhResult.next()) {
                            String channelId = mhResult.getString("channelId");
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

                                                    // Replace the LIVE announcement with OFFLINE
                                                    String rawContent = success.getRawContent().replace(Const.NOW_LIVE,
                                                            Const.OFFLINE);

                                                    // Update the old message
                                                    success.editMessage(rawContent).queue();

                                                    // Remove the entry from the stream table
                                                    deleteFromStream(guildId, platformId, channelName);

                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                    "announcement was successfully edited in: %s%n",
                                                            channelName,
                                                            Main.getJDA().getGuildById(guildId).getName());
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
                                                });
                                        break;
                                    case 2: // Delete old message
                                        oldMessage.queue(
                                                success -> {

                                                    // Delete the old message
                                                    success.deleteMessage().queue();

                                                    // Remove the entry from the stream table
                                                    deleteFromStream(guildId, platformId, channelName);
                                                    System.out.printf("[OFFLINE STREAM] %s has gone offline. The " +
                                                                    "announcement was successfully deleted from: %s%n",
                                                            channelName,
                                                            Main.getJDA().getGuildById(guildId).getName());
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
                                                    System.out.printf("[ERROR] %s has gone offline. There was an " +
                                                                    "error deleting it from guild: %s%n",
                                                            channelName,
                                                            Main.getJDA().getGuildById(guildId).getName());
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
                        logger.error("There was a permission exception when trying to read message history.",
                                pe.getMessage());
                    } catch (NullPointerException npe) {
                        logger.error("There was a NPE when trying to edit/delete a message.",
                                npe.getMessage());
                        npe.printStackTrace();
                    } finally {
                        cleanUp(mhResult, mhStatement, mhConnection);
                    }
                }
                break;
        }

    }

    private static synchronized void announceStream(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        // Send the message to the appropriate channel
        String channelId = getChannelId(guildId);

        MessageBuilder message = new MessageBuilder();

        message.appendString("***" + Const.NOW_LIVE + "***\n");

        notifyLevel(guildId, message);

        if (checkCompact(guildId).equals(1)) {
            message.appendString("**" + channelName + "** is playing **" + gameName + "** at ");
            message.appendString("<" + getPlatformLink(platformId) + channelName + ">");
        } else {
            message.appendString("\t**" + channelName + "** is playing **" + gameName + "**!\n");
            message.appendString("\t\t*" + streamTitle + "*\n");
            message.appendString("\t\tWatch them here: " + getPlatformLink(platformId) + channelName);
        }
        // TODO: re-enable once emoji command is enabled
        //checkEmoji(guildId, message);

        // If the channel doesn't exist, reset it to the default public channel
        if (Main.getJDA().getTextChannelById(channelId) == null) {
            if (Main.getJDA().getGuildById(guildId).getPublicChannel() != null) {
                channelId = Main.getJDA().getGuildById(guildId).getPublicChannel().getId();
                try {
                    connection = Database.getInstance().getConnection();
                    query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, channelId);
                    pStatement.setString(2, guildId);
                    pStatement.executeUpdate();
                } catch (SQLException e) {
                    logger.error("There was an SQL Exception", e);
                } finally {
                    cleanUp(pStatement, connection);
                }
            }
        }

        Main.getJDA().getTextChannelById(channelId).sendMessage(message.build()).queue(
                sentMessage -> {
                    addToStream(guildId, platformId, channelName, streamTitle, gameName, sentMessage.getId());

                    System.out.printf("[STREAM ANNOUNCE][%s:%s][%s:%s][%s]: %s%n",
                            Main.getJDA().getGuildById(guildId).getName(),
                            Main.getJDA().getGuildById(guildId).getId(),
                            Main.getJDA().getTextChannelById(getChannelId(guildId)).getName(),
                            Main.getJDA().getTextChannelById(getChannelId(guildId)).getId(),
                            sentMessage.getId(),
                            channelName + " is streaming " + gameName);

                    new Tracker("Streams Announced");
                }
        );
    }

    private static synchronized Integer checkCompact(String guildId) {
        try {
            ccConnection = Database.getInstance().getConnection();
            query = "SELECT `isCompact` FROM `guild` WHERE `guildId` = ?";
            pStatement = ccConnection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            while (result.next()) {
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
                    message.appendString(" ");
                    message.appendString(ceResult.getString("emoji"));
                    message.appendString(" ");
                    message.appendString(ceResult.getString("emoji"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(ceResult, ceStatement, ceConnection);
        }
        message.appendString("");
        return message;
    }

    private static synchronized String getChannelId(String guildId) {
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

    private static synchronized String getPlatformLink(Integer platformId) {
        try {
            gplConnection = Database.getInstance().getConnection();
            query = "SELECT `baseLink` FROM `platform` WHERE `id` = ?";
            gplStatement = gplConnection.prepareStatement(query);
            gplStatement.setInt(1, platformId);
            gplResult = gplStatement.executeQuery();
            while (gplResult.next()) {
                String baseLink = gplResult.getString("baseLink");
                if (!"".equals(baseLink)) {
                    return baseLink;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(gplResult, gplStatement, gplConnection);
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
                        message.appendString("Hey ");
                        message.appendMention(user);
                        message.appendString(",  ");
                        break;
                    case 2: // User wants @here mention
                        message.appendString("Hey ");
                        message.appendHereMention();
                        message.appendString(",  ");
                        break;
                    case 3: // User wants @everyone mention
                        message.appendString("Hey ");
                        message.appendEveryoneMention();
                        message.appendString(",  ");
                        break;
                    default: // No mention
                        message.appendString("");
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
            logger.info("Mentioned Users Id's: " + this.mentionedUsersId);
        }
    }

    public final String getMentionedUsersId() {
        return this.mentionedUsersId;
    }

    public final String getGuildId() {
        return this.guildIdMessageEvent;
    }
}
