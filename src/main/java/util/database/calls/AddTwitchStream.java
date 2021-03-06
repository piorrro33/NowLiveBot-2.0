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
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

public class AddTwitchStream {
    private Connection connection;
    private PreparedStatement pStatement;

    public AddTwitchStream(CopyOnWriteArrayList<Stream> streams, String flag) {
        process(streams, flag);
    }

    private synchronized void process(CopyOnWriteArrayList<Stream> streams, String flag) {
        if (streams.size() > 0) {
            try {
                String query = "INSERT INTO `twitchstreams` " +
                        "(`guildId`, `typeFlag`, `textChannelId`, `streamsId`, `streamsGame`, `streamsCommunityId`, `streamsViewers`, `streamsCreatedAt`," +
                        "`channelStatus`, `channelBroadcasterLanguage`, `channelDisplayName`, " +
                        "`channelGame`, `channelLanguage`,`channelId`, `channelName`,`channelPartner`, `channelLogo`," +
                        "`channelVideoBanner`, `channelProfileBanner`, `channelUrl`,`channelViews`, `channelFollowers`) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                if (connection == null || connection.isClosed()) {
                    this.connection = Database.getInstance().getConnection();
                }

                this.pStatement = connection.prepareStatement(query);

                streams.forEach(stream -> {
                    if (stream.getAdditionalProperties().get("announceChannel").toString() != null){
                        String guildId = stream.getAdditionalProperties().get("guildId").toString();

                        CheckTwitchStreams checkTwitchStreams = new CheckTwitchStreams();
                        if (!checkTwitchStreams.check(stream.getChannel().getId(), guildId)) {

                            Integer partner = 0;
                            if (stream.getChannel().getPartner()) {
                                partner = 1;
                            }

                            try {
                                pStatement.setString(1, guildId);
                                pStatement.setString(2, flag);
                                pStatement.setString(3, stream.getAdditionalProperties().get("announceChannel").toString());
                                pStatement.setString(4, stream.getId());//streamsId
                                pStatement.setString(5, stream.getGame());//streamsGame
                                pStatement.setString(6, stream.getCommunityId());//streamsCommunityId
                                pStatement.setInt(7, stream.getViewers());//streamsViewers
                                pStatement.setString(8, stream.getCreatedAt());//streamsCreatedAt
                                pStatement.setString(9, stream.getChannel().getStatus());//channelStatus
                                pStatement.setString(10, stream.getChannel().getBroadcasterLanguage());//channelBroadcasterLang
                                pStatement.setString(11, stream.getChannel().getDisplayName());//channelDisplayName
                                pStatement.setString(12, stream.getChannel().getGame());//channelGame
                                pStatement.setString(13, stream.getChannel().getLanguage());//channelLanguage
                                pStatement.setString(14, stream.getChannel().getId());//channelId
                                pStatement.setString(15, stream.getChannel().getName());//channelName
                                pStatement.setInt(16, partner);
                                pStatement.setString(17, stream.getChannel().getLogo());
                                pStatement.setString(18, stream.getChannel().getVideoBanner());
                                pStatement.setString(19, stream.getChannel().getProfileBanner());
                                pStatement.setString(20, stream.getChannel().getUrl());
                                pStatement.setInt(21, stream.getChannel().getViews());
                                pStatement.setInt(22, stream.getChannel().getFollowers());
                                pStatement.addBatch();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                pStatement.executeBatch();
            } catch (SQLException e) {
                System.out.println("[~ERROR~] I threw an exception here");
                e.printStackTrace();
            } finally {
                cleanUp(pStatement, connection);
            }
        }
    }
}
