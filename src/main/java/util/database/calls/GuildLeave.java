package util.database.calls;

import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import util.database.Database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Veteran Software by Ague Mort
 */
public class GuildLeave extends Database {
    private static ArrayList<String> tableList = new ArrayList<>();
    private static Connection connection = null;
    private static Statement statement = null;
    private static Integer result = 0;
    private static String query;

    public static void leaveGuild(GuildLeaveEvent gEvent) {
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
            for (String s : tableList) {
                query = "DELETE FROM `" + s + "` WHERE `guildId` = " + gEvent.getGuild().getId() + "";
                result = statement.executeUpdate(query);
                if (!result.equals(0)) {
                    logger.info("Successfully deleted all data for Guild " + gEvent.getGuild().getId() + " from the "
                            + s.toUpperCase() + " table.");
                }
            }

        } catch (Exception e) {
            logger.error("Failed to remove info from Guild " + gEvent.getGuild().getId() + ".");
        } finally {
            Database.cleanUp(result, statement, connection);
        }

    }
}
