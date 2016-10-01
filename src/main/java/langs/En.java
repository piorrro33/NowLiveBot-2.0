/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package langs;

import util.Const;

/**
 * English language output strings
 *
 * @author Veteran Software
 * @version 1.0
 * @since 10/1/2016
 */
public interface En {

    // Don't forget to add the String reference in Const.java
    public static final String INVITE = "Hey buddy! Invite me to your server!\n"
            + "Click here: https://discordapp.com/oauth2/authorize?&client_id="
            + Const.DISCORD_CLIENT_ID
            + "&scope=bot&permissions=224256";
    public static final String INVITE_HELP = "USAGE: "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " invite\t -Invite NowLive bot to your Discord Server.";
    public static final String PING_HELP = "USAGE: !ping - Ping NowLiveBot.";
    public static final String EMPTY_COMMAND = "Next time you wake me up, please send a command as well.";
    public static final String ADD_HELP = "USAGE: "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " add <option> <content>"
            + "\n\t__**<option>**__\t- __<content>__"
            + "\n\t**game**\t\t-> The name of the game exactly as it appears on the streaming platform"
            + "\n\t**channel**\t-> The streamer's name"
            + "\n\t**team**\t\t-> The name of the streaming team (Twitch only)"
            + "\n\t**tag**\t\t\t-> Word or group of words to search for in the stream title\n";
}
