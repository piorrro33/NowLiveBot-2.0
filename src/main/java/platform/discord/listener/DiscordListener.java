/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import core.CommandParser;
import core.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import util.Const;

import java.util.logging.Logger;

/**
 * @author keesh
 */
public class DiscordListener extends ListenerAdapter {

    private static final Logger LOG = Logger.getLogger(DiscordListener.class.getName());

    /**
     * Incoming message handler.
     *
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("--On Message Receive Event");
        // Log message to console
        if (event.isPrivate()) {
            // PM's are not Guild specific, so don't request Guild and/or channel specific info
            // Will cause an Uncaught Exception from JDA and the message won't be read
            System.out.printf("[PM][%s] : %s\n",
                    event.getAuthor().getUsername(),
                    event.getMessage().getContent());
        } else {
            System.out.printf("[%s][%s][%s] : %s\n",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getAuthor().getUsername(),
                    event.getMessage().getContent());
        }

        //System.out.println("Event Message Content: " + event.getMessage().getContent());
        //System.out.println("Event Message Author ID: " + event.getMessage().getAuthor().getId());
        //System.out.println("Event.getJDA ID: " + event.getJDA().getSelfInfo().getId());

        String cntMsg = event.getMessage().getContent().toLowerCase();
        String jdaID = event.getMessage().getAuthor().getId();

        // Pre-check all core.commands to ignore JDA written messages.
        if (cntMsg.startsWith(Const.COMMAND_PREFIX) && !jdaID.equals(event.getJDA().getSelfInfo().getId())) {
            commandFilter(cntMsg, event);
        } else {
            //TODO: Add DB call for tracking number of messages sent in DB
        }
    }

    private void commandFilter(String cntMsg, MessageReceivedEvent event) {
        if (cntMsg.startsWith("ping", 1) || cntMsg.startsWith(Const.COMMAND, 1)) {
            CommandParser.handleCommand(Main.parser.parse(cntMsg, event));
        }
    }
}
