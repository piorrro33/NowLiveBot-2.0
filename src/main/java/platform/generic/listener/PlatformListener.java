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
    private static Connection clcConnection;
    private static Connection clgConnection;
    private static Connection kcConnection;
    private static Connection connection;
    private static PreparedStatement clcStatement;
    private static PreparedStatement clgStatement;
    private static PreparedStatement kcStatement;
    private static PreparedStatement pStatement;
    private static ResultSet clcResult;
    private static ResultSet clgResult;
    private static ResultSet kcResult;
    private static ResultSet result;
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public PlatformListener() {

        try {
            executor.scheduleWithFixedDelay(this::run, 0, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
        }
    }

    private static synchronized void killConn() {
        try {
            kcConnection = Database.getInstance().getConnection();
            String query = "USE `information_schema`";
            kcStatement = kcConnection.prepareStatement(query);
            kcStatement.execute();

            query = "SELECT * FROM `PROCESSLIST`";
            kcStatement = kcConnection.prepareStatement(query);
            kcResult = kcStatement.executeQuery();
            while (kcResult.next()) {
                if (kcResult.getString("USER").equals(PropReader.getInstance().getProp().getProperty("mysql.username"))) {
                    if (kcResult.getInt("TIME") > 10) {
                        Integer processId = kcResult.getInt("ID");
                        query = "KILL CONNECTION " + processId;
                        kcStatement = kcConnection.prepareStatement(query);
                        kcStatement.execute();
                    }
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

    private synchronized void run() {
        checkLiveChannels();
        checkLiveGames();
        checkOfflineStreams();
    }

    // jda.getUserById("123456789").getJDA().getPresence().getGame().getUrl();

    private synchronized void checkLiveChannels() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking for live channels...**", null);
        System.out.println("[SYSTEM] Checking for live channels. " + timeNow);

        try {
            clcConnection = Database.getInstance().getConnection();
            String query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";
            clcStatement = clcConnection.prepareStatement(query);

            clcResult = clcStatement.executeQuery();

            if (!clcResult.isClosed()) {
                while (clcResult.next()) {
                    switch (clcResult.getInt("platformId")) {
                        case 1:
                            TwitchController twitch = new TwitchController();
                            // Send info to Twitch Controller
                            twitch.checkChannel(clcResult.getString("name"), clcResult.getString("guildId"), clcResult.getInt
                                    ("platformId"));
                            break;
                        case 2:
                            BeamController beam = new BeamController();
                            new BeamController().checkChannel(clcResult.getString("name"), clcResult.getString("guildId"),
                                    clcResult.getInt("platformId"));

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
            String query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
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

    private synchronized void checkOfflineStreams() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking for offline streams...**", null);
        System.out.println("[SYSTEM] Checking for offline streams. " + timeNow);

        try {
            String query = "SELECT * FROM `stream` ORDER BY `messageId` ASC";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    if (result.getInt("platformId") == 1) {

                        TwitchController twitch = new TwitchController();
                        twitch.checkOffline(result.getString("channelName"), result.getString("guildId"),
                                result.getInt("platformId"));
                    }
                    killConn();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }
}
