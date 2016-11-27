package util.database.calls;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class CheckPerms {

    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    public final boolean checkManager(GuildMessageReceivedEvent event) {
        // Check if the called command requires a manager

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

    public final boolean checkAdmins(GuildMessageReceivedEvent event) {

        try {
            String query = "SELECT `userId` FROM `admins`";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            // Iterate through the result set looking to see if the author is a bot admin
            String userId = event.getAuthor().getId();
            while (result.next()) {
                if (userId.equals(result.getString("userId"))) {
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

