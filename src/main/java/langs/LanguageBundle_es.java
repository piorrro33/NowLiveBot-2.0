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
public class LanguageBundle_es extends ListResourceBundle {

    private Object[][] contents = {
            {"added", "Añadido "},
            {"addFail", "No se pudo añadir "},
            {"addHelp", "```Ruby\nADD:  Se usa para añadir cosas a mi base de datos.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <option> <content>"
                    + "\n\t<option>\t<content>"
                    + "\n\tfilter - El juego por el que quieres filtrar streamers"
                    + "\n\tgame - El nombre del juego exactamente como aparece en la plataforma de streams"
                    + "\n\tmanager - Haz gestor a alguien mencionandolo usando @ ```"},
            {"adminOverride", "*El permiso de este comando se ha reemplazado por un desarollador del bot.*"},
            {"alreadyExists", "Parece que esto ya estaba en mi base de datos. ¯\\_(?)_/¯"},
            {"alreadyManager", "Parece que ese usuario ya es gerente. Encuentra más humanos! "},
            {"announceHelp", "```Ruby\nANNOUNCE:  Shhh...  Soy un secreto...\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <content>\n\tEste comando solo puede ser usado por desarrolladores.```"},
            {"beamHelp", "```Ruby\nBEAM:  Añade y quita cosas relacionadas con el Beam.pro\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " beam <sub-comando> <opción> <argumento>"
                    + "\n\t<sub-comando> <opción> <argumento>"
                    + "\n\tadd channel <nombre-de-el-canal>"
                    + "\n\tremove channel <nombre-de-el-canal>```"},
            {"beamUserNoExist", "Este usuario Beam no existe, revisa como lo has escrito e intentalo de nuevo."},
            {"broadcasterLangAllSuccess", " :ok_hand: Solo buscaré streamers en todos los idiomas!"},
            {"broadcasterLangFail", "Algo ha salido mal, seguiré buscando en todos los idiomas."},
            {"broadcasterLangSuccess", " :ok_hand: Solo buscaré streamers en ese idioma!"},
            {"canNotRemoveOwner", "Humano incrédulo, no puedes eliminar al dueño del servidor de la lista de gerentes. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Tíiiio, algo ha salido mal. Intentalo en un ratico."},
            {"cleanupHelp", "```Ruby\nCLEANUP:  Cambia la forma en la que limpio mis notificaciones.\nUSAGE:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <opción>"
                    + "\n<opción>"
                    + "\n\tnone - No le cambiaré nada a mis notficaciones! (por defecto)"
                    + "\n\tedit - Editaré mis notificaciones para que ponga \"OFFLINE\" cuando el streamer se desconecte."
                    + "\n\tdelete - Borraré la notificación cuando el streamer se desconecte.```"},
            {"cleanupSuccessDelete", "Señor, sí señor! Borraré todas las notificaciones a partir de ahora!"},
            {"cleanupSuccessEdit", "Editando las notificaciones..."},
            {"cleanupSuccessNone", " :ok_hand: No le haré nada a las notificaciones."},
            {"compactFail", "Algo ha salido mal. Mi modo compacto no ha cambiado."},
            {"compactHelp", "```Ruby\nCOMPACT:  Cambia mis notificaciones a modo compacto. \nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <opción>"
                    + "\n<opción>"
                    + "\n\ton - Activa el modo compacto"
                    + "\n\toff - Desactiva el modo compacto```"},
            {"compactOff", " :compression: Modo compacto desactivado."},
            {"compactOn", " :compression: Modo compacto activado."},
            {"devMessage", "*Mensaje de los desarrolladores de:" + Const.BOT_NAME + "\n\n\t"},
            {"discordUserNoExist", "Esta persona no es usuario de discord!  Intentalo de nuevo!"},
            {"doesNotExist", "Eso nunca estuvo en mi base de datos."},
            {"emptyArgs", "Creo que te has olvidado de algún comando. Revisa la ayuda para más info."},
            {"emptyCommand", "La próxima vez que me despiertes, pon un comando."},
            {"followersEmbed", "Seguidores"},
            {"guildJoinSuccess", "Hola!  Soy Now Live, el bot notificador de streams! Escribe `" + Const.COMMAND_PREFIX
                    + Const.COMMAND + " help` para una lista de comandos.\n\nSi necesitas ayuda configurandome, ven, " +
                    "únete a mi discord " + Const.DISCORD_URL + " y revisa how-to-setup y commands-list " +
                    "para toda la info!\n\nNo olvides saludar!"},
            {"helpPm", "Hola %s!\n\n" +
                    "Entonces.. Buscas ayuda?  Aquí abajo hay una lista de comandos. para saber lo que hace " +
                    "cada uno, escribe " + Const.COMMAND_PREFIX + Const.COMMAND + "<comando> help\n\n" +
                    "```Ruby\n* add\n* beam\n* cleanup\n* compact\n* invite\n* list\n* move\n* notify\n* ping\n* remove" +
                    "\n* streamlang\n* streams\n* twitch```\n" +
                    "Para que lo sepas, Ague sigue trabajando duro para pulirme, ya que muchos de los " +
                    "comandos aquí puede que no funcionen!  Pero sus trabajos funcionan!.  Puedes ayudar a los tíos " +
                    "que trabajan duro para que esto funcione!\n\n\t~~" + Const.BOT_NAME + "\n\n" +
                    "Además puedes obtener algo de ayuda de mi desarrollador y el resto de la comunidad de Now Live " +
                    "Aquí! Solo haz click aquí para unirte:  " + Const.DISCORD_URL + "\n\n" +
                    "*P.S. No monitorizo los mensajes privados, por lo que no envíes privados al bot*"},
            {"incorrectArgs", "Has escrito algo mal.  Revisa la ayuda para más información" +
                    "."},
            {"invite", "Hola amigo! Invitame a tu server!\n\n\t"
                    + "**Click aquí:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Ruby\nINVITE:  Ya puse mi link de invitaación.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tInvita a NowLive a tu servidor de Discord.```"},
            {"listHelp", "```Ruby\nLIST:  He listado cosas de mi base de datos.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " list <option>"
                    + "\n\t<option>"
                    + "\n\tchannel - Voy a hacer un listado de todos los canales que quieres que monitorize"
                    + "\n\tfilter - Voy a hacer un listado de todos los filtros que me has puesto"
                    + "\n\tgame - Voy a listar todos los juegos que quieres que mire"
                    + "\n\tmanager - Voy a listar todos los gerentes"
                    + "\n\tsetting - Voy a listar otras opciones```"},
            {"moveDoNotOwnChannel", " :no_entry: Ehm... no puedo anunciar en un canal que no existe en tu " +
                    "server!"},
            {"moveFail", " :no_entry: Parece que no puedo enviar notificaciones ahí. Revisa mis permisos. " +
                    "in that channel."},
            {"moveHelp", "```Ruby\nMOVE:  Cambia donde pongo mis notificaciones.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <channel>\n\t"
                    + "<channel> - El canal en el que quieras que notifique los directos (DEBE elcuir el #)```"},
            {"moveSuccess", " :ok_hand: Anunciaré ahí! :arrow_right: "},
            {"needOneManager", "Si lo eliminas... Quien me mandará cosas?"},
            {"noBotManager", "Eso vá en contra de las leyes de la unión de los bots de discord. Lo siento " +
                    "Encuentra a un buen humano para ese trabajo. :thumbsup:"},
            {"noneOnline", "Lo siento tío, Pero no hay nadie de los que sigo en directo."},
            {"notAManager", "Lo siento, sólo los gerentes pueden hacer eso."},
            {"notAnAdmin", "A quien le convenga:  Soy tu sirviente pero no tu no eres mi maestro."},
            {"notifyEveryone", ":tada: GUAU!!  **TODOS** los de este servidor serán notificados cuando alguien " +
                    "se ponga en directo! *(Estás seguro?  No lo recomiendo para grandes servidores...  Puede " +
                    "que alguien se moleste.)*"},
            {"notifyHelp", "```Ruby\nNOTIFY:  Se usa para cambiar el método de notificación global en el server.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " notify <option>"
                    + "\n\tnone - No hay @ notificaciones de ese tipo (por defecto)"
                    + "\n\there - Notificaré a todos los que estén conectados cuando lo anuncie"
                    + "\n\teveryone - Notificaré a TODOS!!  MUAJAJAJAJA!!  (No lo recomiendo en servers grandes)```"},
            {"notifyHere", " :bellhop_bell: Todo el mundo que esté conectado será notificado."},
            {"notifyNone", " :ok_hand: No mencionaré a nadie en mis notificaciones."},
            {"nowLive", "NOW LIVE!\n"},
            {"nowPlayingEmbed", "Reproduciendo"},
            {"nowPlayingLower", " Está reproduciendo "},
            {"nowStreamingEmbed", " Está streameando!"},
            {"offline", "OFFLINE!\n"},
            {"offlineEmbed", " Se ha desconectado!"},
            {"on", " on "},
            {"onlineStreamPm1", "Hey!  Ahora hay "},
            {"onlineStreamPm2", " streamers en linea que te pueden interesar! Miralos y puede que te gusten! \n\n"},
            {"oops", "Oops!  Algo ha salido mal, pero no se ha cambiado nada!  intentemoslo de nuevo."},
            {"ping", "Cuando estaba en china, en el equipo \"All-American Ping Pong\", me encantaba jugar al ping-pong con " +
                    "raqueta Flexolite. Viejos tiempos."},
            {"pingHelp", "```Ruby\nPING:  Se usa para pingearme. Si funciono bien, te enviaré un pong.\nUSAGE: "
                    + Const.COMMAND_PREFIX + "ping```"},
            {"privateMessageReply", "Lo siento, pero el bot al que estás intentando contactar no tiene el mailobx " +
                    "configurado aún.  Porfavor, intentelo más tarde."},
            {"removed", "Eliminado."},
            {"removeFail1", "No puedo eliminar a "},
            {"removeFail2", " porque no está en mi base de datos."},
            {"removeHelp", "```Ruby\nREMOVE:  Se usa para eliminar algo de mi abse de datos.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " remove <option> <content>"
                    + "\n\t<option>\t<content>"
                    + "\n\tfilter - El juego por el que quieres filtrar streamers"
                    + "\n\tgame - El nombre del juego exactamente como aparece en la plataforma"
                    + "\n\tmanager - Menciona con @ al usuario que quieres añadir como gerente```"},
            {"statusHelp", "Muestra estadísticas del bot."},
            {"streamlangHelp", "```Ruby\nSTREAMLANG:  Permite filtrar streams por el idioma en que se emite " +
                    " Esto soporta el idioma escrito en inglés , o el idioma nativo.  Debe ser un idoma  " +
                    "soportado en Twitch listado en el Dashboard.\nUSAGE:  \n"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <language>" +
                    "Debes escribirlo en el idioma nativo del lenguaje o en inglés.  Para más info mira" +
                    "https://github.com/VeteranSoftware/NowLiveBot-2.0/blob/master/README.md```"},
            {"streamTitleEmbed", "Stream Title"},
            {"streamsHelp", "```Ruby\nSTREAMS:  Te enviaré una lista de streams por privado. (NOTA: Recibirás " +
                    "bastantes privados usando este comando, dependiendo cuantos streams estés" +
                    "monitorizando!)\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Visualizaciones totales"},
            {"twitchHelp", "```Ruby\nTWITCH:  Añade y quita cosas relaccionadas con Twitch.tv.\nUSAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " twitch <sub-command> <option> <argument>"
                    + "\n\t<sub-command> <option> <argument>"
                    + "\n\tadd channel <channel-name>"
                    + "\n\tremove channel <channel-name>```"},
            {"typeOnce", "Solo necesitas escribirlo una vez, tontito."},
            {"usePlatform", "Oops!  Eso es como se hacía antes!  Usa el comando específico!  Escribe`" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` para más info."},
            {"watchThemHere", "Miralos aquí: "},
            {"wrongCommand", " :thinking: No conozco ese comando."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}