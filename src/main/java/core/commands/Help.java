package core.commands;

import core.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;

/**
 * @author Veteran Software by Ague Mort
 */
public class Help implements Command {

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        // TODO: need to revamp how help is done bc 2,000 character limit
        MessageBuilder message = new MessageBuilder();
        message.appendString("Hey there, " + event.getAuthor().getName() + "!\n\n");
        message.appendString("So I hear you're looking for some help?  Below is a list of my commands.  To find out what " +
                "each one of them does, type " + Const.COMMAND_PREFIX + Const.COMMAND + "<command> help\n\n");
        message.appendString("```Ruby\n* add\n* beam\n* cleanup\n* compact\n* invite\n* move\n* notify");
        message.appendString("\n* ping\n* remove\n* streams\n* twitch```");
        message.appendString("\nJust to make it known, Ague is still working hard to finish up all of my polish, so some " +
                "of the commands listed may not be working just yet!  But their help is working.  Bear with the guy, " +
                "he's working hard to get things right!\n\n\t~~" + Const.BOT_NAME);
        message.appendString("\n\n*P.S. I don't monitor this mailbox, so please don't send me any messages through PM*");
        sendToPm(event, message.build());
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.TYPE_ONCE);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Help");
    }
}
