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
            {"addHelp", "```Ruby\nADD:  Používá se k přidání něčeho.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <volba> <obsah>"
                    + "\n\t<volba>\t<obsah>"
                    + "\n\tfilter - Jméno hry podle které chcete filtrovat streamy"
                    + "\n\tgame - Název hry přesně tak, jak se udává na streamovací platformě"
                    + "\n\tmanager - Zmínka o uživateli znakem @ kterého chcete přidat jako manažera```"},
            {"adminOverride", "*Oprávnění tohoto příkazu byla přepsána vývojářem.*"},
            {"alreadyExists", "Vypadá to že toto jsi již do mé databáze přidal. ¯\\_(ツ)_/¯"},
            {"alreadyManager", "Vypadá to, že jsem tohoto uživatele již přidala jako správce!"},
            {"announceHelp", "```Ruby\nANNOUNCE:  Shhh...  Jsem tajemství...\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <obsah>\n\tTento příkaz je pouze dostupný pro vývojáře.```"},
            {"beamHelp", "```Ruby\nBEAM:  Přidat a odebrat věci které jsou spojené s Beam.pro.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " beam <pod-příkaz> <volba> <argument>"
                    + "\n\t<pod-příkaz> <volba> <argument>"
                    + "\n\tadd <název-kanálu>"
                    + "\n\tremove kanál <název-kanálu>```"},
            {"beamUserNoExist", "Tento uživatel Beamu neexistuje! Zkontroluj si pravopis a zkus to znovu."},
            {"botLangFail", "Něco se nepovedlo a můj jazyk je stále stejný."},
            {"botLangHelp", "```Ruby\nBOTLANG: Používá se se ke změně jazyku.\nUSAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang jazyk"
                    + "\n\tVlož buďto anglický pravopis, nebo tvůj vrozený pravopis který chcete přidat.```"},
            {"botLangSuccess", "Úspěšně jsi změnil můj jazyk."},
            {"botLangUnsupported", "Tento jazyk není momentálně podporován."},
            {"broadcasterLangAllSuccess", " :ok_hand: Budu sledovat streamy se všemi jazyky."},
            {"broadcasterLangFail", "Něco se nepovedlo a já stále sleduji streamy se všemi jazyky."},
            {"broadcasterLangSuccess", " :ok_hand: Budu sledovat pouze streamy v tomto jazyce!"},
            {"canNotRemoveOwner", "Hloupý člověče, němůžeš odebrat majitele serveru z listu manažerů. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Ah, člověče...  Něco se nepovedlo... Radši to zkus znovu."},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Změňte způsob kterým uklízím svoje oznámení.\nUSAGE:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <volba>"
                    + "\n<volba>"
                    + "\n\tnone - Nic nebudu měnit na mých oznámeních! (základní)"
                    + "\n\tedit - Upravím svoje oznámení aby říkalo \"OFFLINE\" když je stream offline"
                    + "\n\tdelete - Odstraním oznámení když je stream offline```"},
            {"cleanupSuccessDelete", "Pane, ano Pane!  Od teď odstraním každé moje známení!"},
            {"cleanupSuccessEdit", "Upravuji moje oznámení, tak jest."},
            {"cleanupSuccessNone", " :ok_hand: Nebudu dělat nic mým oznámením."},
            {"compactFail", "Um, něco se nepovedlo.  Můj kompaktní mód se nezměnil."},
            {"compactHelp", "```Ruby\nCOMPACT:  Zkrátí moje oznámení.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <volba>"
                    + "\n<volba>"
                    + "\n\ton - Zapne kompaktní mód"
                    + "\n\toff - Vypne kompaktní mód```"},
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
            {"helpPm", "Ahoj %s!\n\n" +
                    "Slyšel jsem že potřebuješ nějakou pomoc?  Dole je list mých příkazů. Aby jsi zjistil co každý " +
                    "z nich dělá, napiš " + Const.COMMAND_PREFIX + Const.COMMAND + "<příkaz> help\n\n" +
                    "```Ruby\n* add\n* beam\n* botlang\n* cleanup\n* compact\n* invite\n* list\n* move\n* notify\n* ping\n* remove" +
                    "* streamlang\n* streams\n* twitch```\n" +
                    "Jen aby jste věděli, Ague stále těžce pracuje aby dokončil všechny moje nedostatky, takže některé " +
                    "vypsané příkazy ještě nemusí fungovat!  Ale jeho pomoc funguje.  Mějte s ním strpení, " +
                    "těžce pracuje aby vše opravil!\n\n\t~~" + Const.BOT_NAME + "\n\n" +
                    "Také můžete získat nějakou pomoc od mého vývojáře a od zbytku celé Now Live komunity na mém discord " +
                    "serveru!  Stačí kliknout na tento odkaz:  " + Const.DISCORD_URL + "\n\n" +
                    "*P.S. Nesleduji tuto schránku, tak mi prosím neposílejte žádné zprávy skrze PM*"},
            {"incorrectArgs", "Předal jsi mi nesprávné argumenty.  Zkoukni help pro více informací." +
                    "."},
            {"invite", "Hej kamaráde! Pozvy mě na svůj server!\n\n\t"
                    + "**Klikni zde:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  používá se k zobrazení mého invite linku\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tPozvy NowLive bota na svůj server.```"},
            {"listHelp", "```Ruby\nLIST:  Používá se kzobrazení věcí z mé databáze.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " list <volba>"
                    + "\n\t<volba>"
                    + "\n\tchannel - Zobrazím Vám všechny streamy které chcete abych sledoval"
                    + "\n\tfilter - Zobrazím Vám všechny filtry her které jste nastavil"
                    + "\n\tgame - Zobrazím Vám všechny hry které chete abych sledoval"
                    + "\n\tmanager - Zobrazím Vám všechny moje manažery "
                    + "\n\tsetting - Zobrazím Vám všechna ostatní nastavení```"},
            {"moveDoNotOwnChannel", " :no_entry: Hej, ale nemůžu oznamovat v kanálu který na tvém serveru " +
                    "neexistuje!"},
            {"moveFail", " :no_entry: Nedaří se mi tam odeslat oznámení.  Ujisti se že tam mám správná " +
                    "práva."},
            {"moveHelp", "```Ruby\nMOVE:  Nastaví, kde budu oznamovat.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <kanál>\n\t"
                    + "<kanál> - Jmeno kanálu, kde chcete abych oznamoval (MUSÍ obsahovat #)```"},
            {"moveSuccess", " :ok_hand: Budu oznamovat tam! :arrow_right: "},
            {"needOneManager", "Pokud ho odstraníš, kdo mě bude ovládat?"},
            {"noBotManager", "Je proti mým Discord bot zákonům aby mě ovládal bot. Promiň, zkus najít " +
                    "vhodného člověka pro tuto práci. :thumbsup:"},
            {"noneOnline", "Promiň kámo, ale zrovna není nikdo koho sleduji online."},
            {"notAManager", "Promiň, mohlo by tě to znepokojit: Jsem tvůj služebník, ale ty nejsi můj mistr."},
            {"notifyEveryone", ":tada: WHOA!!  **EVERYONE** to patří před oznámení které bude na tomto serveru " +
                    "oznamovat streamy!  *(Jsi si jistý?  Nedoporučuji toto na velkých serverech...  Může to lidi " +
                    "rozčílit.)*"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Používá se ke globálnímu nastavení oznámení na tomto serveru.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " notify <volba>"
                    + "\n\tnone - Žádné @ notifikace! (základní)"
                    + "\n\there - Upozorním pouze uživatele co jsou zrovna online na tomto serveru"
                    + "\n\teveryone - Upozorním VŠECHNY!!  Mwahahaha!!  (Nedoporučuji toto na velkých serverech)```"},
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
            {"pingHelp", "```Ruby\nPING:  Používá se aby jsi mě mohl pingnout. Pokud správně funguji, pošlu ti zpětnou zprávu.\nUSAGE: "
                    + Const.COMMAND_PREFIX + "ping```"},
            {"privateMessageReply", "Omlouvám se, ale bot ke kterému se snažíte dostat nemá ještě nastavenou " +
                    "hlasovou schránku.  Prosím zkus odeslat svojí PM později."},
            {"removed", "Odebráno "},
            {"removeFail1", "Nemůžu odebrat "},
            {"removeFail2", " protože to není v mojí databázi."},
            {"removeHelp", "```Ruby\nREMOVE:  Používá se k odebrání něčeho z mojí databáze.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <volba> <obsah>"
                    + "\n\t<volba>\t<obsah>"
                    + "\n\tfilter - Jméno hry podle které chcete filtrovat streamy"
                    + "\n\tgame - Název hry přesně tak, jak se udává na streamovací platformě "
                    + "\n\tmanager - Zmínka o uživateli znakem @ kterého chcete přidat jako manažera```"},
            {"statusHelp", "Zobrazí různé statistiky bota."},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Povolí vám filtrovat streamy podle jazyka " +
                    "in.  Toto podporuje anglický pravopis, nebo tvůj vrozený pravopis.  Musí to být podporovaný " +
                    "jazyk na Twitch který je zobrazený v dashboardu.\nUSAGE:  \n"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <jazyk>" +
                    "Můžeš použít buďto tvůj vrozený nebo anglický pravopis.  Podívej se na " +
                    "https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamTitleEmbed", "Název streamu"},
            {"streamsHelp", "```Ruby\nSTREAMS:  Pošlu ti list momentálně online streamů do PM. (POZNÁMKA: Pravděpodobně dostanete " +
                    "více zpráv po použití tohoto příkazu, záleží to na tom kolik sledujete  " +
                    "streamů!)\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Počet shlédnutí"},
            {"twitchHelp", "```Ruby\nTWITCH:  Přidává a odebírá věci spojené s Twitch.tv.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " twitch <pod-příkaz> <volba> <argument>"
                    + "\n\t<pod-příkaz> <volba> <argument>"
                    + "\n\tadd channel <název-kanálu>"
                    + "\n\tremove channel <název-kanálu>```"},
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