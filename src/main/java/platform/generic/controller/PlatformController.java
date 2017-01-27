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

package platform.generic.controller;

import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformController {

    private static Connection gmiConnection;
    private static Connection connection;
    private static PreparedStatement gmiStatement;
    private static PreparedStatement pStatement;
    private static ResultSet result;
    private static ResultSet gmiResult;

    public static String getAnnounceChannel(String guildId, Integer platformId, String channelName) {
        try {
            String query = "SELECT `channelId` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";

            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setInt(2, platformId);
            pStatement.setString(3, channelName);
            result = pStatement.executeQuery();

            if (result.isBeforeFirst() && result.next()) {
                return result.getString("channelId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return null;
    }

    public static synchronized String getMessageId(String guildId, Integer platformId, String channelName) {

        try {
            String query = "SELECT `messageId` FROM `stream` WHERE `guildId` = ? AND `platformId` = ? AND `channelName` = ?";

            if (gmiConnection == null || gmiConnection.isClosed()) {
                gmiConnection = Database.getInstance().getConnection();
            }

            gmiStatement = gmiConnection.prepareStatement(query);
            gmiStatement.setString(1, guildId);
            gmiStatement.setInt(2, platformId);
            gmiStatement.setString(3, channelName);
            gmiResult = gmiStatement.executeQuery();
            if (gmiResult.next()) {
                return gmiResult.getString("messageId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(gmiResult, gmiStatement, gmiConnection);
        }
        return "";
    }

    public static int getPlatformId(String args) {
        if (args.contains("~")) {
            String platform = args.substring(0, args.indexOf("~"));
            switch (platform) {
                case "twitch":
                    return 1;
                case "beam":
                    return 2;
                default:
                    break;
            }
        }
        return 0;
    }
}