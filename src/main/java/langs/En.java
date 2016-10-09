/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package langs;

import util.Const;
import util.PropReader;

/**
 * English language output strings
 *
 * @author Veteran Software
 * @version 1.0
 * @since 10/1/2016
 */
public interface En {

    // Don't forget to add the String reference in Const.java

    String EMPTY_ARGS = "I think you forgot some of the command.  Check the help command for more info.";
    String EMPTY_COMMAND = "Next time you wake me up, please send a command as well.";
    String HELP_PM = "Hey there!  I heard you needed some help.  Below is the list of commands I recognize.\n\n";
    String INCORRECT_ARGS = "You passed incorrect or missing arguments to me.  Check the help command for more info.";
    String PING = "Surprise, motherfucker!";
    String PRIVATE_MESSAGE_REPLY = "I'm sorry, but the bot you are trying to reach has a voice mail box that has not " +
            "been setup yet.  Please try your PM again later.";
    String TYPE_ONCE = "You only need to type that part once, silly.";
    String ALREADY_EXISTS = "/shrug It looks like you already added that to my database.";
    String DOESNT_EXIST = "That was never added to my database.";
    String COMPACT_MODE_ON = "Compact mode has been turned on.";
    String COMPACT_MODE_OFF = "Compact mode has been turned off.";
    String MOVE_SUCCESS = "All of my announcements will now be made here!";
    String MOVE_FAILURE = "I can't seem to send announcements there.  Make sure I have the proper permissions in that" +
            " channel.";
    String MOVE_DONT_OWN_CHANNEL = "Hey now, I can't announce to a channel that doesn't exists on your server!";
    String COMPACT_FAILURE = "Um, something went wrong.  My compact mode is unchanged.";

    // Command specific text
    String ADD_HELP = "```Ruby\nADD: Used to add something to my database.\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " add <option> <content>"
            + "\n\t<option>\t<content>"
            + "\n\tgame - The name of the game exactly as it appears on the streaming platform"
            + "\n\tchannel - The streamer's name"
            + "\n\tteam - The name of the streaming team (Twitch only)"
            + "\n\ttag - Word or group of words to search for in the stream title```";
    String ANNOUNCE_HELP = "```Ruby\nANNOUNCE: Shhh...  I'm a secret...\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " announce <content>\n\tThis command is only available to the developers.```";
    String COMPACT_HELP = "```Ruby\nCOMPACT: Switch my announcements to a shorter version.\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " compact <option>"
            + "\n<option>"
            + "\n\ton - Turns on Compact Mode"
            + "\n\toff - Turns off Compact Mode```";
    String INVITE = "Hey buddy! Invite me to your server!\n\t"
            + "Click here: https://discordapp.com/oauth2/authorize?&client_id="
            + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=224256";
    String INVITE_HELP = "```Ruby\nINVITE: used to display my invite link\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " invite\n\tInvite NowLive bot to your Discord Server.```";
    String MOVE_HELP = "```Ruby\nMOVE: Change where I make my announcements.\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " move <channel>\n\t"
            + "<channel> - The name of the channel you wish to move my announcements to (MUST include the #)```";
    String PING_HELP = "```Ruby\nPING: Used to ping me. If I am working correctly, I'll send you a pong.\nUSAGE: "
            + Const.COMMAND_PREFIX + "ping```";
    String REMOVE_HELP = "```Ruby\nREMOVE: Used to remove something to my database.\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " remove <option> <content>"
            + "\n\t<option>\t<content>"
            + "\n\tgame - The name of the game exactly as it appears on the streaming platform"
            + "\n\tchannel - The streamer's name"
            + "\n\tteam - The name of the streaming team (Twitch only)"
            + "\n\ttag - Word or group of words to search for in the stream title```";
    String STREAMS_HELP = "```Ruby\nSTREAMS: I'll send you a list of active streams as a PM.\nUSAGE:  "
            + Const.COMMAND_PREFIX
            + Const.COMMAND
            + " streams```";
    String STATUS_HELP = "";
}
