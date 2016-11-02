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
    private static Connection mfConnection;
    private static Connection ceConnection;
    private static Connection clcConnection;
    private static Connection clgConnection;
    private static PreparedStatement mfStatement;
    private static PreparedStatement ceStatement;
    private static PreparedStatement clcStatement;
    private static PreparedStatement clgStatement;
    private static ResultSet ceResult;
    private static ResultSet clcResult;
    private static ResultSet clgResult;
    private static ResultSet mfResult;
    private static String query;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private JDA jda = null;
    private boolean running;

    public PlatformListener() {

        try {
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 5, 30, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 10, 30, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::messageFactory, 15, 5, TimeUnit.SECONDS);
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
                try {
                    System.out.println("attempt to shutdown executor");
                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                    jda = null;
                    running = false;
                }
                catch (InterruptedException e) {
                    System.err.println("tasks interrupted");
                }
                finally {
                    if (!executor.isTerminated()) {
                        System.err.println("cancel non-finished tasks");
                    }
                    executor.shutdownNow();
                    System.out.println("shutdown finished");
                }
                executor.shutdown();

            }
        }
    }

    private void scheduleTasks() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        if (jda != null) {
            logger.info("Starting the executor tasks");
            executor.scheduleWithFixedDelay(this::checkLiveChannels, 0, 15, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::checkLiveGames, 5, 15, TimeUnit.SECONDS);
            executor.scheduleWithFixedDelay(this::messageFactory, 10, 1, TimeUnit.SECONDS);
        } else {
            logger.info("We are way too early...");
        }
    }

    private synchronized boolean checkEnabled(String guildId) {
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

    private synchronized void checkLiveChannels() {
        LocalDateTime timeNow = LocalDateTime.now();
        logger.info("Checking for live channels...  " + timeNow);

        try {
            clcConnection = Database.getInstance().getConnection();
            query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";
            clcStatement = clcConnection.prepareStatement(query);

            clcResult = clcStatement.executeQuery();
            TwitchController twitch = new TwitchController();

            while (clcResult.next()) {
                if (checkEnabled(clcResult.getString("guildId"))) {
                    switch (clcResult.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            twitch.checkChannel(clcResult.getString("name"), clcResult.getString("guildId"), clcResult.getInt
                                    ("platformId"));
                            break;

                        default:
                            break;
                    }
                } else {
                    logger.info("Guild " + clcResult.getString("guildId") + " is not enabled.");
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
        logger.info("Checking for live games... " + timeNow);
        try {
            query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
            clgConnection = Database.getInstance().getConnection();
            clgStatement = clgConnection.prepareStatement(query);
            clgResult = clgStatement.executeQuery();

            while (clgResult.next()) {
                if (checkEnabled(clgResult.getString("guildId"))) {
                    switch (clgResult.getInt("platformId")) {
                        case 1:
                            // Send info to Twitch Controller
                            TwitchController twitch = new TwitchController();
                            twitch.checkGame(clgResult.getString("name"), clgResult.getString("guildId"), clgResult.getInt
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
            cleanUp(clgResult, clgStatement, clgConnection);
        }
    }

    private synchronized void messageFactory() {
        LocalDateTime timeNow = LocalDateTime.now();
        logger.info("Checking to see if I need to make any announcements...  " + timeNow);
        try {
            mfConnection = Database.getInstance().getConnection();
            query = "SELECT * FROM `queue`";
            mfStatement = mfConnection.prepareStatement(query);

            mfResult = mfStatement.executeQuery();
            while (mfResult.next()) {
                if (checkEnabled(mfResult.getString("guildId"))) {
                    String guildId = mfResult.getString("guildId");
                    Integer platformId = mfResult.getInt("platformId");
                    String channelName = mfResult.getString("channelName");
                    String streamTitle = mfResult.getString("streamTitle");
                    String gameName = mfResult.getString("gameName");
                    Integer online = mfResult.getInt("online");

                    DiscordController.messageHandler(guildId, platformId, channelName, streamTitle, gameName, online);
                    TimeUnit.MILLISECONDS.sleep(1100);
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanUp(mfResult, mfStatement, mfConnection);
        }
    }
}
