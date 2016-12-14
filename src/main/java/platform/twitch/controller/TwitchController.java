/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.twitch.controller;

import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import core.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import util.PropReader;
import util.database.Database;
import util.database.calls.GetBroadcasterLang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static platform.discord.controller.DiscordController.announceStream;
import static platform.discord.controller.DiscordController.getChannelId;
import static platform.generic.controller.PlatformController.checkStreamTable;
import static util.database.Database.cleanUp;

/**
 * @author keesh
 */
public class TwitchController extends Twitch {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;
    private Logger logger = LoggerFactory.getLogger("Twitch Controller");

    public TwitchController() {
        this.setClientId(PropReader.getInstance().getProp().getProperty("twitch.client.id"));
    }

    private static synchronized List<String> checkFilters(String guildId) {
        try {
            String query = "SELECT * FROM `filter` WHERE `guildId` = ?";

            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new ArrayList<>();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    filters.add(result.getString("name"));
                }
                return filters;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        return null;
    }

    public final synchronized Boolean checkExists(String channelName) {
        final Boolean[] exists = {false};
        this.channels().get(channelName, new ChannelResponseHandler() {
            @Override
            public void onSuccess(Channel channel) {
                logger.info("Channel exists!");
                exists[0] = true;
            }

            @Override
            public void onFailure(int i, String s, String s1) {
                exists[0] = false;
            }

            @Override
            public void onFailure(Throwable throwable) {
                exists[0] = false;
            }
        });
        return exists[0].equals(true);
    }

    public final synchronized void checkOffline(String channelName, String guildId, Integer platformId) {
        this.streams().get(channelName, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) {
                if (stream == null) {
                    DiscordController.offlineStream(guildId, platformId, channelName, DiscordController.getChannelId(guildId));
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    public final synchronized void checkChannel(String channelName, String guildId, Integer platformId) {

        // Grab the stream info
        this.streams().get(channelName, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) { // If the stream has been found
                // check if the stream is online
                if (stream != null) {
                    // Check for tracked broadcaster languages
                    String casterLang = GetBroadcasterLang.action(guildId);
                    if (casterLang.equals(stream.getChannel().getBroadcasterLanguage()) || "all".equals(casterLang)) {
                        // check if the status and game name are not null
                        if (stream.getChannel().getStatus() != null &&
                                stream.getGame() != null &&
                                !checkStreamTable(guildId, platformId, stream.getChannel().getName())) {
                            // Checking filters
                            List<String> filters = checkFilters(guildId);
                            if (filters != null) {
                                for (String filter : filters) {
                                    if (stream.getGame().equalsIgnoreCase(filter)) {
                                        // If the game filter is equal to the game being played, announce the stream
                                        announceStream(
                                                guildId,
                                                getChannelId(guildId),
                                                platformId,
                                                stream.getChannel().getName(),
                                                stream.getChannel().getStatus(),
                                                stream.getGame(),
                                                stream.getChannel().getUrl(),
                                                stream.getChannel().getLogo(),
                                                stream.getChannel().getProfileBanner()
                                        );
                                    }
                                }
                            } else {
                                // If no filters are set, announce the channel
                                announceStream(
                                        guildId,
                                        getChannelId(guildId),
                                        platformId,
                                        stream.getChannel().getName(),
                                        stream.getChannel().getStatus(),
                                        stream.getGame(),
                                        stream.getChannel().getUrl(),
                                        stream.getChannel().getLogo(),
                                        stream.getChannel().getProfileBanner()
                                );
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    public final synchronized void checkGame(String gameName, String guildId, Integer platformId) {

        // Grab the stream info
        for (int offset = 0; offset <= 1000; offset += 100) {
            RequestParams params = new RequestParams();
            params.put("limit", 100);
            params.put("offset", offset);

            this.search().streams(gameName, params, new StreamsResponseHandler() {
                @Override
                public void onSuccess(int total, List<Stream> list) {
                    for (Stream stream : list) {
                        if (!checkStreamTable(guildId, platformId, stream.getChannel().getName()) &&
                                stream.getGame().equalsIgnoreCase(gameName)) {
                            checkChannel(stream.getChannel().getName(), guildId, platformId);
                        }
                    }
                }

                @Override
                public void onFailure(int i, String s, String s1) {
                    if (Main.debugMode()) {
                        logger.info("onFailure: " + i + " : String1: " + s + " : String2: " + s1);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    logger.error("onFailure: ", throwable);
                }
            });
        }
    }
}
