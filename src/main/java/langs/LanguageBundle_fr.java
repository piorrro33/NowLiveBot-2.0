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
            {"addHelp", "```Markdown\n# ADD\n* Utilisé pour ajouter des managers à votre serveur.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <option> <contenu>"
                    + "\n\t<option>\t<contenu>"
                    + "\n\tmanager - Le @ de l'utilisateur à promouvoir en manager```"
                    + "\n\n## EXEMPLE : " + Const.COMMAND_PREFIX + Const.COMMAND + " add manager @Ague```"},
            {"adminOverride", "*La permission de cette commande a été forcée par un développeur.*"},
            {"alreadyExists", "Il semblerait que vous ayez déjà ajouté ça à ma base de données. ¯\\_(ツ)_/¯"},
            {"alreadyManager", "Il semblerait que vous ayez déjà engagé cet utilisateur en manager. Trouve plus de n'humains !"},
            {"announceHelp", "```Markdown\n# ANNOUNCE:\n*  Chut...  Je suis un secret...\n\n## UTILISATION:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <contenu>\n\tCette commande est uniquement disponible aux développeurs.```"},
            {"announcementMessageText", "Hey ! %s est maintenant en live ! Regarde son stream ici : %s"},
            {"beamHelp", "```Markdown\n# BEAM\n* Ajouter et supprimer tout ce qui a à voir avec Beam.pro.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " beam <sous-commande> <option> <argument>\n"
                    + "\t<sous-commande> <option> <argument>\n"
                    + "\tadd           channel  <nomChaîne>\n"
                    + "\tremove        channel  <nomChaîne>\n\n"
                    + "## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam add channel Ague" + "```"},
            {"beamUserNoExist", "Cet utilisateur Beam n'existe pas ! Vérifiez son écriture et réessayez."},
            {"botLangFail", "Quelque chose s'est mal passé donc je garde la même langue."},
            {"botLangHelp", "```Markdown\n# BOTLANG\n* Utilisé pour changer ma langue.\n\n## UTILISATION : "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " botlang <langue>"
                    + "\n\tEntrez soit l'écriture anglaise soit l'écriture native de la langue que vous souhaitez utiliser."
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " botlang spanish" + "```"},
            {"botLangSuccess", "Vous venez de changer ma langue avec succès."},
            {"botLangUnsupported", "Cette langue n'est pas encore supportée."},
            {"botStatistics", "%s Statistiques"},
            {"broadcasterLangAllSuccess", " :ok_hand: Je ne chercherai que des streams disponibles dans toutes les langues !"},
            {"broadcasterLangFail", "Quelque chose s'est mal passé, donc je continue à chercher des streams disponibles dans toutes les langues."},
            {"broadcasterLangSuccess", " :ok_hand: Je ne chercherai que des streams dans cette langue !"},
            {"canNotRemoveOwner", "Stupide humain, tu ne peux pas enlever le propriétaire du serveur de la liste des managers. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Ah, mec... Quelque chose s'est mal passé... Tu ferais mieux de réessayer."},
            {"cleanupHelp", "```Markdown\n# CLEANUP\n*  Change la façon dont je nettoie mes annonces de stream.\n\n## UTILISATION :"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <option>"
                    + "\n\tnone - Je ne changerai rien après qu'un stream aura été annoncé ! (par défaut)"
                    + "\n\tedit - Je modifierai mes annonces pour dire \"OFFLINE\" quand le streamer sera hors-ligne"
                    + "\n\tdelete - Je supprimerai simplement l'annonce quand le streamer sera hors-ligne"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup edit" + "```"},
            {"cleanupSuccessDelete", "Chef, oui chef ! Je supprimerai désormais les annonces !"},
            {"cleanupSuccessEdit", "Les annonces, je modifierai."},
            {"cleanupSuccessNone", " :ok_hand: Je ne ferai rien aux annonces."},
            {"compactFail", "Hum, quelque chose s'est mal passé. Mon mode compact reste inchangé."},
            {"compactHelp", "```Markdown\n# COMPACT\n* Passer mes annonces dans un mode plus compact.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <option>"
                    + "\n\ton - Activer le mode compact"
                    + "\n\toff - Désactiver le mode compact"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + "compact on" + "```"},
            {"compactOff", " :compression: Le mode compact a bien été désactivé."},
            {"compactOn", " :compression: Le mode compact vient d'être activé."},
            {"devMessage", "*Message des développeurs " + Const.BOT_NAME + " :*\n\n\t"},
            {"discordChannelNoExist", "Ce canal n'existe pas sur ton serveur."},
            {"discordUserNoExist", "Cette personne n'existe pas ! Essaie encore !"},
            {"doesNotExist", "Cela n'a jamais été ajouté à ma base de données."},
            {"emptyArgs", "Je crois que tu as oublié une partie de la commande. Regarde la commande \"help\" pour plus d'infos."},
            {"emptyCommand", "La prochaine fois que tu me réveilles, envoie-moi aussi une commande."},
            {"followersEmbed", "Abonnés"},
            {"guildJoinSuccess", "Hey, salut ! Je suis Now Live, le bot qui annonce les streams ! Tape `" +
                    Const.COMMAND_PREFIX + Const.COMMAND +
                    " help` pour avoir une liste de mes commandes.\n\nSi tu as besoin d'aide pour m'installer, rejoins " +
                    "mon Discord ici : " + Const.DISCORD_URL + " et regarde les canaux how-to-setup et command-list " +
                    "pour avoir toutes les infos !\n\nN'oublie pas de dire bonjour !"},
            {"helpPm", "Hey salut, %s ! Alors comme ça on a besoin d'aide ? Voici une liste de mes commandes.\n\n" +
                    "```Markdown\n" +
                    "# ADD\n" +
                    "* Utilisé pour ajouter des informations à ma base de données. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " add help\n\n" +
                    "# BEAM\n" +
                    "* Ajouter et supprimer tout ce qui a à voir avec Beam.pro. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " beam help\n\n" +
                    "# BOTLANG\n" +
                    "* Utilisé pour changer ma langue.\n" +
                    "* Langues supportées actuellement : English, Czech, German, French, Spanish" +
                    "* Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " botlang help\n\n" +
                    "# CLEANUP\n" +
                    "* Change la façon dont je nettoie mes annonces de stream. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " cleanup help\n\n" +
                    "# COMPACT\n" +
                    "* Passer mes annonces dans un mode plus compact. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " compact help\n\n" +
                    "# INVITE\n" +
                    "* Uilisé pour afficher mon lien d'invitation. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " invite help\n\n" +
                    "# LIST\n" +
                    "* Cette commande liste différentes choses de ma base de données. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " list help\n\n" +
                    "# MOVE\n" +
                    "* Change où je fais mes annonces. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " move help\n\n" +
                    "# NOTIFY\n" +
                    "* Utilisé pour changer l'option de notification pour ce serveur. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " notify help\n\n" +
                    "# PING\n" +
                    "* Utilisé pour me ping. Si je fonctionne bien, je t'enverrai un pong. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " ping help\n\n" +
                    "# REMOVE\n" +
                    "* Utilisé pour supprimer quelque chose de ma base de données. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " remove help\n\n" +
                    "# STREAMLANG\n" +
                    "* Permet de filtrer les streams par la langue dans laquelle ils sont diffusés.\n" +
                    "* Pour plus d'informations, tape : " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang help\n\n" +
                    "# STREAMS\n" +
                    "* Je t'enverrai une liste des chaînes en ligne en MP." +
                    "* Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " streams help\n\n" +
                    "# TWITCH\n" +
                    "* Ajouter ou supprimer tout ce qui a à voir avec Twitch.tv. Pour plus d'informations, tape : " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " twitch help\n\n```\n" +
                    "Ague bosse dur pour finir de nouvelles choses que je pourrai faire pour toi !\n\n~~" + Const.BOT_NAME + "\n\n" +
                    "Si tu as encore besoin d'aide, rejoins mon serveur Discord.  Il y a plein de gens utiles là-bas :  " + Const.DISCORD_URL + "\n\n" +
                    "***P.S. Je ne regarde pas cette boîte de réception, alors merci de ne pas m'envoyer de messages ici.***"},
            {"incorrectArgs", "Tu m'as transmis des arguments incorrects ou il en manque.  Regarde la commande d'aide pour plus d'infos."},
            {"invite", "Hey %s ! Invite-moi dans ton serveur !\n\n\t"
                    + "**Clique ici :** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Markdown\n# INVITE\n* Utilisé pour afficher mon lien d'invitation.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tInviter le bot NowLive dans votre serveur Discord.```"},
            {"listHelp", "```Markdown\n# LIST\n* Cette commande liste des choses de ma base de données.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " list *option*"
                    + "\n\ttwitchChannel   - Liste les chaînes Twitch suivies"
                    + "\n\ttwitchCommunity - Liste les communautés Twitch suivies"
                    + "\n\tgamefilter      - Liste tous les filtres de jeux mis en place"
                    + "\n\ttwitchGame      - Liste les jeux sur Twitch que je surveille"
                    + "\n\tmanager         - Liste les managers du serveur"
                    + "\n\ttitlefilter     - Liste tous les filtres de titre mis en place"
                    + "\n\ttwitchTeam      - Liste les équipes Twitch suivies"
                    + "\n\tsetting         - Liste les paramètres communs du bot"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " list channel" + "```"},
            {"listSettings", "```Markdown\n" +
                    "# Paramètres du bot sur le serveur" +
                    "\n* Le mode compact est %s." +
                    "\n* Les notifications sont sur %s." +
                    "\n* Le nettoyage est en mode %s." +
                    "\n* La langue de diffusion est sur %s." +
                    "\n* La langue du bot est %s.```"},
            {"moveDoNotOwnChannel", " :no_entry: Hé, je peux pas annoncer dans un canal qui n'existe pas sur ton " +
                    "serveur !"},
            {"moveFail", " :no_entry: J'ai pas l'impression de pouvoir annoncer là-bas. Assure-toi que j'ai les bonnes permissions " +
                    "dans ce canal."},
            {"moveHelp", "```Markdown\n# MOVE\n* Changer où je fais mes annonces.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <canal>\n\t"
                    + "<canal> - Le nom du canal dans lequel tu veux que j'annonce (inclure ABSOLUMENT le #)"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " move #discordchannel" + "```"},
            {"moveSuccess", " :ok_hand: J'annoncerai là-bas ! :arrow_right: "},
            {"needOneManager", "Si tu supprimes celui-là, qui va me gérer ?"},
            {"noBotManager", "C'est contre les lois de l'union des bots de Discord de laisser des bots me gérer. Désolé, essaie de trouver un " +
                    "humain approprié pour ce travail. :thumbsup:"},
            {"noneOnline", "Désolé mon pote, personne n'est en ligne en ce moment parmi les chaînes que je surveille."},
            {"notAManager", "Désole, mais seuls mes managers peuvent faire ça. Tape `" + Const.COMMAND_PREFIX + Const.COMMAND +
                    " list manager` pour recevoir une liste des gens qui le peuvent."},
            {"notAnAdmin", "Aux concernés : je suis votre servant, mais vous n'êtes pas mon maître."},
            {"notifyEveryone", ":tada: WOW !!  **TOUT LE MONDE** sur le serveur sera notifié quand j'annoncerai " +
                    "des streams !  *(Es-tu sûr ? Je ne le recommande pas aux grands serveurs... Cela peut " +
                    "énerver les gens.)*"},
            {"notifyHelp", "```Markdown\n# NOTIFY:\n*  Utilisé pour changer les options de notifications globales du serveur.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " notify <option>"
                    + "\n\tnone - Aucune mention @ de quelque sorte que ce soit. (par défaut)"
                    + "\n\there - Je préviendrai uniquement les personnes qui sont en ligne au moment de l'annonce"
                    + "\n\teveryone - Je préviendrai TOUT LE MONDE !! Mouahahaha !! (Je ne le recommande pas aux grands serveurs)"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify everyone" + "```"},
            {"notifyHere", " :bellhop_bell: Tous ceux qui sont en ligne recevront une notification quand j'annoncerai un stream."},
            {"notifyNone", " :ok_hand: Je ne mentionnerai plus personne dans mes annonces."},
            {"nowLive", "EN LIVE !\n"},
            {"nowPlayingEmbed", "Joue à"},
            {"nowPlayingLower", " est en train de jouer "},
            {"nowStreamingEmbed", " est en live !"},
            {"numUniqueMembers", "Nombre de membres uniques"},
            {"offline", "HORS-LIGNE !\n"},
            {"offlineEmbed", " est maintenant hors-ligne !"},
            {"on", " dans l'équipe "},
            {"onlineStreamPm1", "Hello ! Il y a en ce moment "},
            {"onlineStreamPm2", " streamers en ligne qui pourraient t'intéresser ! Suis leur lien pour aller voir " +
                    "leur chaîne : \n\n"},
            {"oops", "Oups ! Quelque chose s'est mal passé et rien n'a été modifié ! Essayons encore."},
            {"ping", "Quand j'étais en Chine dans l'équipe d'Amérique de ping-pong, j'adorais jouer avec ma " +
                    "raquette de ping-pong Flexolite."},
            {"pingHelp", "```Markdown\n# PING\n* Utilisé pour m'envoyer un ping. Si je fonctionne bien, je vous renverrai un pong.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " ping```"},
            {"privateMessageReply", "Je suis désolé, mais le bot que tu essaies d'atteindre a une messagerie vocale qui n'a pas encore " +
                    "été mise en place.  Réessaie d'envoyer ton message plus tard."},
            {"removed", "Enlevé %s %s."},
            {"removeManagerFail", "Je ne peux pas enlever %s car il n'est pas dans ma base de données."},
            {"removeHelp", "```Markdown\n# REMOVE\n* Utilisé pour enlever des managers de ma base de données.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " remove <option> <content>"
                    + "\n\tmanager - Le @ de l'utilisateur à enlever de la liste des managers"
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove manager @AgueMort```"},
            {"servers", "Serveurs"},
            {"statusHelp", "```Markdown\n# STATUS\n* Affiche diverses statistiques du bot.\n\n## UTILISATION :  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " status```"},
            {"streamlangHelp", "```Markdown\n# STREAMLANG\n* Permet de filtrer les streams par la langue dans laquelle ils sont " +
                    "diffusés.  Cette commande supporte l'écriture anglaise de la langue ou l'écriture native.  Ce doit être une " +
                    "langue supportée par Twitch qui est listée dans le Tableau de Bord.\n\n## UTILISATION : "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <langue>\n" +
                    "* Vous pouvez utiliser soit l'écriture native de la langue ou bien son écriture anglaise."
                    + "\n\n## EXEMPLE :  " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang german```"},
            {"streamTitleEmbed", "Titre du Stream"},
            {"streamsHelp", "```Markdown\n# STREAMS\n* Je t'enverrai par message privé une liste des streamers actifs.\n* (NOTE : Tu recevras sûrement " +
                    "plusieurs messages en utilisant cette commande, suivant le nombre de streams que votre serveur " +
                    "surveille !)\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Vues Totales"},
            {"twitchCommunities", "Communautés Twitch"},
            {"twitchAnnounceUpdate", "\n# Le canal des annonces Twitch passe de %s à : %s."},
            {"twitchAnnounceUpdateFail", "\n! Echec du changement du canal des annonces Twitch de %s à : %s."},
            {"twitchChannelAdd", "\n# Chaîne(s) ajoutée(s) : %s."},
            {"twitchChannelAddFail", "\n# Echec de l'ajout des chaînes suivantes : %s."},
            {"twitchChannelAnnounce", "\n# Elles seront annoncées dans : #%s."},
            {"twitchChannelGameFilter", "\n# Elles ne seront annoncées uniquement lorsqu'elles joueront à : %s."},
            {"twitchChannelRemove", "\n# Chaîne(s) supprimée(s) : %s."},
            {"twitchChannelRemoveFail", "\n! Echec de la suppression des chaînes suivantes : %s."},
            {"twitchChannelTitleFilter", "\n# Elles ne seront annoncées uniquement lorsque les mots suivants seront présents dans le titre : %s."},
            {"twitchCommunityAdd", "\n# Communauté(s) ajoutée(s) : %s."},
            {"twitchCommunityAddFail", "\n# Echec de l'ajout des communautés suivantes : %s."},
            {"twitchCommunityAnnounce", "\n# Ces communautés annonceront dans : #%s."},
            {"twitchCommunityNotFound", "\n# Communautés non trouvées sur Twitch : %s."},
            {"twitchCommunityRemove", "\n# Communauté(s) supprimées : %s."},
            {"twitchCommunityRemoveFail", "\n# Echec de la suppression des communautés : %s."},
            {"twitchGameAdd", "\n# Jeu(x) ajoutés : %s."},
            {"twitchGameAddFail", "\n# Echec de l'ajout des jeux suivants : %s."},
            {"twitchGameAnnounce", "\n# Le jeu sera annoncé dans : #%s."},
            {"twitchGameFilterAdd", "\n# Filtre(s) de jeu ajouté(s) : %s."},
            {"twitchGameFilterAddFail", "\n# Echec de l'ajout des filtres de jeu suivant : %s."},
            {"twitchGameFilterRemove", "\n# Filtre(s) de jeu supprimés : %s."},
            {"twitchGameFilterRemoveFail", "\n# Echec de la suppression des filtres de jeu suivants : %s."},
            {"twitchGameRemove", "\n# Jeu(x) supprimés : %s."},
            {"twitchGameRemoveFail", "\n# Echec de la suppression des jeux suivants : %s."},
            {"twitchHelp", "```Markdown\n# TWITCH\n* Add and remove things that are Twitch.tv related.\n"
                    + "* Notes :\n\t"
                    + "Pour ajouter des filtres de jeu et des filtres de titre, vous DEVEZ inclure respectivement les accolades et les crochets.\n\t"
                    + "N'utilisez PAS l'adresse Twitch entière, ça ne marchera pas !! Utilisez uniquement le nom de la chaîne (www.twitch.tv/nomChaine)\n\t"
                    + "Le nom d'équipe doit être celui de l'adresse, non celui d'affichage. (www.twitch.tv/team/nomEquipe)\n\t"
                    + "Vous pouvez ajouter plusieurs chaînes, équipes, jeux, communautés, filtres de jeu/titre en utilisant la barre verticale | entre eux.\n\t"
                    + "Les seules options requises sont nomChaine/nomCommunauté/nomEquipe/nomJeu \n\n"
                    + "## Twitch Channels\n"
                    + "Note : En ajoutant un canal pour les annonces (canalAnnonce), les filtres de jeu/titre sont optionnels."
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel nomChaine #canalAnnonce {filtreJeu} [filtreTitre]\n\n"
                    + "## Communautés Twitch (Annoncer TOUS les lives dans la communauté)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community nomCommunauté #canalAnnonce\n\n"
                    + "## Jeux Twitch (Annoncer TOUS les lives de ce jeu)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game nomJeu #canalAnnonce\n\n"
                    + "## Equipes Twitch (Annoncer TOUS les lives de l'équipe)\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team nomEquipe #canalAnnonce\n\n"
                    + "## Filtres de jeu Twitch (Global)\n"
                    + "* NOTE : Cela affecte toutes les annonces de streams pour Twitch\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch gamefilter {nomJeu|nomJeu} #canalAnnonce\n\n"
                    + "## Filtres de titre Twitch (Global)\n"
                    + "* NOTE : Cela affecte toutes les annonces de streams pour Twitch\n"
                    + "* Format: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch titlefilter nomJeu #canalAnnonce\n\n"
                    + "* Exemples :\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel AgueMort #live-streams {Overwatch|World of "
                    + "Warcraft} (ajoute une chaîne à annoncer dans un certain canal et des filtres de jeu)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game Overwatch (ajoute un jeu au canal d'annonce global)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community MMORPG #live-streams (ajoute la communauté avec un canal d'annonce spécifique)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team thekingdom #the-kingdom-streamers (ajoute une équipe avec un canal d'annonce particulier)\n\n"
                    + "```"},
            {"twitchTeamAdd", "\n# Equipes ajoutée(s) : %s."},
            {"twitchTeamAddFail", "\n# Echec de l'ajout des équipes suivantes : %s."},
            {"twitchTeamAnnounce", "\n# Les équipes annonceront dans : #%s."},
            {"twitchTeamNotFound", "\n# Equipe(s) non trouvées sur Twitch : %s."},
            {"twitchTeamRemove", "\n# Equipe(s) supprimée(s) : %s."},
            {"twitchTeamRemoveFail", "\n# Echec de la suppression des équipes : %s."},
            {"twitchTeams", "Equipes Twitch"},
            {"twitchTitleFilterAdd", "\n# Filtre(s) de titre ajouté(s) : %s."},
            {"twitchTitleFilterAddFail", "\n# Echec de l'ajout des filtres de titre suivants : %s."},
            {"twitchTitleFilterRemove", "\n# Filtres de titre supprimés : %s."},
            {"twitchTitleFilterRemoveFail", "\n# Echec de la suppression des filtres de titre : %s."},
            {"typeOnce", "Gné, tu n'as besoin de taper cette partie qu'une seule fois."},
            {"uniqueChannels", "Chaînes uniques %s"},
            {"uniqueGames", "Jeux uniques %s"},
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