/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package core.commands;

import core.Command;
import langs.LocaleString;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.Const;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;

/**
 * @author Veteran Software by Ague Mort
 */
public class Help implements Command {

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        // TODO: need to revamp how help is done bc 2,000 character limit
        MessageBuilder message = new MessageBuilder();
        message.append("Hey there, " + event.getAuthor().getName() + "!\n\n");
        message.append("So I hear you're looking for some help?  Below is a list of my commands.  To find out what " +
                "each one of them does, type " + Const.COMMAND_PREFIX + Const.COMMAND + "<command> help\n\n");
        message.append("```Ruby\n* add\n* beam\n* cleanup\n* compact\n* invite\n* list\n* move\n* notify");
        message.append("\n* ping\n* remove\n* streamlang\n* streams\n* twitch```");
        message.append("\nJust to make it known, Ague is still working hard to finish up all of my polish, so some " +
                "of the commands listed may not be working just yet!  But their help is working.  Bear with the guy, " +
                "he's working hard to get things right!\n\n\t~~" + Const.BOT_NAME + "\n\n");
        message.append("You can also get some help from my developer and the rest of the Now Live community on my " +
                "Discord server!  just click this link to join:  http://discord.gg/gKbbrFK");
        message.append("\n\n*P.S. I don't monitor this mailbox, so please don't send me any messages through PM*");
        sendToPm(event, message.build());
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "typeOnce"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Help");
    }
}
