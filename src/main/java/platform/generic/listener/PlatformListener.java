package platform.generic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import platform.discord.listener.DiscordListener;
import platform.twitch.controller.TwitchController;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener {
    private static Logger logger = LoggerFactory.getLogger(PlatformListener.class);
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet result;

    public PlatformListener() {
        /*ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable checkLiveGames = PlatformListener::checkLiveGames;
        Runnable checkLiveChannels = PlatformListener::checkLiveChannels;
        Runnable messageFactory = PlatformListener::messageFactory;
        Runnable discordListener = PlatformListener::commandWorker;

        int initialDelay = 10; // Wait this long to start (2 seconds is ample when starting up the bot)
        int period = 60; // Run this task every {x} seconds

        logger.info("Starting the executor tasks");

        try {
            executor.submit(discordListener);
            executor.scheduleWithFixedDelay(checkLiveChannels.get(), initialDelay, 10, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(checkLiveGames, initialDelay + 1, 10, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(messageFactory.get(), initialDelay + 2, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
            new PlatformListener();
        }*/
    }

    public static synchronized void commandWorker() {
        //new DiscordListener();
    }

    public static synchronized void checkLiveChannels() {
        LocalDateTime timeNow = LocalDateTime.now();
        logger.info("Checking for live channels...  " + timeNow);

        try {
            query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";

            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);

                result = pStatement.executeQuery();
                TwitchController twitch = new TwitchController();

                while (result.next()) {
                    switch (result.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            twitch.checkChannel(result.getString("name"), result.getString("guildId"), result.getInt
                                    ("platformId"));
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    public static synchronized void checkLiveGames() {
        logger.info("Checking for live games...");
        try {
            query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
            connection = Database.getInstance().getConnection();
            if (connection != null) {
                pStatement = connection.prepareStatement(query);
                result = pStatement.executeQuery();

                while (result.next()) {
                    switch (result.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            TwitchController twitch = new TwitchController();
                            twitch.checkGame(result.getString("name"), result.getString("guildId"), result.getInt
                                    ("platformId"));
                            break;

                        default:
                            break;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    public static synchronized void messageFactory() {
        try {
            connection = Database.getInstance().getConnection();
            query = "SELECT * FROM `queue`";
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();
            while (result.next()) {
                String guildId = result.getString("guildId");
                Integer platformId = result.getInt("platformId");
                String channelName = result.getString("channelName");
                String streamTitle = result.getString("streamTitle");
                String gameName = result.getString("gameName");
                Integer online = result.getInt("online");

                DiscordController.messageHandler(guildId, platformId, channelName, streamTitle, gameName, online);
                TimeUnit.MILLISECONDS.sleep(1100);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }
}
