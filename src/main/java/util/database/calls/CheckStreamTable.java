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
public class CheckStreamTable {

    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;
    private ResultSet result;

    public CheckStreamTable() {
        setConnection();
    }

    private void setConnection() {
        this.connection = Database.getInstance().getConnection();
    }

    private void setResult(ResultSet result) {
        this.result = result;
    }

    private void setStatement(String query) {
        try {
            if (connection.isClosed() || connection == null) {
                setConnection();
            }
            this.pStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Boolean action(String guildId, Integer platformId, String channelName) {
        try {
            String query = "SELECT COUNT(*) AS `count` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND " +
                    "`channelName` = ?";

            setStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            setResult(pStatement.executeQuery());

            while (result.next()) {
                if (result.getInt("count") == 0) {
                    return false; // Not in the stream table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return true; // Found in the stream table
    }
}
