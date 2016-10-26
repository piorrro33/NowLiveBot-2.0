package util.database.calls;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class GuildJoin {
    public static final Logger logger = LoggerFactory.getLogger(GuildJoin.class);
    private static ArrayList<String> tableList = new ArrayList<>();

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static Integer result = 0;
    private static ResultSet resultSet = null;
    private static String query;
    private static String guildId;
    private static String defaultChannel;

    public static void joinGuild(GuildJoinEvent gEvent) {
        tableList.add("channel");
        tableList.add("game");
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("queue");
        tableList.add("stream");
        tableList.add("tag");
        tableList.add("team");

        try {

            guildId = gEvent.getGuild().getId();
            defaultChannel = gEvent.getGuild().getPublicChannel().getId();


            int failed = 0;
            logger.info("Attempting to join guild: " + guildId);
            for (String s : tableList) {
                connection = Database.getInstance().getConnection();
                query = "SELECT `guildId` FROM `" + s + "` WHERE `guildId` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, gEvent.getGuild().getId());
                resultSet = pStatement.executeQuery();

                if (resultSet.next()) {
                    // If there's still remnants or possible corrupt data, remove it
                    logger.warn("This guild has data remnants in my database!");
                    query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, guildId);
                    result = pStatement.executeUpdate();

                    if (result > 0) {
                        addData(gEvent, s);
                    } else {
                        failed++;
                    }
                } else {
                    // Add data if table is clean
                    addData(gEvent, s);
                }
            }
            if (failed == 0) {
                gEvent.getGuild().getPublicChannel().sendMessage("Your guild has been added!!");
            } else {
                gEvent.getGuild().getPublicChannel().sendMessage("There was an error adding your guild!!");
            }

        } catch (Exception e) {
            logger.error("There was a MySQL exception.", e);
        } finally {
            cleanUp(resultSet, pStatement, connection);
            cleanUp(pStatement, connection);
        }
    }

    private static void addData(GuildJoinEvent gEvent, String s) {
        switch (s) {
            case "guild":
                try {
                    connection = Database.getInstance().getConnection();
                    query = "INSERT INTO `" + s + "` (`guildId`, `channelId`, `isCompact`, `isActive`, `cleanup`) " +
                            "VALUES (?, ?, 0, 0, 0)";
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, guildId);
                    pStatement.setString(2, defaultChannel);
                    result = pStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
                }
                if (result > 0) {
                    logger.info("Successfully added guild " + guildId + " to my database");
                } else {
                    logger.warn("Failed to add guild information to my database");
                }
                break;

            case "manager":
                ArrayList<String> userIds = new ArrayList<>();
                userIds.add(gEvent.getGuild().getOwnerId());
                // Pull the roles from the guild
                for (Role role : gEvent.getGuild().getRoles()) {
                    // Check permissions of each role
                    if (role.hasPermission(Permission.MANAGE_SERVER)) {
                        // See if the user in question has the correct role
                        for (User user : gEvent.getGuild().getUsersWithRole((role))) {
                            // Add them to the list of authorized managers
                            if (!userIds.contains(user.getId())) {
                                userIds.add(user.getId());
                            }
                        }
                    }
                }
                for (String users : userIds) {
                    try {
                        connection = Database.getInstance().getConnection();
                        query = "INSERT INTO `manager` (`guildId`, `userId`) VALUES (?, ?)";
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setString(2, users);
                        result = pStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        cleanUp(pStatement, connection);
                    }
                    if (result > 0) {
                        logger.info("Successfully added manager " + users + " to guild " + guildId + ".");
                    } else {
                        logger.warn("Failed to add manager to my database~");
                    }
                }
                break;

            case "notification":
                try {
                    Integer level = 0;
                    connection = Database.getInstance().getConnection();
                    query = "INSERT INTO `notification` (`guildId`, `level`) VALUES (?, ?)";
                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, guildId);
                    pStatement.setInt(2, level);
                    result = pStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
                }
                if (result > 0) {
                    logger.info("Populated the notification table with default data.");
                } else {
                    logger.info("Failed to add data to the notification table.");
                }
                break;

            default:
                logger.info("No data to add to table: " + s);
                break;
        }
    }
}
