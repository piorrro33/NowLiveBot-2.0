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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Remove extends Add implements Command {

    private static Logger logger = LoggerFactory.getLogger(Add.class);
    public String help;
    private Connection connection;
    private PreparedStatement pStatement;
    private String query;
    private Integer resultInt;
    private String[] options = new String[]{"channel", "game", "manager", "tag", "team", "help"};

    @Override
    public void action(String args, MessageReceivedEvent event) {
        DiscordController dController = new DiscordController(event);

        String guildId = dController.getguildId();

        for (String s : this.options) {
            if (this.option.equals(s) && !this.option.equals("help")) {
                Integer platformId = 1; // platformId is always 1 for Twitch until other platforms are added
                this.argument = this.argument.replace("'", "''");

                try {
                    connection = Database.getInstance().getConnection();

                    // Check to see if the entry already exists in the db for that guild
                    if (this.option.equals("manager")) {
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `userId` = ?";

                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setString(2, dController.getMentionedUsersId());
                    } else {
                        query = "DELETE FROM `" + this.option + "` WHERE `guildId` = ? AND `platformId` = ? AND `name` = ?";
                        pStatement = connection.prepareStatement(query);
                        pStatement.setString(1, guildId);
                        pStatement.setInt(2, platformId);
                        pStatement.setString(3, this.argument);
                    }
                    resultInt = pStatement.executeUpdate();

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

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
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
