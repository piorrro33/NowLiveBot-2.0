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
import util.database.calls.GetCleanUp;
import util.database.calls.Tracker;

import static platform.discord.controller.DiscordController.sendToChannel;

/**
 * @author Veteran Software by Ague Mort
 */
public class CleanUp implements Command {

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !args.isEmpty()) {
            return "none".equals(args) || "edit".equals(args) || "delete".equals(args) || "help".equals(args);
        }

        return false;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        String returnStatement;
        String query;
        switch (args) {
            case "none":
                query = "UPDATE `guild` SET `cleanup` = 0 WHERE `guildId` = ?";
                returnStatement = LocaleString.getString(event.getMessage().getGuild().getId(), "cleanupSuccessNone");
                break;
            case "edit":
                query = "UPDATE `guild` SET `cleanup` = 1 WHERE `guildId` = ?";
                returnStatement = LocaleString.getString(event.getMessage().getGuild().getId(), "cleanupSuccessEdit");
                break;
            case "delete":
                query = "UPDATE `guild` SET `cleanup` = 2 WHERE `guildId` = ?";
                returnStatement = LocaleString.getString(event.getMessage().getGuild().getId(), "cleanupSuccessDelete");
                break;
            default:
                return;
        }
        GetCleanUp cleanup = new GetCleanUp();
        if (cleanup.action(event.getGuild().getId(), query)) {
            sendToChannel(event, returnStatement);
        } else {
            sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "cleanupFail"));
        }
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "cleanupHelp"));
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Cleanup");
    }
}
