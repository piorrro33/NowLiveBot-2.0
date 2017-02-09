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
public class LanguageBundle_fr extends ListResourceBundle {

    private Object[][] contents = {
            {"added", "Ajouté "},
            {"addFail", "Échec de l'ajout "},
            {"addHelp", "```Ruby\nADD:  Utilisé pour ajouter quelque chose à ma base de données.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <option> <contenu>"
                    + "\n\t<option>\t<contenu>"
                    + "\n\tfilter - Nom du jeu à utiliser pour filtrer les streamers"
                    + "\n\tgame - Nom exact du jeu comme il apparait sur la plateforme de stream"
                    + "\n\tmanager - Le @ de l'utilisateur à promouvoir en manager```"},
            {"adminOverride", "*La permission de cette commande a été forcée par un développeur.*"},
            {"alreadyExists", "Il semblerait que vous ayez déjà ajouté ça à ma base de données. �\\_(?)_/�"},
            {"alreadyManager", "Il semblerait que vous avez déjà ajouté cet utilisateur en manager. Trouve plus de n'humains !"},
            {"announceHelp", "```Ruby\nANNOUNCE:  Chut...  Je suis un secret...\nUTILISATION:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <contenu>\n\tCette commande est seulement disponible aux développeurs.```"},
            {"beamHelp", "```Ruby\nBEAM:  Ajouter et supprimer des choses ayant un rapport avec Beam.pro.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " beam <sous-commande> <option> <argument>"
                    + "\n\t<sous-commande> <option> <argument>"
                    + "\n\tadd channel <nom-de-chaîne>"
                    + "\n\tremove channel <nom-de-chaîne>```"},
            {"beamUserNoExist", "Cet utilisateur Beam n'existe pas ! Vérifiez son écriture et réessayez."},
            {"botLangFail", "Quelque chose s'est mal passé et ma langue est toujours la même."},
            {"botLangHelp", "```Ruby\nBOTLANG: Utilisé pour changer la langue de mes réponses.\nUTILISATION: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang <la langue>"
                    + "Entrez l'orthographe anglaise ou l'orthographe native de la langue que vous souhaitez définir."},
            {"botLangSuccess", "Vous avez changé ma langue."},
            {"broadcasterLangAllSuccess", " :ok_hand: Je ne chercherai que des streams disponibles dans toutes les langues !"},
            {"broadcasterLangFail", "Quelque chose s'est mal passé, donc je continue à chercher des streams disponibles dans toutes les langues."},
            {"broadcasterLangSuccess", " :ok_hand: Je ne chercherai que des streams dans cette langue !"},
            {"canNotRemoveOwner", "Stupide humain, tu ne peux pas enlever le propriétaire du serveur de la liste des managers. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Ah, mec... Quelque chose s'est mal passé... Tu ferais mieux de réessayer."},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Change la façon dont je nettoie les annonces de stream.\nUTILISATION :"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <option>"
                    + "\n<option>"
                    + "\n\tnone - Je ne changerai rien après qu'un stream aura été annoncé ! (par défaut)"
                    + "\n\tedit - Je modifierai mes annonces pour dire \"OFFLINE\" quand le streamer sera hors-ligne"
                    + "\n\tdelete - Je supprimerai simplement l'annonce quand le streamer sera hors-ligne```"},
            {"cleanupSuccessDelete", "Chef, oui chef ! Je supprimerai désormais les annonces !"},
            {"cleanupSuccessEdit", "Modifier les annonces, je ferai."},
            {"cleanupSuccessNone", " :ok_hand: Je ne ferai rien aux annonces."},
            {"compactFail", "Hum, quelque chose s'est mal passé. Mon mode compact reste inchangé."},
            {"compactHelp", "```Ruby\nCOMPACT:  Passer mes annonces dans un mode plus compact.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <option>"
                    + "\n<option>"
                    + "\n\ton - Activer le mode compact"
                    + "\n\toff - Désactiver le mode compact```"},
            {"compactOff", " :compression: Le mode compact a bien été désactivé."},
            {"compactOn", " :compression: Le mode compact vient d'être activé."},
            {"devMessage", "*Message des développeurs " + Const.BOT_NAME + " :*\n\n\t"},
            {"discordUserNoExist", "Cette personne n'existe pas ! Essaie encore !"},
            {"doesNotExist", "Cela n'a jamais été ajouté à ma base de données."},
            {"emptyArgs", "Je crois que vous avez oublié une partie de la commande. Regarde la commande \"help\" pour plus d'infos."},
            {"emptyCommand", "La prochaine fois que tu me réveilles, envoie-moi aussi une commande."},
            {"followersEmbed", "Abonnés"},
            {"guildJoinSuccess", "Hey, salut ! Je suis Now Live, le bot qui annonce les streams ! Tape `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` pour avoir une liste de mes commandes.\n\nSi tu as besoin d'aide pour m'installer, rejoins " +
                    "mon Discord ici : " + Const.DISCORD_URL + " et regarde #how-to-setup et #command-list " +
                    "pour avoir toutes les infos !\n\nN'oublie pas de dire salut !"},
            {"helpPm", "Hey there %s!\n\n" +
                    "Alors comme ça on a besoin d'aide ? Voici une liste de mes commandes. Pour savoir à quoi sert chacune d'entre-elles, " +
                    "tape " + Const.COMMAND_PREFIX + Const.COMMAND + "<commande> help\n\n" +
                    "```Ruby\n* add\n* beam\n* cleanup\n* compact\n* invite\n* list\n* move\n* notify\n* ping\n* remove" +
                    "\n* streamlang\n* streams\n* twitch```\n" +
                    "Juste pour que tu sois au courant, Ague bosse dur pour finir de me polir, donc certaines commandes " +
                    "pourraient ne pas marcher correctement.  Mais leur aide fonctionne.  Sois indulgent, il " +
                    "travaille dur pour que tout fonctionne bien !\n\n\t~~" + Const.BOT_NAME + "\n\n" +
                    "Tu peux aussi avoir de l'aide de mon développeur et du reste de la communauté " + Const.BOT_NAME + " sur mon serveur Discord ! " +
                    "Clique simplement sur ce lien pour le rejoindre :  " + Const.DISCORD_URL + "\n\n" +
                    "*P.S. Je ne regarde pas la boîte du bot, donc merci de ne pas m'envoyer de messages par MP.*"},
            {"incorrectArgs", "Tu m'as transmis des arguments incorrects ou il en manque.  Regarde la commande d'aide pour plus d'infos" +
                    "."},
            {"invite", "Hey mon pote ! Invite-moi dans ton serveur !\n\n\t"
                    + "**Clique ici :** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  pour afficher mon lien d'invitation\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tInviter le bot NowLive dans un serveur Discord.```"},
            {"listHelp", "```Ruby\nLIST:  utilisé pour lister des choses dans ma base de données..\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " list <option>"
                    + "\n\t<option>"
                    + "\n\tchannel - Je listerai toutes les chaînes à surveiller"
                    + "\n\tfilter - Je listerai tous les filtres de jeu que vous avez mis en place"
                    + "\n\tgame - Lister les jeux que je surveille"
                    + "\n\tmanager - Lister les managers du serveur"
                    + "\n\tsetting - Lister d'autres paramètres```"},
            {"moveDoNotOwnChannel", " :no_entry: Hé, je peux pas annoncer dans un canal qui n'existe pas sur ton " +
                    "serveur !"},
            {"moveFail", " :no_entry: J'ai pas l'impression de pouvoir annoncer là-bas. Assure-toi que j'ai les bonnes permissions " +
                    "dans ce canal."},
            {"moveHelp", "```Ruby\nMOVE:  Changer où je fais mes annonces.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <canal>\n\t"
                    + "<canal> - Le nom du canal dans lequel tu veux que j'annonce (inclure ABSOLUMENT le #)```"},
            {"moveSuccess", " :ok_hand: J'annoncerai là-bas ! :arrow_right: "},
            {"needOneManager", "Si tu supprimes celui-là, qui va me gérer ?"},
            {"noBotManager", "C'est contre les lois de l'union des bots de Discord de laisser des bots me gérer. Désolé, essaie de trouver un " +
                    "humain approprié pour ce travail. :thumbsup:"},
            {"noneOnline", "Désolé mon pote, personne n'est en ligne en ce moment parmi les chaînes que je surveille."},
            {"notAManager", "Désole, mais seuls mes managers peuvent faire ça."},
            {"notAnAdmin", "Aux concernés : je suis votre servant, mais vous n'êtes pas mon maître."},
            {"notifyEveryone", ":tada: WOW !  **TOUT LE MONDE** sur le serveur sera notifié quand j'annoncerai " +
                    "des streams !  *(Es-tu sûr ? Je ne le recommande pas aux grands serveurs... Cela peut " +
                    "énerver les gens.)*"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Utilisé pour changer les options de notifications globales du serveur.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " notify <option>"
                    + "\n\tnone - Aucune mention @ de quelque sorte que ce soit. (par défaut)"
                    + "\n\there - Je préviendrai uniquement les personnes qui sont en ligne au moment de l'annonce"
                    + "\n\teveryone - Je préviendrai TOUT LE MONDE !! Mouahahaha !! (Je ne le recommande pas aux grands serveurs)```"},
            {"notifyHere", " :bellhop_bell: Tous ceux qui sont en ligne recevront une notification quand j'annoncerai un stream."},
            {"notifyNone", " :ok_hand: Je ne mentionnerai plus personne dans mes annonces."},
            {"nowLive", "EN LIVE!\n"},
            {"nowPlayingEmbed", "Joue à"},
            {"nowPlayingLower", " est en train de jouer "},
            {"nowStreamingEmbed", " est en live !"},
            {"offline", "HORS-LIGNE!\n"},
            {"offlineEmbed", " est maintenant hors-ligne !"},
            {"on", " dans la team "},
            {"onlineStreamPm1", "Hello ! Il y a en ce moment "},
            {"onlineStreamPm2", " streamers en ligne qui pourraient t'intéresser ! Suis leur lien pour aller voir " +
                    "leur chaîne : \n\n"},
            {"oops", "Oups ! Quelque chose s'est mal passé et rien n'a été modifié ! Essayons encore."},
            {"ping", "Quand j'étais en Chine dans l'équipe d'Amérique de ping-pong, j'adorais jouer avec ma " +
                    "raquette de ping-pong Flexolite."},
            {"pingHelp", "```Ruby\nPING:  Utilisé pour m'envoyer un ping. Si je fonctionne bien, je vous renverrai un pong.\nUTILISATION : "
                    + Const.COMMAND_PREFIX + "ping```"},
            {"privateMessageReply", "Je suis désolé, mais le bot que tu essaies d'atteindre a une boîte aux lettres vocales qui n'a pas encore " +
                    "été mise en place.  Réessaie d'envoyer ton message plus tard."},
            {"removed", "Enlevé "},
            {"removeFail1", "Je ne peux pas enlever "},
            {"removeFail2", " car ce n'est pas dans ma base de données."},
            {"removeHelp", "```Ruby\nREMOVE:  Utilisé pour enlever quelque chose de ma base de données.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <option> <contenu>"
                    + "\n\t<option>\t<contenu>"
                    + "\n\tfilter - Le nom du jeu par lequel vous voulez filtrer les streamers"
                    + "\n\tgame - Le nom du jeu exactement comme il apparaît sur la plateforme de stream"
                    + "\n\tmanager - Le @ de l'utilisateur à promouvoir en manager```"},
            {"statusHelp", "Affiche diverses statistiques sur le bot."},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Permet de filtrer les streams par la langue dans laquelle il est diffusé. " +
                    "Cete commande supporte l'écriture anglaise de la langue ou l'écriture native.  Ce doit être une langue " +
                    "supportée par Twitch qui est listée dans le Tableau de Bord.\nUTILISATION :  \n"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <langue>" +
                    "Vous pouvez utiliser soit l'écriture native de la langue ou bien son écriture anglaise.  Voir " +
                    "https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamTitleEmbed", "Titre du Stream"},
            {"streamsHelp", "```Ruby\nSTREAMS:  Je vous enverrai par message privé une liste des streamers actifs. (NOTE: Tu recevras sûrement " +
                    "plusieurs messages en utilisant cette commande, suivant le nombre de streams que votre serveur " +
                    "surveille !)\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Vues Totales"},
            {"twitchHelp", "```Ruby\nTWITCH:  Ajouter et enlever tout ce qui a à voir avec Twitch.tv.\nUTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " twitch <sous-commande> <option> <argument>"
                    + "\n\t<sous-commande> <option> <argument>"
                    + "\n\tadd channel <nom-de-chaîne>"
                    + "\n\tremove channel <nom-de-chaîne>```"},
            {"typeOnce", "Tu n'as besoin de taper cette partie qu'une seule fois, bêta."},
            {"usePlatform", "Oups ! C'est la vieille méthode ! Utilise les commandes spécifiques aux plateformes ! Tape `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` pour plus d'infos."},
            {"watchThemHere", "Regarde le live ici : "},
            {"wrongCommand", " :thinking: Je ne connais pas cette commande."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}