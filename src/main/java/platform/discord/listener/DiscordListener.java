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
        System.out.println("On Message Receive Event");
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

        System.out.println("Event Message Content: " + event.getMessage().getContent());
        System.out.println("Event Message Author ID: " + event.getMessage().getAuthor().getId());
        System.out.println("Event.getJDA ID: " + event.getJDA().getSelfInfo().getId());

        if (event.getMessage().getContent().startsWith(Const.COMMAND_PREFIX) &&
                !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfInfo().getId())) {
            System.out.println("Passed filters!!");
            Main.handleCommand(Main.parser.parse(event.getMessage().getContent().toLowerCase(), event));
        }
        if (event.getMessage().getContent().length() >= Const.COMMAND_LENGTH &&
                event.getMessage().getContent().toLowerCase(Locale.getDefault())
                        .substring(0, Const.COMMAND_LENGTH).equals(Const.COMMAND)) {
            // For debugging purposes, output all messages to the console
            System.out.printf("[%s][%s] : %s said a command.\n",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getAuthor().getUsername());
            // For debugging purposes, output a message from the bot to the Guild channel
            try {
                Message builder = new MessageBuilder().appendString("Someone said a command.").build();

            } catch (Exception e) {
                System.out.printf("[%s][%s] : Failed to write to Discord.\n",
                        event.getGuild().getName(),
                        event.getTextChannel().getName());
            }
        }
    }
}
