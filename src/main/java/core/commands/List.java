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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import platform.discord.controller.DiscordController;
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static platform.discord.controller.DiscordController.sendToPm;
import static util.database.Database.cleanUp;

/**
 * @author Veteran Software by Ague Mort
 */
public class List implements Command {

    private static Connection connection;
    private static PreparedStatement pStatement;
    private static ResultSet resultSet;
    private String option;
    private String query;
    private String guildId;
    private String[] options = new String[]{"channel", "filter", "game", "manager", "streamLang", "tag", "team", "help", "setting"};

    private Message createNotificationMessage(MessageBuilder message, GuildMessageReceivedEvent event) {
        MessageBuilder msg = message;
        try {
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, guildId);
            resultSet = pStatement.executeQuery();

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    msg.append("\n\t");
                    if (!"manager".equals(option)) {
                        msg.append(resultSet.getString(1).replaceAll("''", "'"));
                        switch (resultSet.getInt(2)) {
                            case 1:
                                msg.append(" on Twitch.tv");
                                break;
                            default:
                                break;
                        }
                    } else {
                        String userId = resultSet.getString("userId");
                        User user = event.getJDA().getUserById(userId);
                        String userName = user.getName();
                        msg.append(userName);
                    }

                    // Large msg handler
                    if (msg.length() > 1850) {
                        sendToPm(event, msg.build());
                        msg = new MessageBuilder();
                    }
                }
            } else {
                msg.append("\n\tRuh Roh!  I can't seem to find anything here...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return msg.build();
    }

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        for (String s : this.options) { // Iterate through the available options for this command
            if (args != null && !args.isEmpty()) {
                String arg = args;
                if (arg.endsWith("s")) {
                    arg = args.substring(0, args.length() - 1);
                }
                if (arg.equals(s)) {
                    // Sets the class scope variables that will be used by action()
                    this.option = s;
                    return true;
                } else if ("help".equals(arg)) {
                    // If the help argument is the only argument that is passed
                    return true;
                }
            }
        }
        // If all checks fail
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
        DiscordController dController = new DiscordController(event);

        guildId = dController.getGuildId();

        MessageBuilder message = new MessageBuilder();
        message.append("Heya!  Here's a list of ");
        message.append(option);
        message.append("s that this Discord server is keeping tabs on:\n");

        switch (option) {
            case "channel":
                query = "SELECT `channelName`, `platformId` FROM `channel` WHERE `guildId` = ? " +
                        "ORDER BY `platformId` ASC, `channelName` ASC";
                break;
            case "game":
                query = "SELECT `name`, `platformId` FROM `game` WHERE `guildId` = ? " +
                        "ORDER BY `platformId` ASC, `name` ASC";
                break;
            case "filter":
                query = "SELECT `name`, `platformId` FROM `filter` WHERE `guildId` = ? " +
                        "ORDER BY `platformId` ASC, `name` ASC";
                break;
            case "manager":
                query = "SELECT `userId` FROM `manager` WHERE `guildId` = ? ORDER BY `userId` ASC";
                break;
            case "tag":
                query = "SELECT `name`, `platformId` FROM `tag` WHERE `guildId` = ? " +
                        "ORDER BY `platformId` ASC, `name` ASC";
                break;
            case "team":
                query = "SELECT `name`, `platformId` FROM `team` WHERE `guildId` = ? " +
                        "ORDER BY `platformId` ASC, `name` ASC";
                break;
            case "setting":
                sendToPm(event, getSettings(message));
                return;
            default:
                break;
        }

        sendToPm(event, createNotificationMessage(message, event));
    }

    private String getLanguage(String name) {
        switch (name) {
            case "en":
                return "English";
            case "da":
                return "Danish/Dansk";
            case "de":
                return "German/Deutsch";
            case "es":
                return "Spanish/Español";
            case "fr":
                return "French/Français";
            case "it":
                return "Italian/Italiano";
            case "hu":
                return "Hungarian/Magyar";
            case "nn":
                return "Norwegian/Norsk";
            case "pl":
                return "Polish/Polski";
            case "pt":
                return "Portugese/Português";
            case "sl":
                return "Slovenian/Slovenščina/Slovenčina";
            case "fi":
                return "Finnish/Suomi";
            case "sv":
                return "Swedish/Svenska";
            case "vi":
                return "Vietnamese/Tiếng Việt";
            case "tr":
                return "Turkish/Türkçe";
            case "cs":
                return "Czech/Čeština";
            case "el":
                return "Greek/ελληνικά";
            case "bg":
                return "Bulgarian/български";
            case "ru":
                return "Russian/русский";
            case "ar":
                return "Arabic/العربية";
            case "th":
                return "Thai/ภาษาไทย";
            case "zh":
                return "Chinese/中文";
            case "ja":
                return "Japanese/日本語";
            case "ko":
                return "Korean/한국어";
            default:
                return "all";
        }
    }


    private Message getSettings(MessageBuilder message) {
        Integer compact = 0;
        Integer cleanup = 0;
        String broadLang = "";
        String serverLang = "";
        Integer notify = 0;

        try {
            if (connection == null || connection.isClosed()) {
                connection = Database.getInstance().getConnection();
            }

            PreparedStatement pStatement = connection.prepareStatement("SELECT `isCompact`, `cleanup`, `broadcasterLang`, `serverLang` FROM `guild` WHERE `guildId` = ?");
            pStatement.setString(1, guildId);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                compact = resultSet.getInt(1);
                cleanup = resultSet.getInt(2);
                broadLang = resultSet.getString(3);
                serverLang = resultSet.getString(4);
            }

            pStatement = connection.prepareStatement("SELECT `level` FROM `notification` WHERE `guildId` = ?");
            pStatement.setString(1, guildId);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                notify = resultSet.getInt(1);
            }

            message.append(util.Const.LIST_SETTINGS
                    .replace("compactSetting", (compact == 0 ? "On" : "Off"))
                    .replace("notificationSetting", (notify == 0 ? "no one" : notify == 2 ? "here" : "everyone"))
                    .replace("cleanupSetting", (cleanup == 0 ? "do nothing" : cleanup == 1 ? "edit" : "delete"))
                    .replace("broadLang", getLanguage(broadLang))
                    .replace("serverLang", getLanguage(serverLang)));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanUp(resultSet, pStatement, connection);
        }
        return message.build();
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "listHelp"));
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("List");
    }
}
