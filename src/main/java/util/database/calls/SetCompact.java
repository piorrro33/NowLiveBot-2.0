package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class SetCompact {

    private static Connection connection = Database.getInstance().getConnection();
    private static PreparedStatement pStatement;

    public synchronized static Boolean action(String guildId, int isCompact) {
        try {
            String query = "UPDATE `guild` SET `isCompact` = ? WHERE `guildId` = ?";

            if (connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, isCompact);
            pStatement.setString(2, guildId);

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
