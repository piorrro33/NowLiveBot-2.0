package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AddOther {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;

    public synchronized static Boolean action(String tableName, String guildId, int platformId, String name) {
        final String query = "INSERT INTO `" + tableName + "` (`id`, `guildId`, `platformId`, `name`) VALUES (null, " +
                "?, ?, ?)";
        try {
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, name);
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
