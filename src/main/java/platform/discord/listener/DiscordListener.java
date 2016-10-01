/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import core.Main;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import util.Const;

import java.util.Locale;
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
        if (event.getMessage().getContent().startsWith(Const.COMMAND_PREFIX) && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
            // DEBUG STATEMENT: Remove in production
            System.out.println("Passed filters!!");
            if (event.getMessage().getContent().toLowerCase().startsWith(Const.COMMAND_PREFIX + Const.COMMAND)) {
                // Make sure at least COMMMAND is present (including COMMAND_PREFIX and a trailing space)
                if (event.getMessage().getContent().length() >= Const.COMMAND_LENGTH + 2) {
                    // Strip out the COMMAND, COMMAND_PREFIX and the trailing space
                    String strippedMessage = event.getMessage().getContent().toLowerCase().substring(Const.COMMAND_LENGTH + 2);
                    // null check (maybe unecessary at this point)
                    if (!strippedMessage.isEmpty()) {
                        Main.handleCommand(Main.parser.parse(strippedMessage.toLowerCase(), event));
                    }
                } else {
                    // If COMMAND_PREFIX + COMMAND is invoked with no command
                    event.getTextChannel().sendMessage(langs.En.EMPTY_COMMAND);
                }
                // Ping is the only command that doesn't use COMMAND_PREFIX + COMMAND, so send it
            } else if (event.getMessage().getContent().toLowerCase().startsWith(Const.COMMAND_PREFIX + "ping")) {
                Main.handleCommand(Main.parser.parse(event.getMessage().getContent().toLowerCase(), event));
            } else {
                // TODO: Make database call to update message count.
            }

        }
    }
}
