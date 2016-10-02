/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.logging.Logger;

/**
 * Common variables used throughout
 *
 * @author Veteran Software
 * @version 1.0
 * @since 10/1/2016
 */
public class Const {

    // Commonly used Constants
    public static final String DISCORD_BOT_TOKEN = "MjI4NTgzODExMDI4NDg0MDk4.Cs_YDg.O6oyssWJYFBRwcZUIq4-Me26xXQ";
    public static final String TWITCH_CLIENT_ID = "7713uzjnh50pdd9v2v0apto553xeo67";
    public static final String COMMAND_PREFIX = "-";// Requires single, regex safe character (at the moment) wahahahaha
    public static final String COMMAND = "nowlive";
    public static final String DISCORD_CLIENT_ID = "228583811028484098";
    public static final Integer COMMAND_LENGTH = COMMAND.length();
    // Language specific variables
    // TODO:  Replace 'En' with the guild language from the DB (when it's setup that way)
    public static final String INVITE = langs.En.INVITE;
    public static final String INVITE_HELP = langs.En.INVITE_HELP;
    public static final String PING_HELP = langs.En.PING_HELP;
    public static final String EMPTY_COMMAND = langs.En.EMPTY_COMMAND;
    public static final String ADD_HELP = langs.En.ADD_HELP;
    public static final String COMPACT_HELP = langs.En.COMPACT_HELP;
    private static final Logger LOG = Logger.getLogger(Const.class.getName());

}
