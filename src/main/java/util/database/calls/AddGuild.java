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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class AddGuild {
    private final Logger logger = LoggerFactory.getLogger("AddGuild");
    private Connection connection;
    private PreparedStatement pStatement;
    private PreparedStatement pStmt;
    private PreparedStatement pSt;
    private ResultSet result;
    private Integer resultInt;

    private synchronized String getQuery(String table) {
        switch (table) {
            case "guild":
                return "SELECT COUNT(*) AS `count` FROM `guild` WHERE `guildId` = ?";
            case "manager":
                return "SELECT COUNT(*) AS `count` FROM `manager` WHERE `guildId` = ?";
            case "notification":
                return "SELECT COUNT(*) AS `count` FROM `notification` WHERE `guildId` = ?";
            case "permission":
                return "SELECT COUNT(*) AS `count` FROM `permission` WHERE `guildId` = ?";
            case "twitch":
                return "SELECT COUNT(*) AS `count` FROM `twitch` WHERE `guildId` = ?";
            case "twitchstreams":
                return "SELECT COUNT(*) AS `count` FROM `twitchstreams` WHERE `guildId` = ?";
            default:
                break;
        }
        return null;
    }

    public synchronized void action(GuildMessageReceivedEvent event) {

        List<String> tableList = new CopyOnWriteArrayList<>();
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("twitch");
        tableList.add("twitchstreams");

        for (String table : tableList) {
            try {
                String query = getQuery(table);

                if (query != null) {
                    if (connection == null || connection.isClosed()) {
                        connection = Database.getInstance().getConnection();
                    }
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, event.getGuild().getId());
                    result = pStatement.executeQuery();

                    while (result.next()) {
                        if (result.getInt("count") == 0) {
                            switch (table) {
                                case "guild":
                                    try {
                                        if (connection == null || connection.isClosed()) {
                                            connection = Database.getInstance().getConnection();
                                        }
                                        String guildQuery = "INSERT INTO `guild` (`guildId`, `channelId`, `isCompact`, `cleanup`," +
                                                " `emoji`) VALUES (?, ?, 0, 0, ?)";
                                        pStmt = connection.prepareStatement(guildQuery);
                                        pStmt.setString(1, event.getGuild().getId());
                                        pStmt.setString(2, event.getGuild().getId());
                                        pStmt.setString(3, ":heart_eyes_cat:");
                                        resultInt = pStmt.executeUpdate();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    } finally {
                                        cleanUp(pStmt, connection);
                                    }
                                    break;
                                case "manager":
                                    List<String> userIds = new CopyOnWriteArrayList<>();
                                    // Auto add the guild owner as a manager
                                    userIds.add(Main.getJDA().getGuildById(event.getGuild().getId()).getOwner().getUser().getId());
                                    // Pull the roles from the guild
                                    for (Role role : Main.getJDA().getGuildById(event.getGuild().getId()).getRoles()) {
                                        // Check permissions of each role
                                        if (role.hasPermission(Permission.MANAGE_SERVER) || role.hasPermission(Permission.ADMINISTRATOR)) {
                                            // See if the user in question has the correct role
                                            for (Member member : Main.getJDA().getGuildById(event.getGuild().getId())
                                                    .getMembersWithRoles(role)) {
                                                // Add them to the list of authorized managers
                                                if (!userIds.contains(member.getUser().getId())) {
                                                    userIds.add(member.getUser().getId());
                                                }
                                            }
                                        }
                                    }

                                    for (String users : userIds) {
                                        try {
                                            query = "INSERT INTO `manager` (`guildId`, `userId`) VALUES (?, ?)";

                                            if (connection == null || connection.isClosed()) {
                                                connection = Database.getInstance().getConnection();
                                            }
                                            pSt = connection.prepareStatement(query);

                                            pSt.setString(1, event.getGuild().getId());
                                            pSt.setString(2, users);
                                            resultInt = pSt.executeUpdate();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        } finally {
                                            cleanUp(pSt, connection);
                                        }

                                        if (Main.debugMode()) {
                                            if (resultInt > 0) {
                                                logger.info("Successfully added manager " + users + " to G:" + event.getGuild
                                                        ().getName() + ':' + event.getGuild().getId() + '.');
                                            } else {
                                                logger.warn("Failed to add manager to my database~");
                                            }
                                        }
                                    }
                                    break;
                                case "notification":
                                    try {
                                        query = "INSERT INTO `notification` (`guildId`, `level`) VALUES (?, 0)";

                                        if (connection == null || connection.isClosed()) {
                                            connection = Database.getInstance().getConnection();
                                        }
                                        pStatement = connection.prepareStatement(query);

                                        pStatement.setString(1, event.getGuild().getId());
                                        pStatement.executeUpdate();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    } finally {
                                        cleanUp(pStatement, connection);
                                    }
                                    break;
                                default:
                                    if (Main.debugMode()) {
                                        logger.info("No data to add to this table");
                                    }
                                    break;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(result, pStatement, connection);
            }
        }
    }
}
