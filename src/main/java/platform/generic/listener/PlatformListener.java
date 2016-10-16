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
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        Runnable checkLiveChannels = this::checkLiveChannels;

        Runnable checkLiveGames = this::checkLiveGames;

        int initialDelay = 5;
        int period = 30; // Run this task every minute

        executor.scheduleWithFixedDelay(checkLiveChannels, initialDelay, period, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(checkLiveGames, initialDelay, period, TimeUnit.SECONDS);
    }

    private synchronized void checkLiveChannels() {
        Connection connection = Database.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM `channel` ORDER BY `guildId` ASC";
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                switch (result.getInt("platformId")) {
                    case 1:
                        // Send info to Twitch Controller
                        TwitchController twitch = new TwitchController();
                        twitch.checkChannel(result.getString("name"), result.getString("guildId"), result.getInt
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
