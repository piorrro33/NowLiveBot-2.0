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
            {"added", "Added "},
            {"addFail", "Failed to add "},
            {"addHelp", "```Markdown\n# ADD\n* Used to add managers for your server.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <option> <content>"
                    + "\n\t<option> <content>"
                    + "\n\tmanager - The @ mention of the user to add as a manager"
                    + "\n\n## EXAMPLE: " + Const.COMMAND_PREFIX + Const.COMMAND + " add manager @Ague```"},
            {"adminOverride", "*Permission of this command have been overridden by a developer.*"},
            {"alreadyExists", "It looks like you already added that to my database. ¯\\_(ツ)_/¯"},
            {"alreadyManager", "It seems I've already hired that user as a manager.  Find moar humanz!"},
            {"announceHelp", "```Markdown\n# ANNOUNCE\n* Shhh...  I'm a secret...\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <content>\n\tThis command is only available to the developers.```"},
            {"announcementMentionMessageText", "Hey %s! %s has just gone live! Watch their stream here: %s"},
            {"announcementNoMentionMessageText", "Hey! %s has just gone live! Watch their stream here: %s"},
            {"beamHelp", "```Markdown\n# BEAM\n* Add and remove things that are Beam.pro related.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " beam <sub-command> <option> <argument>\n"
                    + "\t<sub-command> <option> <argument>\n"
                    + "\tadd           channel  <channelname>\n"
                    + "\tremove        channel  <channel-name>\n\n"
                    + "## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam add channel Ague" + "```"},
            {"beamUserNoExist", "That Beam user does not exist! Check your spelling and try again!"},
            {"botLangFail", "Something went wrong and my language is still the same."},
            {"botLangHelp", "```Markdown\n# BOTLANG\n* Used to change the language of my responses.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang language"
                    + "\n\tEnter either the English spelling or the native spelling of the language you wish to set."
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " botlang spanish" + "```"},
            {"botLangSuccess", "You've successfully changed my language."},
            {"botLangUnsupported", "That language is currently not supported."},
            {"botStatistics", "%s Statistics"},
            {"broadcasterLangAllSuccess", " :ok_hand: I'll look for streams from all languages."},
            {"broadcasterLangFail", "Something went wrong and I'm still looking for all languages."},
            {"broadcasterLangSuccess", " :ok_hand: I'll only look for streams that are in that language!"},
            {"canNotRemoveOwner", "Silly human, you can't remove the server owner from the manager list. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Ah, man...  Something went wrong... Better try that again."},
            {"cleanupHelp", "```Markdown\n# CLEANUP\n*  Change the way I clean up my stream announcements.\n\n## USAGE:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <option>"
                    + "\n\tnone   - I won't change a thing to my announcements! (default)"
                    + "\n\tedit   - I'll edit my announcements to say \"OFFLINE\" when the streamer goes offline"
                    + "\n\tdelete - I'll just delete the announcement when the streamer is no longer live"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup edit" + "```"},
            {"cleanupSuccessDelete", "Sir, yes Sir!  I will delete all my announcements from now on!"},
            {"cleanupSuccessEdit", "Editing my announcements, it is."},
            {"cleanupSuccessNone", " :ok_hand: I won't do anything to my announcements."},
            {"compactFail", "Um, something went wrong.  My compact mode is unchanged."},
            {"compactHelp", "```Markdown\n# COMPACT\n* Switch my announcements to a shorter version.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <option>"
                    + "\n\ton  - Turns on Compact Mode"
                    + "\n\toff - Turns off Compact Mode"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " compact on" + "```"},
            {"compactOff", " :compression: Compact mode has been turned off."},
            {"compactOn", " :compression: Compact mode has been turned on."},
            {"devMessage", "*Message from the " + Const.BOT_NAME + " developers:*\n\n\t"},
            {"discordChannelNoExist", "That text channel doesn't exist on your server."},
            {"discordUserNoExist", "That person isn't a Discord user!  Try again!"},
            {"doesNotExist", "That was never added to my database."},
            {"emptyArgs", "I think you forgot some of the command.  Check the help command for more info."},
            {"emptyCommand", "Next time you wake me up, please send a command as well."},
            {"followersEmbed", "Followers"},
            {"guildJoinSuccess", "Hi there!  I'm Now Live, the stream announcing bot!  Type `" +
                    Const.COMMAND_PREFIX + Const.COMMAND +
                    " help` for a list of my commands.\n\nIf you need some help setting me up, come " +
                    "join my Discord at " + Const.DISCORD_URL + " and check out the how-to-setup and command-list " +
                    "channels for all the info!\n\nDon't forget to say hey!"},
            {"helpPm", "Hey there, %s! So I hear you're looking for some help? Below is a list of my commands.\n\n" +
                    "```Markdown\n" +
                    "# ADD\n" +
                    "* Used to add information to my database. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " add help\n\n" +
                    "# BEAM\n" +
                    "* Add and remove things that are Beam.pro related. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " beam help\n\n" +
                    "# BOTLANG\n" +
                    "* Used to change the language of my responses.\n" +
                    "* Currently supported languages: English, Czech, German, French, Spanish" +
                    "* For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " botlang help\n\n" +
                    "# CLEANUP\n" +
                    "* Change the way I clean up my stream announcements. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " cleanup help\n\n" +
                    "# COMPACT\n" +
                    "* Switch my announcements to a shorter version. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " compact help\n\n" +
                    "# INVITE\n" +
                    "* Used to display my invite link. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " invite help\n\n" +
                    "# LIST\n" +
                    "* This command lists things from the database. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " list help\n\n" +
                    "# MOVE\n" +
                    "* Change where I make my announcements. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " move help\n\n" +
                    "# NOTIFY\n" +
                    "* Used to change the global notification option for this server. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " notify help\n\n" +
                    "# PING\n" +
                    "* Used to ping me. If I am working correctly, I'll send you a pong. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " ping help\n\n" +
                    "# REMOVE\n" +
                    "* Used to remove something from my database. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " remove help\n\n" +
                    "# STREAMLANG\n" +
                    "* Allows you to filter streams by the language it is being broadcast in.\n" +
                    "* For more information, type: " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang help\n\n" +
                    "# STREAMS\n" +
                    "* I'll send you a list of active streams as a PM." +
                    "* For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " streams help\n\n" +
                    "# TWITCH\n" +
                    "* Add and remove things that are Twitch.tv related. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " twitch help\n\n```\n" +
                    "Ague is still working hard to finish up new things for me to do for you!\n\n~~" + Const.BOT_NAME + "\n\n" +
                    "If you need additional help, join my Discord.  Lots of helpful people there:  " + Const.DISCORD_URL + "\n\n" +
                    "***P.S. I don't monitor this mailbox, so please don't send me any messages through PM***"},
            {"incorrectArgs", "You passed incorrect or missing arguments to me.  Check the help command for more info."},
            {"invite", "Hey %s! Invite me to your server!\n\n\t"
                    + "**Click here:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Markdown\n# INVITE\n* Used to display my invite link.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tInvite NowLive bot to your Discord Server.```"},
            {"listHelp", "```Markdown\n# LIST\n* This command lists things from the database.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " list *option*"
                    + "\n\ttwitchChannel   - Lists the Twitch Channels you follow"
                    + "\n\ttwitchCommunity - List the Twitch Communities you follow"
                    + "\n\tgamefilter      - Lists all game filters you have set up"
                    + "\n\ttwitchGame      - List the Twitch Games that I'm tracking for you"
                    + "\n\tmanager         - Lists the managers of your server"
                    + "\n\ttitlefilter     - Lists all title filters you have set up"
                    + "\n\ttwitchTeam      - Lists the Twitch teams you follow"
                    + "\n\tsetting         - Lists common bot settings"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " list channel" + "```"},
            {"listSettings", "```Markdown\n" +
                    "# Bot Settings on Your Server" +
                    "\n* Compact mode is %s." +
                    "\n* Notification is set to %s." +
                    "\n* Cleanup is set to %s." +
                    "\n* Broadcaster language is set to %s." +
                    "\n* Server language is set to %s.```"},
            {"moveDoNotOwnChannel", " :no_entry: Hey now, I can't announce to a channel that doesn't exists on your " +
                    "server!"},
            {"moveFail", " :no_entry: I can't seem to send announcements there.  Make sure I have the proper permissions " +
                    "in that channel."},
            {"moveHelp", "```Markdown\n# MOVE\n* Change where I make my announcements.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <channel>\n\t"
                    + "<channel> - The name of the channel you wish to move my announcements to (MUST include the #)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " move #discordchannel" + "```"},
            {"moveSuccess", " :ok_hand: I'll announce over there!"},
            {"needOneManager", "If you remove that manager, who will manage me?"},
            {"noBotManager", "It's against the Discord Bot Union By-Laws for bots to manage me. Sorry, try and find a " +
                    "suitable human for the job. :thumbsup:"},
            {"noneOnline", "Sorry %s, but there's nobody online right now that this server is following."},
            {"notAManager", "Sorry, but only my managers can do that. Type `" + Const.COMMAND_PREFIX + Const.COMMAND +
                    " list manager` for a list of people that can."},
            {"notAnAdmin", "To whom it may concern:  I am your servant, but you are not my master."},
            {"notifyEveryone", ":tada: WHOA!!  **EVERYONE** that belongs to the server will get notified when I " +
                    "announce streams!  *(Are you sure?  I don't recommend this for large servers...  It can make " +
                    "people grumpy.)*"},
            {"notifyHelp", "```Markdown\n# NOTIFY\n* Used to change the global notification option for this server.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " notify <option>"
                    + "\n\tnone     - No @ notifications of any kind (default)"
                    + "\n\there     - I will notify only those people that are online when I make the announcement"
                    + "\n\teveryone - I'll notify EVERYONE!!  Mwahahaha!!  (I don't recommend this on large servers)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify everyone" + "```"},
            {"notifyHere", " :bellhop_bell: All who are online will get a mention when I announce streams."},
            {"notifyNone", " :ok_hand: I won't mention anyone in my announcements."},
            {"nowLive", "NOW LIVE!\n"},
            {"nowPlayingEmbed", "Now Playing"},
            {"nowPlayingLower", " is now playing "},
            {"nowStreamingEmbed", " is now streaming!"},
            {"numUniqueMembers", "Number Unique Members"},
            {"offline", "OFFLINE!\n"},
            {"offlineEmbed", " has gone offline!"},
            {"on", " on "},
            {"onlineStreamPm1", "Hey there!  There's currently "},
            {"onlineStreamPm2", " streamers online that you may be interested in!  Follow their links to check them " +
                    "out: \n\n"},
            {"oops", "Oops!  Something went wrong and nothing was changed!  Let's try that again."},
            {"ping", "When I was in China on the All-American Ping Pong team, I just loved playing ping-pong with my " +
                    "Flexolite ping pong paddle."},
            {"pingHelp", "```Markdown\n# PING\n* Used to ping me. If I am working correctly, I'll send you a pong.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " ping```"},
            {"privateMessageReply", "I'm sorry, but the bot you are trying to reach has a voice mail box that has not " +
                    "been setup yet.  Please try your PM again later."},
            {"removed", "Removed %s %s."},
            {"removeManagerFail", "I can't remove %s because they are not in my database."},
            {"removeHelp", "```Markdown\n# REMOVE\n* Used to remove managers from my database.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <option> <content>"
                    + "\n\t<option>\t<content>"
                    + "\n\tmanager - The @ mention of the user to add as a manager"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove filter Overwatch```"},
            {"servers", "Servers"},
            {"statusHelp", "```Markdown\n# STATUS\n* Shows various statistics of the bot.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " status```"},
            {"streamlangHelp", "```Markdown\n# STREAMLANG\n* Allows you to filter streams by the language it is being broadcast " +
                    "in.  This supports the English spelling of the language, or the native spelling.  Must be a supported " +
                    "language on Twitch that is listed in the Dashboard.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <language>\n" +
                    "* You may use either the native spelling of the language or the English spelling of the language."
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang german```"},
            {"streamTitleEmbed", "Stream Title"},
            {"streamsHelp", "```Markdown\n# STREAMS\n* I'll send you a list of active streams as a PM.\n* (NOTE: You will likely " +
                    "receive several private messages using this command, depending on how many streams your Discord " +
                    "monitors!)\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Total Views"},
            {"twitchCommunities", "Twitch Communities"},
            {"twitchAnnounceUpdate", "\n# Updated the Twitch announcement channel for %s to: %s."},
            {"twitchAnnounceUpdateFail", "\n! Failed to change the Twitch announce channel for %s to: %s."},
            {"twitchChannelAdd", "\n# Added channel(s): %s."},
            {"twitchChannelAddFail", "\n# Failed to add channels: %s."},
            {"twitchChannelAnnounce", "\n# They will be announced in: #%s."},
            {"twitchChannelGameFilter", "\n# They will only be announced when they are playing: %s."},
            {"twitchChannelRemove", "\n# Removed channels: %s."},
            {"twitchChannelRemoveFail", "\n! Failed to delete channels: %s."},
            {"twitchChannelTitleFilter", "\n# They will only be announced when these words are in the title: %s."},
            {"twitchCommunityAdd", "\n# Added community(s): %s."},
            {"twitchCommunityAddFail", "\n# Failed to add community(s): %s."},
            {"twitchCommunityAnnounce", "\n# The community(s) will announce in: #%s."},
            {"twitchCommunityNotFound", "\n# Community(s) not found on Twitch: %s."},
            {"twitchCommunityRemove", "\n# Removed community(s): %s."},
            {"twitchCommunityRemoveFail", "\n# Failed to remove community(s): %s."},
            {"twitchGameAdd", "\n# Added game(s): %s."},
            {"twitchGameAddFail", "\n# Failed to add game(s): %s."},
            {"twitchGameAnnounce", "\n# The game will announce in: #%s."},
            {"twitchGameFilterAdd", "\n# Added game filter(s): %s."},
            {"twitchGameFilterAddFail", "\n# Failed to add game filter(s): %s."},
            {"twitchGameFilterRemove", "\n# Removed game filter(s): %s."},
            {"twitchGameFilterRemoveFail", "\n# Failed to remove game filter(s): %s."},
            {"twitchGameRemove", "\n# Removed game(s): %s."},
            {"twitchGameRemoveFail", "\n# Failed to remove game(s): %s."},
            {"twitchHelp", "```Markdown\n# TWITCH\n* Add and remove things that are Twitch.tv related.\n"
                    + "* Notes:\n\t"
                    + "To add game filters and title filters, you MUST include the brackets.\n\t"
                    + "Do NOT use the full Twitch URL. It will not work!! Use only the channel name (www.twitch.tv/channelName)\n\t"
                    + "The Team name must be from the URL, not the display name of the team. (www.twitch.tv/team/teamName)\n\t"
                    + "You may add multiple channels, teams, games, communities, game and title filters by using the pipe character | between them.\n\t"
                    + "The only required options are: channelName/communityName/teamName/gameName\n\n"
                    + "## Twitch Channels\n"
                    + "Note: Adding an announcement channel, game and title filters are optional."
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel channelName #announcementChannel {gameFilters} [titleFilters]\n\n"
                    + "## Twitch Communities (Announce ALL live streams in the community)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community communityName #announcementChannel\n\n"
                    + "## Twitch Games (Announce ALL live streams for that game)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game gameName #announcementChannel\n\n"
                    + "## Twitch Teams (Announce ALL live streams in the team)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team teamName #announcementChannel\n\n"
                    + "## Twitch Game Filters (Global)\n"
                    + "* NOTE: This affects all stream announcements for Twitch\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch gamefilter {gameName|gameName} #announcementChannel\n\n"
                    + "## Twitch Title Filters (Global)\n"
                    + "* NOTE: This affects all stream announcements for Twitch\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch titlefilter gameName #announcementChannel\n\n"
                    + "* Examples:\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel AgueMort #live-streams {Overwatch|World of "
                    + "Warcraft} (adds a channel to announce in a certain channel and game filters)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game Overwatch (adds a game to the global announcement channel)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community MMORPG #live-streams (adds the community with a specified announcement channel)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team thekingdom #the-kingdom-streamers (adds a team with a specific announcement channel)\n\n"
                    + "```"},
            {"twitchTeamAdd", "\n# Added team(s): %s."},
            {"twitchTeamAddFail", "\n# Failed to add team(s): %s."},
            {"twitchTeamAnnounce", "\n# The team(s) will announce in: #%s."},
            {"twitchTeamNotFound", "\n# Team(s) not found on Twitch: %s."},
            {"twitchTeamRemove", "\n# Removed team(s): %s."},
            {"twitchTeamRemoveFail", "\n# Failed to remove team(s): %s."},
            {"twitchTeams", "Twitch Teams"},
            {"twitchTitleFilterAdd", "\n# Added title filter(s): %s."},
            {"twitchTitleFilterAddFail", "\n# Failed to add title filter(s): %s."},
            {"twitchTitleFilterRemove", "\n# Removed title filter(s): %s."},
            {"twitchTitleFilterRemoveFail", "\n# Failed to remove title filter(s): %s."},
            {"typeOnce", "You only need to type that part once, silly."},
            {"uniqueChannels", "Unique Channels %s"},
            {"uniqueGames", "Unique Games %s"},
            {"usePlatform", "Oops!  That's the old way of doing things!  Use the platform specific command!  Type `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` for more info."},
            {"watchThemHere", "Watch them here: "},
            {"wrongCommand", " :thinking: I don't know that command."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}