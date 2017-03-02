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

import platform.twitch.models.Stream;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

public class GetSpecificGameFilters {

    private Connection connection = Database.getInstance().getConnection();
    private PreparedStatement pStatement;

    public final synchronized List<String> fetch(Stream stream, String flag, String name) {
        ResultSet result = null;

        String guildId = stream.getAdditionalProperties().get("guildId").toString();

        String query;
        String value;
        switch (flag) {
            case "game":
                value = stream.getGame();
                query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL AND `gameName` = ?";
                break;
            case "channel":
                value = stream.getChannel().getId();
                query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL AND `channelId` = ?";
                break;
            case "community":
                value = name;
                query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL AND `communityName` = ?";
                break;
            default:// Team name is passed
                value = name;
                query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` IS NOT NULL AND `teamName` = ?";
                break;
        }

        try {
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setString(2, value);
            result = pStatement.executeQuery();

            List<String> gameFilters = new CopyOnWriteArrayList<>();

            if (result.isBeforeFirst()) {
                while (result.next()) {
                    gameFilters.add(result.getString("gameFilter").replaceAll("''", "'"));
                }
                return gameFilters;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }
}
