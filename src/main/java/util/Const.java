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

package util;

import langs.En;

/**
 * Common variables used throughout
 *
 * @author Veteran Software
 * @version 1.0
 * @since 10/1/2016
 */
public interface Const {

    // Commonly used Constants

    String DISCORD_URL = "https://discord.gg/gKbbrFK";
    String BOT_LOGO = "https://cdn.discordapp.com/attachments/251541740484296705/251973422521778177/nl2_bot.png";
    String COMMAND_PREFIX = "-";// Requires single, regex safe character (at the moment) wahahahaha
    String BOT_NAME = "Now Live";
    String COMMAND = "nl";
    String PLAYING = COMMAND_PREFIX + COMMAND + " help";
    String LOG_CHANNEL = PropReader.getInstance().getProp().getProperty("discord.log.chan");
    String WARN = "warning";

    // Language specific variables
    // TODO:  Replace 'En' with the guild language from the DB (when it's setup that way)
    String HELP_PM = En.HELP_PM;
    String TYPE_ONCE = En.TYPE_ONCE;
    String INCORRECT_ARGS = En.INCORRECT_ARGS;
    String EMPTY_COMMAND = En.EMPTY_COMMAND;
    String EMPTY_ARGS = En.EMPTY_ARGS;
    String PRIVATE_MESSAGE_REPLY = En.PRIVATE_MESSAGE_REPLY;
    String ALREADY_EXISTS = En.ALREADY_EXISTS;
    String DOESNT_EXIST = En.DOESNT_EXIST;
    String NONE_ONLINE = En.NONE_ONLINE;
    String ONLINE_STREAM_PM_1 = En.ONLINE_STREAM_PM_1;
    String ONLINE_STREAM_PM_2 = En.ONLINE_STREAM_PM_2;
    String NOW_PLAYING_LOWER = En.NOW_PLAYING_LOWER;
    String BEAM_USER_NO_EXIST = En.BEAM_USER_NO_EXIST;
    String DISCORD_USER_NO_EXIST = En.DISCORD_USER_NO_EXIST;
    String ON = En.ON;
    String WATCH_THEM_HERE = En.WATCH_THEM_HERE;
    String NOTIFY_NONE = En.NOTIFY_NONE;
    String NOTIFY_HERE = En.NOTIFY_HERE;
    String NOTIFY_EVERYONE = En.NOTIFY_EVERYONE;
    String OOPS = En.OOPS;
    String WRONG_COMMAND = En.WRONG_COMMAND;
    String ALREADY_MANAGER = En.ALREADY_MANAGER;
    String NOT_A_MANAGER = En.NOT_A_MANAGER;
    String NOT_AN_ADMIN = En.NOT_AN_ADMIN;
    String ADMIN_OVERRIDE = En.ADMIN_OVERRIDE;
    String NOW_LIVE = En.NOW_LIVE;
    String OFFLINE = En.OFFLINE;
    String CANT_REMOVE_OWNER = En.CANT_REMOVE_OWNER;
    String NO_BOT_MANAGER = En.NO_BOT_MANAGER;
    String NEED_ONE_MANAGER = En.NEED_ONE_MANAGER;
    String GUILD_JOIN_SUCCESS = En.GUILD_JOIN_SUCCESS;
    String BROADCASTER_LANG_SUCCESS = En.BROADCASTER_LANG_SUCCESS;
    String BROADCASTER_LANG_ALL_SUCCESS = En.BROADCASTER_LANG_ALL_SUCCESS;
    String BROADCASTER_LANG_FAIL = En.BROADCASTER_LANG_FAIL;

    // Command specific text
    String ADD_HELP = En.ADD_HELP;
    String ANNOUNCE_HELP = En.ANNOUNCE_HELP;
    String BEAM_HELP = En.BEAM_HELP;
    String CLEANUP_HELP = En.CLEANUP_HELP;
    String CLEANUP_SUCCESS_NONE = En.CLEANUP_SUCCESS_NONE;
    String CLEANUP_SUCCESS_EDIT = En.CLEANUP_SUCCESS_EDIT;
    String CLEANUP_SUCCESS_DELETE = En.CLEANUP_SUCCESS_DELETE;
    String CLEANUP_FAIL = En.CLEANUP_FAIL;
    String COMPACT_HELP = En.COMPACT_HELP;
    String COMPACT_FAILURE = En.COMPACT_FAILURE;
    String COMPACT_MODE_ON = En.COMPACT_MODE_ON;
    String COMPACT_MODE_OFF = En.COMPACT_MODE_OFF;
    String INVITE = En.INVITE;
    String INVITE_HELP = En.INVITE_HELP;
    String LIST_HELP = En.LIST_HELP;
    String MOVE_DONT_OWN_CHANNEL = En.MOVE_DONT_OWN_CHANNEL;
    String MOVE_FAILURE = En.MOVE_FAILURE;
    String MOVE_HELP = En.MOVE_HELP;
    String MOVE_SUCCESS = En.MOVE_SUCCESS;
    String NOTIFY_HELP = En.NOTIFY_HELP;
    String PING = En.PING;
    String PING_HELP = En.PING_HELP;
    String REMOVE_HELP = En.REMOVE_HELP;
    String STREAMLANG_HELP = En.STREAMLANG_HELP;
    String STREAMS_HELP = En.STREAMS_HELP;
    String STATUS_HELP = En.STATUS_HELP;
    String TWITCH_HELP = En.TWITCH_HELP;
    String USE_PLATFORM = En.USE_PLATFORM;
}
