package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Streams implements Command {

    private static Logger logger = LoggerFactory.getLogger(Streams.class);
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result = null;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            if ("help".equals(args)) { // If the help argument is the only argument that is passed
                return true;
            } else {
                sendToChannel(event, Const.INCORRECT_ARGS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            Statement statement = connection.createStatement();
            // Grab the number of rows first

            String query = "SELECT COUNT(*) as `rowCount` FROM `nowlivebot`.`stream` WHERE `guildId` = " +
                    "'" + event.getGuild().getId() + "'";
            result = statement.executeQuery(query);
            Integer rowCount = -1;
            while (result.next()) {
                rowCount = result.getInt("rowCount");
            }

            // Grab the actual results to iterate through
            query = "SELECT `platform`.`baseLink` AS `link`, `stream`.`channelName` AS `channel`, `platform`.`name` " +
                    "AS `platform`, `stream`.`gameName` AS `game` " +
                    "FROM `stream` " +
                    "INNER JOIN `platform` " +
                    "ON `stream`.`platformId` = `platform`.`id` " +
                    "WHERE `stream`.`guildId` = '" + event.getGuild().getId() + "' " +
                    "ORDER BY `stream`.`channelName`";
            result = statement.executeQuery(query);

            if (rowCount < 1) { // If no streams are online
                event.getAuthor().getPrivateChannel().sendMessage(Const.NONE_ONLINE);
            } else { // If there's at least one stream online
                String outputMessage = Const.ONLINE_STREAM_PM_1 + rowCount + Const.ONLINE_STREAM_PM_2;
                while (result.next()) {
                    outputMessage += "**" + result.getString("channel") + "**" + Const.NOW_PLAYING_LOWER + "**" +
                            result.getString("game") + "**" + Const.ON + "**" + result.getString("platform") + "**" +
                            "!\n\t" + Const.WATCH_THEM_HERE + "__*" + result.getString("link") +
                            result.getString("channel") + "*__\n\n";
                }
                // TODO: Add DB value to offer preference to user to send pm vs send to channel
                sendToPm(event, outputMessage);
            }

        } catch (Exception e) {
            logger.error("There was a problem fetching live streams for an on demand request.", e);
        } finally {
            cleanUp(result, statement, connection);
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.STREAMS_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Streams");
    }
}
