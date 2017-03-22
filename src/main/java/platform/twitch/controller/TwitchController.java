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
import platform.twitch.models.CommunityByName;
import platform.twitch.models.IdConversion;
import platform.twitch.models.Team;
import util.ExceptionHandlerNoRestart;
import util.PropReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author keesh
 */
public class TwitchController {

    private HttpClient client = HttpClientBuilder.create().build();
    private HttpGet get;
    private HttpResponse response;

    public TwitchController() {
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandlerNoRestart());
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
}
