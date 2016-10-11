/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 * @author keesh
 */
public interface Command {

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    boolean called(String args, MessageReceivedEvent event);

    /**
     * Action taken after the command is verified
     *
     * @param args Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    void action(String args, MessageReceivedEvent event);

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    void help(MessageReceivedEvent event);

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event From JDA: MessageReceivedEvent
     */
    void executed(boolean success, MessageReceivedEvent event);
}
