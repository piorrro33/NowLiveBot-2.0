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

public class AddTwitch {
    private Connection connection;
    private PreparedStatement pStatement;

    public synchronized void process(String guildId, String textChannelId, Integer platformId, Stream stream) {
        try {
            String query = "INSERT INTO `twitch` " +
                    "(`streamsId`, `streamsGame`, `streamsCommunityId`, `streamsViewers`, `streamsCreatedAt`, `streamsIsPlaylist`," +
                    "`online`, `channelMature`, `channelStatus`, `channelBroadcasterLanguage`, `channelDisplayName`, " +
                    "`channelGame`, `channelLanguage`,`channelId`, `channelName`,`channelPartner`, `channelLogo`," +
                    "`channelVideoBanner`, `channelProfileBanner`, `channelUrl`,`channelViews`, `channelFollowers`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }

            Long streamIdLong = stream.getId();
            String streamId = streamIdLong.toString();

            //Date streamCreatedAt = new Date(stream.getCreatedAt())

            this.pStatement = connection.prepareStatement(query);
            pStatement.setString(1, streamId);//streamsId
            pStatement.setString(2, stream.getGame());//streamsGame
            pStatement.setString(3, "");//streamsCommunityId
            pStatement.setInt(4, stream.getViewers());//streamsViewers
            //pStatement.setInt(5, );//streamsCreatedAt
            //pStatement.setInt(6, averageFps);//streamsIsPlaylist
            pStatement.setInt(7, 0);//online
            pStatement.setString(8, stream.getCreatedAt());//channelMature
            pStatement.setBoolean(9, false);//channelStatus
            pStatement.setString(10, stream.getChannel().getName());//channelBroadcasterLang
            pStatement.setString(11, stream.getChannel().getLogo());//channelDisplayName
            pStatement.setString(12, stream.getChannel().getProfileBanner());//channelGame
            pStatement.setString(13, stream.getChannel().getUrl());//channelLanguage
            pStatement.setLong(14, stream.getChannel().getViews());//channelId
            pStatement.setLong(15, stream.getChannel().getFollowers());//channelName
            pStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] I threw an exception here");
        } finally {
            cleanUp(pStatement, connection);
        }
    }
}
