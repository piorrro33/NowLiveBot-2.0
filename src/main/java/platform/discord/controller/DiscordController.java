/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import core.Main;
import net.dv8tion.jda.JDA;
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

import static platform.generic.controller.PlatformController.addToStream;
import static platform.generic.controller.PlatformController.checkStreamTable;
import static platform.generic.controller.PlatformController.deleteFromQueue;
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
    private JDA jda = Main.jda;

    public DiscordController(MessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public static void sendToChannel(MessageReceivedEvent event, String message) {
        try {
            query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";

            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, event.getGuild().getId());

                result = pStatement.executeQuery();
                String channelId;
                if (result.next()) {
                    channelId = result.getString(1);
                } else {
                    channelId = event.getGuild().getPublicChannel().getId();
                }
                // TODO: CHECK PERMISSIONS
                event.getJDA().getTextChannelById(channelId).sendMessage(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    public static void sendToPm(MessageReceivedEvent event, String message) {
        event.getAuthor().getPrivateChannel().sendMessage(message);
    }

    public static synchronized void messageHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName, Integer online) {
        switch (online) {
            case 1:
                if (!checkStreamTable(guildId, platformId, channelName)) {
                    addToStream(guildId, platformId, channelName, streamTitle, gameName);
                    deleteFromQueue(guildId, platformId, channelName);
                    announceStream(guildId, platformId, channelName, streamTitle, gameName);
                }
                break;
            default:

                break;
        }
    }

    private static synchronized void announceStream(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {

        try {
            // Send the message to the appropriate channel
            String channelId = getChannelId(guildId);

            // Get the base link for the platform
            String platformLink = getPlatformLink(platformId);
            String message = "***NOW LIVE!***\t**" + channelName + "** is playing some **"
                    + gameName + "**!\n\t*" + streamTitle + "*\n\tWatch " + channelName + " here: " + platformLink +
                    channelName + " :heart_eyes_cat: :heart_eyes_cat:";

            Message msg = Main.jda.getTextChannelById(channelId).sendMessage(message);
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

    private synchronized void notifyLevel(String guildId, MessageBuilder message) {
        String query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
        try {
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                level = pStatement.executeQuery();

                while (level.next()) {
                    switch (level.getInt("level")) {
                        case 1: // User wants a @User mention
                            String userId = level.getString("userId");
                            User user = this.jda.getUserById(userId);
                            message.appendMention(user);
                            message.appendString("  ");
                            break;
                        case 2: // User wants @here mention
                            message.appendHereMention();
                            message.appendString("  ");
                            break;
                        case 3: // User wants @everyone mention
                            message.appendEveryoneMention();
                            message.appendString("  ");
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
    }


    private void mentionedUsersID(MessageReceivedEvent event) {
        for (User u : event.getMessage().getMentionedUsers()) {
            this.mentionedUsersId = u.getId();
            logger.info("Mentioned Users Id's: " + this.mentionedUsersId);
        }
    }

    public String getMentionedUsersId() {
        return this.mentionedUsersId;
    }

    public String getguildId() {
        return this.guildIdMessageEvent;
    }
}
