/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

import core.Main;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Veteran Software by Ague Mort
 */
public class DiscordController {

    private static Logger logger = LoggerFactory.getLogger(DiscordController.class);
    private static Connection connection;
    private static Statement statement;
    private static String query;
    private String guildIdMessageEvent;
    private String mentionedUsersId;
    private JDA jda = Main.jda;

    public DiscordController(MessageReceivedEvent event) {

        connection = Database.getInstance().getConnection();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public static void sendToChannel(MessageReceivedEvent event, String message) {
        try {
            query = "SELECT `channelId` FROM `guild` WHERE `guildId` = '" + event.getGuild().getId() + "'";
            ResultSet resultSet = statement.executeQuery(query);
            String channelId;
            if (resultSet.next()) {
                channelId = resultSet.getString(1);
            } else {
                channelId = event.getGuild().getPublicChannel().getId();
            }
            // TODO: CHECK PERMISSIONS ASSHOLE
            event.getJDA().getTextChannelById(channelId).sendMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void sendToPm(MessageReceivedEvent event, String message) {
        event.getAuthor().getPrivateChannel().sendMessage(message);
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

    public synchronized void streamFromQueue() {
        MessageBuilder message = new MessageBuilder();

        // Grab the current queue
        query = "SELECT * FROM `queue` WHERE `platformId` IS NOT NULL ORDER BY `timeAdded` ASC";

        try {
            ResultSet result = statement.executeQuery(query);

            String guildId = result.getString("guildId");

            notifyLevel(guildId, message); // Determine the beginning of the message

            while (result.next()) {

                if (streamChecks(guildId, "isActive")) {

                    // Get the channel ID to push the message out to
                    String channelQuery = "SELECT `channelId` FROM `guild` WHERE `guildId` = '" + result
                            .getString("guildId") + "'";
                    ResultSet channel = statement.executeQuery(channelQuery);

                    while (result.next()) {

                        //channelId = result.getString("channelId");

                    }

                    if (streamChecks(result.getString("guildId"), "isCompact")) { // Check for compact mode

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean streamChecks(String guildId, String type) {
        String checksQuery = "SELECT `" + type + "` FROM `guild` WHERE `guildId` = '" + guildId + "'";
        try {
            ResultSet checks = statement.executeQuery(checksQuery);
            while (checks.next()) {
                if (checks.getString(type).equals(1)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private synchronized void notifyLevel(String guildId, MessageBuilder message) {
        String levelQuery = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = '" + guildId + "'";
        try {
            ResultSet level = statement.executeQuery(levelQuery);

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
                    default: // No mention

                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
