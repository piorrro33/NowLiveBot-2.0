package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class GetAnnounceChannel {

    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;
    private ResultSet result;

    public GetAnnounceChannel() {
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

    public synchronized String action(Map<String, String> args, Integer platformId) {
        try {
            if (args.get("gameName") == null || "".equals(args.get("gameName"))) {
                args.replace("gameName", "some game");
            }

            String query = "SELECT `channelId` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND " +
                    "`channelName` = ?";
            setStatement(query);
            pStatement.setString(1, args.get("guildId"));
            pStatement.setInt(2, platformId);
            pStatement.setString(3, args.get("channelName"));
            setResult(pStatement.executeQuery());

            if (result.isBeforeFirst()) {
                if (result.next()) {
                    return result.getString("channelId");
                }
            }
        } catch (SQLException e) {
            System.out.println("I threw an exception here");
        } finally {
            cleanUp(pStatement, connection);
        }

        return null;
    }

}
