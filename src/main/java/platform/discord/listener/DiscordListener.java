/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.discord.listener;

import java.util.Locale;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import util.Const;

/**
 *
 * @author keesh
 */
public class DiscordListener extends ListenerAdapter {

    /**
     * Incoming message handler.
     *
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
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

        if (event.getMessage().getContent().length() >= Const.COMMAND_LENGTH
                && event.getMessage().getContent().toLowerCase(Locale.getDefault())
                .substring(0, Const.COMMAND_LENGTH).equals(Const.COMMAND)) {
            // For debugging purposes, output all mesages to the console
            System.out.printf("[%s][%s] : %s said a command.\n",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getAuthor().getUsername());
            // For debugging purposes, output a message from the bot to the Guild channel
        }
    }
}
