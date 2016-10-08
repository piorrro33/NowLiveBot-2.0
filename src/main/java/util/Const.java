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
    public static final Integer COMMAND_LENGTH = COMMAND.length();
    public static final String PLAYING = "Playstation 5";
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

    // Command specific text
    public static final String ANNOUNCE_HELP = En.ANNOUNCE_HELP;
    public static final String COMPACT_HELP = En.COMPACT_HELP;
    public static final String PING = En.PING;
    public static final String PING_HELP = En.PING_HELP;
    public static final String INVITE = En.INVITE;
    public static final String INVITE_HELP = En.INVITE_HELP;
    public static final String MOVE_HELP = En.MOVE_HELP;
    public static final String REMOVE_HELP = En.REMOVE_HELP;
    public static final String STREAMS_HELP = En.STREAMS_HELP;

}
