package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class CountManagers {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;
    private static ResultSet result;

    public synchronized static Boolean action(String tableName, String guildId, String userId) {
        final String query = "SELECT COUNT(*) AS `count` FROM `" + tableName + "` WHERE `guildId` = ? AND `userId` = ?";
        try {
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                pStatement.setString(2, userId);
                result = pStatement.executeQuery();

                while (result.next()) {
                    if (result.getInt("count") > 0) {
                        return true; // If they are a manager already
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false; // If they're not a manager
    }

}
