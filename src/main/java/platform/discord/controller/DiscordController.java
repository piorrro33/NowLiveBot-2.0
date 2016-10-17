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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
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


    private synchronized void notifyLevel(String guildId, MessageBuilder message) {
        String query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            ResultSet level = pStatement.executeQuery();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
