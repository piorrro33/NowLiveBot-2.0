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
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet result;
    private static ResultSet level;
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
                        connection = Database.getInstance().getConnection();
                        query = "SELECT `cleanup`, `channelId` FROM `guild` WHERE `guildId` = ?";
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        result = pStatement.executeQuery();

                        while (result.next()) {
                            logger.info("Got the channelID and cleanup code");
                            String channelId = result.getString("channelId");
                            String messageId;
                            switch (result.getInt("cleanup")) {
                                case 1:
                                    logger.info("This guild is set to edit announcements");
                                    messageId = getMessageId(guildId, platformId, channelName);
                                    logger.info("messageId = " + messageId);

                                    String oldMessage = Main.getJDA().getGuildById(guildId).getJDA().getTextChannelById
                                            (channelId).getMessageById(messageId).getRawContent();
                                    logger.info("Old message was: " + oldMessage);

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
                        cleanUp(result, pStatement, connection);
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

            message.appendString("***NOW LIVE!***\n\t");
            notifyLevel(guildId, message);
            message.appendString("**" + channelName + "** is playing some **" + gameName + "**!\n");
            message.appendString("\t\t*" + streamTitle + "*\n");
            message.appendString("\t\tWatch " + channelName + " here: " + getPlatformLink(platformId) + channelName);
            checkEmoji(guildId, message);
            //message.build();

            Message msg = Main.getJDA().getTextChannelById(channelId).sendMessage(message.build());
            // Grab the message ID
            String msgId = msg.getId();

            query = "UPDATE `stream` SET `messageId` = ? WHERE `guildId` = ? AND `platformId` = ? AND `channelName` =" +
                    " ?";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, msgId);
            pStatement.setString(2, guildId);
            pStatement.setInt(3, platformId);
            pStatement.setString(4, channelName);
            pStatement.executeUpdate();
            new Tracker("Streams Announced");
        } catch (SQLException e) {
            logger.error("There was an SQL Exception", e);
        } finally {
            cleanUp(pStatement, connection);
        }
    }

    private static synchronized MessageBuilder checkEmoji(String guildId, MessageBuilder message) {
        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT `emoji` FROM `guild` WHERE `guildId` = ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            // Checking to see how emoji are passed from Discord
            /*for(Emote emote : Main.jda.getGuildById(guildId).getEmotes()) {
                System.out.println(emote);
            }*/

            while (result.next()) {
                if (result.getString("emoji") != null) {
                    message.appendString(" ");
                    message.appendString(result.getString("emoji"));
                    message.appendString(" ");
                    message.appendString(result.getString("emoji"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        message.appendString("");
        return message;
    }

    private static synchronized String getChannelId(String guildId) {
        query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";
        try {
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);

                result = pStatement.executeQuery();
                while (result.next()) {
                    return result.getString("channelId");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return "";
    }

    private static synchronized String getPlatformLink(Integer platformId) {
        query = "SELECT `baseLink` FROM `platform` WHERE `id` = ?";
        try {
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                pStatement.setInt(1, platformId);
                result = pStatement.executeQuery();
                while (result.next()) {
                    return result.getString("baseLink");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return "";
    }

    private static synchronized MessageBuilder notifyLevel(String guildId, MessageBuilder message) {
        try {
            if (connection != null) {
                connection = Database.getInstance().getConnection();
                query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                level = pStatement.executeQuery();

                while (level.next()) {
                    switch (level.getInt("level")) {
                        case 1: // User wants a @User mention
                            String userId = level.getString("userId");
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(level, pStatement, connection);
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
