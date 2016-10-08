package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

import java.lang.reflect.Field;

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
        String message = "";
        Field[] c = Const.class.getDeclaredFields();
        for (Field field : c) {
            Const nullObject = new Const();
            try { 
                Object value = field.get(nullObject);
                if (value.toString().contains("USAGE")) {
                    message += value.toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        event.getAuthor().getPrivateChannel().sendMessage(Const.HELP_PM + message);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(Const.TYPE_ONCE);

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }
}
