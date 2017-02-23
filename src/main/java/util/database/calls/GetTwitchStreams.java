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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

public class GetTwitchStreams {


    private ConcurrentHashMap<String, Map<String, String>> onlineStreams = new ConcurrentHashMap<>();
    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    public synchronized ConcurrentHashMap<String, Map<String, String>> onlineStreams(String flag) {
        try {
            String query = "SELECT * FROM `twitchstreams` WHERE `messageId` IS NULL AND typeFlag = ? AND `online` = 1 ORDER BY `streamsId` DESC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, flag);
            result = pStatement.executeQuery();

            while (result.next()) {
                this.onlineStreams.put(result.getString("id"), populateMap(result));
            }
            return onlineStreams;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    public synchronized CopyOnWriteArrayList<String> gameStreams(String game) {
        try {
            String query = "SELECT `channelId` FROM `twitchstreams` WHERE `streamsGame` = ? ORDER BY `channelId` DESC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, game);
            result = pStatement.executeQuery();

            CopyOnWriteArrayList<String> channelIds = new CopyOnWriteArrayList<>();

            while (result.next()) {
                channelIds.add(result.getString("channelId"));
            }
            if (channelIds.size() > 0) {
                return channelIds;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    public synchronized ConcurrentHashMap<String, Map<String, String>> onlineStreams() {
        try {
            String query = "SELECT * FROM `twitchstreams` ORDER BY `streamsId` DESC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            while (result.next()) {

                this.onlineStreams.put(result.getString("channelId"), populateMap(result));
            }
            return onlineStreams;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    public synchronized ConcurrentHashMap<String, Map<String, String>> onlineStreams(Integer offset) {
        try {
            String query = "SELECT * FROM `twitchstreams` ORDER BY `streamsId` DESC LIMIT " + offset + ",100";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            while (result.next()) {

                this.onlineStreams.put(result.getString("channelId"), populateMap(result));
            }
            return onlineStreams;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    public synchronized ConcurrentHashMap<String, Map<String, String>> offline() {
        try {
            String query = "SELECT * FROM `twitchstreams` WHERE `online` = 0 ORDER BY `streamsId` DESC";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            ConcurrentHashMap<String, Map<String, String>> offlineStreams = new ConcurrentHashMap<>();

            while (result.next()) {
                offlineStreams.put(result.getString("id"), populateMap(result));
            }
            if (offlineStreams.size() > 0) {
                return offlineStreams;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    private synchronized HashMap<String, String> populateMap(ResultSet result) {
        HashMap<String, String> streamData = new HashMap<>();
        try {
            streamData.put("guildId", result.getString("guildId"));
            streamData.put("messageId", result.getString("messageId"));
            streamData.put("streamsId", result.getString("streamsId"));
            streamData.put("streamsGame", result.getString("streamsGame"));
            streamData.put("streamsCommunityId", result.getString("streamsCommunityId"));
            streamData.put("streamsViewers", result.getString("streamsViewers"));
            streamData.put("streamsCreatedAt", result.getString("streamsCreatedAt"));
            streamData.put("channelStatus", result.getString("channelStatus"));
            streamData.put("channelBroadcasterLanguage", result.getString("channelBroadcasterLanguage"));
            streamData.put("channelDisplayName", result.getString("channelDisplayName"));
            streamData.put("channelGame", result.getString("channelGame"));
            streamData.put("channelLanguage", result.getString("channelLanguage"));
            streamData.put("channelId", result.getString("channelId"));
            streamData.put("channelName", result.getString("channelName"));
            streamData.put("channelPartner", result.getString("channelPartner"));
            streamData.put("channelLogo", result.getString("channelLogo"));
            streamData.put("channelVideoBanner", result.getString("channelVideoBanner"));
            streamData.put("channelProfileBanner", result.getString("channelProfileBanner"));
            streamData.put("channelUrl", result.getString("channelUrl"));
            streamData.put("channelViews", result.getString("channelViews"));
            streamData.put("channelFollowers", result.getString("channelFollowers"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return streamData;
    }
}
