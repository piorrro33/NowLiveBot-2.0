package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import platform.discord.controller.DiscordController;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Remove extends Add implements Command {

    private static Logger logger = LoggerFactory.getLogger(Add.class);
    public String help;
    private String[] options = new String[]{"channel", "game", "manager", "tag", "team", "help"};

    @Override
    public void action(String args, MessageReceivedEvent event) {
        DiscordController dController = new DiscordController(event);

        String guildId = dController.getguildId();

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {

                Connection connection;
                Statement statement;
                Integer resultInt;
                Integer platformId = 1; // platformId is always 1 for Twitch until other platforms are added
                this.argument = this.argument.replace("'", "''");

                try {
                    connection = Database.getInstance().getConnection();
                    statement = connection.createStatement();

                    // Check to see if the entry already exists in the db for that guild
                    String query;
                    if (this.option.equals("manager")) {
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = '" + guildId + "' AND " +
                                "`userId` = '" + dController.getMentionedUsersId() + "'";
                    } else {
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = '" + guildId + "' AND " +
                                "`platformId` = " + platformId + " AND `name` = '" + this.argument + "'";
                    }

                    resultInt = statement.executeUpdate(query);

                    if (resultInt > 0) {
                        sendToChannel(event, "Removed `" + this.option + "` " + this.argument);
                        logger.info("Successfully removed " + this.argument + " from the database for guildId: " +
                                guildId + ".");
                    } else {
                        sendToChannel(event, "I can't remove `" + this.option + "` " + this.argument + " because " +
                                "it's not in my database.");
                        logger.info("Failed to remove " + this.option + " " + this.argument + " from the database" +
                                " for guildId: " + guildId + ".");
                    }

                    Database.getInstance();
                    Database.cleanUp(resultInt, statement, connection);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.REMOVE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Remove");
    }
}
