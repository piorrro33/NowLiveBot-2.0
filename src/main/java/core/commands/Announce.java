package core.commands;

import core.Command;
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
import java.util.concurrent.TimeUnit;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Announce implements Command {

    private static Logger logger = LoggerFactory.getLogger(Announce.class);
    private Connection connection;
    private PreparedStatement pStatement;
    private ResultSet result;

    @Override
    public final boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            return true;
        } else {
            sendToChannel(event, Const.INCORRECT_ARGS);
            return false;
        }
    }

    @Override
    public final void action(String args, MessageReceivedEvent event) {
        try {
            connection = Database.getInstance().getConnection();
            String query = "SELECT `guildId` FROM `guild` ORDER BY `guildId` ASC";
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();
            while (result.next()) {
                event.getJDA().getGuildById(result.getString("guildId")).getPublicChannel()
                        .sendMessage("*Message from the " + Const.BOT_NAME + " developers:*\n\n\t" + args);
                try {
                    TimeUnit.MILLISECONDS.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        //sendToChannel(event, "*Message from the " + Const.BOT_NAME + " developers:*\n\n\t" + args);
        logger.info("Global announcement sent");
    }

    @Override
    public final void help(MessageReceivedEvent event) {
        // TODO: Add some sort of check for being a bot admin here so this doesn't show up to guild owners and users
        sendToChannel(event, Const.ANNOUNCE_HELP);
    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Announce");
    }
}
