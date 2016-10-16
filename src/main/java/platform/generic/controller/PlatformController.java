package platform.generic.controller;

import util.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformController {

    private Connection connection;
    private Statement statement;
    private String query;
    private Integer result;

    public PlatformController() {
        connection = Database.getInstance().getConnection();
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean streamToQueue(String guildId, Integer platformId, String channelName, String streamTitle,
                                 String gameName) {

        // Check to see if the stream has been announced

        // Should return false if the streamer hasn't been announced
        if (!checkStreamTable(guildId, platformId, channelName)) {
            System.out.println("Streamer has not been announced");
            // Should return boolean true if streamer is not in the queue
            if (checkQueueTable(guildId, platformId, channelName)) {
                System.out.println("Streamer is not in the message queue.");
                // Start inserting the info to the message queue
                query = "INSERT INTO `queue` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`) " +
                        "VALUES ('" +
                        guildId + "', " + platformId + ", '" + channelName + "', '" + streamTitle + "', '" + gameName + "')";

                try {
                    result = statement.executeUpdate(query);
                    if (result.equals(1)) {
                        System.out.println("Streamer has been added to the message queue.");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                addToStreamTable(guildId, platformId, channelName, streamTitle, gameName);
                deleteFromQueue(guildId, platformId, channelName);
            }
        }
        return false;
    }

    private synchronized boolean deleteFromQueue(String guildId, Integer platformId, String channelName) {
        query = "DELETE FROM `queue` WHERE `guildId` = '" + guildId + "' AND `platformId` = " + platformId + " AND " +
                "channelName = '" + channelName + "'";
        try {
            result = statement.executeUpdate(query);
            if (result.equals(1)) {
                System.out.println("Streamer has been deleted from the message queue");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private synchronized boolean addToStreamTable(String guildId, Integer platformId, String channelName, String streamTitle,
                                  String gameName) {
        query = "INSERT INTO `stream` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`) " +
                "VALUES ('" + guildId + "', " + platformId + ", '" + channelName + "', '" + streamTitle + "', '" +
                gameName + "')";

        try {
            result = statement.executeUpdate(query);
            if (result.equals(1)) {
                System.out.println("Streamer has been added to the stream table and announced.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private synchronized boolean checkStreamTable(String guildId, Integer platformId, String channelName) {
        query = "SELECT COUNT(*) AS `count` FROM `stream` WHERE `guildId` = '" + guildId + "' AND `platformId` = " +
                platformId + " AND `channelName` = '" + channelName + "'";

        try {
            ResultSet checkStream = statement.executeQuery(query);
            while (checkStream.next()) {
                if (checkStream.getInt("count") == 0) {
                    System.out.println("Streamer: " + channelName + " has NOT yet been announced");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private synchronized boolean checkQueueTable(String guildId, Integer platformId, String channelName) {
        query = "SELECT COUNT(*) AS `count` FROM `queue` WHERE `guildId` = '" + guildId + "' AND `platformId` = " +
                platformId + " AND `channelName` = '" + channelName + "'";

        try {
            ResultSet checkQueue = statement.executeQuery(query);
            while (checkQueue.next()) {
                if (checkQueue.getInt("count") == 0) {
                    System.out.println("Streamer: " + channelName + " has NOT yet been queued");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
