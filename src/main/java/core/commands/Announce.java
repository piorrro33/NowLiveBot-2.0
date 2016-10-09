package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.calls.Tracker;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Announce implements Command {

    private static Logger logger = LoggerFactory.getLogger(Announce.class);

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {

            return true;
        } else {
            sendToChannel(event, Const.INCORRECT_ARGS);

            return false;
        }
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        // TODO: When the DB gets setup, iterate through all guilds the bot is in and send this out to all guilds
        sendToChannel(event, "*Message from the " + Const.BOT_NAME + " developers:*\n\n\t" + args);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        // TODO: Add some sort of check for being a bot admin here so this doesn't show up to guild owners and users
        sendToChannel(event, Const.ANNOUNCE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        try {
            new Tracker("Invite");
        } catch (PropertyVetoException | IOException | SQLException e) {
            logger.warn("There was a problem tracking this command usage.");
        }
    }
}
