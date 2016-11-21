package platform.generic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.twitch.controller.TwitchController;
import util.PropReader;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private static Connection ceConnection;
    private static Connection clcConnection;
    private static Connection clgConnection;
    private static Connection kcConnection;
    private static PreparedStatement pStatement;
    private static PreparedStatement ceStatement;
    private static PreparedStatement clcStatement;
    private static PreparedStatement clgStatement;
    private static PreparedStatement kcStatement;
    private static ResultSet ceResult;
    private static ResultSet clcResult;
    private static ResultSet clgResult;
    private static ResultSet kcResult;
    private static String query;
    private static List<String> tableList = new ArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public PlatformListener() {

        // Need to rethink this a little bit when I'm not so tired
        // The idea is to check if the bot was booted while offline and still has remnants in the database
        /*try {
            List<Guild> activeGuilds = Main.getJDA().getGuilds();
            query = "SELECT `guildId` FROM `guild`";
            ceConnection = Database.getInstance().getConnection();
            ceStatement = ceConnection.prepareStatement(query);
            ceResult = ceStatement.executeQuery();
            while (ceResult.next()) {
                for (Guild guilds : activeGuilds) {
                    if (!ceResult.getString("guildId").equals(guilds.getId())) {
                        leaveGuild(ceResult.getString("guildId"));
                        Main.getJDA().getGuildById(guilds.getId()).leave().queue();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(ceResult, ceStatement, ceConnection);
        }*/

        try {
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 0, 30, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 0, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
        }
    }

    private static synchronized boolean checkEnabled(String guildId) {
        try {
            ceConnection = Database.getInstance().getConnection();
            query = "SELECT `isActive` FROM `guild` WHERE `guildId` = ?";
            ceStatement = ceConnection.prepareStatement(query);

            ceStatement.setString(1, guildId);
            ceResult = ceStatement.executeQuery();
            while (ceResult.next()) {
                if (ceResult.getInt("isActive") == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(ceResult, ceStatement, ceConnection);
        }
        return false;
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

    private synchronized void checkLiveChannels() {
        LocalDateTime timeNow = LocalDateTime.now();
        System.out.println("[SYSTEM] Checking for live channels. " + timeNow);

        try {
            clcConnection = Database.getInstance().getConnection();
            query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";
            clcStatement = clcConnection.prepareStatement(query);

            clcResult = clcStatement.executeQuery();

            while (clcResult.next()) {
                if (checkEnabled(clcResult.getString("guildId"))) {
                    switch (clcResult.getInt("platformId")) {
                        case 1:
                            TwitchController twitch = new TwitchController();
                            // Send info to Twitch Controller
                            twitch.checkChannel(clcResult.getString("name"), clcResult.getString("guildId"), clcResult.getInt
                                    ("platformId"));
                            break;
                        default:
                            break;
                    }
                }
                killConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(clcResult, clcStatement, clcConnection);
        }
    }

    private synchronized void checkLiveGames() {
        LocalDateTime timeNow = LocalDateTime.now();
        System.out.println("[SYSTEM] Checking for live games. " + timeNow);
        try {
            clgConnection = Database.getInstance().getConnection();
            query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
            clgStatement = clgConnection.prepareStatement(query);
            clgResult = clgStatement.executeQuery();

            while (clgResult.next()) {
                if (checkEnabled(clgResult.getString("guildId"))) {
                    switch (clgResult.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            TwitchController twitch = new TwitchController();
                            twitch.checkGame(clgResult.getString("name").replaceAll("''", "'"), clgResult.getString
                                    ("guildId"), clgResult.getInt("platformId"));
                            break;
                        default:
                            break;
                    }
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
