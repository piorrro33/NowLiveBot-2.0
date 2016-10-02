/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

import java.util.logging.Logger;

/**
 *
 * @author keesh
 */
public class Add implements Command {
    private static final Logger LOG = Logger.getLogger(Add.class.getName());
    
    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        // TODO: Determine if the appropriate arguements were passed
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        event.getTextChannel().sendMessage("Add command works.");
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
