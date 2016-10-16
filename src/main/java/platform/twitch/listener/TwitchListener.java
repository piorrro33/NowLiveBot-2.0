/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.twitch.listener;

import com.mb3364.twitch.api.Twitch;
import util.PropReader;
import util.database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author keesh
 */
public class TwitchListener extends Twitch {

    public TwitchListener() {

        this.setClientId(PropReader.getInstance().getProp().getProperty("twitch.client.id"));

    }

    public String getTwitchClientId() {
        return this.getClientId();
    }

    public void checkLiveChannels() {
        Connection connection = Database.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
