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
public class LanguageBundle_de extends ListResourceBundle {

    private Object[][] contents = {
            {"added", "Hinzugefügt: "},
            {"addFail", "Fehler beim Hinzufügen von "},
            {"addHelp", "```Ruby\nADD:  Befehl um etwas meiner Datenbank hinzuzufügen.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <Option> <Inhalt>"
                    + "\n\t<Option>\t<Inhalt>"
                    + "\n\tfilter - Der Name des Spiels das zum Filter der Streamer benutzt werden soll"
                    + "\n\tgame - Der Name des Spiels exakt so, wie er auf der Streaming Platform zu sehen ist"
                    + "\n\tmanager - Der @ Name des Benutzers, den du als Manager hinzufügen möchtest```"},
            {"adminOverride", "*Permission dieses Befehls wurden von einem Bot Developer überschrieben.*"},
            {"alreadyExists", "Es scheint als wäre das bereits in meiner Datenbank. ¯\\_(ツ)_/"},
            {"alreadyManager", "Dieser Benutzer ist bereits ein Manager.  Find moar humanz!"},
            {"announceHelp", "```Ruby\nANNOUNCE:  Shhh...  I'm a secret...\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <content>\n\tThis command is only available to the developers.```"},
            {"beamHelp", "```Ruby\nBEAM:  Hinzufügen und Entfernen von Beam.pro Inhalten.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " beam <Unterbefehl> <Option> <Argument>"
                    + "\n\t<Unterbefehl> <Option> <Argument>"
                    + "\n\tadd channel <Channelname>"
                    + "\n\tremove channel <Channelname>```"},
            {"beamUserNoExist", "Beam-benutzer konnte nicht gefunden werden!"},
            {"botLangFail", "Etwas ging schief und meine Sprache ist immer noch das gleiche."},
            {"botLangHelp", "```Ruby\nBOTLANG: Wird verwendet, um die Sprache meiner Antworten zu ändern.\nVERWENDUNG: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang Sprache"
                    + "\n\tGeben Sie entweder die englische Schreibweise oder die native Schreibweise der Sprache ein, die "
                    + "Sie einstellen möchten.```"},
            {"botLangSuccess", "Sie haben meine Sprache erfolgreich geändert."},
            {"broadcasterLangAllSuccess", " :ok_hand: Ich werde nach Streams in allen Sprachen suchen!"},
            {"broadcasterLangFail", "Etwas ging schief und ich suche immernoch nach allen Sprachen."},
            {"broadcasterLangSuccess", " :ok_hand: Ich werde nach Streams in dieser Sprache suchen!"},
            {"canNotRemoveOwner", "Haha, du kannst nicht den Serverbesitzer von der Managerliste löschen. :laughing: :laughing:"},
            {"cleanupFail", "Ach, mensch...  Da hat was nicht funktioniert... am besten nochmal versuchen."},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Ändert wie ich Stream Benachrichtigungen aufräume.\nVERWENDUNG:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <Option>"
                    + "\n<Option>"
                    + "\n\tnone - Ich ändere nichts! (Standardeinstellung)"
                    + "\n\tedit - Ich editiere die Benachrichtung und markiere Streams als \"OFFLINE\""
                    + "\n\tdelete - Ich lösche Benachrichtungen, wenn die entsprechenden Streamer offline gehen```"},
            {"cleanupSuccessDelete", "Sir, ja Sir!  Ich lösche meine Benachrichtigungen ab jetzt!"},
            {"cleanupSuccessEdit", "Benachrichtigungen editieren, alles klar."},
            {"cleanupSuccessNone", " :ok_hand: Ich lasse alle Benachrichtungen unverändert."},
            {"compactFail", "Uhm, irgendwas ging schief.  Mein Compact Mode ist nicht geändert worden."},
            {"compactHelp", "```Ruby\nCOMPACT:  Im Compact Mode sind meine Benachrichtigungen kompakter.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <Option>"
                    + "\n<Option>"
                    + "\n\ton - Aktiviert Compact Mode"
                    + "\n\toff - Deaktiviert Compact Mode```"},
            {"compactOff", " :compression: Compact Mode wurde deaktiviert."},
            {"compactOn", " :compression: Compact Mode wurde aktiviert."},
            {"devMessage", "*Nachricht der " + Const.BOT_NAME + " Developer:*\n\n\t"},
            {"discordUserNoExist", "Diese Person ist kein Discord Benutzer!  Versuche es nochmal!"},
            {"doesNotExist", "Das wurde nie meiner Datenbank hinzugefügt."},
            {"emptyArgs", "Ich glaube du hast da einen Teil des Befehls vergessen.  Mit der Option *help* erkläre ich dir den Befehl gerne."},
            {"emptyCommand", "Wenn du mich nächstes mal weckst, gib mir bitte auch etwas zu tun."},
            {"followersEmbed", "Follower"},
            {"guildJoinSuccess", "Hi!  Ich bin Now Live, der Streambenachrichtigungsbot!  Schreibe `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` für eine Liste meiner Befehle.\n\nSolltest zu Hilfe beim Einrichten brauchen," +
                    "tritt meinem Server (" + Const.DISCORD_URL + ") bei und sieh dir die how-to-setup und command-list " +
                    "Channel für weitere Informationen an!\n\nNicht vergessen hallo zu sagen!"},
            {"helpPm", "Hallo %s!\n\n" +
                    "Ich hab gehört du suchst Hilfe?  Hier ist eine Liste meiner Befehle. Um herauszufinden was diese Befehle tun, " +
                    "schreibe " + Const.COMMAND_PREFIX + Const.COMMAND + " <command> help\n\n" +
                    "```Ruby\n* add\n* beam\n* cleanup\n* compact\n* invite\n* list\n* move\n* notify\n* ping\n* remove" +
                    "\n* streamlang\n* streams\n* twitch```\n" +
                    "Nur damit du's weißt, Ague arbeitet immernoch viel an meiner Programmierung, also funktionieren manche " +
                    "Befehle noch nicht (komplett)!  Die *help* Befehle sind alle bereits da.  Hab bitte Geduld mit ihm, er " +
                    "gibt sich wirklich Mühe!\n\n\t~~" + Const.BOT_NAME + "\n\n" +
                    "In meinem Discord Server können mein Developer und die Now Live Community dir helfen, " +
                    "solltest du Probleme haben! Hier ist ein Einladelink:  " + Const.DISCORD_URL + "\n\n" +
                    "*P.S. Ich schaue nicht in diese privaten Nachrichten, also schreibe mir hier bitte keine Nachrichten*"},
            {"incorrectArgs", "Argument(e) fehlen oder sind fehlerhaft.  Benutze die *help* Option für mehr Details" +
                    "."},
            {"invite", "Hey Kumpel! Lade mich zu deinem Server ein!\n\n\t"
                    + "**Klicke hier:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  Postet meinen Einladelink\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tLade den NowLive-Bot zu deinem Discord Server ein.```"},
            {"listHelp", "```Ruby\nLIST:  Erstellt eine Liste von Dingen in meiner Datenbank.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " list <Option>"
                    + "\n\t<Option>"
                    + "\n\tchannel - Liste aller Streams, die ich beobachten soll"
                    + "\n\tfilter - Liste aller Spielefilter, die du erstellt hast"
                    + "\n\tgame - Liste aller Spiele, die ich beobachtens soll"
                    + "\n\tmanager - Liste aller Manager auf diesem Server"
                    + "\n\tsetting - Liste anderer Einstellungen```"},
            {"moveDoNotOwnChannel", " :no_entry: Ähm, Ich kann nichts in einem Channel posten, " +
                    "der auf deinem Server nicht existiert!"},
            {"moveFail", " :no_entry: Ich kann dort scheinbar keine Benachrichtigungen posten.  Stell sicher dass du in diesem Channel " +
                    "mir Schreibrechte gegeben hast."},
            {"moveHelp", "```Ruby\nMOVE:  Ändert wo ich Benachrichtigungen poste.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <Channel>\n\t"
                    + "<Channel> - Der Name des Channels in dem ich Benachrichtigungen posten soll (MUSS mit # beginnen)```"},
            {"moveSuccess", " :ok_hand: Okay ich werde dort posten! :arrow_right: "},
            {"needOneManager", "Wenn du das tust, wer managet mich dann?"},
            {"noBotManager", "Bots als Manager hinzufügen verstößt gegen die Discord Bot Union By-Laws. Sorry, versuche einen " +
                    "geeignet Menschen für diesen Job zu finden. :thumbsup:"},
            {"noneOnline", "Sorry Kumpel, aktuell ist niemanden, dem dieser Server folgt, online."},
            {"notAManager", "Entschuldige, aber nur meine Manager dürfen das."},
            {"notAnAdmin", "Ich mag zwar dein Diener sein, aber du bist nicht mein Meister."},
            {"notifyEveryone", ":tada: WOW!!  **JEDER** in diesem Server wird benachrichtigt, wenn ich " +
                    "einen Stream poste!  *(Bist du sicher? In größeren Server könnte es  " +
                    "Leute nerven...)*"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Ändert die globablen Mitteilungseinstellungen.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " notify <Option>"
                    + "\n\tnone - Keine @ Mitteilungen (Standardeinstellung)"
                    + "\n\there - Ich benachrichtige alle, die online sind (via ``@here``)"
                    + "\n\teveryone - Ich benachrichtige ALLE!!  Muhahaha!!  (Nicht für größere Server empfohlen)```"},
            {"notifyHere", " :bellhop_bell: Alle, die online sind, werden benachrichtigt."},
            {"notifyNone", " :ok_hand: Ich werde keine @ Mitteilungen mehr benutzen."},
            {"nowLive", "JETZT LIVE!\n"},
            {"nowPlayingEmbed", "spielt"},
            {"nowPlayingLower", " spielt gerade "},
            {"nowStreamingEmbed", " streamt jetzt!"},
            {"offline", "OFFLINE!\n"},
            {"offlineEmbed", " ist schon offline!"},
            {"on", " auf "},
            {"onlineStreamPm1", "Hey! Aktuell sind "},
            {"onlineStreamPm2", " Streamer online, die dich interessieren könnten! Folge ihren Links um sie " +
                    "auszuchecken: \n\n"},
            {"oops", "Ups! Irgendwas ist schiefgelaufen! Lass uns das nochmal versuchen."},
            {"ping", "Als ich in China war, war ich in einem amerikanischen Ping-Pongteam. Ich liebte es mit meinem " +
                    "Flexolite Ping-Pongsschläger zu spielen."},
            {"pingHelp", "```Ruby\nPING:  Ping-Befehl. Wenn ich online bin, werde ich antworten\nVERWENDUNG: "
                    + Const.COMMAND_PREFIX + "ping```"},
            {"privateMessageReply", "Es tut uns leid, aber der Bot, den sie gerufen haben antwortet nicht " +
                    "auf private Nachrichten.  Versuchen Sie es später nocheinmal."},
            {"removed", "Entfernt: "},
            {"removeFail1", "Ich kann "},
            {"removeFail2", " nicht entfernen, da kein solcher Eintrag in meiner Datenbank existiert."},
            {"removeHelp", "```Ruby\nREMOVE:  Entfernt etwas aus meiner Datenbank.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <Option> <Inhalt>"
                    + "\n\t<Option>\t<Inhalt>"
                    + "\n\tfilter - Der Name des Spiels das zum Filter der Streamer benutzt werden soll"
                    + "\n\tgame - Der Name des Spiels exakt so, wie er auf der Streaming Platform zu sehen ist"
                    + "\n\tmanager - Der @ Name des Benutzers, den du als Manager hinzufügen möchtest```"},
            {"statusHelp", "Zeigt einige Statistiken über den Bot."},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Erlaubt das Filtern von Streams nach Sprache " +
                    "Unterstützt entweder den englischen oder nativen Namen der Sprache. Die Sprache " +
                    "muss im Twitch Dashboard unterstützt werden.\nVERWENDUNG:  \n"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <Sprache>" +
                    "Du kannst entweder den nativen oder den englischen Namen der Sprache verwenden.  Mehr unter " +
                    "https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamTitleEmbed", "Stream Titel"},
            {"streamsHelp", "```Ruby\nSTREAMS:  Ich schicke dir alle aktuell aktiven Streams als Nachricht. (HINWEIS: Du wirst wahrscheinlich " +
                    "mehrere private Nachrichten durch diesen Befehl erhalten, je nachdem wie viele Streams dein Discord Server " +
                    "beobachtet!)\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Alle Aufrufe"},
            {"twitchHelp", "```Ruby\nTWITCH:  Hinzufügen und Entfernen von TwitchTV-Inhalten.\nVERWENDUNG:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " twitch <Unterbefehl> <Option> <Argument>"
                    + "\n\t<Unterbefehl> <Option> <Argument>"
                    + "\n\tadd channel <Channelname>"
                    + "\n\tremove channel <Channelname>```"},
            {"typeOnce", "Das musst du nicht doppelt schreiben, Dussel."},
            {"usePlatform", "Ups!  Das ist der alte Befehl!  Benutze bitte die neuen plattformspezifischen Befehle!  Schreibe `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` für weitere Informationen."},
            {"watchThemHere", "Jetzt zuschauen: "},
            {"wrongCommand", " :thinking: Den Befehl kenne ich nicht."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}