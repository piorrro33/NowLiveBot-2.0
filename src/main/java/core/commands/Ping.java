/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Const;
import util.database.Database;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author keesh
 */
public class Ping implements Command {

    private static Logger logger = LoggerFactory.getLogger(Ping.class);

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
        Database.getInstance().checkPooledStatus();
        sendToChannel(event, Const.PING);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.PING_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Ping");
    }

}
