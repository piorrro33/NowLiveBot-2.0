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
public class LanguageBundle_cs extends ListResourceBundle {

    private Object[][] contents = {
            {"added", "Přidáno "},
            {"addFail", "Nepodařilo se přidat "},
            {"addHelp", "```Markdown\n# ADD\n* Používá se k přidání něčeho.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <volba> <obsah>"
                    + "\n\t<option> <content>"
                    + "\n\tfilter  - Jméno hry podle které chcete filtrovat streamy"
                    + "\n\tgame    - Název hry přesně tak, jak se udává na streamovací platformě"
                    + "\n\tmanager - Zmínka o uživateli znakem @ kterého chcete přidat jako manažera"
                    + "\n\n## EXAMPLE: " + Const.COMMAND_PREFIX + Const.COMMAND + " add manager @Ague```"},
            {"adminOverride", "*Oprávnění tohoto příkazu byla přepsána vývojářem.*"},
            {"alreadyExists", "Vypadá to že toto jsi již do mé databáze přidal. ¯\\_(ツ)_/¯"},
            {"alreadyManager", "Vypadá to, že jsem tohoto uživatele již přidala jako správce!"},
            {"announceHelp", "```Markdown\n# ANNOUNCE\n* Shhh...  Jsem tajemství...\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <obsah>\n\tTento příkaz je pouze dostupný pro vývojáře.```"},
            {"beamHelp", "```Markdown\n# BEAM\n* Přidat a odebrat věci které jsou spojené s Beam.pro.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " beam <pod-příkaz> <volba> <argument>\n"
                    + "\t<pod-příkaz> <volba> <argument>\n"
                    + "\tadd          channel <název-kanálu>\n"
                    + "\tremove       channel <název-kanálu>\n\n"
                    + "## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam add channel Ague" + "```"},
            {"beamUserNoExist", "Tento uživatel Beamu neexistuje! Zkontroluj si pravopis a zkus to znovu."},
            {"botLangFail", "Něco se nepovedlo a můj jazyk je stále stejný."},
            {"botLangHelp", "```Markdown\n# BOTLANG\n* Používá se se ke změně jazyku.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang jazyk"
                    + "\n\tVlož buďto anglický pravopis, nebo tvůj vrozený pravopis který chcete přidat."
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " botlang spanish" + "```"},
            {"botLangSuccess", "Úspěšně jsi změnil můj jazyk."},
            {"botLangUnsupported", "Tento jazyk není momentálně podporován."},
            {"broadcasterLangAllSuccess", " :ok_hand: Budu sledovat streamy se všemi jazyky."},
            {"broadcasterLangFail", "Něco se nepovedlo a já stále sleduji streamy se všemi jazyky."},
            {"broadcasterLangSuccess", " :ok_hand: Budu sledovat pouze streamy v tomto jazyce!"},
            {"canNotRemoveOwner", "Hloupý člověče, němůžeš odebrat majitele serveru z listu manažerů. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Ah, člověče...  Něco se nepovedlo... Radši to zkus znovu."},
            {"cleanupHelp", "```Markdown\n# CLEANUP\n*  Změňte způsob kterým uklízím svoje oznámení.\n\n## USAGE:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <volba>"
                    + "\n\tnone   - Nic nebudu měnit na mých oznámeních! (základní)"
                    + "\n\tedit   - Upravím svoje oznámení aby říkalo \"OFFLINE\" když je stream offline"
                    + "\n\tdelete - Odstraním oznámení když je stream offline"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup edit" + "```"},
            {"cleanupSuccessDelete", "Pane, ano Pane!  Od teď odstraním každé moje známení!"},
            {"cleanupSuccessEdit", "Upravuji moje oznámení, tak jest."},
            {"cleanupSuccessNone", " :ok_hand: Nebudu dělat nic mým oznámením."},
            {"compactFail", "Um, něco se nepovedlo.  Můj kompaktní mód se nezměnil."},
            {"compactHelp", "```Markdown\n# COMPACT\n* Zkrátí moje oznámení.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <volba>"
                    + "\n\ton  - Zapne kompaktní mód"
                    + "\n\toff - Vypne kompaktní mód"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " compact on" + "```"},
            {"compactOff", " :compression: Kompaktní mód byl vypnut."},
            {"compactOn", " :compression: Kompaktní mód byl zapnut."},
            {"devMessage", "*Zpráva od " + Const.BOT_NAME + " vývojářů:*\n\n\t"},
            {"discordUserNoExist", "Tato osoba není uživatlem Discordu!  Zkus to znovu!"},
            {"doesNotExist", "To nebylo nikdy přidáno do mé databáze."},
            {"emptyArgs", "Myslím si že jsi zapoměl nějaký příkaz.  Ověř si to pomocí help příkazu a zkus to znovu."},
            {"emptyCommand", "Příště až mě probudíš, tak také použij nějaký příkaz."},
            {"followersEmbed", "Sledující"},
            {"guildJoinSuccess", "Ahoj!  Jsem Now Live, bot který oznamuje streamy!  Napiš `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` pro list mých příkazů.\n\nPokud potřebuješ pomoc s nastavením " +
                    "připoj se na můj Discord " + Const.DISCORD_URL + " a podívej se do how-to-setup a command-list " +
                    "kanálů pro všechny informace!\n\nNezapomeň říct ahoj!"},
            {"helpPm", "Hey there %s! So I hear you're looking for some help? Below is a list of my commands.\n\n" +
                    "```Markdown\n" +
                    "# ADD\n" +
                    "* Používá se k přidání něčeho. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " add help\n\n" +
                    "# BEAM\n" +
                    "* Přidat a odebrat věci které jsou spojené s Beam.pro. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " beam help\n\n" +
                    "# BOTLANG\n" +
                    "* Používá se se ke změně jazyku.\n" +
                    "* Vlož buďto anglický pravopis, nebo tvůj vrozený pravopis který chcete přidat." +
                    "* For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " botlang help\n\n" +
                    "# CLEANUP\n" +
                    "* Změňte způsob kterým uklízím svoje oznámení. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " cleanup help\n\n" +
                    "# COMPACT\n" +
                    "* Zkrátí moje oznámení. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " compact help\n\n" +
                    "# INVITE\n" +
                    "* Používá se k zobrazení mého invite linku. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " invite help\n\n" +
                    "# LIST\n" +
                    "* Používá se kzobrazení věcí z mé databáze. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " list help\n\n" +
                    "# MOVE\n" +
                    "* Nastaví, kde budu oznamovat. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " move help\n\n" +
                    "# NOTIFY\n" +
                    "* Používá se ke globálnímu nastavení oznámení na tomto serveru. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " notify help\n\n" +
                    "# PING\n" +
                    "* Používá se aby jsi mě mohl pingnout. Pokud správně funguji, pošlu ti zpětnou zprávu. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " ping help\n\n" +
                    "# REMOVE\n" +
                    "* Používá se k odebrání něčeho z mojí databáze. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " remove help\n\n" +
                    "# STREAMLANG\n" +
                    "* Povolí vám filtrovat streamy podle jazyka in.\n" +
                    "* For more information, type: " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang help\n\n" +
                    "# STREAMS\n" +
                    "* Pošlu ti list momentálně online streamů do PM." +
                    "* For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " streams help\n\n" +
                    "# TWITCH\n" +
                    "* Přidává a odebírá věci spojené s Twitch.tv. For more information, type: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " twitch help\n\n```\n" +
                    "Ague is still working hard to finish up new things for me to do for you!\n\n~~" + Const.BOT_NAME + "\n\n" +
                    "If you need additional help, join my Discord.  Lots of helpful people there:  " + Const.DISCORD_URL + "\n\n" +
                    "***P.S. Nesleduji tuto schránku, tak mi prosím neposílejte žádné zprávy skrze PM***"},
            {"incorrectArgs", "Předal jsi mi nesprávné argumenty.  Zkoukni help pro více informací." +
                    '.'},
            {"invite", "Hej kamaráde! Pozvy mě na svůj server!\n\n\t"
                    + "**Klikni zde:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Markdown\n# INVITE\n* Používá se k zobrazení mého invite linku.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tPozvy NowLive bota na svůj server.```"},
            {"listHelp", "```Markdown\n# LIST\n* Používá se kzobrazení věcí z mé databáze.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " list *option*"
                    + "\n\tchannel     - Zobrazím Vám všechny streamy které chcete abych sledoval"
                    + "\n\tgamefilter  - Zobrazím Vám všechny filtry her které jste nastavil"
                    + "\n\tgame        - Zobrazím Vám všechny hry které chete abych sledoval"
                    + "\n\tmanager     - Zobrazím Vám všechny moje manažery"
                    + "\n\ttitlefilter - Lists your title filters"
                    + "\n\tteam        - Lists the Twitch teams you follow"
                    + "\n\tsetting     - Zobrazím Vám všechna ostatní nastavení"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " list channel" + "```"},
            {"moveDoNotOwnChannel", " :no_entry: Hej, ale nemůžu oznamovat v kanálu který na tvém serveru " +
                    "neexistuje!"},
            {"moveFail", " :no_entry: Nedaří se mi tam odeslat oznámení.  Ujisti se že tam mám správná práva."},
            {"moveHelp", "```Markdown\n# MOVE\n* Nastaví, kde budu oznamovat.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <kanál>\n\t"
                    + "<kanál> - Jmeno kanálu, kde chcete abych oznamoval (MUSÍ obsahovat #)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " move #discordchannel" + "```"},
            {"moveSuccess", " :ok_hand: Budu oznamovat tam! :arrow_right: "},
            {"needOneManager", "Pokud ho odstraníš, kdo mě bude ovládat?"},
            {"noBotManager", "Je proti mým Discord bot zákonům aby mě ovládal bot. Promiň, zkus najít " +
                    "vhodného člověka pro tuto práci. :thumbsup:"},
            {"noneOnline", "Promiň kámo, ale zrovna není nikdo koho sleduji online."},
            {"notAManager", "Promiň, mohlo by tě to znepokojit: Jsem tvůj služebník, ale ty nejsi můj mistr."},
            {"notifyEveryone", ":tada: WHOA!!  **EVERYONE** to patří před oznámení které bude na tomto serveru " +
                    "oznamovat streamy!  *(Jsi si jistý?  Nedoporučuji toto na velkých serverech...  Může to lidi " +
                    "rozčílit.)*"},
            {"notifyHelp", "```Markdown\n# NOTIFY\n* Používá se ke globálnímu nastavení oznámení na tomto serveru.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " notify <volba>"
                    + "\n\tnone     - Žádné @ notifikace! (základní)"
                    + "\n\there     - Upozorním pouze uživatele co jsou zrovna online na tomto serveru"
                    + "\n\teveryone - Upozorním VŠECHNY!!  Mwahahaha!!  (Nedoporučuji toto na velkých serverech)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify everyone" + "```"},
            {"notifyHere", " :bellhop_bell: Všichni kdo jsou zrovna online na serveu dostanou upozornění."},
            {"notifyNone", " :ok_hand: Nebudu nikoho upozorňovat."},
            {"nowLive", "NYNÍ ŽIVĚ!\n"},
            {"nowPlayingEmbed", "Nyní hraje"},
            {"nowPlayingLower", " nyní hraje "},
            {"nowStreamingEmbed", " nyní streamuje!"},
            {"offline", "OFFLINE!\n"},
            {"offlineEmbed", " přešel do režimu offline!"},
            {"on", " na "},
            {"onlineStreamPm1", "Hej tam!  K dispozici jsou v současné době "},
            {"onlineStreamPm2", " streameři online kteří by Vás mohly zajímat!  Sleduj jejich odkazy aby jsi je mohl " +
                    "zkouknout: \n\n"},
            {"oops", "Oops!  Něco se nepovedlo a nic se nezměnilo!  Pojďme to zkusit znovu."},
            {"ping", "Když jsem byl v Číně v All-American Ping Pong teamu, miloval jsem hrát ping-pong s mojí " +
                    "Flexolite ping pong pálkou."},
            {"pingHelp", "```Markdown\n# PING\n* Používá se aby jsi mě mohl pingnout. Pokud správně funguji, pošlu ti zpětnou zprávu.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " ping```"},
            {"privateMessageReply", "Omlouvám se, ale bot ke kterému se snažíte dostat nemá ještě nastavenou " +
                    "hlasovou schránku.  Prosím zkus odeslat svojí PM později."},
            {"removed", "Odebráno "},
            {"removeFail1", "Nemůžu odebrat "},
            {"removeFail2", " protože to není v mojí databázi."},
            {"removeHelp", "```Markdown\n# REMOVE\n* Používá se k odebrání něčeho z mojí databáze.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <volba> <obsah>"
                    + "\n\t<volba>\t<obsah>"
                    + "\n\tfilter  - Jméno hry podle které chcete filtrovat streamy"
                    + "\n\tgame    - Název hry přesně tak, jak se udává na streamovací platformě"
                    + "\n\tmanager - Zmínka o uživateli znakem @ kterého chcete přidat jako manažera"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove filter Overwatch```"},
            {"statusHelp", "```Markdown\n# STATUS\n* Zobrazí různé statistiky bota.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " status```"},
            {"streamlangHelp", "```Markdown\n# STREAMLANG\n* Povolí vám filtrovat streamy podle jazyka " +
                    "in.  Toto podporuje anglický pravopis, nebo tvůj vrozený pravopis.  Musí to být podporovaný " +
                    "jazyk na Twitch který je zobrazený v dashboardu.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <jazyk>\n" +
                    "* Můžeš použít buďto tvůj vrozený nebo anglický pravopis."
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang german```"},
            {"streamTitleEmbed", "Název streamu"},
            {"streamsHelp", "```Markdown\n# STREAMS\n* Pošlu ti list momentálně online streamů do PM.\n* (POZNÁMKA: Pravděpodobně dostanete " +
                    "více zpráv po použití tohoto příkazu, záleží to na tom kolik sledujete streamů!)\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Počet shlédnutí"},
            {"twitchHelp", "```Markdown\n# TWITCH\n* Přidává a odebírá věci spojené s Twitch.tv.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " twitch <pod-příkaz> <volba> <argument>"
                    + "\n\t<pod-příkaz>  <volba>  <argument>"
                    + "\n\tadd           channel  <název-kanálu>"
                    + "\n\tremove        channel  <název-kanálu>"
                    + "\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch add channel aguemort"
                    + "\n\n- NOTE:  Do NOT include the full URL!  It will not work!  Use ONLY the channel name!```"},
            {"typeOnce", "Stačí když tuto část napíšeš pouze jednou, hlupáku."},
            {"usePlatform", "Oops!  To je starý způsob, jak dělat věci!  Použij specifický příkaz pro danou platformu!  Napiš `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` pro více informací."},
            {"watchThemHere", "Sledujte jej zde: "},
            {"wrongCommand", " :thinking: Neznám tento příkaz."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}