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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import platform.twitch.models.*;
import util.PropReader;
import util.database.Database;
import util.database.calls.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

/**
 * @author keesh
 */
public class TwitchController {

    private Connection connection;
    private PreparedStatement pStatement;
    private HttpClient client = HttpClientBuilder.create().build();
    private HttpGet get;
    private HttpResponse response;
    private List<String> online = new CopyOnWriteArrayList<>();

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

    private synchronized List<String> checkGameFilters(String guildId) {
        ResultSet result = null;

        try {
            String query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            this.pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new CopyOnWriteArrayList<>();

            if (result != null) {
                while (result.next()) {
                    filters.add(result.getString("gameFilter").replaceAll("''", "'"));
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

    public final synchronized void checkOffline() {
        CountTwitchChannels countTwitchChannels = new CountTwitchChannels();
        Integer amount = countTwitchChannels.streams();

        for (Integer c = 0; c <= amount; c += 100) {

            GetTwitchStreams getTwitchStreams = new GetTwitchStreams();
            HashMap<String, Map<String, String>> offSetStreams = getTwitchStreams.onlineStreams(c);

            StringBuilder channelString = new StringBuilder();

            offSetStreams.values().forEach(channel -> {
                if (channelString.length() > 0) {
                    channelString.append(",");
                }
                channelString.append(channel.get("channelId"));
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

                offSetStreams.values().forEach(
                        dbStream -> {
                            for (Stream stream : streams.getStreams()) {
                                if (streams.getTotal().equals(0) || !dbStream.containsValue(stream.getId())) {
                                    online.add(stream.getId());
                                }
                                if (dbStream.get("channelId").equals(stream.getChannel().getId()) &&
                                        !dbStream.get("streamsId").equals(stream.getId())) {
                                    online.add(stream.getId());
                                }
                            }
                        });
                offSetStreams.values().forEach(dbStream -> {
                    if (!online.contains(dbStream.get("streamsId"))) {
                        UpdateOffline offline = new UpdateOffline();
                        offline.executeUpdate(dbStream.get("streamsId"));
                    }
                });
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
            //System.out.println("Looping.  Last offset: " + c);
        }
    }

    public final synchronized void twitchChannels() {

        CountTwitchChannels countTwitchChannels = new CountTwitchChannels();
        Integer amount = countTwitchChannels.fetch();

        for (Integer c = 0; c <= amount; c += 100) {

            GetTwitchChannels twitchChannels = new GetTwitchChannels();
            List<String> channels = twitchChannels.fetch(c);

            if (channels != null) {
                StringBuilder channelString = new StringBuilder();

                channels.forEach(channel -> {
                    if (channelString.length() > 0) {
                        channelString.append(",");
                    }
                    channelString.append(channel);
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
                    try {
                        Streams streams = objectMapper.readValue(
                                new InputStreamReader(response.getEntity().getContent()), Streams.class
                        );

                        if (streams.getTotal() > 0) {
                            streams.getStreams().forEach(stream -> {
                                GetGuildsByStream guildsByStream = new GetGuildsByStream();
                                CopyOnWriteArrayList<String> guildIds = guildsByStream.fetch(stream.getChannel().getId());

                                for (String guildId : guildIds) {
                                    CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                                    if (!checkTwitchStreams.check(stream.getId(), guildId)) {
                                        onLiveTwitchStream(stream, guildId, "channel");
                                    }
                                }
                            });
                        }
                    } catch (JsonParseException e) {
                        System.out.println(uri);
                        e.printStackTrace();
                    }
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Looping.  Last offset: " + c);
        }
    }

    public final synchronized void twitchTeam(String team, String guildId) {

    }

    public final synchronized void twitchGames() {
        // Retrieve a list of game names
        GetTwitchGames getTwitchGames = new GetTwitchGames();
        List<String> gameList = getTwitchGames.fetch();

        // Iterate through all the games
        if (gameList != null) {
            gameList.forEach(gameName -> {

                URIBuilder uriBuilder = setBaseUrl("/streams");
                try {
                    uriBuilder.setParameter("game", URLEncoder.encode(gameName.replaceAll("''", "'"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                    // Live streams for the game
                    StreamsGames games = objectMapper.readValue(
                            new InputStreamReader(response.getEntity().getContent()), StreamsGames.class
                    );

                    if (games != null && games.getTotal() > 0) {

                        // Find all guilds that track this game
                        GetGuildsByGame guildsByGame = new GetGuildsByGame();
                        CopyOnWriteArrayList<String> guilds = guildsByGame.fetch(gameName);

                        // Add the stream to the twitch streams table for each guild
                        guilds.forEach(
                                guildId -> {
                                    games.getStreams().forEach(
                                            stream -> {
                                                CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();

                                                if (!checkTwitchStreams.check(stream.getId(), guildId)) {
                                                    onLiveTwitchStream(stream, guildId, "game");
                                                }
                                            });
                                });
                    }
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Method by Hopewell
     *
     * @param guildId Guild Id
     * @param stream  Stream object
     * @return boolean
     */
    private synchronized boolean filterCheck(String guildId, Stream stream) {
        List<String> filters = checkGameFilters(guildId);
        if (filters == null || filters.isEmpty()) {
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
     * @param stream  Stream object
     * @param guildId Guild Id
     */
    private synchronized void onLiveTwitchStream(Stream stream, String guildId, String flag) {
        GetBroadcasterLang getBroadcasterLang = new GetBroadcasterLang();
        String lang = getBroadcasterLang.action(guildId);

        if (lang != null &&
                (lang.equalsIgnoreCase(stream.getChannel().getBroadcasterLanguage()) || "all".equals(lang))) {
            if (stream.getChannel().getStatus() != null && stream.getGame() != null) {
                CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                if (filterCheck(guildId, stream) && !checkTwitchStreams.check(stream.getId(), guildId)) {
                    new AddTwitchStream(guildId, stream, flag);
                }
            }
        }
    }
}
