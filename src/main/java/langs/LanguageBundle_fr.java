package langs;

import util.Const;
import util.PropReader;

import java.util.ListResourceBundle;

/**
 * @author Veteran Software by Ague Mort
 */
public class LanguageBundle_fr extends ListResourceBundle {

    private Object[][] contents = {
            {"emptyArgs", "Je pense que vous avez oublié une partie de l'instruction. Référez vous à l'Aide pour plus d'informations."},
            {"emptyCommand", "La prochaine fois que vous me réveillez, donnez-moi des instructions."},
            {"helpPrivateMessage", "Bonjour! J’ai entendu dire que vous aviez besoin d’aide. Voici une liste de " +
                    "commandes que je reconnais.\n\n"},
            {"incorrectArgs", "La commande est incomplète ou comporte des erreurs. Référez-vous à l’Aide pour une " +
                    "liste des commandes que je reconnais."},
            {"ping", "Quand j’étais en Chine dans l’équipe de Ping Pong américaine, j’adorais jouer au ping-pong avec " +
                    "ma raquette de ping-pong Flexolite."},
            {"privateMessageReply", "Je suis désolé, mais la boîte vocale du bot que vous essayez de contacter " +
                    "n’a pas encore été configurée.  S’il vous plaît réessayez plus tard."},
            {"typeOnce", "Grand galopin ! Vous n’avez qu’à écrire cette partie qu’une seule fois!"},
            {"wrongCommand", ":thinking: Je ne connais pas cette commande."},
            {"alreadyExists", "Il me semble que ceci ce trouve déjà dans ma base de données.  ¯\\_(ツ)_/¯"},
            {"beamUserNoExist", "That Beam user does not exist! Check your spelling and try again!"},
            {"discordUserNoExist", "That person isn't a Discord user!  Try again!"},
            {"doesNotExist", "Ceci n’a jamais été ajouté à ma base de données."},
            {"adminOverride", "*Cette commande a été annulée par un développeur bot.*"},
            {"broadcasterLangSuccess", ":ok_hand: Je vais désormais seulement chercher des streams qui sont dans ce langage!"},
            {"broadcasterLangAllSuccess", ":ok_hand: Je vais désormais chercher des streams dans tous les langages!"},
            {"broadcasterLangFail", ""},
            {"cleanupSuccessNone", ":ok_hand: Je ne ferai rien à mes annonces."},
            {"cleanupSuccessEdit", "D’accord, j’édite mes annonces."},
            {"cleanupSuccessDelete", "M’sieur oui M’sieur! J'effacerai toutes mes annonces à partir de maintenant!"},
            {"cleanupFail", "Oups :/ Une erreur s’est produite..."},
            {"compactFail", "Une erreur s’est produite. Mon mode compact reste inchangé."},
            {"compactOn", ":compression: Mode compact activé."},
            {"compactOff", ":compression: Mode compact désactivé."},
            {"moveDoNotOwnChannel", ":no_entry: Je ne peux pas annoncer sur un channel qui n’existe pas sur votre " +
                    "serveur!"},
            {"moveFail", ":no_entry: Apparemment, je ne peux pas faire d’annonces là. Assurez-vous que j’ai " +
                    "les permissions nécessaires dans ce channel."},
            {"moveSuccess", ":ok_hand: Je vais l’annoncer là-bas! :arrow_right: "},
            {"noneOnline", "Désolé l'ami, mais personne n’est en ligne actuellement parmis les utilisateurs que ce " +
                    "Discord suit."},
            {"notifyNone", ":ok_hand: Je ne mentionnerai personne dans mes annonces."},
            {"notifyHere", ":bellhop_bell: Tous les utilisateurs seront désormais mentionnés quand j’annoncerai les streams."},
            {"notifyEveryone", ":tada: WHOA!!  **EVERYONE** sur le serveur seront notifiés quand " +
                    "j’annoncerai les streams! *(Êtes-vous sûr? Je ne le recommande pas pour les gros serveurs... Ça " +
                    "peut rendre les gens grincheux.)*"},
            {"notAManager", "Désolé, mais uniquement les manageurs peuvent faire cela."},
            {"notAnAdmin", "À qui cela concerne : je suis votre serviteur, mais vous n’êtes pas mon maître."},
            {"nowPlayingLower", " Joue actuellement "},
            {"onlineStreamPm1", "Hey!  Il y a actuellement "},
            {"onlineStreamPm2", " streamers en ligne qui pourraient vous intéresser. Suivez leurs liens pour faire un " +
                    "tour: \n\n"},
            {"on", " Activé "},
            {"oops", "Oops!  Une erreur s’est produite et rien n’a changé! Recommençons."},
            {"watchThemHere", "Regardez-les ici: "},
            {"nowLive", "EN DIRECT!\n"},
            {"offline", "HORS LIGNE!\n"},
            {"alreadyManager", "It seems I've already hired that user as a manager.  Find moar humanz!"},
            {"canNotRemoveManager", "Grand galopin! Vous ne pouvez pas retirer le propriétaire du serveur de la liste " +
                    "de manageurs! :laughing: :laughing:"},
            {"noBotManager", "Il est contraire à l’Union des Bots de Discord de me manager. Désolé, essayer de trouver un" +
                    "humain pour vous aider :thumbsup:"},
            {"needOneManager", "Si tu retires celui-ci, qui me contrôlera?"},
            {"usePlatform", "Oops! C’est une façon désuète de faire les choses! Utilisez la commande spécifique! Écrivez `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + "` help pour une liste de mes commandes."},
            {"guildJoinSuccess", "Hi there!  I'm Now Live, the stream announcing bot!  Écrivez `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` pour une liste de mes commandes.\n\nSi vous avez besoin d’aide pour me " +
                    "configurer, venez rejoindre mon iscord à https://discord.gg/gKbbrFK et venez voir les " +
                    "channels « Comment configurer » et « Liste de commandes » pour toute information! \n\n Noubliez " +
                    "pas de dire bonjour!"},
            {"addHelp", "```Ruby\nADD:  Utilisé pour ajouter quelque chose à ma base de données." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " add <option> <content>" +
                    "\n\t<option>\t<content>" +
                    "\n\tfilter - Le nom du jeu que vous souhaitez filtrer" +
                    "\n\tgame - Le nom du jeu exactement tel qu'il apparaît sur la plateforme de streaming" +
                    "\n\tmanager - La mention @ de l'utilisateur à ajouter en tant que gestionnaire```"},
            {"announceHelp", "```Ruby\nANNOUNCE:  Shhh...  Je suis un secret..." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " announce <content>" +
                    "\n\tCette commande n'est disponible que pour les développeurs.```"},
            {"beamHelp", "```Ruby\nBEAM:  Ajouter et supprimer des éléments liés à Beam.pro." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam <sub-command> <option> <argument>" +
                    "\n\t<sub-command> <option> <argument>" +
                    "\n\tadd channel <channel-name>" +
                    "\n\tremove channel <channel-name>```"},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Modifier la façon dont je nettoie mes annonces de flux." +
                    "\nUSAGE:" + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup <option>" +
                    "\n<option>" +
                    "\n\tnone - Je ne changerai rien à mes annonces! (default)" +
                    "\n\tedit - Je vais modifier mes annonces pour dire \"HORS LIGNE\" quand le streamer est hors ligne" +
                    "\n\tdelete - Je supprimerai simplement l'annonce lorsque le streamer n'est plus en direct```"},
            {"compactHelp", "```Ruby\nCOMPACT:  Passer mes annonces à une version plus courte." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " compact <option>" +
                    "\n<option>" +
                    "\n\ton - Active le Mode Compact" +
                    "\n\toff - Désactive le Mode Compact```"},
            {"invite", "Hey mon pote! Invitez-moi sur votre serveur!" +
                    "\n\n\t" + "**Par ici:** https://discordapp.com/oauth2/authorize?&client_id=" +
                    PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  Utilisé pour afficher mon lien d'invitation" +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " invite" +
                    "\n\tInvitez Now Live sur votre serveur Discord.```"},
            {"listHelp", "```Ruby\nLIST:  Utilisé pour lister les éléments de ma base de données." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " list <option>" +
                    "\n\t<option>" +
                    "\n\tgame - Liste des jeux que je vais suivre pour vous" +
                    "\n\tchannel - Je vais lister tous les canaux de flux que vous voulez que je regarde```"},
            {"moveHelp", "```Ruby\nMOVE:  Changer l'endroit où je fais mes annonces." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " move <channel>" +
                    "\n\t" + "<channel> - Le nom du channel où vous souhaitez déplacer mes annonces (DOIT inclure le #)```"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Utilisé pour modifier l'option de notification globale de ce serveur." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify <option>" +
                    "\n\tnone - Aucune notification de quelque nature que ce soit (par défaut)" +
                    "\n\there - Je ne notifierai que les personnes qui sont en ligne lors de mes annonces" +
                    "\n\teveryone - Je vous avertirai TOUS! Mwahahaha !! (Je ne recommande pas cela sur les grands serveurs)```"},
            {"pingHelp", "```Ruby\nPING:  Utilisé pour me ping. Si je fonctionne correctement je vous enverrai un pong." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + "ping```"},
            {"removeHelp", "```Ruby\nREMOVE:  Utilisé pour supprimer quelque chose de ma base de données." +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove <option> <content>" +
                    "\n\t<option>\t<content>" +
                    "\n\tfilter - Le nom du jeu que vous souhaitez filtrer" +
                    "\n\tgame - Le nom du jeu exactement tel qu'il apparaît sur la plateforme de streaming" +
                    "\n\tmanager - La mention @ de l'utilisateur à ajouter en tant que gestionnaire```"},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Permet de filtrer les flux en fonction de la langue qu'ils diffusent. " +
                    "L'anglais est supporté en tant que langue native.  Les autres langues doivent être prises en charge" +
                    "par Twitch (répertoriées dans le Tableau de Bord)." +
                    "\nUSAGE: " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang <language>" +
                    "\n\nVous pouvez utiliser soit la langue par défaut (Anglais) soit les langues supportées.  Reportez-vous à " +
                    "https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamsHelp", "```Ruby\nSTREAMS:  Je vais vous envoyer une liste de flux actifs en MP. (NOTE: Vous recevrez probablement " +
                    "plusieurs messages privés en utilisant cette commande, selon le nombre de flux surveillés par " +
                    "votre Discord!)" +
                    "\nUSAGE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " streams```"},
            {"statusHelp", "Affiche diverses statistiques du bot."},
            {"twitchHelp", "```Ruby\nTWITCH:  Ajouter et supprimer des éléments liés à Twitch.tv." +
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