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

import static util.database.Database.cleanUp;

public class GetAnnounceChannel {
    private Connection connection;

    public synchronized String action(String guildId, String column, String value) {
        PreparedStatement pStatement = null;
        ResultSet result = null;

        String query;
        switch (column) {
            case "channel":
                query = "SELECT `announceChannel` FROM `twitch` WHERE `guildId` = ? AND `channelId` = ?";
                break;
            case "community":
                query = "SELECT `announceChannel` FROM `twitch` WHERE `guildId` = ? AND `communityName` = ?";
                break;
            case "game":
                query = "SELECT `announceChannel` FROM `twitch` WHERE `guildId` = ? AND `gameName` = ?";
                break;
            default:
                query = "SELECT `announceChannel` FROM `twitch` WHERE `guildId` = ? AND `teamName` = ?";
                break;
        }

        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setString(2, value);
            result = pStatement.executeQuery();

            String announceChannel = null;
            if (result.next()) {
                announceChannel = result.getString("announceChannel");
            }
            if (announceChannel != null) {
                return announceChannel;
            } else {
                GetGlobalAnnounceChannel globalAnnounceChannel = new GetGlobalAnnounceChannel();
                String global = globalAnnounceChannel.fetch(guildId);
                if (global != null) {
                    return global;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }
}
