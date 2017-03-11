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
import util.database.Database;
import util.database.calls.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static platform.discord.controller.DiscordController.sendToChannel;
import static util.database.Database.cleanUp;

public class BotLanguage implements Command {

    private static Connection connection;
    private static PreparedStatement pStatement;

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public boolean called(String args, GuildMessageReceivedEvent event) {
        if (args != null && !"".equals(args)) {
            switch (args.toLowerCase()) {
                case "english":
                    //case "danish": // Danish
                    //case "dansk":
                case "german": // German
                case "deutsch":
                case "spanish": // Spanish
                case "español":
                case "french": // French
                case "français":
                    //case "italian": // Italian
                    //case "italiano":
                    //case "hungarian": // Hungarian
                    //case "magyar":
                    //case "norwegian": // Norwegian
                    //case "norsk":
                    //case "polish": // Polish
                    //case "polski":
                    //case "portguese": // Portuguese
                    //case "português":
                    //case "slovenian": // Slovenian
                    //case "slovenščina":
                    //case "slovenčina":
                    //case "finnish": // Finnish
                    //case "suomi":
                    //case "swedish": // Swedish
                    //case "svenska":
                    //case "vietnamese": // Vietnamese
                    //case "tiếng việt":
                    //case "turkish": // Turkish
                    //case "türkçe":
                case "czech": // Czech
                case "čeština":
                    //case "greek": // Greek
                    //case "ελληνικά":
                    //case "bulgarian": // Bulgarian
                    //case "български":
                case "russian": // Russian
                case "русский":
                    //case "arabic": // Arabic
                    //case "العربية":
                    //case "thai": // Thai language
                    //case "ภาษาไทย":
                    //case "chinese": // Chinese
                    //case "中文":
                    //case "japanese": // Japanese
                    //case "日本語":
                    //case "korean": // Korean
                    //case "한국어":
                    //case "all":
                case "help":
                    return true;
                default:
                    return false;
            }
        } else if ("help".equals(args)) {
            return true;
        }
        return false;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void action(String args, GuildMessageReceivedEvent event) {
        String langCode = null;
        if (args != null && !"".equals(args)) {
            switch (args.toLowerCase()) {
                case "english":
                    langCode = "en";
                    break;
                /*case "danish": // Danish
                case "dansk":
                    langCode = "da";
                    break;*/
                case "german": // German
                case "deutsch":
                    langCode = "de";
                    break;
                case "spanish": // Spanish
                case "español":
                    langCode = "es";
                    break;
                case "french": // French
                case "français":
                    langCode = "fr";
                    break;/*
                case "italian": // Italian
                case "italiano":
                    langCode = "it";
                    break;
                case "hungarian": // Hungarian
                case "magyar":
                    langCode = "hu";
                    break;
                case "norwegian": // Norwegian
                case "norsk":
                    langCode = "nn";
                    break;
                case "polish": // Polish
                case "polski":
                    langCode = "pl";
                    break;
                case "portguese": // Portuguese
                case "português":
                    langCode = "pt";
                    break;
                case "slovenian": // Slovenian
                case "slovenščina":
                case "slovenčina":
                    langCode = "sl";
                    break;
                case "finnish": // Finnish
                case "suomi":
                    langCode = "fi";
                    break;
                case "swedish": // Swedish
                case "svenska":
                    langCode = "sv";
                    break;
                case "vietnamese": // Vietnamese
                case "tiếng việt":
                    langCode = "vi";
                    break;
                case "turkish": // Turkish
                case "türkçe":
                    langCode = "tr";
                    break;*/
                case "czech": // Czech
                case "čeština":
                    langCode = "cs";
                    break;/*
                case "greek": // Greek
                case "ελληνικά":
                    langCode = "el";
                    break;
                case "bulgarian": // Bulgarian
                case "български":
                    langCode = "bg";
                    break;*/
                case "russian": // Russian
                case "русский":
                    langCode = "ru";
                    break;
                /*case "arabic": // Arabic
                case "العربية":
                    langCode = "ar";
                    break;
                case "thai": // Thai language
                case "ภาษาไทย":
                    langCode = "th";
                    break;
                case "chinese": // Chinese
                case "中文":
                    langCode = "zh";
                    break;
                case "japanese": // Japanese
                case "日本語":
                    langCode = "ja";
                    break;
                case "korean": // Korean
                case "한국어":
                    langCode = "ko";
                    break;*/
                default:
                    // Any language (default)
                    sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "botLangUnsupported"));
                    break;
            }
            if (langCode != null) {
                try {
                    String query = "UPDATE `guild` SET `serverLang` = ? WHERE `guildId` = ?";

                    if (connection == null || connection.isClosed()) {
                        connection = Database.getInstance().getConnection();
                    }

                    pStatement = connection.prepareStatement(query);
                    pStatement.setString(1, langCode);
                    pStatement.setString(2, event.getGuild().getId());

                    if (pStatement.executeUpdate() > 0) {
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "botLangSuccess"));
                    } else {
                        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "botLangFail"));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(pStatement, connection);
                }
            }
        }
    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: MessageReceivedEvent
     */
    @Override
    public void help(GuildMessageReceivedEvent event) {
        sendToChannel(event, LocaleString.getString(event.getMessage().getGuild().getId(), "botLangHelp"));
    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: MessageReceivedEvent
     */
    @Override
    public void executed(boolean success, GuildMessageReceivedEvent event) {
        new Tracker("Command");
    }
}
