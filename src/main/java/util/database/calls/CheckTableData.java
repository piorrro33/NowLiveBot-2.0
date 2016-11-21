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
public class CheckTableData {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;
    private static ResultSet result;

    public synchronized static Boolean action(String tableName, String guildId, Integer platformId, String name) {
        final String query = "SELECT `name` FROM `" + tableName + "` WHERE `guildId` = ? AND `platformId` = ? AND " +
                "`name` = ?";
        try {
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, name);
            result = pStatement.executeQuery();
            if (result.isBeforeFirst()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

}
