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

package langs;

import util.Const;
import util.PropReader;

import java.util.ListResourceBundle;

/**
 * @author Veteran Software by Ague Mort
 */
public class LanguageBundle extends ListResourceBundle {

    private Object[][] contents = {
            {"emptyArgs", "I think you forgot some of the command.  Check the help command for more info."},//DONE
            {"emptyCommand", "Next time you wake me up, please send a command as well."},//DONE
            {"helpPrivateMessage", "Hey there!  I heard you needed some help.  Below is the list of commands I " +
                    "recognize.\n\n"},//NOT FOUND
            {"incorrectArgs", "You passed incorrect or missing arguments to me.  Check the help command for more info" +
                    "."},//DONE
            {"ping", "When I was in China on the All-American Ping Pong team, I just loved playing ping-pong with my " +
                    "Flexolite ping pong paddle."},//DONE
            {"privateMessageReply", "I'm sorry, but the bot you are trying to reach has a voice mail box that has not" +
                    " been setup yet.  Please try your PM again later."},//DONE
            {"typeOnce", "You only need to type that part once, silly."},//DONE
            {"wrongCommand", ":thinking: I don't know that command."},//DONE
            {"alreadyExists", "It looks like you already added that to my database. ¯\\_(ツ)_/¯"},//DONE
            {"beamUserNoExist", "That Beam user does not exist! Check your spelling and try again!"},//DONE
            {"discordUserNoExist", "That person isn't a Discord user!  Try again!"},//DONE
            {"doesNotExist", "That was never added to my database."},//NOT FOUND
            {"adminOverride", "*Permission of this command have been overridden by a bot developer.*"},//DONE
            {"broadcasterLangSuccess", ":ok_hand: I'll only look for streams that are in that language!"},//DONE
            {"broadcasterLangAllSuccess", ":ok_hand: I'll only look for streams that all languages!"},//DONE
            {"broadcasterLangFail", ""},//NEED TO BUILD STRING
            {"cleanupSuccessNone", ":ok_hand: I won't do anything to my announcements."},
            {"cleanupSuccessEdit", "Editing my announcements, it is."},
            {"cleanupSuccessDelete", "Sir, yes Sir!  I will delete all my announcements from now on!"},
            {"cleanupFail", "Ah, man...  Something went wrong..."},
            {"compactFail", "Um, something went wrong.  My compact mode is unchanged."},
            {"compactOn", ":compression: Compact mode has been turned on."},
            {"compactOff", ":compression: Compact mode has been turned off."},
            {"moveDoNotOwnChannel", ":no_entry: Hey now, I can't announce to a channel that doesn't exists on your " +
                    "server!"},
            {"moveFail", ":no_entry: I can't seem to send announcements there.  Make sure I have the proper permissions " +
                    "in that channel."},
            {"moveSuccess", ":ok_hand: I'll announce over there! :arrow_right: "},
            {"noneOnline", "Sorry bud, but there's nobody online right now that this Discord is following."},
            {"notifyNone", ":ok_hand: I won't mention anyone in my announcements."},
            {"notifyHere", ":bellhop_bell: All who are online will get a mention when I announce streams."},
            {"notifyEveryone", ":tada: WHOA!!  **EVERYONE** that belongs to the server will get notified when I " +
                    "announce streams!  *(Are you sure?  I don't recommend this for large servers...  It can make " +
                    "people grumpy.)*"},
            {"notAManager", "Sorry, but only my managers can do that."},
            {"notAnAdmin", "To whom it may concern:  I am your servant, but you are not my master."},
            {"nowPlayingLower", " is now playing "},
            {"onlineStreamPm1", "Hey there!  There's currently "},
            {"onlineStreamPm2", " streamers online that you may be interested in!  Follow their links to check them " +
                    "out: \n\n"},
            {"on", " on "},
            {"oops", "Oops!  Something went wrong and nothing was changed!  Let's try that again."},
            {"watchThemHere", "Watch them here: "},
            {"nowLive", "NOW LIVE!\n"},
            {"offline", "OFFLINE!\n"},
            {"alreadyManager", "It seems I've already hired that user as a manager.  Find moar humanz!"},
            {"canNotRemoveManager", "Silly human, you can't remove the server owner from the manager list. :laughing: " +
                    ":laughing:"},
            {"noBotManager", "It's against the Discord Bot Union By-Laws for bots to manage me. Sorry, try and find a " +
                    "suitable human for the job. :thumbsup:"},
            {"needOneManager", "If you remove that one, who will manage me?"},
            {"usePlatform", "Oops!  That's the old way of doing things!  Use the platform specific command!  Type `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` for more info."},
            {"guildJoinSuccess", "Hi there!  I'm Now Live, the stream announcing bot!  Type `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` for a list of my commands.\n\nIf you need some help setting me up, come " +
                    "join my Discord at " + Const.DISCORD_URL + " and check out the how-to-setup and command-list " +
                    "channels for all the info!\n\nDon't forget to say hey!"},
            {"addHelp", "```Ruby\nADD:  Used to add something to my database." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " add <option> <content>" +
                    "\n\t<option>\t<content>" +
                    "\n\tfilter - The game name that you want to filter streamers by" +
                    "\n\tgame - The name of the game exactly as it appears on the streaming platform" +
                    "\n\tmanager - The @ mention of the user to add as a manager```"},
            {"announceHelp", "```Ruby\nANNOUNCE:  Shhh...  I'm a secret..." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " announce <content>" +
                    "\n\tThis command is only available to the developers.```"},
            {"beamHelp", "```Ruby\nBEAM:  Add and remove things that are Beam.pro related." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam <sub-command> <option> <argument>" +
                    "\n\t<sub-command> <option> <argument>" +
                    "\n\tadd channel <channel-name>" +
                    "\n\tremove channel <channel-name>```"},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Change the way I clean up my stream announcements." +
                    "\nUSAGE:" + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup <option>" +
                    "\n<option>" +
                    "\n\tnone - I won't change a thing to my announcements! (default)" +
                    "\n\tedit - I'll edit my announcements to say \"OFFLINE\" whe the streamer goes offline" +
                    "\n\tdelete - I'll just delete the announcement when the streamer is no longer live```"},
            {"compactHelp", "```Ruby\nCOMPACT:  Switch my announcements to a shorter version." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " compact <option>" +
                    "\n<option>" +
                    "\n\ton - Turns on Compact Mode" +
                    "\n\toff - Turns off Compact Mode```"},
            {"invite", "Hey buddy! Invite me to your server!" +
                    "\n\n\t" + "**Click here:** https://discordapp.com/oauth2/authorize?&client_id=" +
                    PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  used to display my invite link" +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " invite" +
                    "\n\tInvite Now Live to your Discord Server.```"},
            {"listHelp", "```Ruby\nLIST:  Used to list things from my database." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " list <option>" +
                    "\n\t<option>" +
                    "\n\tgame - List the games that I'm tracking for you" +
                    "\n\tchannel - I'll list out all of the individual stream channels you want me to watch```"},
            {"moveHelp", "```Ruby\nMOVE:  Change where I make my announcements." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " move <channel>" +
                    "\n\t" + "<channel> - The name of the channel you wish to move my announcements to (MUST include " +
                    "the #)```"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Used to change the global notification option for this server." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify <option>" +
                    "\n\tnone - No @ notifications of any kind (default)" +
                    "\n\there - I will notify only those people that are online when I make the announcement" +
                    "\n\teveryone - I'll notify EVERYONE!!  Mwahahaha!!  (I don't recommend this on large servers)```"},
            {"pingHelp", "```Ruby\nPING:  Used to ping me. If I am working correctly, I'll send you a pong." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + "ping```"},
            {"removeHelp", "```Ruby\nREMOVE:  Used to remove something from my database." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove <option> <content>" +
                    "\n\t<option>\t<content>" +
                    "\n\tfilter - The game name that you want to filter streamers by" +
                    "\n\tgame - The name of the game exactly as it appears on the streaming platform" +
                    "\n\tmanager - The @ mention of the user to add as a manager```"},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Allows you to filter streams by the language it is being broadcast " +
                    "in.  This supports the English spelling of the language, or the native spelling.  Must be a supported " +
                    "language on Twitch that is listed in the Dashboard." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang <language>" +
                    "\n\nYou may use either the native spelling of the language or the English spelling of the " +
                    "language.  See https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamsHelp", "```Ruby\nSTREAMS:  I'll send you a list of active streams as a PM. (NOTE: You will likely " +
                    "receive several private messages using this command, depending on how many streams your Discord " +
                    "monitors!)" +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " streams```"},
            {"statusHelp", "SAHows various statistics of the bot."},
            {"twitchHelp", "```Ruby\nTWITCH:  Add and remove things that are Twitch.tv related." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch <sub-command> <option> <argument>" +
                    "\n\t<sub-command> <option> <argument>" +
                    "\n\tadd channel <channel-name>" +
                    "\n\tremove channel <channel-name>```"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}