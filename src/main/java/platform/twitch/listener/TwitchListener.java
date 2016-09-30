/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.twitch.listener;

import platform.twitch.controller.TwitchController;

/**
 *
 * @author keesh
 */
public class TwitchListener {

    private final String twitchClientId;
    private final TwitchController twitchController;

    public TwitchListener(String clientID) {
        // Instantiate TwitchController
        this.twitchController = new TwitchController();
        // Populate class scope of clientID
        this.twitchClientId = clientID;
    }

    /**
     * @return String
     */
    public String channels(String restOfCommand) {
        return twitchController.channels(twitchClientId);
    }

    private void channelsFeed() {

    }

    private void chat() {

    }

    private void follows() {

    }

    private void games() {

    }

    private void search() {

    }

    private void streams() {

    }

    private void teams() {

    }

    private void users() {

    }

    private void videos() {

    }

}
