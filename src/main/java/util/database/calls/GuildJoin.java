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
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public final class GuildJoin {

    private static final Logger logger = LoggerFactory.getLogger("GuildJoin");
    private static List<String> tableList = new ArrayList<>();
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static Integer result = 0;
    private static ResultSet resultSet = null;
    private static String query;
    private static String guildId;
    private static String defaultChannel;
    private static Connection remConnection;
    private static PreparedStatement remStatement;

    public static void joinGuild(GuildJoinEvent gEvent) {

        tableList.add("channel");
        tableList.add("game");
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("stream");
        tableList.add("tag");
        tableList.add("team");

        guildId = gEvent.getGuild().getId();
        defaultChannel = gEvent.getGuild().getPublicChannel().getId();


        int failed = 0;
        for (String s : tableList) {
            try {
                query = "SELECT `guildId` FROM `" + s + "` WHERE `guildId` = ?";
                if (connection == null || connection.isClosed()) {
                    connection = Database.getInstance().getConnection();
                }
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, gEvent.getGuild().getId());
                resultSet = pStatement.executeQuery();

                if (resultSet.next()) {
                    // If there's still remnants or possible corrupt data, remove it
                    if (Main.debugMode()) {
                        logger.warn("This guild has data remnants in my database!");
                    }
                    try {
                        query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                        if (remConnection == null || remConnection.isClosed()) {
                            remConnection = Database.getInstance().getConnection();
                        }
                        remStatement = remConnection.prepareStatement(query);
                        remStatement.setString(1, guildId);
                        result = remStatement.executeUpdate();

                        if (result > 0) {
                            addData(gEvent, s);
                        } else {
                            failed++;
                        }
                    } catch (Exception e) {
                        logger.error("There was a MySQL exception.", e);
                    } finally {
                        cleanUp(remStatement, remConnection);
                    }
                } else {
                    // Add data if table is clean
                    addData(gEvent, s);
                }
            } catch (Exception e) {
                logger.error("There was a MySQL exception.", e);
            } finally {
                cleanUp(resultSet, pStatement, connection);
            }
        }
        if (failed == 0) {
            gEvent.getGuild().getPublicChannel().sendMessage(Const.GUILD_JOIN_SUCCESS).queue(
                    guildJoinSuccess -> System.out.printf("[SYSTEM] Joined G:%s:%s%n",
                            gEvent.getGuild().getName(),
                            gEvent.getGuild().getId())
            );
        } else {
            gEvent.getGuild().getPublicChannel().sendMessage("There was an error adding your guild!!").queue();
        }

    }

    private static void addData(GuildJoinEvent gEvent, String s) {
        switch (s) {
            case "guild":
                try {
                    query = "INSERT INTO `" + s + "` (`guildId`, `channelId`, `isCompact`, `cleanup`, " +
                            "`emoji`) VALUES (?, ?, 0, 0, ?)";
                    if (connection == null || connection.isClosed()) {
                        connection = Database.getInstance().getConnection();
                    }
                    pStatement = connection.prepareStatement(query);

                    pStatement.setString(1, guildId);
                    pStatement.setString(2, defaultChannel);
                    pStatement.setString(3, ":heart_eyes_cat:");
                    result = pStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
                }

                if (Main.debugMode()) {
                    if (result > 0) {
                        logger.info("Successfully added guild " + guildId + " to my database");
                    } else {
                        logger.warn("Failed to add guild information to my database");
                    }
                }
                break;

            case "manager":
                addManager(gEvent);
                break;

            case "notification":
                if (Main.debugMode()) {
                    if (addNotification() > 0) {
                        logger.info("Populated the notification table with default data.");
                    } else {
                        logger.info("Failed to add data to the notification table.");
                    }
                }
                break;

            default:
                if (Main.debugMode()) {
                    logger.info("No data to add to table: " + s);
                }
                break;
        }
    }

    private static void addManager(GuildJoinEvent gEvent) {
        List<String> userIds = new ArrayList<>();
        // Auto add the guild owner as a manager
        userIds.add(gEvent.getGuild().getOwner().getUser().getId());
        // Pull the roles from the guild
        for (Role role : gEvent.getGuild().getRoles()) {
            // Check permissions of each role
            if (role.hasPermission(Permission.MANAGE_SERVER) || role.hasPermission(Permission.ADMINISTRATOR)) {
                // See if the user in question has the correct role
                for (Member member : gEvent.getGuild().getMembersWithRoles(role)) {
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
                pStatement = connection.prepareStatement(query);

                pStatement.setString(1, guildId);
                pStatement.setString(2, users);
                result = pStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleanUp(pStatement, connection);
            }

            if (Main.debugMode()) {
                if (result > 0) {
                    logger.info("Successfully added manager " + users + " to guild " + guildId + ".");
                } else {
                    logger.warn("Failed to add manager to my database~");
                }
            }
        }
    }

    private static Integer addNotification() {
        try {
            query = "INSERT INTO `notification` (`guildId`, `level`) VALUES (?, ?)";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);

            pStatement.setString(1, guildId);
            pStatement.setInt(2, 0);
            return pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(pStatement, connection);
        }
        return -1;
    }
}
