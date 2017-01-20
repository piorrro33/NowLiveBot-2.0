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

package platform.twitch.controller;

import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
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
                    DiscordController.offlineStream(guildId, platformId, channelName, getChannelId(guildId));
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

        String query = "SELECT * FROM `channel` ORDER BY `name` ASC";
        try {
            connection = Database.getInstance().getConnection();
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Grab the stream info
        this.streams().get(channelName, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) { // If the stream has been found
                // check if the stream is online
                if (stream != null) {
                    // Check for tracked broadcaster languages
                    String casterLang = GetBroadcasterLang.action(guildId);
                    if (casterLang != null &&
                            casterLang.equals(stream.getChannel().getBroadcasterLanguage()) || "all".equals(casterLang)) {
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
                                                stream);
                                    }
                                }
                            } else {
                                // If no filters are set, announce the channel
                                announceStream(
                                        guildId,
                                        getChannelId(guildId),
                                        platformId,
                                        stream);
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
            params.put("game", gameName);

            this.streams().get(params, new StreamsResponseHandler() {
                @Override
                public void onSuccess(int i, List<Stream> list) {
                    for (Stream stream : list) {
                        if (!checkStreamTable(guildId, platformId, stream.getChannel().getName())) {
                            String casterLang = GetBroadcasterLang.action(guildId);
                            if (casterLang != null &&
                                    (casterLang.equalsIgnoreCase(stream.getChannel().getBroadcasterLanguage())
                                            || "all".equals(casterLang))) {
                                if (stream.getChannel().getStatus() != null
                                        && stream.getGame() != null) {
                                    List<String> filters = checkFilters(guildId);
                                    if (filters != null) {
                                        for (String filter : filters) {
                                            if (stream.getGame().equalsIgnoreCase(filter)) {
                                                // If the game filter is equal to the game being played, announce the stream
                                                announceStream(
                                                        guildId,
                                                        getChannelId(guildId),
                                                        platformId,
                                                        stream);
                                            }
                                        }
                                    } else {
                                        // If no filters are set, announce the channel
                                        announceStream(
                                                guildId,
                                                getChannelId(guildId),
                                                platformId,
                                                stream);
                                    }
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
    }
}
