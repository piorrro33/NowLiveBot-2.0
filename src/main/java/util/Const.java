/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class Const {

    // Commonly used Constants

    public static final String TWITCH_CLIENT_ID = "7713uzjnh50pdd9v2v0apto553xeo67";
    public static final String COMMAND_PREFIX = "-";// Requires single, regex safe character (at the moment) wahahahaha
    public static final String BOT_NAME = "NowLiveBot";
    public static final String COMMAND = "nl";
    //public static final Integer COMMAND_LENGTH = COMMAND.length();
    public static final String PLAYING = "Playstation 360";
    // Language specific variables
    // TODO:  Replace 'En' with the guild language from the DB (when it's setup that way)
    public static final String HELP_PM = En.HELP_PM;
    public static final String TYPE_ONCE = En.TYPE_ONCE;
    public static final String INCORRECT_ARGS = En.INCORRECT_ARGS;
    public static final String EMPTY_COMMAND = En.EMPTY_COMMAND;
    public static final String EMPTY_ARGS = En.EMPTY_ARGS;
    public static final String PRIVATE_MESSAGE_REPLY = En.PRIVATE_MESSAGE_REPLY;
    public static final String ADD_HELP = En.ADD_HELP;
    public static final String ALREADY_EXISTS = En.ALREADY_EXISTS;
    public static final String DOESNT_EXIST = En.DOESNT_EXIST;
    public static final String NONE_ONLINE = En.NONE_ONLINE;
    public static final String ONLINE_STREAM_PM_1 = En.ONLINE_STREAM_PM_1;
    public static final String ONLINE_STREAM_PM_2 = En.ONLINE_STREAM_PM_2;
    public static final String NOW_PLAYING_LOWER = En.NOW_PLAYING_LOWER;
    public static final String ON = En.ON;
    public static final String WATCH_THEM_HERE = En.WATCH_THEM_HERE;
    public static final String NOTIFY_NONE = En.NOTIFY_NONE;
    public static final String NOTIFY_ME = En.NOTIFY_ME;
    public static final String NOTIFY_HERE = En.NOTIFY_HERE;
    public static final String NOTIFY_EVERYONE = En.NOTIFY_EVERYONE;
    public static final String OOPS = En.OOPS;
    public static final String WRONG_COMMAND = En.WRONG_COMMAND;
    public static final String NOT_A_MANAGER = En.NOT_A_MANAGER;
    public static final String NOT_AN_ADMIN = En.NOT_AN_ADMIN;
    public static final String ADMIN_OVERRIDE = En.ADMIN_OVERRIDE;
    public static final String NOW_LIVE = En.NOW_LIVE;

    // Command specific text
    public static final String ANNOUNCE_HELP = En.ANNOUNCE_HELP;
    public static final String CLEANUP_HELP = En.CLEANUP_HELP;
    public static final String CLEANUP_SUCCESS_NONE = En.CLEANUP_SUCCESS_NONE;
    public static final String CLEANUP_SUCCESS_EDIT = En.CLEANUP_SUCCESS_EDIT;
    public static final String CLEANUP_SUCCESS_DELETE = En.CLEANUP_SUCCESS_DELETE;
    public static final String CLEANUP_FAIL = En.CLEANUP_FAIL;
    public static final String COMPACT_HELP = En.COMPACT_HELP;
    public static final String COMPACT_FAILURE = En.COMPACT_FAILURE;
    public static final String COMPACT_MODE_ON = En.COMPACT_MODE_ON;
    public static final String COMPACT_MODE_OFF = En.COMPACT_MODE_OFF;
    public static final String DISABLE_HELP = En.DISABLE_HELP;
    public static final String DISABLE_SUCCESS = En.DISABLE_SUCCESS;
    public static final String DISABLE_FAIL = En.DISABLE_FAIL;
    public static final String ENABLE_HELP = En.ENABLE_HELP;
    public static final String ENABLE_SUCCESS = En.ENABLE_SUCCESS;
    public static final String ENABLE_FAIL = En.ENABLE_FAIL;
    public static final String INVITE = En.INVITE;
    public static final String INVITE_HELP = En.INVITE_HELP;
    public static final String MOVE_DONT_OWN_CHANNEL = En.MOVE_DONT_OWN_CHANNEL;
    public static final String MOVE_FAILURE = En.MOVE_FAILURE;
    public static final String MOVE_HELP = En.MOVE_HELP;
    public static final String MOVE_SUCCESS = En.MOVE_SUCCESS;
    public static final String NOTIFY_HELP = En.NOTIFY_HELP;
    public static final String PING = En.PING;
    public static final String PING_HELP = En.PING_HELP;
    public static final String REMOVE_HELP = En.REMOVE_HELP;
    public static final String STREAMS_HELP = En.STREAMS_HELP;
    public static final String STATUS_HELP = En.STATUS_HELP;

}
