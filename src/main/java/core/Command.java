/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 *
 * @author keesh
 */
public interface Command {

    /**
     * Called once determined if command exists
     * Returns boolean if all arguments are met
     *
     * @param args
     * @param event
     * @return boolean
     */
    public boolean called(String[] args, MessageReceivedEvent event);
    
    /**
     * Action taken when the command is called
     * @param args
     * @param event 
     */
    public void action(String[] args, MessageReceivedEvent event);
    
    /**
     * Returns help info for the command
     * @return 
     */
    public String help();
    
    /**
     * 
     * @param success
     * @param event 
     */
    public void executed(boolean success, MessageReceivedEvent event);
}
