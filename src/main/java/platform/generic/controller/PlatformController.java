package platform.generic.controller;

import util.database.Database;
import util.database.calls.CheckStreamTable;
import util.database.calls.GetAnnounceChannel;
import util.database.calls.GetChannelId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static platform.discord.controller.DiscordController.messageHandler;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformController {

    private static Connection gmiConnection;
    private static Connection dfsConnection;
    private static PreparedStatement gmiStatement;
    private static PreparedStatement dfsStatement;
    private static String query;
    private static ResultSet gmiResult;

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
        if (new CheckStreamTable().action(guildId, platformId, channelName)) {
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
                case "beam":
                    return 2;
                default:
                    break;
            }
        }
        return 0;
    }

    public synchronized void onlineStreamHandler(Map<String, String> args, Integer platformId) {
        if (!new CheckStreamTable().action(args.get("guildId"), platformId, args.get("channelName"))) {

            // Streamer has not been announced
            args.put("channelId", new GetChannelId().action(args.get("guildId")));

            messageHandler(args, platformId, 1);
        }
    }

    public synchronized void offlineStreamHandler(Map<String, String> args, Integer platformId) {
        if (new CheckStreamTable().action(args.get("guildId"), platformId, args.get("channelName"))) {

            // Streamer is offline and was announced
            args.put("channelId", new GetAnnounceChannel().action(args, platformId));

            messageHandler(args, platformId, 0);
        }
    }
}