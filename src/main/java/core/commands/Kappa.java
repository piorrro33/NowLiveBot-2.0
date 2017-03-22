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
import core.Main;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.discord.controller.DiscordController;
import util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class Kappa implements Command {

    private Connection connection;

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void action(String args, GuildMessageReceivedEvent event) {
        PreparedStatement pStatement = null;
        ResultSet result = null;

        String query = "SELECT `channelId`, `guildId` FROM `guild`";
        try {
            if (connection == null || connection.isClosed()) {
                this.connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            result = pStatement.executeQuery();

            MessageBuilder message = new MessageBuilder();
            message.append("Here's a list of guilds that don't have the proper permissions set for the bot:\n");

            while (result.next()) {
                String guildName = Main.getJDA().getGuildById(result.getString("guildId")).getName();
                Channel channel = Main.getJDA().getTextChannelById(result.getString("channelId"));
                Member selfMember = Main.getJDA().getGuildById(result.getString("guildId")).getSelfMember();
                if (channel != null && !selfMember.hasPermission(channel, Permission.ADMINISTRATOR) ||
                        (!selfMember.hasPermission(channel, Permission.MESSAGE_READ) &&
                                !selfMember.hasPermission(channel, Permission.MESSAGE_WRITE) &&
                                !selfMember.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS))) {
                    String appended = String.format("\tG:%s:%s\n", guildName, result.getString("guildId"));
                    message.append(appended);
                }
                if (message.length() >= 1800) {
                    DiscordController.sendToChannel(event, message.build().getRawContent());
                    message = new MessageBuilder();
                }
            }
            DiscordController.sendToChannel(event, message.build().getRawContent());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(result, pStatement, connection);
        }
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(GuildMessageReceivedEvent event) {

    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {

    }
}
