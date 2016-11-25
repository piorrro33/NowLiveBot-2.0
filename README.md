# Now Live
[![Build Status](https://travis-ci.org/VeteranSoftware/NowLiveBot-2.0.svg?branch=master)](https://travis-ci.org/VeteranSoftware/NowLiveBot-2.0)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/af8f87629fd84849a8789530683231e7)](https://www.codacy.com/app/VeteranSoftware/NowLiveBot-2-0?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=VeteranSoftware/NowLiveBot-2.0&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/58324817eaa74b0049b51498/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58324817eaa74b0049b51498)

> A Discord bot that announces streamers as they go live!

## Supported Platforms
* Twitch

## Planned Platforms
* YouTube
* HitBox

## The Basics

> You MUST be a manager to execute these commands!

### Invite the bot to your server
To add Now Live to your server, [click here](https://discordapp.com/oauth2/authorize?&client_id=240729664035880961&scope=bot&permissions=224256).

> **IMPORTANT:**  Leave the bot permissions as they are!  The bot needs all of those permissions to do its job!  Removing any of those permissions could cause the bot to not work properly in your server!

### Add Additional Managers (optional)
> **NOTE:**  All peeps in your server with Administrator and Manage Server permissions are automagically added as managers!

To add additional managers use the following command:
`-nl add manager @<user>`
Additionally, you can remove managers by typing:
`-nl remove manmager @<user>`

### Add Channels to Monitor
> **NOTE:** Twitch is currently the only supported platform.  I will update this when more platforms are added.

To add channels, type:
`-nl twitch add channel <channel-name>`
Example:  `-nl twitch add channel AgueMort`
> **IMPORTANT:** Only add the channel name.  Adding the full URL of the channel will not work.

If you messed up, you can remove the channel using:
`-nl twitch remove channel <channel-name>`
#### Filters
Filters allow you to only have channels announced when the streamer is playing a specific game.  Filters are global and will apply to all platforms that are implemented in the future!

To add a filter, type:
`-nl add filter <game-name>`
To remove filters, type:
`-nl remove filter <game-name>`

### Add Games, Tags and Teams
> **NOTE:**  Currently Tags and Teams are still being worked on for Twitch.  I expect to have them ready soon.

To add a game *(this will announce EVERY LIVE STREAM for that game!)*, type:
`-nl add game <game-name>`
To remove a game, type:
`-nl remove game <game-name>`

### Change Which Discord Channel the Streams Are Announced In (optional)
> **NOTE:**  You MUST include the hastag (#) in front of the channel name!  This command will not work otherwise!

`-nl move #<channel-name>`

### Enable the Bot
> **NOTE:** The bot is disabled by default!

`-nl enable`

---
## Command List
### Add (Managers only)
> Add game filters, full games, tags and managers

`-nl add filter <game-name>`
`-nl add game <game-name>`
`-nl add manager @<user>`
`-nl add tag <tag>`

### Cleanup (Managers only)
> Changes what happenes when the streamer goes offline

`-nl cleanup none` *(default)*
`-nl cleanup edit`
`-nl cleanup delete`

### Compact (Managers only)
> Changes how streams are announces (removes the auto-embedded image)

`-nl compact on`
`-nl compact off` *(default)*

### Disable (Server Owner only)
> Disables the bot

`-nl disable`

### Enable (Server Owner only)
> Enable the bot

`-nl enable`

### List
> List the specified things from the database for the Discord server

`-nl list channels`
`-nl list games`
`-nl list manager`
`-nl list tag`
`-nl list team`

### Move (Managers only)
> Change which channel streams are announced in

`-nl move #<channel-name>`

### Notify (Managers only)
> Changes who gets notified with the announcements

`-nl notify none` *(default)*
`-nl notify here`
`-nl notify everyone`

### Remove (Managers only)
> Remove game filters, full games, tags and managers

`-nl remove filter <game-name>`
`-nl remove game <game-name>`
`-nl remove manager @<user>`
`-nl remove tag <tag>`

### Streams
> Send you a Private Message with a list of all streamers that are actively streaming that your Discord server is following

`-nl streams`

### Twitch
> Adds and removes Twitch channels to monitor

`-nl twitch add channel <channel-name>`
`-nl twitch remove channel <channel-name>`

---
## Now Live has a Discord Server!

[Join us on Discord and chat with the developers!](https://discord.gg/gKbbrFK)

---
## Problems?
If you are having issues with the bot, please do not open an issue here.  Go to the Discord listed above and let the developers know first.  If it's a big enough issue, we'll tell you to open an issue here.  If any issues are opened up here without discussing them first in Discord, they will be automatically closed and not actioned upon.