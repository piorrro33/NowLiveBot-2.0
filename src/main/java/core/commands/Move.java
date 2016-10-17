package core.commands;

import core.Command;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Move implements Command {
    private static Connection connection;
    private static PreparedStatement pStatement;
    private static String query;
    private static Integer result;
    public static final Logger logger = LoggerFactory.getLogger(Database.class);

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            if (args.substring(0, 1).equals("#") && !args.contains(" ")) {
                return true;
            } else if (!"help".equals(args)) {
                sendToChannel(event, Const.INCORRECT_ARGS);
                return false;
            } else {
                return true;
            }
        } else {
            sendToChannel(event, Const.EMPTY_ARGS);
            return false;
        }
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        // Get the channelID from the guild and insert into the DB

        for (TextChannel textChannel : event.getJDA().getTextChannelsByName(args.substring(1))) {

            if (textChannel.getGuild().getId().equals(event.getGuild().getId())) {
                try {
                    String query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
                    connection = Database.getInstance().getConnection();
                    pStatement = connection.prepareStatement(query);

                    pStatement.setString(1, textChannel.getId());
                    pStatement.setString(2, event.getGuild().getId());

                    result = pStatement.executeUpdate();

                    if (result.equals(1)) {
                        sendToChannel(event, Const.MOVE_SUCCESS);
                    } else {
                        sendToChannel(event, Const.MOVE_FAILURE);
                    }
                } catch (SQLException e) {
                    logger.error("There was a problem updating Move in the database", e);
                } finally {
                    cleanUp(result, pStatement, connection);
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.MOVE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Move");
    }
}
