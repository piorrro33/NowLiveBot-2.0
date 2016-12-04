package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AddToStream {

    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;

    public AddToStream() {
        setConnection();
    }

    private void setConnection() {
        this.connection = Database.getInstance().getConnection();
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

    public synchronized Boolean action(Map<String, String> args, Integer platformId) {
        try {
            if (args.get("gameName") == null || "".equals(args.get("gameName"))) {
                args.replace("gameName", "some game");
            }

            String query = "INSERT INTO `stream` (`guildId`, `channelId`, `platformId`, `channelName`, `streamTitle`," +
                    " `gameName`, `messageId`) VALUES (?,?,?,?,?,?,?)";
            setStatement(query);
            pStatement.setString(1, args.get("guildId"));
            pStatement.setString(2, args.get("channelId"));
            pStatement.setInt(3, platformId);
            pStatement.setString(4, args.get("channelName"));
            pStatement.setString(5, args.get("streamTitle"));
            pStatement.setString(6, args.get("gameName"));
            pStatement.setString(7, args.get("messageId"));

            if (pStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("I threw an exception here");
        } finally {
            cleanUp(pStatement, connection);
        }

        return false;
    }

}
