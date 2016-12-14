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
public class GetBroadcasterLang {
    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;
    private static ResultSet result;

    public synchronized static String action(String guildId) {
        try {
            String query = "SELECT `broadcasterLang` FROM `guild` WHERE `guildId` = ?";
            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();
            if (result.next()) {
                return result.getString("broadcasterLang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }
}
