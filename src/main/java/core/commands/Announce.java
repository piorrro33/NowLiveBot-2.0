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
import net.dv8tion.jda.core.exceptions.PermissionException;
import util.DiscordLogger;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Announce implements Command {

    private static ResultSet result;
    private static PreparedStatement pStatement;
    private static Connection connection;

    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return args != null && !args.isEmpty();

    }

    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {
        try {
            String query = "SELECT `guildId` FROM `guild` ORDER BY `guildId` ASC";
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);

            result = pStatement.executeQuery();
            while (result.next()) {
                try {
                    event.getJDA().getGuildById(result.getString("guildId")).getPublicChannel()
                            .sendMessage(LocaleString.getString(event.getMessage().getGuild().getId(), "devMessage") + args).complete();
                } catch (PermissionException pe) {
                    System.out.println("Permissions exception.");
                    pe.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }

        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "devMessage") + args);
        new DiscordLogger(" :globe_with_meridians: Global announcement sent.", event);
        System.out.println("[SYSTEM] Global announcement sent.");
    }

    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "announceHelp"));
    }

    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }
}
