package util.database.calls;

import core.Main;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public final class GuildLeave {
    public static final Logger logger = LoggerFactory.getLogger("Guild Leave");
    private static List<String> tableList = new ArrayList<>();
    private static Connection connection = null;
    private static PreparedStatement pStatement = null;
    private static Integer result = 0;
    private static String query;

    private GuildLeave() {

    }

    public static void leaveGuild(GuildLeaveEvent gEvent) {
        tableList.add("channel");
        tableList.add("game");
        tableList.add("guild");
        tableList.add("manager");
        tableList.add("notification");
        tableList.add("permission");
        tableList.add("stream");
        tableList.add("tag");
        tableList.add("team");

        try {
            connection = Database.getInstance().getConnection();
            for (String s : tableList) {
                query = "DELETE FROM `" + s + "` WHERE `guildId` = ?";
                pStatement = connection.prepareStatement(query);
                pStatement.setString(1, gEvent.getGuild().getId());
                result = pStatement.executeUpdate();
                if (!result.equals(0)) {
                    if (Main.debugMode()) {
                        logger.info("Successfully deleted all data for Guild " + gEvent.getGuild().getId() + " from the "
                                + s.toUpperCase() + " table.");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Failed to remove info from Guild " + gEvent.getGuild().getId() + ".");
        } finally {
            cleanUp(pStatement, connection);
        }

    }
}
