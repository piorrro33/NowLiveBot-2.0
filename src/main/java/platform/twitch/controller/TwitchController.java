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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import platform.discord.controller.DiscordController;
import platform.twitch.models.*;
import util.PropReader;
import util.database.Database;
import util.database.calls.CountDbChannels;
import util.database.calls.GetBroadcasterLang;
import util.database.calls.GetDbChannels;
import util.database.calls.GetGuildsByStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static platform.discord.controller.DiscordController.getChannelId;
import static util.database.Database.cleanUp;

/**
 * @author keesh
 */
public class TwitchController {

    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;
    private HttpClient client = HttpClientBuilder.create().build();
    private HttpGet get;
    private HttpResponse response;

    public TwitchController() {
    }

    private synchronized URIBuilder setBaseUrl(String endpoint) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("api.twitch.tv").setPath("/kraken" + endpoint);
        return builder;
    }

    public synchronized String convertNameToId(String name) {
        URIBuilder uriBuilder = setBaseUrl("/users");
        uriBuilder.setParameter("login", name);

        try {
            URI uri = uriBuilder.build();
            get = new HttpGet(uri);
            get.addHeader("Cache-Control", "no-cache");
            get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
            get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
            response = client.execute(get);

            ObjectMapper objectMapper = new ObjectMapper();
            IdConversion idConversion = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()), IdConversion.class);

            if (idConversion.getTotal() != null && idConversion.getTotal().equals(1)) {
                return idConversion.getUsers().get(0).getId();
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized List<User> convertNameToId(List<String> names) {
        URIBuilder uriBuilder = setBaseUrl("/users");
        StringBuilder nameList = new StringBuilder();
        names.forEach(name -> {
            if (nameList.length() > 0) {
                nameList.append(",");
            }
            nameList.append(name);
        });
        uriBuilder.setParameter("login", nameList.toString());

        try {
            URI uri = uriBuilder.build();
            get = new HttpGet(uri);
            get.addHeader("Cache-Control", "no-cache");
            get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
            get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
            response = client.execute(get);

            ObjectMapper objectMapper = new ObjectMapper();
            IdConversion idConversion = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()), IdConversion.class);

            if (idConversion.getTotal() > 0) {
                return idConversion.getUsers();
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized List<String> checkFilters(String guildId) {
        ResultSet result = null;

        try {
            String query = "SELECT * FROM `filter` WHERE `guildId` = ?";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new CopyOnWriteArrayList<>();

            if (result != null) {
                while (result.next()) {
                    filters.add(result.getString("name").replaceAll("''", "'"));
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

    public final synchronized void checkOffline(HashMap<String, Map<String, String>> streams) {

        streams.forEach((String messageId, Map<String, String> streamData) -> {

            URIBuilder uriBuilder = setBaseUrl("/streams");
            uriBuilder.setParameter("channel", streamData.get("channelId"));

            try {
                URI uri = uriBuilder.build();
                get = new HttpGet(uri);
                get.addHeader("Cache-Control", "no-cache");
                get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
                response = client.execute(get);

                ObjectMapper objectMapper = new ObjectMapper();
                Streams stream = objectMapper.readValue(
                        new InputStreamReader(response.getEntity().getContent()), Streams.class
                );

                if (stream.getTotal().equals(0)) {
                    DiscordController discord = new DiscordController();
                    discord.offlineStream(streamData);
                }
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public final synchronized void checkChannel(Integer platformId) {

        CountDbChannels countDbChannels = new CountDbChannels();
        Integer amount = countDbChannels.fetch();

        for (Integer c = 0; c <= amount; c += 100) {

            GetDbChannels dbChannels = new GetDbChannels();
            List<User> channels = convertNameToId(dbChannels.fetch(c));

            if (channels != null) {
                StringBuilder channelString = new StringBuilder();

                channels.forEach(channel -> {
                    if (channelString.length() > 0) {
                        channelString.append(",");
                    }
                    channelString.append(channel.getId());
                });

                URIBuilder uriBuilder = setBaseUrl("/streams");
                uriBuilder.setParameter("channel", channelString.toString());
                uriBuilder.setParameter("limit", "100");

                try {
                    URI uri = uriBuilder.build();
                    get = new HttpGet(uri);
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                    get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
                    response = client.execute(get);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Streams streams = objectMapper.readValue(
                            new InputStreamReader(response.getEntity().getContent()), Streams.class
                    );

                    if (streams.getTotal() > 0) {
                        DiscordController discord = new DiscordController();
                        streams.getStreams().forEach(stream -> {
                            GetGuildsByStream guildsByStream = new GetGuildsByStream();
                            CopyOnWriteArrayList<String> guildIds = guildsByStream.fetch(stream.getChannel().getName());

                            guildIds.forEach(guildId -> onLiveStream(stream, guildId, platformId, discord));
                        });
                    }
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final synchronized void checkTeam(String team, String guildId, Integer platformId) {

    }

    public final synchronized void checkGame(String gameName, String guildId, Integer platformId) {

        int[] values = new int[]{0, 0};

        //for (int count = 0; count < values[1]; count += 100) {
            URIBuilder uriBuilder = setBaseUrl("/streams");
            uriBuilder.setParameter("game", gameName);
            uriBuilder.setParameter("limit", "100");
            uriBuilder.setParameter("offset", "0");

            try {
                URI uri = uriBuilder.build();
                get = new HttpGet(uri);
                get.addHeader("Cache-Control", "no-cache");
                get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
                response = client.execute(get);

                ObjectMapper objectMapper = new ObjectMapper();
                StreamsGames games = objectMapper.readValue(
                        new InputStreamReader(response.getEntity().getContent()), StreamsGames.class
                );

                if (games.getTotal() > 0) {
                    if (values[1] == 0) {
                        values[1] = games.getTotal();
                    }
                    DiscordController discord = new DiscordController();
                    games.getStreams().forEach(stream -> onLiveStream(stream, guildId, platformId, discord));
                }
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        //}
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
                }
            }
        }
    }
}
