package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Help implements Command {

    @Override
    public final boolean called(String args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String args, MessageReceivedEvent event) {
        // TODO: need to revamp how help is done bc 2,000 character limit
        StringBuilder message = new StringBuilder();
        message.append("Hey there, " + event.getAuthor().getUsername() + "!\n\n");
        message.append("So I hear you're looking for some help?  Below is a list of my commands.  To find out what " +
                "each one of them does, type " + Const.COMMAND_PREFIX + Const.COMMAND + "<command> help\n\n");
        message.append("```Ruby\n* add\n* cleanup\n* compact\n* disable\n* enable\n* invite\n* move\n* notify\n* " +
                "permissions");
        message.append("\n* ping\n* remove\n* status\n* streams```");
        message.append("\nJust to make it known, Ague is still working hard to finish up all of my polish, so some " +
                "of the commands listed may not be working just yet!  But their help is working.  Bear with the guy, " +
                "he's working hard to get things right!\n\n\t~~" + Const.BOT_NAME);
        message.append("\n\n*P.S. I don't monitor this mailbox, so please don't send me any messages through PM*");
        event.getAuthor().getPrivateChannel().sendMessage(String.valueOf(message));
    }

    @Override
    public final void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.TYPE_ONCE);
    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Help");
    }
}
