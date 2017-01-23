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

package util.database.calls;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static util.database.Database.cleanUp;

public class GetOnlineStreams {


    private HashMap<String, Map<String, String>> onlineStreams = new HashMap<>();
    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;
    private ResultSet result;

    public GetOnlineStreams() {
    }

    public HashMap<String, Map<String, String>> getOnlineStreams(Integer platformId) {
        onlineStreams(platformId);
        return onlineStreams;
    }

    private synchronized void onlineStreams(Integer platformId) {
        try {
            String query = "SELECT * FROM `streams` WHERE `platformId` = ? ORDER BY `messageId` DESC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, platformId);

            result = pStatement.executeQuery();

            while (result.next()) {
                HashMap<String, String> streamData = new HashMap<>();
                streamData.put("guildId", result.getString("guildId"));
                streamData.put("platformId", String.valueOf(platformId));
                streamData.put("textChannelId", result.getString("textChannelId"));
                streamData.put("messageId", result.getString("messageId"));
                streamData.put("streamsGame", result.getString("streamsGame"));
                streamData.put("streamsViewers", result.getString("streamsViewers"));
                streamData.put("channelStatus", result.getString("channelStatus"));
                streamData.put("channelDisplayName", result.getString("channelDisplayName"));
                streamData.put("channelLanguage", result.getString("channelLanguage"));
                streamData.put("channelId", result.getString("channelId"));
                streamData.put("channelName", result.getString("channelName"));
                streamData.put("channelLogo", result.getString("channelLogo"));
                streamData.put("channelProfileBanner", result.getString("channelProfileBanner"));
                streamData.put("channelUrl", result.getString("channelUrl"));
                streamData.put("channelViews", result.getString("channelViews"));
                streamData.put("channelFollowers", result.getString("channelFollowers"));
                this.onlineStreams.put(result.getString("messageId"), streamData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

}
