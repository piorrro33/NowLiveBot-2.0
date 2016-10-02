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
    String INCORRECT_ARGS = "You passed incorrect or missing arguments to me.  Check the help command for more info.";
    String INVITE = "Hey buddy! Invite me to your server!\n\t"
            + "Click here: https://discordapp.com/oauth2/authorize?&client_id="
            + Const.DISCORD_CLIENT_ID
            + "&scope=bot&permissions=224256";
    String INVITE_HELP = "USAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " invite\n\tInvite NowLive bot to your Discord Server.";
    String PING_HELP = "USAGE: !ping\n\tPing NowLiveBot.";
    String EMPTY_COMMAND = "Next time you wake me up, please send a command as well.";
    String ADD_HELP = "USAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " add <option> <content>"
            + "\n\t__**<option>**__\t- __<content>__"
            + "\n\t**game** - The name of the game exactly as it appears on the streaming platform"
            + "\n\t**channel** - The streamer's name"
            + "\n\t**team** - The name of the streaming team (Twitch only)"
            + "\n\t**tag** - Word or group of words to search for in the stream title\n";
    String COMPACT_HELP = "USAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " compact <option>"
            + "\n__**<option>**__"
            + "\n\t**on** - Turns on Compact Mode"
            + "\n\t**off** - Turns off Compact Mode";
    String ANNOUNCE_HELP = "USAGE:  " +
            Const.COMMAND_PREFIX +
            Const.COMMAND +
            " announce <content>";
}
