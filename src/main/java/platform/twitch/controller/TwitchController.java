/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.twitch.controller;

import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.GamesResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.handlers.StreamsResponseHandler;
import com.mb3364.twitch.api.models.Game;
import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.Streams;
import platform.generic.controller.PlatformController;
import util.PropReader;

import java.util.List;

/**
 * @author keesh
 */
public class TwitchController extends Twitch {

    private PlatformController pController = new PlatformController();

    public TwitchController() {
        this.setClientId(PropReader.getInstance().getProp().getProperty("twitch.client.id"));
    }

    public synchronized void checkChannel(String channelName, String guildId, Integer platformId) {

        // Grab the stream info
        this.streams().get(channelName, new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream) { // If the stream has been found
                if (stream != null) { // check if the stream is online
                    String channelName = stream.getChannel().getDisplayName();
                    String streamTitle = stream.getChannel().getStatus();
                    String gameName = stream.getGame();
                    // Integer viewers = stream.getViewers();

                    // Send the stream info to the stream queue
                    pController.streamToQueue(guildId, platformId, channelName, streamTitle, gameName);
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

    public synchronized void checkGame(String gameName, String guildId, Integer platformId) {

        // Grab the stream info
        RequestParams params = new RequestParams();
        params.put("limit", 100);

        this.search().streams(gameName, params, new StreamsResponseHandler() {
            @Override
            public void onSuccess(int i, List<Stream> list) {
                for (Stream stream : list) {
                    stream.getChannel().getDisplayName();
                    System.out.println(stream.getChannel().getDisplayName() + " is playing " + gameName);
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
