package platform.generic.listener;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.events.DisconnectEvent;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
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

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener implements EventListener {
    private static Logger logger = LoggerFactory.getLogger(PlatformListener.class);
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static String query;
    private static ResultSet result;
    private static JDA jda = null;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private boolean running;

    public PlatformListener() {


        try {
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 5, 90, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 10, 90, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::messageFactory, 15, 45, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            logger.info("Init Complete!");

            jda = event.getJDA();
            if (!running) {
                scheduleTasks();
                running = true;
            }
        }

        if (event instanceof DisconnectEvent) {
            logger.info("Got disconnected!!");
            if (!executor.isShutdown() || !executor.isTerminated()) {
                executor.shutdownNow();
                jda = null;
                running = false;
            }
        }

    }

    private void scheduleTasks() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        if (jda != null) {
            logger.info("Starting the executor tasks");
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 0, 15, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 0, 15, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::messageFactory, 0, 1, TimeUnit.SECONDS);
        } else {
            logger.info("We are way too early...");
        }
    }

    private synchronized void checkLiveChannels() {
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

    private synchronized void checkLiveGames() {
        LocalDateTime timeNow = LocalDateTime.now();
        logger.info("Checking for live games... " + timeNow);
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

    private synchronized void messageFactory() {
        LocalDateTime timeNow = LocalDateTime.now();
        logger.info("Checking to see if I need to make any announcements...  " + timeNow);
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
