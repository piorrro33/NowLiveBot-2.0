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

/**
 * @author Veteran Software by Ague Mort
 */
public class CheckTwitchData {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet result;

    public synchronized static Boolean action(String tableName, String guildId, String name) {
        String query = "";
        switch (tableName) {
            case "channel":
                query = "SELECT `channelName` FROM `twitch` WHERE `guildId` = ? AND `channelName` LIKE ?";
                break;
            case "community":
                query = "SELECT `communityName` FROM `twitch` WHERE `guildId` = ? AND `communityName` LIKE ?";
                break;
            case "gameFilter":
                query = "SELECT `gameFilter` FROM `twitch` WHERE `guildId` = ? AND `gameFilter` LIKE ?";
                break;
            case "game":
                query = "SELECT `gameName` FROM `twitch` WHERE `guildId` = ? AND `gameName` LIKE ?";
                break;
            case "team":
                query = "SELECT `teamName` FROM `twitch` WHERE `guildId` = ? AND `teamName` LIKE ?";
                break;
            case "titleFilter":
                query = "SELECT `titleFilter` FROM `twitch` WHERE `guildId` = ? AND `titleFilter` LIKE ?";
                break;
            default:
                break;
        }

        try {
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setString(2, name);
            result = pStatement.executeQuery();
            if (result.isBeforeFirst()) {
                System.out.println("Found channel: " + name);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return false;
    }

}
