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
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
    private ConcurrentHashMap<Integer, Stream> online = new ConcurrentHashMap<>();
    protected Integer count = 0;

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

            if (idConversion.getTotal() != null && idConversion.getTotal() > 0) {
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

            if (result.isBeforeFirst()) {
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

    private synchronized void channels() {
        CountTwitchChannels countTwitchChannels = new CountTwitchChannels();
        Integer amount = countTwitchChannels.fetch();


        System.out.println(amount);
        CopyOnWriteArrayList<String> streamChannelIds = new CopyOnWriteArrayList<>();
        CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
        GetTwitchChannels twitchChannels = new GetTwitchChannels();
        GetGuildsByStream guildsByStream = new GetGuildsByStream();
        StringBuilder channelString = new StringBuilder();

        CopyOnWriteArrayList<String> channels = twitchChannels.fetch(1);

        if (channels != null && channels.size() > 0) {

            channels.forEach(channel -> {
                if (channelString.length() > 0) {
                    channelString.append(",");
                }
                channelString.append(channel);
                streamChannelIds.add(channel);

                if (streamChannelIds.size() == 100) {

                    URIBuilder uriBuilder = setBaseUrl("/streams");
                    uriBuilder.setParameter("channel", channelString.toString());
                    uriBuilder.setParameter("limit", "100");

                    channelString.setLength(0); // Clear the variable for the GC

                    URI uri = null;
                    try {
                        uri = uriBuilder.build();
                    } catch (URISyntaxException e) {
                        System.out.println("[~ERROR~] Malformed URI found.");
                        e.printStackTrace();
                    }

                    // A little snooze to make sure to be in compliance with Twitch's Rate limiting policy
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    get = new HttpGet(uri);
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                    get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));

                    try {
                        response = client.execute(get);
                    } catch (IOException e) {
                        System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                        e.printStackTrace();
                    }

                    ObjectMapper objectMapper = new ObjectMapper();

                    Streams streams = null;
                    try {
                        streams = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), Streams.class);
                    } catch (JsonParseException jpe) {
                        System.out.println("[~ERROR~] Json Parse Exception found when parsing HTTP Response");
                        System.out.println(uri);
                        jpe.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                        e.printStackTrace();
                    }

                    // Checks to make sure that some streams are returned
                    if (streams != null && streams.getTotal() != null && streams.getTotal() > 0) {// Make sure the returned object is not null

                        List<Stream> onlineStreams = streams.getStreams();

                        onlineStreams.forEach(stream -> {
                            if (streamChannelIds.contains(stream.getChannel().getId())) {
                                streamChannelIds.remove(stream.getChannel().getId());//Leftovers are offline
                            }

                            CopyOnWriteArrayList<String> guildIds = guildsByStream.fetch(stream.getChannel().getId());

                            if (guildIds != null && guildIds.size() > 0) {
                                guildIds.forEach(guildId -> {
                                    if (!checkTwitchStreams.check(stream.getChannel().getId(), guildId)) {
                                        Stream updatedStream = stream;
                                        updatedStream.setAdditionalProperty("guildId", guildId);

                                        onLiveTwitchStream(updatedStream);
                                    }
                                });
                            }
                        });

                        // Set streams offline
                        if (streamChannelIds.size() > 0) {
                            UpdateOffline offline = new UpdateOffline();
                            offline.executeUpdate(streamChannelIds);
                        }
                        if (online.size() > 0) {
                            new AddTwitchStream(online, "channel");
                            online.clear();
                        }
                    }
                    streamChannelIds.clear();
                    channelString.setLength(0);
                }
            });
        }
    }

    private synchronized void games() {
        CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
        GetTwitchStreams getGameStreams = new GetTwitchStreams();
        GetGuildsByGame guildsByGame = new GetGuildsByGame();

        // Retrieve a list of game names
        GetTwitchGames getTwitchGames = new GetTwitchGames();
        List<String> gameList = getTwitchGames.fetch();

        if (gameList != null && gameList.size() > 0) {
            gameList.forEach(gameName -> {
                StreamsGames games = null;

                URIBuilder uriBuilder = setBaseUrl("/streams");
                uriBuilder.setParameter("game", gameName.replaceAll("''", "'"));
                uriBuilder.setParameter("limit", "100");
                uriBuilder.setParameter("offset", "0");

                URI uri = null;
                try {
                    uri = uriBuilder.build();
                } catch (URISyntaxException e) {
                    System.out.println("[~ERROR~] Malformed URI found.");
                    e.printStackTrace();
                }

                get = new HttpGet(uri);
                get.addHeader("Cache-Control", "no-cache");
                get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));
                try {
                    response = client.execute(get);
                } catch (IOException e) {
                    System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                    e.printStackTrace();
                }

                ObjectMapper objectMapper = new ObjectMapper();
                // Live streams for the game
                try {
                    games = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), StreamsGames.class);
                } catch (JsonParseException jpe) {
                    System.out.println("[~ERROR~] Json Parse Exception found when parsing HTTP Response");
                    System.out.println(uri);
                    jpe.printStackTrace();
                } catch (IOException e) {
                    System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                    e.printStackTrace();
                }

                if (games != null && games.getTotal() != null && games.getTotal() > 0) {
                    List<String> guilds = guildsByGame.fetch(gameName);
                    CopyOnWriteArrayList<String> gameChannelIds = getGameStreams.gameStreams(gameName);

                    if (guilds != null && guilds.size() > 0) {
                        List<Stream> gameStreamers = games.getStreams();

                        guilds.forEach(guildId -> gameStreamers.forEach(stream -> {

                            // Remove online streams
                            if (gameChannelIds != null && gameChannelIds.contains(stream.getChannel().getId())) {
                                gameChannelIds.remove(stream.getChannel().getId());//Leftover is offline streams
                            }

                            if (!checkTwitchStreams.check(stream.getChannel().getId(), guildId)) {
                                stream.setAdditionalProperty("guildId", guildId);
                                onLiveTwitchStream(stream);
                            }
                        }));
                    }
                    // Set streams offline
                    if (gameChannelIds != null && gameChannelIds.size() > 0) {
                        UpdateOffline offline = new UpdateOffline();
                        offline.executeUpdate(gameChannelIds);
                    }
                    if (online.size() > 0) {
                        new AddTwitchStream(online, "game");
                        online.clear();
                    }
                }
            });
        }
    }

    public final synchronized void checkLiveStreams() {
        channels();
        games();

    }

    public final synchronized void twitchTeam(String team, String guildId) {

    }

    /**
     * Method by Hopewell
     *
     * @param stream  Stream object
     * @return boolean
     */
    private synchronized boolean filterCheck(Stream stream) {
        String guildId = stream.getAdditionalProperties().get("guildId").toString();

        List<String> filters = checkGameFilters(guildId);
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
     * @param stream  Stream object
     */
    private synchronized void onLiveTwitchStream(Stream stream) {
        String guildId = stream.getAdditionalProperties().get("guildId").toString();

        GetBroadcasterLang getBroadcasterLang = new GetBroadcasterLang();
        String lang = getBroadcasterLang.action(guildId);

        if (lang != null &&
                (lang.equalsIgnoreCase(stream.getChannel().getBroadcasterLanguage()) || "all".equals(lang))) {
            if (stream.getChannel().getStatus() != null && stream.getGame() != null) {
                if (filterCheck(stream)) {
                    online.put(count, stream);
                    count++;
                }
            }
        }
    }
}
