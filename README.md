# Fabric Essentials

## Commands
This mod adds a lot of commands to Minecraft. Check out [COMMANDS.md](https://github.com/DrexHD/FabricEssentials/blob/main/COMMANDS.md) for a full list.

## Config
```json5
{
  "homes": {
    // How many homes each player should have independent of permissions
    "defaultLimit": 3,
    // A list of configurations to increase the home limit. The system will pick the highest value from all entries with
    // stacking disabled and add all values with stacking enabled
    "homesLimits": [
      {
        // The permission that a player needs to get this 'fabric-essentials.command.sethome.limit.<permission>'
        "permission": "vip",
        "limit": 5,
        "stacks": false
      }
    ]
  },
  "tpa": {
    // How many seconds teleport requests will stay active for
    "expiry": 30
  },
  "commands": {
    // Whether player argument commands allow partial names (only the start of a name) to target a player
    "allowPartialNames": false
  },
  "teleportation": {
    "waitingPeriod": {
      // How many seconds players need to wait before being teleported
      "period": 2,
      "cancellation": {
        // Whether damage cancels teleportation
        "damage": false,
        // How far players can move before teleportation is cancelled
        "maxMoveDistance": -1
      }
    },
    // How many locations can be saved for the back command
    "savedBackLocations": 32,
    // Enabling this will create a back location when running /back, effectively allowing you to toggle between your 
    // most recent and your current location using /back
    "saveBackCommandLocation": false
  },
  "itemEdit": {
    // The maximum name length for the item edit command
    "name": {
      "maxLength": 50,
      "experienceLevelCost": 1
    },
    // The maximum lore length for the item edit command
    "lore": {
      "maxLength": 50,
      "maxLines": 5,
      "experienceLevelCost": 1
    }
  },
  "ignoreCommandSpyCommands": [
    "me",
    "msg",
    "teammsg",
    "tell",
    "w"
  ]
}
```

## Messages
All messages in this mod are completely customizable and translatable. If you want to change them or add translations 
(you can also PR these so they can be included in this project) navigate to `./config/messages/fabric-essentials/<language_code>.json`.
For a list of all available language codes check out [the wiki](https://minecraft.wiki/w/Language).

## Miscellaneous

### Commandspy

Run `/commandspy` to toggle spying. Any commands run by players will be sent to you.
Any commands listed in `ignoreCommandSpyCommands` config option will not be sent.

### Styled Input

Allows users to use [patboxs text format](https://placeholders.pb4.eu/user/text-format/) in anvils, books, signs, items.
They will require the following permission to do so `fabric-essentials.style.<type>.<tag>`, where `<type>` can be:
`anvil`, `book`, `sign`, `item.name`, `item.lore` and `<tag>` can be any of
the [available tags](https://placeholders.pb4.eu/user/text-format/#list-of-available-tags)!

## Import mod data

This mod provides data importers for data from some other mods to help you transition. 
**Before starting the import process, make sure to take a backup!**
There is currently support for [`kiloessentials`](https://github.com/DrexHD/KiloEssentials) and [`essential_commands`](https://github.com/John-Paul-R/Essential-Commands).
If you wish to import data from any of these mods:
1. Shutdown your server
2. Remove the old mod
3. Install this mod
4. Start the server
5. Run `/essentials import <id>` from console to start the import process.