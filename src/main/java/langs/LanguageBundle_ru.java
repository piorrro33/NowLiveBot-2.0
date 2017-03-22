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
public class LanguageBundle_ru extends ListResourceBundle {

    private Object[][] contents = {
            {"added", "Добавить "},
            {"addFail", "Не удалось добавить "},
            {"addHelp", "```Markdown\n# ADD\n* Используется для добавления менеджеров для вашего сервера.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " add <option> <content>"
                    + "\n\t<option> <content>"
                    + "\n\tmanager - @ Упоминание пользователя о добавлении в качестве менеджера"
                    + "\n\n## ПРИМЕР: " + Const.COMMAND_PREFIX + Const.COMMAND + " добавить менеджера @Ague```"},
            {"adminOverride", "*Разрешение этой команды было отменено разработчиком.*"},
            {"alreadyExists", "Похоже, вы уже добавили это в мою базу данных.. ¯\\_(ツ)_/¯"},
            {"alreadyManager", "Кажется, я уже установил этого пользователя в качестве менеджера."},
            {"announceHelp", "```Markdown\n# ANNOUNCE\n* Тсс... Я секрет...\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " announce <content>\n\tЭта команда доступна только для разработчиков.```"},
            {"announcementMessageText", "Привет!  %s начал(а) трансляцию! Смотрите стрим здесь: %s"},
            {"beamHelp", "```Markdown\n# BEAM\n* Добавление и удаление данных связанных с Beam.pro.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " beam <sub-command> <option> <argument>\n"
                    + "\t<sub-command> <option> <argument>\n"
                    + "\tadd           channel  <channelname>\n"
                    + "\tremove        channel  <channel-name>\n\n"
                    + "## ПРИМЕР:  " + Const.COMMAND_PREFIX + Const.COMMAND + " beam add channel Ague" + "```"},
            {"beamUserNoExist", "Указанного пользователя Beam не существует! Проверьте правильность написания и повторите попытку!"},
            {"botLangFail", "Что-то пошло не так. Мой язык все тот же."},
            {"botLangHelp", "```Markdown\n# BOTLANG\n* Используется для изменения языка моих ответов.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " Язык бота"
                    + "\n\tВведите либо английское правописание, либо родное написание языка, который вы хотите установить."
                    + "\n\n## ПРИМЕР:  " + Const.COMMAND_PREFIX + Const.COMMAND + " botlang spanish" + "```"},
            {"botLangSuccess", "Вы успешно изменили мой язык."},
            {"botLangUnsupported", "Этот язык в настоящее время не поддерживается."},
            {"botStatistics", "%s Статистика"},
            {"broadcasterLangAllSuccess", " :ok_hand: Я буду искать трансляции на всех языках."},
            {"broadcasterLangFail", "Что-то пошло не так, и я по-прежнему ищу все языки."},
            {"broadcasterLangSuccess", " :ok_hand: Я буду искать только те трансляции, которые находятся на этом языке!"},
            {"canNotRemoveOwner", "Глупо. Вы не можете удалить владельца сервера из списка менеджеров. :laughing: " +
                    ":laughing:"},
            {"cleanupFail", "Хм... Что-то пошло не так... Лучше попробуйте это еще раз."},
            {"cleanupHelp", "```Markdown\n# CLEANUP\n*  Изменение способа очистки моих объявлений трансляций.\n\n## USAGE:"
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " cleanup <option>"
                    + "\n\tnone   - Я ничего не буду менять в своих объявлениях! (default)"
                    + "\n\tedit   - Я отредактирую свои объявления, чтобы сказать \"OFFLINE\" , когда стрим окончен"
                    + "\n\tdelete - Я просто удалю объявление, когда стрим окончен"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " cleanup edit" + "```"},
            {"cleanupSuccessDelete", "Как скажете!  Теперь я буду удалять все мои объявления, если трансляция окончена!"},
            {"cleanupSuccessEdit", "Я буду редактировать мои объявления, если трансляция началась или окончена."},
            {"cleanupSuccessNone", " :ok_hand: Я ничего не буду делать с моими объявлениями."},
            {"compactFail", "Эм... что-то пошло не так. Мой компактный режим не изменился."},
            {"compactHelp", "```Markdown\n# COMPACT\n* Мои объявления изменены на более компактную версию.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " compact <option>"
                    + "\n\ton  - Turns on Compact Mode"
                    + "\n\toff - Turns off Compact Mode"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " compact on" + "```"},
            {"compactOff", " :compression: Компактный режим отключен."},
            {"compactOn", " :compression: Компактный режим включен."},
            {"devMessage", "*Message from the " + Const.BOT_NAME + " developers:*\n\n\t"},
            {"discordChannelNoExist", "Данный текстовый канал отсутствует на вашем сервере.."},
            {"discordUserNoExist", "Данного пользователя Discord не существует! Попробуй еще раз!"},
            {"doesNotExist", "Это никогда не добавлялось в мою базу данных."},
            {"emptyArgs", "Кажется вы забыли какую-то команду.  Изучите команды помощи для получения дополнительной информации.."},
            {"emptyCommand", "В следующий раз, когда ты меня разбудишь, пожалуйста отправьте также команду."},
            {"followersEmbed", "Подписчиков"},
            {"guildJoinSuccess", "Всем привет!  Я Now Live. Я объявлю если стримеры начинают трансляции!  Type `" +
                    Const.COMMAND_PREFIX + Const.COMMAND +
                    " help` для списка моих команд.\n\nIf вам нужна помощь в настройке, приходите " +
                    "присоединяйтесь к моему Discord на " + Const.DISCORD_URL + " и ознакомьтесь с инструкциями по настройке и списком команд " +
                    "каналы для всей информации!\n\nНе забудь поздороваться!"},
            {"helpPm", "Привет всем, %s! Итак, я слышал, ты ищешь помощи? Ниже приведен список моих команд.\n\n" +
                    "```Markdown\n" +
                    "# ADD\n" +
                    "* Используется для добавления информации в мою базу данных. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " add help\n\n" +
                    "# BEAM\n" +
                    "* Добавьте и удалите данные, связанные с Beam.pro. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " beam help\n\n" +
                    "# BOTLANG\n" +
                    "* Используется для изменения языка моих ответов.\n" +
                    "* Поддерживаемые языки: Английский, Чешский, Германский, Французский, Русский" +
                    "* Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " botlang help\n\n" +
                    "# CLEANUP\n" +
                    "* Измените способ очистки моих анонсов. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " cleanup help\n\n" +
                    "# COMPACT\n" +
                    "* Переключить мои объявления в более компактный вид. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " compact help\n\n" +
                    "# INVITE\n" +
                    "* Используется для показа моей ссылки приглашения. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " invite help\n\n" +
                    "# LIST\n" +
                    "* Эта команда перечисляет вещи из базы данных. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " list help\n\n" +
                    "# MOVE\n" +
                    "* Используется для изменения канала, в котором я делаю свои объявления. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " move help\n\n" +
                    "# NOTIFY\n" +
                    "* Используется для изменения опции глобального уведомления для этого сервера. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " notify help\n\n" +
                    "# PING\n" +
                    "* Используется для проверки скорости моей реакции. Если я правильно работаю, я пришлю вам ответ. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " ping help\n\n" +
                    "# REMOVE\n" +
                    "* Используется для удаления чего-либо из моей базы данных. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " remove help\n\n" +
                    "# STREAMLANG\n" +
                    "* Позволяет фильтровать трансляции по языку, на котором они проходят.\n" +
                    "* Для получения дополнительной информации введите: " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang help\n\n" +
                    "# STREAMS\n" +
                    "* Я вышлю вам список активных трансляций в личные сообщения." +
                    "* Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " streams help\n\n" +
                    "# TWITCH\n" +
                    "* Добавmnе и удалите связанные с Twitch.tv вещи. Для получения дополнительной информации введите: " +
                    Const.COMMAND_PREFIX + Const.COMMAND + " twitch help\n\n```\n" +
                    "Ague все еще упорно трудится, чтобы закончить мои новые функции и предоставить вам!\n\n~~" + Const.BOT_NAME + "\n\n" +
                    "Если вам нужна дополнительная помощь, присоединяйтесь к моему серверу в Discord.  Там много пользователей, которые будут полезны вам:  " + Const.DISCORD_URL + "\n\n" +
                    "***P.S. Я не проверяю личных сообщений, поэтому не отправляйте их***"},
            {"incorrectArgs", "Вы передали мне неверные или отсутствующие значения.  Проверьте команду help для получения дополнительной информации.."},
            {"invite", "Привет %s! Пригласите меня на свой сервер!\n\n\t"
                    + "**Кликабельно:** https://discordapp.com/oauth2/authorize?&client_id="
                    + PropReader.getInstance().getProp().getProperty("discord.client.id") + "&scope=bot&permissions=8"},
            {"inviteHelp", "```Markdown\n# INVITE\n* Используется для показа моей ссылки приглашения.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " invite\n\tПриглашение бота NowLive на ваш сервер Discord.```"},
            {"listHelp", "```Markdown\n# LIST\n* Эта команда показывает базу данных.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " list *option*"
                    + "\n\ttwitchChannel   - Перечисляет Twitch каналы, которые вы назначали"
                    + "\n\ttwitchCommunity - Список Twitch сообществ, которые вы назначали"
                    + "\n\tgamefilter      - Список всех, установленных вами, игровых фильтров"
                    + "\n\ttwitchGame      - Список всех условий ваших игровых фильтров"
                    + "\n\tmanager         - Список менеджеров на этом сервере"
                    + "\n\ttitlefilter     - Выводит список всех фильтров заголовков, которые вы создали"
                    + "\n\ttwitchTeam      - Перечисляет Twitch team, которые вы назначали"
                    + "\n\tsetting         - Список общих настроек бота"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " list twitchChannel" + "```"},
            {"listSettings", "```Markdown\n" +
                    "# Параметры бота на вашем сервере" +
                    "\n* Компактный режим %s:" +
                    "\n* Уведомление настроено на %s:" +
                    "\n* Очистка установлена на %s." +
                    "\n* Язык объявлений о стриме: %s." +
                    "\n* Язык бота: %s.```"},
            {"moveDoNotOwnChannel", " :no_entry: Теперь, я не объявляю в канале которого нет на " +
                    "сервере!"},
            {"moveFail", " :no_entry: Кажется, я не могу отправлять объявления.  Удостоверьтесь, что у меня есть соответствующие разрешения " +
                    "на этом канале."},
            {"moveHelp", "```Markdown\n# MOVE\n* Изменить, где я делаю свои объявления.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " move <channel>\n\t"
                    + "<channel> - Название канала, на который вы хотите переместить мои объявления: (MUST include the #)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " move #discordchannel" + "```"},
            {"moveSuccess", " :ok_hand: Я объявлю там!"},
            {"needOneManager", "Если вы удалите этого менеджера, кто будет управлять мной?"},
            {"noBotManager", "Это противоречит Уставу Союза Discord Бота, чтобы боты управляли мной. Извините, попробуйте найти " +
                    "подходящего человека для работы. :thumbsup:"},
            {"noneOnline", "Извините %s, Но нет никого онлайн прямо сейчас на этом сервере."},
            {"notAManager", "Извините, но только мои руководители могут это сделать. Type `" + Const.COMMAND_PREFIX + Const.COMMAND +
                    " list manager` для списка людей, которые могут."},
            {"notAnAdmin", "К кому это может относиться: я твой слуга, но ты не мой господин."},
            {"notifyEveryone", ":tada: ВАУ!!  **EVERYONE** на сервере уведомляется, когда я " +
                    "объявляю о трансляции!  *(Ты уверен?  Я не рекомендую это для больших серверов...  Это может " +
                    "рассердить людей.)*"},
            {"notifyHelp", "```Markdown\n# NOTIFY\n* Используется для изменения опции глобального уведомления для этого сервера.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " notify <option>"
                    + "\n\tnone     - Нет @ упоминаний любого вида (по умолчанию)"
                    + "\n\there     - Я буду уведомлять только тех людей, которые находятся в сети, когда я делаю объявление"
                    + "\n\teveryone - Я сообщу ВСЕМ!!  Ахаха!!  (Я не рекомендую это на больших серверах)"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " notify everyone" + "```"},
            {"notifyHere", " :bellhop_bell: Все, кто онлайн, получат упоминание, когда я анонсирую трансляции."},
            {"notifyNone", " :ok_hand: Я не буду упоминать никого в моих объявлениях."},
            {"nowLive", "НАЧАЛ(А) ТРАНСЛЯЦИЮ!\\\n"},
            {"nowPlayingEmbed", "Сейчас Играет в"},
            {"nowPlayingLower", " сейчас играет в "},
            {"nowStreamingEmbed", " сейчас в сети!"},
            {"numUniqueMembers", "Количество Уникальных Участников"},
            {"offline", "НЕ В СЕТИ!\n"},
            {"offlineEmbed", " отключился!"},
            {"on", " on "},
            {"onlineStreamPm1", "Всем привет!  Сейчас есть "},
            {"onlineStreamPm2", " трансляция(и) онлайн, которая(ые) могут вас заинтересовать!  Переходите по ссылке(ам), чтобы проверить " +
                    "их: \n\n"},
            {"oops", "Проблемка!  Что-то пошло не так. Ничего не изменилось! Попробуем снова."},
            {"ping", "Когда я был в Китае от Американской сборной по пинг-понгу ,  я очень любил играть моей " +
                    "Flexolite ракеткой."},
            {"pingHelp", "```Markdown\n# PING\n* Используйте для проверки моего времени отклика. Если я правильно работаю, я пришлю вам pong.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " ping```"},
            {"privateMessageReply", "Я извиняюсь, но бот, которого вы пытаетесь достать, имеет автоответчик, который " +
                    "еще не настроен. Повторите попытку позже.."},
            {"removed", "Removed %s %s."},
            {"removeManagerFail", "Я не могу удалить %s , так как их нет в моей базе данных."},
            {"removeHelp", "```Markdown\n# REMOVE\n* Используется для удаления менеджеров из моей базы данных.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " remove manager @userName"
                    + "\n\tmanager - @ Упоминание пользователя об удалении в качестве менеджера"
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " remove manager @AgueMort```"},
            {"servers", "Servers"},
            {"statusHelp", "```Markdown\n# STATUS\n* Показывает различную статистику бота.\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX + Const.COMMAND + " status```"},
            {"streamlangHelp", "```Markdown\n# STREAMLANG\n* Позволяет фильтровать потоки по тому языку, который транслируется " +
                    "in.  Это поддерживает английское правописание языка, или родное правописание. Должен быть поддержан " +
                    "язык на Twitch, указынный в профиле.\n\n## USAGE: "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND +
                    " streamlang <language>\n" +
                    "* Вы можете использовать написание либо на родном, либо на Английском."
                    + "\n\n## EXAMPLE:  " + Const.COMMAND_PREFIX + Const.COMMAND + " streamlang german```"},
            {"streamTitleEmbed", "Описание"},
            {"streamsHelp", "```Markdown\n# STREAMS\n* Я вышлю вам список активных трансляций в личные сообщения.\n* (ПРИМЕЧАНИЕ: вы скорее всего " +
                    "получите несколько личных сообщений, используя эту команду, в зависимости от того, сколько трансляций " +
                    "отслеживается!)\n\n## USAGE:  "
                    + Const.COMMAND_PREFIX
                    + Const.COMMAND
                    + " streams```"},
            {"totalViewsEmbed", "Всего просмотров"},
            {"twitchCommunities", "Twitch Сообщества"},
            {"twitchAnnounceUpdate", "\n# Обновлен канал объявлений Twitch %s на: %s."},
            {"twitchAnnounceUpdateFail", "\n! Не удалось изменить канал объявления Twitch с %s на: %s."},
            {"twitchChannelAdd", "\n# Добавлен(ы) канал(ы): %s."},
            {"twitchChannelAddFail", "\n# Не удалось добавить канал(ы): %s."},
            {"twitchChannelAnnounce", "\n# Он(и) будут объявлен(ы) в: #%s."},
            {"twitchChannelGameFilter", "\n# Он(и) будут объявлен(ы) только тогда, когда они играют: %s."},
            {"twitchChannelNotFound", "\n# Канал(ы) не найден(ы): %s"},
            {"twitchChannelRemove", "\n# Удален(ы) канал(ы): %s."},
            {"twitchChannelRemoveFail", "\n! Не удалось удалить канал(ы): %s."},
            {"twitchChannelTitleFilter", "\n# Он(и) будут объявлен(ы) только тогда, когда в названии есть слово(а): %s."},
            {"twitchCommunityAdd", "\n# Добавлена(ы) сообщество(а): %s."},
            {"twitchCommunityAddFail", "\n# Не удалось добавить сообщество(а): %s."},
            {"twitchCommunityAnnounce", "\n# Сообщество(а) будет(ут) объявляться в: #%s."},
            {"twitchCommunityNotFound", "\n# Сообщество(а) не найдены на Twitch: %s."},
            {"twitchCommunityRemove", "\n# Сообщество(а) удалено(ы): %s."},
            {"twitchCommunityRemoveFail", "\n# Добавлена(ы) сообщество(ва): %s."},
            {"twitchGameAdd", "\n# Добавлена(ы) игра(ы): %s."},
            {"twitchGameAddFail", "\n# Не удалось добавить игру(ы): %s."},
            {"twitchGameAnnounce", "\n# Игра будет объявляться в: #%s."},
            {"twitchGameFilterAdd", "\n# Добавлен(ы) игровой(ые) фильтр(ы): %s."},
            {"twitchGameFilterAddFail", "\n# Не удалось добавить игровой(ые) фильтр(ы): %s."},
            {"twitchGameFilterRemove", "\n# Удален(ы) игровой(ые) фильтр(ы): %s."},
            {"twitchGameFilterRemoveFail", "\n# Не удалось удалить игровой(ые) фильтр(ы): %s."},
            {"twitchGameRemove", "\n# Удалена(ы) игра(ы): %s."},
            {"twitchGameRemoveFail", "\n# Не удалось удалить игру(ы): %s."},
            {"twitchHelp", "```Markdown\n# TWITCH\n* Добавление и удаление объектов, связанных с Twitch.tv.\n"
                    + "* Примечания:\n\t"
                    + "Чтобы добавить игровые фильтры и фильтры заголовков, вы ДОЛЖНЫ включить скобки.\n\t"
                    + "НЕ ИСПОЛЬЗУЙТЕ полный URL Twitch. Это не будет работать!! Использовать только название канала (www.twitch.tv/channelName)\n\t"
                    + "Имя команды должно быть из URL, а не с отображаемого имени команды. (www.twitch.tv/team/teamName)\n\t"
                    + "Вы можете добавить несколько каналов, команд, игр, сообществ, игр и фильтров заголовков, используя символ | между ними.\n\t"
                    + "Единственные необходимые параметры: имя канала / имя сообщества / название команды / название игры\n\n"
                    + "## Twitch Каналы\n"
                    + "Добавление канала объявлений, игр и фильтров заголовков необязательно."
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel channelName #announcementChannel {gameFilters} [titleFilters]\n\n"
                    + "## Twitch Сообщества (Объявлет ВСЕХ трансляций сообщества)\n"
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community communityName #announcementChannel\n\n"
                    + "## Twitch Игры (Объявлет ВСЕХ трансляций игры)\n"
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game gameName #announcementChannel\n\n"
                    + "## Twitch Команды (Объявлет ВСЕХ трансляций команды)\n"
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team teamName #announcementChannel\n\n"
                    + "## Twitch Игровые фильтры (Глобальный)\n"
                    + "* Примечание: Это влияет на все потоковые объявления для Twitch\n"
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch gfilter {gameName|gameName} #announcementChannel\n\n"
                    + "## Twitch фильтр описания (Глобальный)\n"
                    + "* Примечание: Это влияет на все потоковые объявления для Twitch\n"
                    + "* Образец: " + Const.COMMAND_PREFIX + Const.COMMAND + " twitch tfilter gameName #announcementChannel\n\n"
                    + "* Примеры:\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch channel AgueMort #live-streams {Overwatch|World of "
                    + "Warcraft} (adds a channel to announce in a certain channel and game filters)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch game Overwatch (добавляет игру с глобальным канал объявлений)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch community MMORPG #live-streams (добавляет сообщество с заданный канал объявлений)\n\t"
                    + Const.COMMAND_PREFIX + Const.COMMAND + " twitch team thekingdom #the-kingdom-streamers (добавляет команду с заданным каналом объявлений.)\n\n"
                    + "```"},
            {"twitchTeamAdd", "\n# Команда(ы) добавлена(ы): %s."},
            {"twitchTeamAddFail", "\n# Не удалось добавить команду(ы): %s."},
            {"twitchTeamAnnounce", "\n# Команда(ы) будут объявляться в: #%s."},
            {"twitchTeamNotFound", "\n# Команда(ы) не найдена(ы) на Twitch: %s."},
            {"twitchTeamRemove", "\n# Команда(ы) удалена(ы): %s."},
            {"twitchTeamRemoveFail", "\n# Не удалось удалить команду(ы): %s."},
            {"twitchTeams", "Twitch Команды"},
            {"twitchTitleFilterAdd", "\n# Добавлен(ы) фильтр(ы) описания: %s."},
            {"twitchTitleFilterAddFail", "\n# Не удалось добавить фильтр(ы)описания: %s."},
            {"twitchTitleFilterRemove", "\n# Фильтр(ы) описания удален(ы): %s."},
            {"twitchTitleFilterRemoveFail", "\n# Не удалось удалить фильтр(ы) описания: %s."},
            {"typeOnce", "Вам нужно всего лишь ввести эту часть."},
            {"uniqueChannels", "Уникальные каналы %s"},
            {"uniqueGames", "Уникальные игры %s"},
            {"usePlatform", "Ой! Это старый способ! Используйте команду для конкретной платформы!  Введите `" +
                    Const.COMMAND_PREFIX + Const.COMMAND + " help` для получения дополнительной информации."},
            {"watchThemHere", "Подробнее: "},
            {"wrongCommand", " :thinking: Я не знаю эту команду."}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}