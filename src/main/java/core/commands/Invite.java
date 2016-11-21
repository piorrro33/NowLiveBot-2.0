/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.commands;

import core.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author keesh
 */
public class Invite implements Command {

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {

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
    public final void action(String args, GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.INVITE);
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, Const.INVITE_HELP);
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Invite");
    }
}
