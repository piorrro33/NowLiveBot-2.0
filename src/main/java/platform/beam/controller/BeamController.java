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

package platform.beam.controller;

import platform.beam.http.ApiRequest;
import platform.beam.http.BeamChannel;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class BeamController {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;

    private static synchronized List<String> checkFilters(String guildId) {
        System.out.println("Checking for filters...");
        try {
            String query = "SELECT * FROM `filter` WHERE `guildId` = ?";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            result = pStatement.executeQuery();

            List<String> filters = new CopyOnWriteArrayList<>();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    filters.add(result.getString("name"));
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

    public static synchronized Boolean channelExists(String channel) {

        System.out.println("Checking to see if the channel exists...");

        ApiRequest beamRequest = new ApiRequest();

        beamRequest.getChannel(channel);

        return !beamRequest.getResponseCode().equals(404);

    }

    public final synchronized void checkChannel(String channel, String guildId, Integer platformId) {
        System.out.println("Checking Beam channel...");

        BeamChannel beam = new ApiRequest().getChannel(channel);

        if (beam != null &&
                beam.getOnline() &&
                beam.getType().getName() != null &&
                !beam.getType().getName().isEmpty()) {
            System.out.println("Beam returned not null...");
            System.out.println("Channel is online...");
            System.out.println("Game name is not null or empty...");

            List<String> filters = checkFilters(guildId);

            if (filters != null) {
                System.out.println("Filters are present...");
                for (String filter : filters) {
                    if (beam.getType().getName().equalsIgnoreCase(filter)) {
                        System.out.println("Game name matches, announcing stream...");
                        /*DiscordController.announceStream(guildId, getChannelId(guildId), platformId, beam.getToken(),
                                beam.getName(), beam.getType().getName(), "https://beam.pro/" + beam.getToken(),
                                beam.getThumbnail().getUrl(), beam.getCover().getUrl()
                        );*/
                    }
                }
            } else {
                System.out.println("No filters were found, announcing the stream...");
               /* DiscordController.announceStream(guildId, getChannelId(guildId), platformId, beam.getToken(),
                        beam.getName(), beam.getType().getName(), "https://beam.pro/" + beam.getToken(),
                        beam.getThumbnail().getUrl(), beam.getCover().getUrl()
                );*/
            }
        }
    }

}
