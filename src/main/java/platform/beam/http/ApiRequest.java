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

package platform.beam.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import util.PropReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

/**
 * Custom Beam API setup
 * Created by keesh on 12/12/2016.
 */
public class ApiRequest {

    private final String AUTHORITY = "beam.pro";
    private final String PATH = "/api/v1/channels/";
    private final String SCHEME = "https://";
    private final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private final String ACCEPT_ENCODING = "gzip, deflate, sdch, br";
    private final String CACHE_CONTROL = "no-cache";
    private final String DO_NOT_TRACK = "1";
    private final String PRAGMA = "no-cache";
    private final String UPGRADE_INSECURE_REQUESTS = "1";
    private final String USER_AGENT = "Mozilla/5.0";
    private Integer responseCode;
    private String xRateLimit;
    private String xRateLimitRemaining;
    private String xRateLimitReset;
    private HttpResponse response;
    private String cookie = PropReader.getInstance().getProp().getProperty("beam.cookie");

    public String getxRateLimit() {
        return xRateLimit;
    }

    private void setxRateLimit(String xRateLimit) {
        this.xRateLimit = xRateLimit;
    }

    private String getxRateLimitRemaining() {
        return xRateLimitRemaining;
    }

    private void setxRateLimitRemaining(String xRateLimitRemaining) {
        this.xRateLimitRemaining = xRateLimitRemaining;
    }

    private String getxRateLimitReset() {
        return xRateLimitReset;
    }

    private void setxRateLimitReset(String xRateLimitReset) {
        this.xRateLimitReset = xRateLimitReset;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    private void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public BeamChannel getChannel(String channel) {
        System.out.println("Starting http GET request...");
        String url = SCHEME + AUTHORITY + PATH + channel;

        HttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
        System.out.println("HttpClientBuilder created...");
        HttpGet request = new HttpGet(url);

        request.addHeader("accept", ACCEPT);
        request.addHeader("accept-encoding", ACCEPT_ENCODING);
        request.addHeader("cache-control", CACHE_CONTROL);
        request.addHeader("dnt", DO_NOT_TRACK);
        request.addHeader("pragma", PRAGMA);
        request.addHeader("upgrade-insecure-requests", UPGRADE_INSECURE_REQUESTS);
        request.addHeader("user-agent", USER_AGENT);

        if (cookie != null) {
            request.addHeader("cookie", cookie);
        }

        try {
            response = client.execute(request);
            System.out.println("Response received...");

            // Handle rate limiting
            for (Header header : response.getAllHeaders()) {
                switch (header.getName().toLowerCase()) {
                    case "x-rate-limit":
                        setxRateLimit(header.getValue());
                        break;
                    case "x-ratelimit-remaining":
                        setxRateLimitRemaining(header.getValue());
                        break;
                    case "x-ratelimit-reset":
                        setxRateLimitReset(header.getValue());
                        break;
                    default:
                        break;
                }
            }
            if (getxRateLimitRemaining() != null &&
                    getxRateLimitReset() != null &&
                    Integer.parseInt(getxRateLimitRemaining()) <= 1) {
                System.out.println("Rate limit remaining: " + getxRateLimitRemaining());
                System.out.println("Rate limit reset time: " + getxRateLimitReset());
                while (Instant.now().toEpochMilli() <= Integer.parseInt(getxRateLimitReset())) {
                    // Just wait here for rate limit to expire
                    System.out.print(".");
                }
            }
            setResponseCode(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Trying to read the JSON content from the API response...");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), BeamChannel.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
