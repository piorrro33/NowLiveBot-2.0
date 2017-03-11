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

import core.Main;
import langs.LocaleString;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static util.database.Database.cleanUp;

public class NotifyLevel {

    public synchronized MessageBuilder getLevel(String textChannel, Map<String, String> data, MessageBuilder message) {

        Connection connection = Database.getInstance().getConnection();
        PreparedStatement pStatement = null;
        ResultSet result = null;

        try {
            String query = "SELECT `level`, `userId` FROM `notification` WHERE `guildId` = ?";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            if (connection != null && data.get("guildId") != null) {
                if (Main.getJDA().getTextChannelById(textChannel) == null) {
                    textChannel = data.get("guildId");
                }

                pStatement = connection.prepareStatement(query);

                pStatement.setString(1, data.get("guildId"));
                result = pStatement.executeQuery();


                // Not going to add these to the Lang files because they will eventually be tokenized for customization
                if (result.isBeforeFirst() && textChannel != null && !textChannel.isEmpty()) {
                    while (result.next()) {
                        //System.out.println("Notification Level Result: " + result.getInt("level"));
                        //System.out.println("Guild: " + Main.getJDA().getGuildById(data.get("guildId")));
                        //System.out.println("Self Member: " + Main.getJDA().getGuildById(data.get("guildId")).getSelfMember());
                        //System.out.println("Text Channel: " + Main.getJDA().getTextChannelById(textChannel));

                        switch (result.getInt("level")) {
                            case 1: // User wants a @User mention
                                String userId = result.getString("userId");
                                User user = Main.getJDA().getUserById(userId);
                                message.append(user.getAsMention());
                                message.append(String.format(" " + LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                        data.get("channelDisplayName"),
                                        data.get("channelUrl")));
                                break;
                            case 2: // User wants @here mention
                                message.append(MessageBuilder.HERE_MENTION);
                                message.append(" ");
                                message.append(String.format(LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                        data.get("channelDisplayName"),
                                        data.get("channelUrl")));
                                break;
                            case 3: // User wants @everyone mention

                                message.append(MessageBuilder.EVERYONE_MENTION);
                                message.append(String.format(" " + LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                        data.get("channelDisplayName"),
                                        data.get("channelUrl")));
                                break;
                            default:
                                message.append(String.format(LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                        data.get("channelDisplayName"),
                                        data.get("channelUrl")));
                                break;
                        }
                    }
                } else {
                    DefaultNotification dn = new DefaultNotification();
                    Integer results = dn.defaultData(data.get("guildId"));
                    if (results.equals(1)) {
                        System.out.println("[SYSTEM] Added missing notification data");
                        message.append(String.format(LocaleString.getString(data.get("guildId"), "announcementMessageText"),
                                data.get("channelDisplayName"),
                                data.get("channelUrl")));
                    } else {
                        System.out.println("[~ERROR~] Failed to add missing notification data");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
        return message;
    }
}
