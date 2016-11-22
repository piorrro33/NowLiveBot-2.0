package platform.generic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.messageHandler;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformController {

    private static Connection atsConnection;
    private static Connection cstConnection;
    private static Connection gmiConnection;
    private static Connection dfsConnection;
    private static Logger logger = LoggerFactory.getLogger("Platform Controller");
    private static PreparedStatement atsStatement;
    private static PreparedStatement cstStatement;
    private static PreparedStatement gmiStatement;
    private static PreparedStatement dfsStatement;
    private static String query;
    private static ResultSet gmiResult;
    private static ResultSet cstResult;

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
            streamTitle, String gameName, String messageId) {
        try {
            String game = gameName;
            if (game == null || "".equals(game)) {
                game = "Some Game";
            }
            atsConnection = Database.getInstance().getConnection();
            query = "INSERT INTO `stream` (`guildId`, `platformId`, `channelName`, `streamTitle`, `gameName`, " +
                    "`messageId`) VALUES (?,?,?,?,?,?)";
            atsStatement = atsConnection.prepareStatement(query);
            atsStatement.setString(1, guildId);
            atsStatement.setInt(2, platformId);
            atsStatement.setString(3, channelName);
            atsStatement.setString(4, streamTitle);
            atsStatement.setString(5, game);
            atsStatement.setString(6, messageId);
            atsStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.info("I threw an exception here", e);
        } finally {
            cleanUp(atsStatement, atsConnection);
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
            cstConnection = Database.getInstance().getConnection();
            query = "SELECT COUNT(*) AS `count` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` " +
                    "= ?";
            cstStatement = cstConnection.prepareStatement(query);
            cstStatement.setString(1, guildId);
            cstStatement.setInt(2, platformId);
            cstStatement.setString(3, channelName);
            cstResult = cstStatement.executeQuery();
            while (cstResult.next()) {
                if (cstResult.getInt("count") == 0) {
                    return false; // Not in the stream table
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(cstResult, cstStatement, cstConnection);
        }
        return true; // Found in the stream table
    }

    public static synchronized String getMessageId(String guildId, Integer platformId, String channelName) {

        try {
            gmiConnection = Database.getInstance().getConnection();
            query = "SELECT `messageId` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
            gmiStatement = gmiConnection.prepareStatement(query);
            gmiStatement.setString(1, guildId);
            gmiStatement.setInt(2, platformId);
            gmiStatement.setString(3, channelName);
            gmiResult = gmiStatement.executeQuery();
            if (gmiResult.next()) {
                return gmiResult.getString("messageId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(gmiResult, gmiStatement, gmiConnection);
        }
        return "";
    }

    public static synchronized boolean deleteFromStream(String guildId, Integer platformId, String channelName) {
        if (checkStreamTable(guildId, platformId, channelName)) {
            try {
                dfsConnection = Database.getInstance().getConnection();
                query = "DELETE FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";
                dfsStatement = dfsConnection.prepareStatement(query);
                dfsStatement.setString(1, guildId);
                dfsStatement.setInt(2, platformId);
                dfsStatement.setString(3, channelName);

                dfsStatement.executeUpdate();
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(dfsStatement, dfsConnection);
            }
        }
        return false;
    }

    public static int getPlatformId(String args) {
        if (args.contains("~")) {
            String platform = args.substring(0, args.indexOf("~"));
            switch (platform) {
                case "twitch":
                    return 1;
                default:
                    break;
            }
        }
        return 0;
    }

    public final synchronized void onlineStreamHandler(String guildId, Integer platformId, String channelName, String
            streamTitle, String gameName) {
        if (!checkStreamTable(guildId, platformId, channelName)) {
            // Streamer has not been announced
            messageHandler(guildId, platformId, channelName, streamTitle, gameName, 1);
        }
    }

    public final synchronized void offlineStreamHandler(String guildId, Integer platformId, String channelName) {
        if (checkStreamTable(guildId, platformId, channelName)) {
            messageHandler(guildId, platformId, channelName, null, null, 0);
        }
    }
}