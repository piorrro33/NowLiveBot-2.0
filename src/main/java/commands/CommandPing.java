/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import core.Command;
import java.util.logging.Logger;
import static langs.En.PING_HELP;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 *
 * @author keesh
 */
public class CommandPing implements Command {
    private static final Logger LOG = Logger.getLogger(CommandPing.class.getName());

    /**
     * Help message for CommandPing
     */
    public final String HELP = PING_HELP;

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getTextChannel().sendMessage("Pong!");
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        // TODO: Database command count + other post-script
    }

}
