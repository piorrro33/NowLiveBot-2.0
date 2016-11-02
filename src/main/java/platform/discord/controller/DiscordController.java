/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import core.Main;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DiscordController.class);
    private static Connection nlConnection;
    private static Connection mhConnection;
    private static Connection asConnection;
    private static Connection ceConnection;
    private static Connection gciConnection;
    private static Connection gplConnection;
    private static Connection ccConnection;
    private static PreparedStatement nlStatement;
    private static PreparedStatement mhStatement;
    private static PreparedStatement asStatement;
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

    public DiscordController(MessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public static void sendToChannel(MessageReceivedEvent event, String message) {
        event.getMessage().getChannel().sendMessage(message);
    }

    public static void sendToPm(MessageReceivedEvent event, Message message) {
        event.getAuthor().getPrivateChannel().sendMessage(message);
        logger.info("Private message sent to " + event.getAuthor().getUsername());
    }

    public static synchronized void messageHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName, Integer online) {
        switch (online) {
            case 1: // Stream is online
                logger.info("Stream is online");
                if (!checkStreamTable(guildId, platformId, channelName)) {
                    logger.info("Adding stream to the Stream table.");
                    addToStream(guildId, platformId, channelName, streamTitle, gameName);
                    logger.info("Deleting stream from the Queue table.");
                    deleteFromQueue(guildId, platformId, channelName);
                    logger.info("Announcing the stream");
                    announceStream(guildId, platformId, channelName, streamTitle, gameName);
                }
                break;
            default: // Stream is offline
                if (checkStreamTable(guildId, platformId, channelName)) {
                    logger.info("Stream is offline.");
                    try {
                        mhConnection = Database.getInstance().getConnection();
                        query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";

                        mhStatement = mhConnection.prepareStatement(query);

                        mhStatement.setString(1, guildId);
                        mhResult = mhStatement.executeQuery();

                        while (mhResult.next()) {
                            String channelId = mhResult.getString("channelId");
                            String messageId;
                            switch (mhResult.getInt("cleanup")) {
                                case 1:
                                    messageId = getMessageId(guildId, platformId, channelName);

                                    String oldMessage = Main.getJDA().getGuildById(guildId).getJDA().getTextChannelById
                                            (channelId).getMessageById(messageId).getRawContent();

                                    String newMessage = oldMessage.replaceFirst("NOW LIVE", "OFFLINE");

                                    Main.getJDA().getGuildById(guildId).getJDA().getTextChannelById(channelId)
                                            .getMessageById(messageId).updateMessageAsync(newMessage, null);

                                    break;
                                case 2:
                                    messageId = getMessageId(guildId, platformId, channelName);
                                    Main.getJDA().getGuildById(guildId).getJDA().getTextChannelById(channelId)
                                            .getMessageById(messageId).deleteMessage();
                                    break;
                                default:
                                    break;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        cleanUp(mhResult, mhStatement, mhConnection);
                    }
                    logger.info("Deleting offline stream from the Stream table.");
                    deleteFromStream(guildId, platformId, channelName);
                }
                logger.info("Deleting offline stream from the Queue table.");
                deleteFromQueue(guildId, platformId, channelName);
                break;
        }
    }

    private static synchronized void announceStream(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {

        try {
            // Send the message to the appropriate channel
            String channelId = getChannelId(guildId);

            MessageBuilder message = new MessageBuilder();

            message.appendString("***NOW LIVE!***\n");

            notifyLevel(guildId, message);

            if (checkCompact(guildId).equals(1)) {
                message.appendString("**" + channelName + "** is playing some **" + gameName + "** at ");
                message.appendString("<" + getPlatformLink(platformId) + channelName + ">");
            } else {
                message.appendString("\t**" + channelName + "** is playing some **" + gameName + "**!\n");
                message.appendString("\t\t*" + streamTitle + "*\n");
                message.appendString("\t\tWatch " + channelName + " here: " + getPlatformLink(platformId) + channelName);
            }
            checkEmoji(guildId, message);

            Message msg = Main.getJDA().getTextChannelById(channelId).sendMessage(message.build());

            // Grab the message ID
            String msgId = msg.getId();

            asConnection = Database.getInstance().getConnection();
            query = "UPDATE `stream` SET `messageId` = ? WHERE `guildId` = ? AND `platformId` = ? AND `channelName` =" +
                    " ?";
            asStatement = asConnection.prepareStatement(query);
            asStatement.setString(1, msgId);
            asStatement.setString(2, guildId);
            asStatement.setInt(3, platformId);
            asStatement.setString(4, channelName);
            asStatement.executeUpdate();
            new Tracker("Streams Announced");
        } catch (SQLException e) {
            logger.error("There was an SQL Exception", e);
        } finally {
            cleanUp(asStatement, asConnection);
        }
    }

    private static synchronized Integer checkCompact (String guildId) {
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


    private void mentionedUsersID(MessageReceivedEvent event) {
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
