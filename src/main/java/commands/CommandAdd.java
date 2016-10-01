/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import core.Command;
import java.util.Arrays;
import java.util.logging.Logger;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 *
 * @author keesh
 */
public class CommandAdd implements Command {
    private static final Logger LOG = Logger.getLogger(CommandAdd.class.getName());
    
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        // TODO: Determine if the appropriate arguements were passed
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        // DEBUG STATEMENT: Remove for production
        System.out.println(Arrays.toString(args));
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(Const.ADD_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        // TODO: Database command count + other post-script
    }
    
    
}
