package platform.generic.controller;

import core.Main;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.entities.Message;
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

    private static Logger logger = LoggerFactory.getLogger(PlatformController.class);

    public Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private Integer result;
    private ResultSet resultSet;
    private ResultSet checkStream;
    private ResultSet checkQueue;

    public synchronized boolean setOnline(String guildId, Integer platformId, String channelName, String streamTitle,
                                          String gameName, Integer online) {

        if (!checkStreamTable(guildId, platformId, channelName)) { // Boolean false if not in stream table
            if (!checkQueueTable(guildId, platformId, channelName)) { // Boolean true if streamer is not in the queue
                // Start inserting the info to the message queue
                query = "INSERT INTO `queue` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`, " +
                        "`online`) VALUES (?, ?, ?, ?, ?, ?)";
                try {
                    connection = Database.getInstance().getConnection();
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
                    e.printStackTrace();
                } finally {
                    cleanUp(result, pStatement, connection);
                }
            }
        }
        return false;
    }

    public synchronized void setOffline(String guildId, Integer platformId, String channelName, Integer online) {
        if (checkStreamTable(guildId, platformId, channelName)) { // Boolean true if in the stream table
            try {
                query = "DELETE FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
                connection = Database.getInstance().getConnection();
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                pStatement.setInt(2, platformId);
                pStatement.setString(3, channelName);
                pStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(result, pStatement, connection);
            }
        } else {
            if (!checkQueueTable(guildId, platformId, channelName)) { // Make sure it's not in the queue already
                try {
                    query = "INSERT INTO `queue` (`guildId`, `platformId`, `channelName`, `online`) VALUES (?, ?, ?, ?)";
                    connection = Database.getInstance().getConnection();
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, guildId);
                    pStatement.setInt(2, platformId);
                    pStatement.setString(3, channelName);
                    pStatement.setInt(4, online);
                    pStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(result, pStatement, connection);
                }
            }
        }
    }

    public synchronized void onlineStreamHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        if (!checkStreamTable(guildId, platformId, channelName)) {
            addToStream(guildId, platformId, channelName, streamTitle, gameName);
            deleteFromQueue(guildId, platformId, channelName);
            announceStream(guildId, platformId, channelName, streamTitle, gameName);
        }
    }

    public synchronized void offlineStreamHandler(String guildId, Integer platformId, String channelName) {

    }

    /**
     * Announce the stream to the appropriate Discord channel
     * @param guildId String
     * @param platformId Integer
     * @param channelName String
     * @param streamTitle String
     * @param gameName String
     */
    private synchronized void announceStream(String guildId, Integer platformId, String channelName, String streamTitle,
                                             String gameName) {
        query = "SELECT `channelId` FROM `guild` WHERE `guildId` = ?";
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);

            resultSet = pStatement.executeQuery();
            String channelId = "";
            while (resultSet.next()) {
                channelId = resultSet.getString("channelId");
            }

            // Get the platform link
            String platformLink = "";
            query = "SELECT `baseLink` FROM `platform` WHERE `id` = ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, platformId);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                platformLink = resultSet.getString("baseLink");
            }

            String message = "NOW LIVE!\n\t" + channelName + " is playing some "
                    + gameName + "!\n\n\t" + streamTitle + "\n\t" + platformLink + channelName + " :heart_eyes_cat: ";

            // Send the message to the appropriate channel
            Message msg = Main.jda.getTextChannelById(channelId).sendMessage(message);
            String msgId = msg.getId();
            logger.info("Message ID: " + msgId);

            try {
                Thread.sleep(1150);
            } catch(InterruptedException ex) {
                logger.info("I have been awakened prematurely :<");
            }

            query = "UPDATE `stream` SET `messageId` = ? WHERE `guildId` = ? AND `platformId` = ? AND `channelName` =" +
                    " ?";
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, msgId);
            pStatement.setString(2, guildId);
            pStatement.setInt(3, platformId);
            pStatement.setString(4, channelName);

            pStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
    }

    /**
     * Add the stream to the stream table, signifying that it is live and has been announced.
     * @param guildId String
     * @param platformId Integer
     * @param channelName String
     * @param streamTitle String
     * @param gameName String
     * @return Boolean
     */
    private synchronized boolean addToStream(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        query = "INSERT INTO `stream` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`) " +
                "VALUES (?,?,?,?,?)";
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            pStatement.setString(4, streamTitle);
            pStatement.setString(5, gameName);

            pStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return false;
    }

    /**
     * Delete item from the queue
     * @param guildId String
     * @param platformId Integer
     * @param channelName String
     * @return Boolean
     */
    private synchronized boolean deleteFromQueue(String guildId, Integer platformId, String channelName) {

        query = "DELETE FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);

            pStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Integer result = 0;
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

    public synchronized boolean deleteFromStream(String guildId, Integer platformId, String channelName) {
        if (checkStreamTable(guildId, platformId, channelName)) {
            logger.info(channelName + " is still in the STREAM table.  Deleting them now");

            query = "DELETE FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";

            try {
                connection = Database.getInstance().getConnection();
                pStatement = connection.prepareStatement(query);

                pStatement.setString(1, guildId);
                pStatement.setInt(2, platformId);
                pStatement.setString(3, channelName);

                logger.info(String.valueOf(pStatement));

                if (pStatement.executeUpdate() > 0) {

                    logger.info("Offline stream has been deleted.");

                    // TODO: Delete or Edit Discord message

                    return true;
                } else {
                    logger.info("There was a problem deleting a streamer from the Stream table.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(result, pStatement, connection);
            }
        } else {
            logger.info(channelName + " is not in the STREAM table");
        }
        return false;
    }

    /**
     * Check the stream table to see if the streamer has already been announced.
     * @param guildId String representing the Discord Guild ID
     * @param platformId Integer representing the platform in question (Twitch/HitBox/etc)
     * @param channelName String representing the streamers channel
     * @return Boolean [false] > Not in stream table | [true] > In the stream table
     */
    private synchronized boolean checkStreamTable(String guildId, Integer platformId, String channelName) {
        query = "SELECT COUNT(*) AS `count` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` " +
                "= ?";

        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            checkStream = pStatement.executeQuery();

            while (checkStream.next()) {
                if (checkStream.getInt("count") == 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return true;
    }

    /**
     * Check the queue table to see if the streamer has already been queued.
     * @param guildId String representing the Discord Guild ID
     * @param platformId Integer representing the platform in question (Twitch/HitBox/etc)
     * @param channelName String representing the streamers channel
     * @return Boolean [false] > Not in stream table | [true] > In the stream table
     */
    private synchronized boolean checkQueueTable(String guildId, Integer platformId, String channelName) {
        query = "SELECT COUNT(*) AS `count` FROM `queue` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";

        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            checkQueue = pStatement.executeQuery();
            while (checkQueue.next()) {
                if (checkQueue.getInt("count") == 0) {
                    return false; // Streamer has not been queued.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return true;
    }
}