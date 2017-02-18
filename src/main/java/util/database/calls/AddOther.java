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
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AddOther {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static String query;

    public synchronized static Boolean action(String tableName, String guildId, String name, String channelId) {

        switch (tableName) {
            case "channel":
                query = "INSERT INTO `twitch` (`id`, `guildId`, `channelName`, `channelId`) VALUES (null,?,?,?)";
                break;
            case "filter":
                query = "INSERT INTO `twitch` (`id`, `guildId`, `gameFilter`) VALUES (null,?,?)";
                break;
            case "game":
                query = "INSERT INTO `twitch` (`id`, `guildId`, `gameName`) VALUES (null,?,?)";
                break;
            case "tag":
                query = "INSERT INTO `twitch` (`id`, `guildId`, `titleFilter`) VALUES (null,?,?)";
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
            if (tableName.equalsIgnoreCase("channel")) {
                pStatement.setString(3, channelId);
            }
            pStatement.setString(2, name);
            if (pStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return false;
    }

}
