/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.controller;

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
    private String guildIdMessageEvent;
    private String mentionedUsersId;

    public DiscordController(MessageReceivedEvent event) {

        this.guildIdMessageEvent = event.getGuild().getId();
        mentionedUsersID(event);
    }

    public static void sendToChannel(MessageReceivedEvent event, String message) {
        Connection connection;
        try {
            connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT `channelId` FROM `guild` WHERE `guildId` = '" + event.getGuild().getId() + "'";
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
