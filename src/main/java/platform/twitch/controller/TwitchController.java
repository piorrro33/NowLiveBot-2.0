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
import util.database.calls.CountDbChannels;
import util.database.calls.GetBroadcasterLang;
import util.database.calls.GetDbChannels;
import util.database.calls.GetGuildsByStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static platform.discord.controller.DiscordController.getChannelId;
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

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
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

    public final synchronized void checkOffline(HashMap<String, Map<String, String>> streams, Integer platformId) {

        streams.forEach((String messageId, Map<String, String> streamData) -> {

            this.streams().get(streamData.get("channelName"), new StreamResponseHandler() {
                @Override
                public void onSuccess(Stream stream) {
                    if (stream == null) {
                        DiscordController discord = new DiscordController();
                        discord.offlineStream(streamData);
                    }
                }

                @Override
                public void onFailure(int i, String s, String s1) {

                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        });
    }

    public final synchronized void checkChannel(Integer platformId) {

        GetDbChannels dbChannels = new GetDbChannels();
        CountDbChannels countDbChannels = new CountDbChannels();
        GetGuildsByStream guildsByStream = new GetGuildsByStream();

        Integer amount = countDbChannels.fetch();

        for (Integer c = 0; c <= amount; c += 100) {
            List<String> channels = dbChannels.fetch(c);

            StringBuilder channelString = new StringBuilder();

            channels.forEach(channel -> {
                if (channelString.length() > 0) {
                    channelString.append(",");
                }
                channelString.append(channel);
            });

            RequestParams params = new RequestParams();
            params.put("channel", channelString.toString());

            this.streams().get(params, new StreamsResponseHandler() {
                @Override
                public void onSuccess(int i, List<Stream> list) {

                    list.forEach(stream -> {

                        List<String> guildIds = guildsByStream.fetch(stream.getChannel().getName());

                        guildIds.forEach(guildId -> onLiveStream(stream, guildId, platformId, new DiscordController()));
                    });
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

    public final synchronized void checkGame(String gameName, String guildId, Integer platformId) {

        int[] values = new int[]{0, 0};

        RequestParams params = new RequestParams();
        params.put("game", gameName);
        params.put("limit", 100);

        //for (int count = 0; count < values[1]; count += 100) {
        params.put("offset", 0);

        this.streams().get(params, new StreamsResponseHandler() {
            @Override
            public void onSuccess(int i, List<Stream> list) {
                if (values[1] == 0) {
                    values[1] = i;
                }
                list.forEach(stream -> onLiveStream(stream, guildId, platformId, new DiscordController()));
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        // }
    }

    /**
     * Method by Hopewell
     *
     * @param guildId Guild Id
     * @param stream  Stream object
     * @return boolean
     */
    private synchronized boolean filterCheck(String guildId, Stream stream) {
        List<String> filters = checkFilters(guildId);
        if (filters == null) {
            return true;
        }
        for (String filter : filters) {
            if (stream.getGame().equalsIgnoreCase(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method by Hopewell
     *
     * @param stream     Stream object
     * @param guildId    Guild Id
     * @param platformId Platform Id
     * @param discord    Discord Controller object
     */
    private synchronized void onLiveStream(Stream stream, String guildId, Integer platformId, DiscordController discord) {
        GetBroadcasterLang getBroadcasterLang = new GetBroadcasterLang();
        String lang = getBroadcasterLang.action(guildId);

        if (lang != null &&
                (lang.equalsIgnoreCase(stream.getChannel().getBroadcasterLanguage()) || "all".equals(lang))) {
            if (stream.getChannel().getStatus() != null && stream.getGame() != null) {
                if (filterCheck(guildId, stream)) {
                    discord.announceStream(guildId, getChannelId(guildId), platformId, stream);
                } else {
                    discord.announceStream(guildId, getChannelId(guildId), platformId, stream);
                }
            }
        }
    }
}
