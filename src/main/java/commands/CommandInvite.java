/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import core.Command;
import java.util.logging.Logger;
import static langs.En.*;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 *
 * @author keesh
 */
public class CommandInvite implements Command {

    private static final Logger LOG = Logger.getLogger(CommandInvite.class.getName());

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        // TODO: #help arguement
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(INVITE);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(INVITE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        // TODO: Database command count + other post-script
    }

}
