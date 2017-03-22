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
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public final class GuildLeave {
    private static final Logger logger = LoggerFactory.getLogger("Guild Leave");
    private static List<String> tableList = new CopyOnWriteArrayList<>();
    private static Connection connection;
    private static PreparedStatement pStatement;

    private GuildLeave() {

    }

    public static void leaveGuild(GuildLeaveEvent gEvent) {
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("twitch");
        tableList.add("twitchstreams");

        try {
            for (String s : tableList) {
                String query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                if (connection == null || connection.isClosed()) {
                    connection = Database.getInstance().getConnection();
                }
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, gEvent.getGuild().getId());
                Integer result = pStatement.executeUpdate();
                if (!result.equals(0)) {
                    if (Main.debugMode()) {
                        logger.info("Successfully deleted all data for Guild " + gEvent.getGuild().getId() + " from the "
                                + s.toUpperCase() + " table.");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Failed to remove info from Guild " + gEvent.getGuild().getId() + '.');
        } finally {
            cleanUp(pStatement, connection);
        }
    }

    public static void deleteGuild(String guildId) {
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("twitch");
        tableList.add("twitchstreams");

        try {
            for (String s : tableList) {
                String query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                if (connection == null || connection.isClosed()) {
                    connection = Database.getInstance().getConnection();
                }
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, guildId);
                Integer result = pStatement.executeUpdate();
                if (!result.equals(0)) {
                    if (Main.debugMode()) {
                        logger.info("Successfully deleted all data for Guild " + guildId + " from the " + s.toUpperCase() + " table.");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Failed to remove info from Guild " + guildId + '.');
        } finally {
            cleanUp(pStatement, connection);
        }
    }
}
