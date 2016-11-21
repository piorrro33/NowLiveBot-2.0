package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AddManager {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;

    public synchronized static Boolean action(String tableName, String guildId, String userId) {
        final String query = "INSERT INTO `" + tableName + "` (`id`, `guildId`, `userId`) VALUES (null, ?, ?)";
        try {
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setString(2, userId);
            if (pStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return false;
    }

}
