package core.commands;

import core.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Move implements Command {
    public static final Logger logger = LoggerFactory.getLogger(Database.class);
    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private Integer result;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
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
    public final void action(String args, GuildMessageReceivedEvent event) {
        // Get the channelID from the guild and insert into the DB

        for (TextChannel textChannel : event.getGuild().getTextChannelsByName(args.substring(1), true)) {

            if (textChannel.getGuild().getId().equals(event.getGuild().getId())) {
                try {
                    query = "UPDATE `guild` SET `channelId` = ? WHERE `guildId` = ?";
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
                    cleanUp(pStatement, connection);
                }
            } else {
                sendToChannel(event, Const.MOVE_DONT_OWN_CHANNEL);
                break;
            }
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.MOVE_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Move");
    }
}
