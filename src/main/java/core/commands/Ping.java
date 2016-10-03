/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.logging.Logger;

import static langs.En.PING;
import static langs.En.PING_HELP;

/**
 *
 * @author keesh
 */
public class Ping implements Command {
    private static final Logger LOG = Logger.getLogger(Ping.class.getName());

    /**
     * Help message for Ping
     */
    public final String HELP = PING_HELP;

    @Override
    public boolean called(String args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        // TODO: Add ping times to the bot and the database
        int timer = 0;
        while (timer < 10) {
            event.getTextChannel().sendTyping();
            timer++;
        }
        event.getTextChannel().sendMessage(PING);
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
