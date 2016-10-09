package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Streams implements Command {

    private static Logger logger = LoggerFactory.getLogger(Streams.class);

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
        event.getAuthor().getPrivateChannel().sendMessage("Once my database is set up, this message will return a " +
                "list of streams that are currently live!  Only streams with the criteria set by my managers in your " +
                "Discord server will be displayed.");
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
