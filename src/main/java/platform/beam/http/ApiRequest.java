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
        String url = SCHEME + AUTHORITY + PATH + channel;

        HttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
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
                    getxRateLimitReset()!= null &&
                    Integer.parseInt(getxRateLimitRemaining()) <= 1) {
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
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new InputStreamReader(response.getEntity().getContent()), BeamChannel.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
