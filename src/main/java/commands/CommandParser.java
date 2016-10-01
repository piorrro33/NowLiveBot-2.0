/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;

/**
 *
 * @author keesh
 */
public class CommandParser {
    private static final Logger LOG = Logger.getLogger(CommandParser.class.getName());
    
    public CommandContainer parse (String rw, MessageReceivedEvent event) {
        ArrayList<String> split = new ArrayList<>();
        String raw = rw;
        // Remove COMMAND_PREFIX
        String beheaded = raw.replaceFirst(Const.COMMAND_PREFIX, "");
        // Split remaining arguements for parsing
        String[] splitBeheaded = beheaded.split(" ");
        split.addAll(Arrays.asList(splitBeheaded));
        // Invoke the base command
        String invoke = split.get(0);
        // Populate String args for passing to the parser
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);
        
        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, event);
    }

    public static class CommandContainer {
        
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;

        public CommandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent event) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }
    }
    
}
