package platform.generic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.beam.controller.BeamController;
import platform.twitch.controller.TwitchController;
import util.DiscordLogger;
import util.PropReader;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener {
    private static Logger logger = LoggerFactory.getLogger("Platform Listener");
    private static Connection connection;
    private static Connection clcConnection;
    private static Connection clgConnection;
    private static Connection kcConnection;
    private static PreparedStatement pStatement;
    private static PreparedStatement clcStatement;
    private static PreparedStatement clgStatement;
    private static PreparedStatement kcStatement;
    private static ResultSet result;
    private static ResultSet clcResult;
    private static ResultSet clgResult;
    private static ResultSet kcResult;
    private static String query;

    public PlatformListener() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        try {
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 0, 45, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 0, 45, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
        }
    }

    private static synchronized void killConn() {
        try {
            kcConnection = Database.getInstance().getConnection();
            query = "USE `information_schema`";
            kcStatement = kcConnection.prepareStatement(query);
            kcStatement.execute();

            query = "SELECT * FROM `PROCESSLIST`";
            kcStatement = kcConnection.prepareStatement(query);
            kcResult = kcStatement.executeQuery();
            while (kcResult.next()) {
                if (kcResult.getString("USER").equals(PropReader.getInstance().getProp()
                        .getProperty("mysql" + ".username")) && kcResult.getInt("TIME") > 10) {
                    Integer processId = kcResult.getInt("ID");
                    query = "KILL CONNECTION " + processId;
                    kcStatement = kcConnection.prepareStatement(query);
                    kcStatement.execute();
                }
            }
            query = "USE `" + PropReader.getInstance().getProp().getProperty("mysql.schema") + "`";
            kcStatement = kcConnection.prepareStatement(query);
            kcStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(kcResult, kcStatement, kcConnection);
        }
    }

    private synchronized void checkOfflineChannels() {
        new DiscordLogger(" :poop: **Checking for offline streams**", null);
        System.out.println("[SYSTEM] Checking for offline streams.");

        try {
            query = "SELECT * FROM `stream` ORDER BY `messageId` DESC";
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    new TwitchController().checkChannel(result.getString("channelName"), result.getString("guildId"),
                            result.getInt("platformId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    private synchronized void checkLiveChannels() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking for live channels...**", null);
        System.out.println("[SYSTEM] Checking for live channels. " + timeNow);

        try {
            clcConnection = Database.getInstance().getConnection();
            query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";
            clcStatement = clcConnection.prepareStatement(query);

            clcResult = clcStatement.executeQuery();

            if (!clcResult.isClosed()) {
                while (clcResult.next()) {
                    switch (clcResult.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            new TwitchController().checkChannel(clcResult.getString("name"), clcResult.getString
                                    ("guildId"), clcResult.getInt("platformId"));
                            break;
                        case 2:
                            /*new BeamController().checkChannel(clcResult.getString("name"), clcResult.getString
                            ("guildId"), clcResult.getInt("platformId"));*/

                            break;
                        default:
                            break;
                    }
                    killConn();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(clcResult, clcStatement, clcConnection);
        }
    }

    private synchronized void checkLiveGames() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking for live games...**", null);
        System.out.println("[SYSTEM] Checking for live games. " + timeNow);
        try {
            clgConnection = Database.getInstance().getConnection();
            query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
            clgStatement = clgConnection.prepareStatement(query);
            clgResult = clgStatement.executeQuery();

            while (clgResult.next()) {
                switch (clgResult.getInt("platformId")) {
                    case 1:
                        // Send info to Twitch Controller
                        TwitchController twitch = new TwitchController();
                        twitch.checkGame(clgResult.getString("name").replaceAll("''", "'"),
                                clgResult.getString("guildId"), clgResult.getInt("platformId"));
                        break;
                    case 2:
                        BeamController beam = new BeamController();
                        /*beam.checkGame(clcResult.getString("name"), clcResult.getString("guildId"),
                                clcResult.getInt("platformId"));*/

                        break;
                    default:
                        break;
                }
                killConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(clgResult, clgStatement, clgConnection);
        }
    }
}
