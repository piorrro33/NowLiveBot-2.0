package platform.generic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.twitch.controller.TwitchController;
import util.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener {

    private static Logger logger = LoggerFactory.getLogger(PlatformListener.class);

    public PlatformListener() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable checkLiveChannels = this::checkLiveChannels;

        int initialDelay = 2; // Wait this long to start (2 seconds is ample when starting up the bot)
        int period = 10; // Run this task every {x} seconds

        logger.info("Starting the executor tasks");

        try {
            executor.scheduleWithFixedDelay(checkLiveChannels, initialDelay, period, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
            new PlatformListener();
        }
    }

    private synchronized void checkLiveChannels() {
        logger.info("Checking if there are any live channels...");

        try {
            Connection connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT `guildId`, `name`, `platformId` FROM `channel` ORDER BY `guildId` ASC";
            ResultSet result = statement.executeQuery(query);
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
            twitch = null;
            Database.cleanUp(result, statement, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void checkLiveGames() {
        Connection connection = Database.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM `game` ORDER BY `guildId` ASC";
            ResultSet result = statement.executeQuery(query);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
