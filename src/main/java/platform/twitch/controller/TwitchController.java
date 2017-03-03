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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import platform.twitch.models.*;
import util.PropReader;
import util.database.calls.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * @author keesh
 */
public class TwitchController {

    protected Integer count = 0;
    private HttpClient client = HttpClientBuilder.create().build();
    private HttpGet get;
    private HttpResponse response;
    private CopyOnWriteArrayList<Stream> online = new CopyOnWriteArrayList<>();

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

    private synchronized void channels(CopyOnWriteArrayList<String> channelIds,String flag, String value) {

        CopyOnWriteArrayList<String> streamChannelIds = new CopyOnWriteArrayList<>();
        CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
        GetTwitchChannels twitchChannels = new GetTwitchChannels();
        StringBuilder channelString = new StringBuilder();

        CopyOnWriteArrayList<String> channels;
        if (flag.equals("channel")) {
            channels = twitchChannels.fetch(0);
        } else {
            channels = channelIds;
        }
        if (channels != null && channels.size() > 0) {

            channels.forEach(channel -> {
                if (channelString.length() > 0) {
                    channelString.append(",");
                }
                channelString.append(channel);
                streamChannelIds.add(channel);

                if (streamChannelIds.size() == 100 || channels.size() == streamChannelIds.size()) {
                    GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();

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
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    get = new HttpGet(uri);
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                    get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));

                    try {
                        response = client.execute(get);
                    } catch (ClientProtocolException cpe) {
                        System.out.println("[~ERROR~] HTTP Protocol Error when checking Channels");
                        cpe.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                        e.printStackTrace();
                    }
                    ObjectMapper objectMapper = new ObjectMapper();

                    Streams streams = null;
                    try {
                        streams = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), Streams.class);
                    } catch (UnsupportedOperationException uoe) {
                        System.out.println("[~ERROR~] Unsupported Operation Exception - Content does not match");
                        System.out.println(uri);
                        uoe.printStackTrace();
                    } catch (JsonMappingException jme) {
                        System.out.println("[~ERROR~] The input JSON structure (Stream) does not match structure expected");
                        System.out.println(uri);
                        jme.printStackTrace();
                    } catch (JsonParseException jpe) {
                        System.out.println("[~ERROR~] The underlying input contains invalid content");
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

                            CopyOnWriteArrayList<String> guildIds;
                            switch (flag) {
                                case "channel":
                                    GetGuildsByStream guildsByStream = new GetGuildsByStream();
                                    guildIds = guildsByStream.fetch(stream.getChannel().getId());
                                    break;
                                case "community":
                                    GetGuildsByTeamCommunity guildsByCommunity = new GetGuildsByTeamCommunity();
                                    guildIds = guildsByCommunity.fetch("community", value);
                                    break;
                                default:
                                    GetGuildsByTeamCommunity guildsByTeam = new GetGuildsByTeamCommunity();
                                    guildIds = guildsByTeam.fetch("team", value);
                                    break;
                            }

                            if (guildIds != null && guildIds.size() > 0) {
                                guildIds.forEach(guildId -> {
                                    if (!checkTwitchStreams.check(stream.getChannel().getId(), guildId)) {
                                        stream.setAdditionalProperty("guildId", guildId);

                                        switch (flag) {
                                            case "channel":
                                                stream.setAdditionalProperty("announceChannel",
                                                        getAnnounceChannel.action(guildId, flag, stream.getChannel().getId()));
                                                onLiveTwitchStream(stream, flag, null);
                                                break;
                                            case "community":
                                                stream.setAdditionalProperty("announceChannel",
                                                        getAnnounceChannel.action(guildId, flag, value));
                                                onLiveTwitchStream(stream, flag, value);
                                                break;
                                            default:
                                                stream.setAdditionalProperty("announceChannel",
                                                        getAnnounceChannel.action(guildId, flag, value));
                                                onLiveTwitchStream(stream, flag, value);
                                                break;
                                        }
                                    }
                                });
                            }
                        });
                        // Set streams offline
                        if (streamChannelIds.size() > 0) {
                            UpdateOffline offline = new UpdateOffline();
                            offline.executeUpdate(streamChannelIds);
                        }
                    }
                    streamChannelIds.clear();
                    channelString.setLength(0);
                }
            });
            if (online.size() > 0) {
                switch (flag) {
                    case "channel":
                        new AddTwitchStream(online, "channel");
                        break;
                    case "community":
                        new AddTwitchStream(online, "community");
                        break;
                    default:
                        new AddTwitchStream(online, "team");
                        break;
                }
                online.clear();
                this.count = 0;
            }
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
            GetAnnounceChannel getAnnounceChannel = new GetAnnounceChannel();

            gameList.forEach(gameName -> {
                StreamsGames games = null;

                URIBuilder uriBuilder = setBaseUrl("/streams");
                try {
                    uriBuilder.setParameter("game", gameName.replaceAll("''", "'"));
                } catch (PatternSyntaxException pse) {
                    System.out.println("[~ERROR~] Invalid Regex syntax");
                    pse.printStackTrace();
                }
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
                } catch (ClientProtocolException cpe) {
                    System.out.println("[~ERROR~] HTTP Protocol Error when checking Games");
                    cpe.printStackTrace();
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
                                stream.setAdditionalProperty("announceChannel",
                                        getAnnounceChannel.action(guildId, "game", stream.getChannel().getId()));

                                onLiveTwitchStream(stream, "game", null);
                            }
                        }));
                    }
                    // Set streams offline
                    if (gameChannelIds != null && !gameChannelIds.isEmpty() && gameChannelIds.size() > 0) {
                        UpdateOffline offline = new UpdateOffline();
                        offline.executeUpdate(gameChannelIds);
                    }
                }
            });
            if (online.size() > 0) {
                new AddTwitchStream(online, "game");
                online.clear();
                this.count = 0;
            }
        }
    }

    public final synchronized void checkLiveStreams() {
        System.out.println("Checking channels");
        channels(new CopyOnWriteArrayList<>(), "channel", null);
        System.out.println("Checking teams");
        teams();
        System.out.println("Checking communities");
        communities();
        System.out.println("Checking games");
        games();
    }

    private synchronized void communities() {
        GetTwitchCommunities twitchCommunities = new GetTwitchCommunities();
        ConcurrentHashMap<String,String> communities = twitchCommunities.fetch();

        if (communities != null && !communities.isEmpty() && communities.size() > 0) {
            communities.forEach((String communityName, String communityId) -> {
                if (communityId != null) {
                    URIBuilder uriBuilder = setBaseUrl("/streams");
                    uriBuilder.setParameter("community_id", communityId);

                    URI uri = null;
                    try {
                        uri = uriBuilder.build();
                    } catch (URISyntaxException e) {
                        System.out.println("[~ERROR~] Malformed URI found.");
                        e.printStackTrace();
                    }

                    // A little snooze to make sure to be in compliance with Twitch's Rate limiting policy
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    get = new HttpGet(uri);
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                    get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));

                    try {
                        response = client.execute(get);
                    } catch (ClientProtocolException cpe) {
                        System.out.println("[~ERROR~] HTTP Protocol Error when checking Communities");
                        cpe.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                        e.printStackTrace();
                    }

                    if (response.getStatusLine().getStatusCode() == 200) {
                        ObjectMapper objectMapper = new ObjectMapper();

                        Streams streams = null;
                        try {
                            streams = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), Streams.class);
                        } catch (JsonMappingException jme) {
                            System.out.println("[~ERROR~] The input JSON structure (Communities) does not match structure expected");
                            System.out.println(uri);
                            jme.printStackTrace();
                        } catch (JsonParseException jpe) {
                            System.out.println("[~ERROR~] The underlying input contains invalid content");
                            System.out.println(uri);
                            jpe.printStackTrace();
                        } catch (IOException e) {
                            System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                            e.printStackTrace();
                        }

                        if (streams != null && streams.getTotal() > 0) {
                            CopyOnWriteArrayList<String> communityList = new CopyOnWriteArrayList<>();
                            streams.getStreams().forEach(stream -> {
                                communityList.add(stream.getChannel().getId());
                                if (communityList.size() == 100) {
                                    channels(communityList, "community", communityName);
                                    communityList.clear();
                                }
                            });
                            channels(communityList, "community", communityName);
                        }

                    } else if (response.getStatusLine().getStatusCode() == 404) {
                        System.out.printf("Community %s not found.", communityName);
                    }
                }
            });
        }
    }

    public final synchronized Integer getTeamId(String teamName) {
        if (teamName != null) {
            URIBuilder uriBuilder = setBaseUrl("/teams/" + teamName);

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
            } catch (ClientProtocolException cpe) {
                System.out.println("[~ERROR~] HTTP Protocol Error when checking Team ID");
                cpe.printStackTrace();
            } catch (IOException e) {
                System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                e.printStackTrace();
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();

                Team team = null;
                try {
                    team = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), Team.class);
                } catch (JsonMappingException jme) {
                    System.out.println("[~ERROR~] The input JSON structure (Teams ID) does not match structure expected");
                    System.out.println(uri);
                    jme.printStackTrace();
                } catch (JsonParseException jpe) {
                    System.out.println("[~ERROR~] The underlying input contains invalid content");
                    System.out.println(uri);
                    jpe.printStackTrace();
                } catch (IOException e) {
                    System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                    e.printStackTrace();
                }

                if (team != null) {
                    return team.getId();
                }

            } else if (response.getStatusLine().getStatusCode() == 404) {
                System.out.printf("Team %s not found.%n", teamName);
                return -1;
            }
        }
        return -1;
    }

    public final synchronized String getCommunityId(String communityName) {
        if (communityName != null) {
            URIBuilder uriBuilder = setBaseUrl("/communities");
            uriBuilder.setParameter("name", communityName);

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
            } catch (ClientProtocolException cpe) {
                System.out.println("[~ERROR~] HTTP Protocol Error when checking Community ID");
                cpe.printStackTrace();
            } catch (IOException e) {
                System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                e.printStackTrace();
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();

                CommunityByName community = null;
                try {
                    community = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), CommunityByName.class);
                } catch (JsonMappingException jme) {
                    System.out.println("[~ERROR~] The input JSON structure (Community By Name) does not match structure expected");
                    System.out.println(uri);
                    jme.printStackTrace();
                } catch (JsonParseException jpe) {
                    System.out.println("[~ERROR~] The underlying input contains invalid content");
                    System.out.println(uri);
                    jpe.printStackTrace();
                } catch (IOException e) {
                    System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                    e.printStackTrace();
                }

                if (community != null) {
                    return community.getId();
                }

            } else if (response.getStatusLine().getStatusCode() == 404) {
                System.out.printf("Team %s not found.%n", communityName);
                return null;
            }
        }
        return null;
    }

    private synchronized void teams() {
        GetTwitchTeams twitchTeams = new GetTwitchTeams();

        CopyOnWriteArrayList<String> teams = twitchTeams.fetch();

        if (teams != null && !teams.isEmpty() && teams.size() > 0) {
            teams.forEach(teamName -> {
                if (teamName != null) {
                    URIBuilder uriBuilder = setBaseUrl("/teams/" + teamName);

                    URI uri = null;
                    try {
                        uri = uriBuilder.build();
                    } catch (URISyntaxException e) {
                        System.out.println("[~ERROR~] Malformed URI found.");
                        e.printStackTrace();
                    }

                    // A little snooze to make sure to be in compliance with Twitch's Rate limiting policy
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    get = new HttpGet(uri);
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Accept", "application/vnd.twitchtv.v5+json");
                    get.addHeader("Client-ID", PropReader.getInstance().getProp().getProperty("twitch.client.id"));

                    try {
                        response = client.execute(get);
                    } catch (ClientProtocolException cpe) {
                        System.out.println("[~ERROR~] HTTP Protocol Error when checking Teams");
                        cpe.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("[~ERROR~] Input/Output Exception when getting HTTP Response");
                        e.printStackTrace();
                    }

                    if (response.getStatusLine().getStatusCode() == 200) {
                        ObjectMapper objectMapper = new ObjectMapper();

                        Team team = null;
                        try {
                            team = objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), Team.class);
                        } catch (JsonMappingException jme) {
                            System.out.println("[~ERROR~] The input JSON structure (Teams) does not match structure expected");
                            System.out.println(uri);
                            jme.printStackTrace();
                        } catch (JsonParseException jpe) {
                            System.out.println("[~ERROR~] The underlying input contains invalid content");
                            System.out.println(uri);
                            jpe.printStackTrace();
                        } catch (IOException e) {
                            System.out.println("[~ERROR~] Input/Output Exception when reading HTTP Response");
                            e.printStackTrace();
                        }

                        if (team != null && team.getId() > 0) {
                            CopyOnWriteArrayList<String> teamList = new CopyOnWriteArrayList<>();
                            team.getUsers().forEach(user -> {
                                teamList.add(user.getId());
                                if (teamList.size() == 100) {
                                    channels(teamList, "team", teamName);
                                    teamList.clear();
                                }
                            });
                            channels(teamList, "team", teamName);
                        }

                    } else if (response.getStatusLine().getStatusCode() == 404) {
                        System.out.printf("Team %s not found.", teamName);
                    }
                }
            });
        }
    }

    /**
     * Method by Hopewell
     *
     * @param stream Stream object
     */
    private synchronized void onLiveTwitchStream(Stream stream, String flag, String name) {

        GetBroadcasterLang getBroadcasterLang = new GetBroadcasterLang();
        String lang = getBroadcasterLang.action(stream.getAdditionalProperties().get("guildId").toString());

        if (lang != null &&
                (lang.equalsIgnoreCase(stream.getChannel().getBroadcasterLanguage()) || "all".equals(lang))) {
            if (stream.getChannel().getStatus() != null
                    && stream.getGame() != null
                    && !stream.getGame().isEmpty()
                    && stream.getChannel().getGame() != null
                    && !stream.getChannel().getGame().isEmpty()) {
                if (gameFilterCheck(stream, flag, name) && titleFilterCheck(stream, flag, name)) {
                    online.addIfAbsent(stream);
                    count++;
                }
            }
        }
    }

    private synchronized boolean gameFilterCheck(Stream stream, String flag, String name) {
        Boolean specificGameFilter = false;
        Boolean globalGameFilter = false;

        // Check channel specific game filters
        GetSpecificGameFilters specificGameFilters = new GetSpecificGameFilters();
        List<String> gameFilters = specificGameFilters.fetch(stream, flag, name);

        if (gameFilters != null && gameFilters.size() > 0) {
            for (String filter : gameFilters) {
                if (filter.equalsIgnoreCase(stream.getGame())) {
                    specificGameFilter = true;
                }
            }
        } else {
            specificGameFilter = true;
        }

        // Check global game filters
        GetGlobalFilters globalFilters = new GetGlobalFilters();
        List<String> filters = globalFilters.fetch(stream, "game");

        if (filters != null && filters.size() > 0) {
            for (String filter : filters) {
                if (stream.getGame().equalsIgnoreCase(filter)) {
                    globalGameFilter = true;
                }
            }
        } else {
            globalGameFilter = true;
        }

        return specificGameFilter.equals(true) && globalGameFilter.equals(true);
    }

    private synchronized boolean titleFilterCheck(Stream stream, String flag, String name) {
        Boolean specificTitleFilter = false;
        Boolean globalTitleFilter = false;

        // Check channel specific game filters
        GetSpecificTitleFilters specificTitleFilters = new GetSpecificTitleFilters();
        List<String> titleFilters = specificTitleFilters.fetch(stream, flag, name);

        List<String> titleWords = Arrays
                .stream(stream.getChannel().getStatus().split("\\s+"))
                .collect(Collectors.toList());

        if (titleWords != null && !titleWords.isEmpty() && titleWords.size() > 0) {
            if (titleFilters != null && titleFilters.size() > 0) {
                for (String filter : titleFilters) {
                    for (String word : titleWords) {
                        if (word.equalsIgnoreCase(filter)) {
                            specificTitleFilter = true;
                        }
                    }
                }
            } else {
                specificTitleFilter = true;
            }

            // Check global game filters
            GetGlobalFilters globalFilters = new GetGlobalFilters();
            List<String> filters = globalFilters.fetch(stream, "title");

            if (filters != null && filters.size() > 0) {
                for (String filter : filters) {
                    for (String word : titleWords) {
                        if (filter != null && filter.equalsIgnoreCase(word)) {
                            globalTitleFilter = true;
                        }
                    }
                }
            } else {
                globalTitleFilter = true;
            }
        }

        return specificTitleFilter.equals(true) && globalTitleFilter.equals(true);
    }
}
