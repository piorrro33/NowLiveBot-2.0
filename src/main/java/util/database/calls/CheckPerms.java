package util.database.calls;

import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class CheckPerms {

    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    public final boolean checkManager(MessageReceivedEvent event, String command) {
        // Check if the called command requires a manager
        /*ArrayList<String> managerList = new ArrayList<>();
        Boolean isManager = false;

        try {
            String query = "SELECT `name` AS `command` FROM `command` ORDER BY `command` ASC";
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                result = pStatement.executeQuery();
                while (result.next()) {
                    managerList.add(result.getString("command"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        if (!managerList.contains(command)) {
            return true;
        }

        try {
            String query = "SELECT `userId` FROM `manager` WHERE `guildId` = ?";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, event.getGuild().getId());
            result = pStatement.executeQuery();

            // Iterate through the result set looking to see if the author is a manager
            String userId = event.getAuthor().getId();
            while (result.next()) {
                if (userId.equals(result.getString("userId"))) {
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
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;*/
        return true;
    }

    public final boolean checkAdmins(MessageReceivedEvent event, String command) {
        ArrayList<String> adminList = new ArrayList<>();
        Boolean isAdmin = false;

        adminList.add("announce");

        try {
            String query = "SELECT `userId` FROM `admins`";
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                result = pStatement.executeQuery();

                // Iterate through the result set looking to see if the author is a bot admin
                String userId = event.getAuthor().getId();
                while (result.next()) {
                    if (userId.equals(result.getString("userId"))) {
                        isAdmin = true;
                    }
                }
                if (!adminList.contains(command) && isAdmin.equals(true)) {
                    sendToChannel(event, Const.ADMIN_OVERRIDE);
                    return true;
                } else if (adminList.contains(command) && isAdmin.equals(true)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

}

