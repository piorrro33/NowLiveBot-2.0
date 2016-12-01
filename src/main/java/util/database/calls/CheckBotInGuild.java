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
public class CheckBotInGuild {
    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;
    private static ResultSet result;

    public synchronized static Boolean action(GuildMessageReceivedEvent event) {
        final String query = "SELECT COUNT(*) AS `count` FROM `guild` WHERE `guildId` = ?";
        try {
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, event.getGuild().getId());
            result = pStatement.executeQuery();

            while (result.next()) {
                if (result.getInt("count") == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false; // Bot was added while it was offline
    }
}
