package util.database.calls;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import util.database.Database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Veteran Software by Ague Mort
 */
public class GuildJoin extends Database {
    private static ArrayList<String> tableList = new ArrayList<>();

    private static Connection connection = null;
    private static Statement statement = null;
    private static Integer result = 0;
    private static ResultSet resultSet = null;
    private static String query;
    private static String guildId;
    private static String defaultChannel;

    public GuildJoin() throws PropertyVetoException, SQLException, IOException {

    }

    public static void joinGuild(GuildJoinEvent gEvent) throws PropertyVetoException, SQLException, IOException {
        logger.info("Made it into the joinGuild method.");
        tableList.add("channel");
        tableList.add("game");
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("queueitem");
        tableList.add("stream");
        tableList.add("tag");
        tableList.add("team");
        try {
            connection = getInstance().getConnection();
            statement = connection.createStatement();
            guildId = gEvent.getGuild().getId();
            defaultChannel = gEvent.getGuild().getPublicChannel().getId();
            logger.info("Attempting to join guild: " + guildId);
            int failed = 0;
            for (String s : tableList) {
                query = "SELECT `guildId` FROM `" + s + "` WHERE `guildId` = '" + guildId + "'";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    logger.warn("This guild has data remnants in my database!");
                    query = "DELETE FROM `" + s + "` WHERE `guildId` = '" + guildId + "'";
                    result = statement.executeUpdate(query);
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
            cleanUp(result, statement, connection);
        }
    }

    private static void addData(GuildJoinEvent gEvent, String s) throws SQLException {
        switch (s) {
            case "guild":
                query = "INSERT INTO `" + s + "` (`guildId`, `channelId`, `isCompact`, `isActive`, " +
                        "`cleanup`) VALUES ('" + guildId + "', '" + defaultChannel + "', 0, 0, " +
                        "0)";
                result = statement.executeUpdate(query);
                if (result > 0) {
                    logger.info("Successfully added guild " + guildId + " to my database");
                } else {
                    logger.warn("Failed to add guild information to my database");
                }
                break;
            case "manager":
                ArrayList<String> userIds = new ArrayList<>();
                userIds.add(gEvent.getGuild().getOwnerId());
                for (Role role : gEvent.getGuild().getRoles()) {
                    if (role.hasPermission(Permission.MANAGE_SERVER)) {
                        for (User user : gEvent.getGuild().getUsersWithRole((role))) {
                            if (!userIds.contains(user.getId())) {
                                userIds.add(user.getId());
                            }
                        }
                    }
                }

                for (String users : userIds) {
                    query = "INSERT INTO `manager` (`guildId`, `userId`) VALUES ('" + guildId + "', '" + users + "')";
                    result = statement.executeUpdate(query);
                    if (result > 0) {
                        logger.info("Successfully added manager " + users + " to guild " + guildId + ".");
                    } else {
                        logger.warn("Failed to add manager to my database~");
                    }
                }
                break;
        }
    }
}
