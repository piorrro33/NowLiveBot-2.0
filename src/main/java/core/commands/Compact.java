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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.database.calls.SetCompact;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class Compact implements Command {

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        String[] options = new String[]{"on", "off", "help"};

        for (String s : options) {
            // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                if (optionCheck(args, s)) {
                    return true;
                } else if ("help".equals(args)) {
                    // If the help argument is the only argument that is passed
                    return true;
                }
            }
        }
        // If all checks fail
        return false;
    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        Integer intArg = -1;
        // Make sure that on/off are transposed properly for DB insertion
        switch (args) {
            case "on":
                intArg = 1;
                break;
            case "off":
                intArg = 0;
                break;
            default:
                sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "incorrectArgs"));
                break;
        }

        if (intArg.equals(1) || intArg.equals(0)) {
            if (SetCompact.action(event.getGuild().getId(), intArg)) {
                switch (args) {
                    case "off":
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "compactOff"));
                        break;
                    default:
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "compactOn"));
                        break;
                }
            }
        }
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "compactHelp"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }

    private static boolean optionCheck(String args, String option) {
        return args.toLowerCase().substring(0, option.length()).equals(option);
    }
}
