package util.database.calls;

import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class CheckPerms extends Database {

    private Connection connection;
    private Statement statement;
    private String query;

    public CheckPerms() {
        connection = getInstance().getConnection();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkManager(MessageReceivedEvent event, String command) {
        // Check if the called command requires a manager
        ArrayList<String> managerList = new ArrayList<>();
        Boolean isManager = false;

        query = "SELECT `name` AS `command` FROM `command` ORDER BY `command` ASC";
        try {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                managerList.add(result.getString("command"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!managerList.contains(command)) {
            return true;
        }

        query = "SELECT `userId` FROM `manager` WHERE `guildId` = '" + event.getGuild().getId() + "'";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            // Iterate through the result set looking to see if the author is a manager
            String userId = event.getAuthor().getId();
            while (resultSet.next()) {
                if (userId.equals(resultSet.getString("userId"))) {
                    isManager = true;
                }
            }
            if (managerList.contains(command) && isManager.equals(true)) {
                return true;
            } else if (managerList.contains(command) && isManager.equals(false)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkAdmins(MessageReceivedEvent event, String command) {
        ArrayList<String> adminList = new ArrayList<>();
        Boolean isAdmin = false;

        adminList.add("announce");

        try {
            query = "SELECT `userId` FROM `admins`";
            ResultSet resultSet = statement.executeQuery(query);
            // Iterate through the result set looking to see if the author is a bot admin
            String userId = event.getAuthor().getId();
            while (resultSet.next()) {
                if (userId.equals(resultSet.getString("userId"))) {
                    isAdmin = true;
                }
            }
            if (!adminList.contains(command) && isAdmin.equals(true)) {
                sendToChannel(event, Const.ADMIN_OVERRIDE);
                return true;
            } else if (adminList.contains(command) && isAdmin.equals(true)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

