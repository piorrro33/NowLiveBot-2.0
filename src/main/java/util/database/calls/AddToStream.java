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
import java.sql.SQLException;

import static util.database.Database.cleanUp;

public class AddToStream {

    private Connection connection;

    public synchronized void process(String guildId, String textChannelId, Integer platformId, Stream stream) {
        PreparedStatement pStatement = null;
        try {
            String query = "INSERT INTO `stream` " +
                    "(`guildId`, `textChannelId`, `platformId`, `streamsGame`, `streamsViewers`, " +
                    "`channelStatus`, `channelDisplayName`, `channelLanguage`, `channelId`, `channelName`, `channelLogo`," +
                    "`channelProfileBanner`, `channelUrl`, `channelViews`, `channelFollowers`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }

            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            pStatement.setString(2, textChannelId);
            pStatement.setInt(3, platformId);
            pStatement.setString(4, stream.getGame());
            pStatement.setInt(5, stream.getViewers());
            pStatement.setString(6, stream.getChannel().getStatus());
            pStatement.setString(7, stream.getChannel().getDisplayName());
            pStatement.setString(8, stream.getChannel().getLanguage());
            pStatement.setLong(9, stream.getChannel().getId());
            pStatement.setString(10, stream.getChannel().getName());
            pStatement.setString(11, stream.getChannel().getLogo());
            pStatement.setString(12, stream.getChannel().getProfileBanner());
            pStatement.setString(13, stream.getChannel().getUrl());
            pStatement.setLong(14, stream.getChannel().getViews());
            pStatement.setLong(15, stream.getChannel().getFollowers());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] I threw an exception here");
        } finally {
            cleanUp(pStatement, connection);
        }
    }
}
