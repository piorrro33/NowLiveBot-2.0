/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author keesh
 */
public class Invite implements Command {

    @Override
    public boolean called(String args, MessageReceivedEvent event) {

        if (args != null && !args.isEmpty()) {
            if ("help".equals(args)) { // If the help argument is the only argument that is passed
                return true;
            } else {
                sendToChannel(event, Const.INCORRECT_ARGS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void action(String args, MessageReceivedEvent event) {
        sendToChannel(event, Const.INVITE);
    }

    @Override
    public void help(MessageReceivedEvent event) {
        sendToChannel(event, Const.INVITE_HELP);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        new Tracker("Invite");
    }
}
