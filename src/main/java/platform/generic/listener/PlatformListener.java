/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package platform.generic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.twitch.controller.TwitchController;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.GetOnlineStreams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener {
    private static Logger logger = LoggerFactory.getLogger("Platform Listener");
    private static Connection clgConnection;
    private static PreparedStatement clgStatement;
    private static ResultSet clgResult;
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public PlatformListener() {

        try {
            executor.scheduleWithFixedDelay(this::run, 0, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("******************* Caught an exception while keeping the executors active ", e);
            logger.info("Attempting to restart the executors...");
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

        Integer platformId = 1;
        switch (platformId) {
            case 1:
                TwitchController twitch = new TwitchController();
                twitch.checkChannel(platformId);
                break;
            case 2:
                //System.out.println("Found a Beam channel, starting the announcement checking process...");
                            /*new BeamController().checkChannel(clcResult.getString("name"), clcResult.getString("guildId"),
                                    clcResult.getInt("platformId"));*/
                //System.out.println();
                break;
            default:
                break;
        }
    }

    private synchronized void checkLiveGames() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking for live games...**", null);
        System.out.println("[SYSTEM] Checking for live games. " + timeNow);
        try {
            String query = "SELECT * FROM `game` ORDER BY `guildId` ASC";

            if (clgConnection == null || clgConnection.isClosed()) {
                clgConnection = Database.getInstance().getConnection();
            }
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
                        //BeamController beam = new BeamController();
                        /*beam.checkGame(clcResult.getString("name"), clcResult.getString("guildId"),
                                clcResult.getInt("platformId"));*/

                        break;
                    default:
                        break;
                }
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

        GetOnlineStreams onlineStreams = new GetOnlineStreams();
        HashMap<String, Map<String, String>> streams = onlineStreams.getOnlineStreams(1);

        TwitchController twitch = new TwitchController();
        twitch.checkOffline(streams);
    }
}
