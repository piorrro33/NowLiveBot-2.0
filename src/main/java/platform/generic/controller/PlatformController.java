package platform.generic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformController {

    public static Connection connection;
    private static Logger logger = LoggerFactory.getLogger(PlatformController.class);
    private static PreparedStatement pStatement;
    private static PreparedStatement cqtStatement;
    private static PreparedStatement atsStatement;
    private static PreparedStatement dfqStatement;
    private static PreparedStatement cstStatement;
    private static String query;
    private static ResultSet resultSet;
    private static ResultSet checkStream;
    private static ResultSet checkQueue;

    public static synchronized boolean deleteFromQueue(String guildId, Integer platformId, String channelName) {

        try {
            connection = Database.getInstance().getConnection();
            query = "DELETE FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";

            dfqStatement = connection.prepareStatement(query);
            dfqStatement.setString(1, guildId);
            dfqStatement.setInt(2, platformId);
            dfqStatement.setString(3, channelName);
            dfqStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(dfqStatement, connection);
        }
        return false;
    }

    /**
     * Add the stream to the stream table, signifying that it is live and has been announced.
     *
     * @param guildId     String
     * @param platformId  Integer
     * @param channelName String
     * @param streamTitle String
     * @param gameName    String
     * @return Boolean
     */
    public static synchronized boolean addToStream(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        query = "INSERT INTO `stream` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`) " +
                "VALUES (?,?,?,?,?)";
        try {
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                if (gameName == null || "".equals(gameName)) {
                    gameName = "Some Game";
                }
                atsStatement = connection.prepareStatement(query);
                atsStatement.setString(1, guildId);
                atsStatement.setInt(2, platformId);
                atsStatement.setString(3, channelName);
                atsStatement.setString(4, streamTitle);
                atsStatement.setString(5, gameName);
                atsStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            logger.info("I threw an exception here", e);
        } finally {
            cleanUp(atsStatement, connection);
        }

        return false;
    }

    /**
     * Check the stream table to see if the streamer has already been announced.
     *
     * @param guildId     String representing the Discord Guild ID
     * @param platformId  Integer representing the platform in question (Twitch/HitBox/etc)
     * @param channelName String representing the streamers channel
     * @return Boolean [false] > Not in stream table | [true] > In the stream table
     */
    public static synchronized boolean checkStreamTable(String guildId, Integer platformId, String channelName) {
        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT COUNT(*) AS `count` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` " +
                    "= ?";
            cstStatement = connection.prepareStatement(query);
            cstStatement.setString(1, guildId);
            cstStatement.setInt(2, platformId);
            cstStatement.setString(3, channelName);
            checkStream = cstStatement.executeQuery();
            while (checkStream.next()) {
                if (checkStream.getInt("count") == 0) {
                    return false; // Not in the stream table
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(checkStream, cstStatement, connection);
        }
        return true; // Found in the stream table
    }

    public static synchronized String getMessageId(String guildId, Integer platformId, String channelName) {

        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT `messageId` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            resultSet = pStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("messageId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return "";
    }

    public static synchronized boolean deleteFromStream(String guildId, Integer platformId, String channelName) {
        if (checkStreamTable(guildId, platformId, channelName)) {
            try {
                connection = Database.getInstance().getConnection();
                query = "DELETE FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                pStatement.setInt(2, platformId);
                pStatement.setString(3, channelName);

                pStatement.executeUpdate();
                logger.info("Stream deleted from the stream table");
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(pStatement, connection);
            }
        }
        return false;
    }

    public synchronized void onlineStreamHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        if (!checkStreamTable(guildId, platformId, channelName)) {
            // Streamer has not been announced
            if (!checkQueueTable(guildId, platformId, channelName)) {
                // Streamer has not been queued yet
                setOnline(guildId, platformId, channelName, streamTitle, gameName, 1);
            }
        }
    }

    public synchronized void offlineStreamHandler(String guildId, Integer platformId, String channelName) {
        if (checkQueueTable(guildId, platformId, channelName)) {
            updateQueueOffline(guildId, platformId, channelName);
        } else if (checkStreamTable(guildId, platformId, channelName) && !checkQueueTable(guildId, platformId, channelName)) {
            // Not really setting it online, just don't want a different named method with the same content
            setOnline(guildId, platformId, channelName, null, null, 0);
        }
    }

    /**
     * Check the queue table to see if the streamer has already been queued.
     *
     * @param guildId     String representing the Discord Guild ID
     * @param platformId  Integer representing the platform in question (Twitch/HitBox/etc)
     * @param channelName String representing the streamers channel
     * @return Boolean [false] > Not in queue table | [true] > In the queue table
     */

    private synchronized boolean checkQueueTable(String guildId, Integer platformId, String channelName) {
        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT COUNT(*) AS `count` FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` =" +
                    " ?";

            cqtStatement = connection.prepareStatement(query);
            cqtStatement.setString(1, guildId);
            cqtStatement.setInt(2, platformId);
            cqtStatement.setString(3, channelName);
            checkQueue = cqtStatement.executeQuery();
            if (checkQueue.next()) {
                if (checkQueue.getInt("count") == 0) {
                    return false; // Streamer has not been queued.
                }
            }
        } catch (SQLException e) {
            logger.error("checkQueueTable() error: ", e);
        } finally {
            cleanUp(resultSet, cqtStatement, connection);
        }
        return true;
    }

    private synchronized boolean setOnline(String guildId, Integer platformId, String channelName, String streamTitle,
                                           String gameName, Integer online) {
        try {
            connection = Database.getInstance().getConnection();
            query = "INSERT INTO `queue` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`, " +
                    "`online`) VALUES (?, ?, ?, ?, ?, ?)";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            pStatement.setString(4, streamTitle);
            pStatement.setString(5, gameName);
            pStatement.setInt(6, online);
            pStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("setOnline() error: ", e);
        } finally {
            cleanUp(pStatement, connection);
        }
        return false;
    }

    private synchronized void updateQueueOffline(String guildId, Integer platformId, String channelName) {
        try {
            connection = Database.getInstance().getConnection();
            query = "UPDATE `queue` SET `online` = 0 WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(3, channelName);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);

            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
    }
}