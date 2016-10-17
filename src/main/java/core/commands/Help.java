package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import java.lang.reflect.Field;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Help implements Command {

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        // TODO: need to revamp how help is done bc 2,000 character limit
        StringBuilder message = new StringBuilder();
        Field[] c = Const.class.getDeclaredFields();
        for (Field field : c) {
            Const nullObject = new Const();
            try {
                Object value = field.get(nullObject);
                if (value.toString().contains("USAGE")) {
                    message.append(value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        event.getAuthor().getPrivateChannel().sendMessage(Const.HELP_PM + message);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.TYPE_ONCE);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Help");
    }
}
