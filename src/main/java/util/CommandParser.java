/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.logging.Logger;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

/**
 *
 * @author keesh
 */
public class CommandParser {
    private static final Logger LOG = Logger.getLogger(CommandParser.class.getName());
    
    public CommandContainer parse (String rw, MessageReceivedEvent event) {
        ArrayList<String> split = new ArrayList<>();
        String raw = rw;
        String beheaded = raw.replaceFirst("!", "");
        String[] splitBeheaded = beheaded.split(" ");
        for (String s : splitBeheaded) {
            split.add(s);
        }
        String invoke = split.get(0);
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
