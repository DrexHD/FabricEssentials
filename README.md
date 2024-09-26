# Fabric Essentials

## Commands

| Command                                                                                   | Alias                     | Permission                                     | Default |
|-------------------------------------------------------------------------------------------|---------------------------|------------------------------------------------|---------|
| `/anvil`                                                                                  |                           | `fabric-essentials.command.anvil`              | ✘       |
| `/cartographytable`                                                                       |                           | `fabric-essentials.command.cartographytable`   | ✘       |
| `/enchantment`                                                                            |                           | `fabric-essentials.command.enchantment`        | ✘       |
| `/enderchest`                                                                             | `/ec`                     | `fabric-essentials.command.enderchest`         | ✘       |
| `/grindstone`                                                                             |                           | `fabric-essentials.command.grindstone`         | ✘       |
| `/loom`                                                                                   |                           | `fabric-essentials.command.loom`               | ✘       |
| `/smithing`                                                                               |                           | `fabric-essentials.command.smithing`           | ✘       |
| `/stonecutter`                                                                            |                           | `fabric-essentials.command.stonecutter`        | ✘       |
| `/workbench`                                                                              | `/craft`, `/wb`           | `fabric-essentials.command.workbench`          | ✘       |
| `/deletehome [<home>] [<player>]`                                                         | `/delhome`, `/removehome` | `fabric-essentials.command.deletehome`         | ✔       |
| `/home [<home>] [<player>]`                                                               |                           | `fabric-essentials.command.home`               | ✔       |
| `/homes [<player>]`                                                                       |                           | `fabric-essentials.command.homes`              | ✔       |
| `/sethome [<home>] [<player>\|-confirm]`                                                  |                           | `fabric-essentials.command.sethome`            | ✔       |
| `/deletewarp <warp>`                                                                      | `/delwarp`                | `fabric-essentials.command.deletewarp`         | ✘       |
| `/warp <warp>`                                                                            |                           | `fabric-essentials.command.warp`               | ✔       |
| `/setwarp <warp>`                                                                         |                           | `fabric-essentials.command.setwarp`            | ✘       |
| `/warps`                                                                                  |                           | `fabric-essentials.command.warps`              | ✔       |
| `/tpa <target>`                                                                           | `/tpr`                    | `fabric-essentials.command.tpa`                | ✔       |
| `/tpahere <target>`                                                                       | `/tprhere`                | `fabric-essentials.command.tpahere`            | ✔       |
| `/tpall`                                                                                  |                           | `fabric-essentials.command.tpall`              | ✘       |
| `/tpaccept <target>`                                                                      |                           | `fabric-essentials.command.tpaccept`           | ✔       |
| `/tpdeny <target>`                                                                        |                           | `fabric-essentials.command.tpdeny`             | ✔       |
| `/back [<target>]`                                                                        |                           | `fabric-essentials.command.back`               | ✘       |
| `/broadcast <message>`                                                                    |                           | `fabric-essentials.command.broadcast`          | ✘       |
| `/commandspy`                                                                             |                           | `fabric-essentials.command.commandspy`         | ✘       |
| `/essentials reload`, `/essentials import <importer>`                                     |                           | `fabric-essentials.command.essentials`         | ✘       |
| `/feed [<player>]`                                                                        |                           | `fabric-essentials.command.feed`               | ✘       |
| `/hat`                                                                                    |                           | `fabric-essentials.command.hat`                | ✘       |
| `/itemedit name (<name>\|clear)`, `/itemedit lore <line> (<lore>\|clear)`                 |                           | `fabric-essentials.command.itemedit`           | ✘       |
| `/heal [<player>]`                                                                        |                           | `fabric-essentials.command.heal`               | ✘       |
| `/glow [<player>]`                                                                        |                           | `fabric-essentials.command.glow`               | ✘       |
| `/ping [<player>]`                                                                        |                           | `fabric-essentials.command.ping`               | ✘       |
| `/signedit <line> (<text>\|clear)`                                                        |                           | `fabric-essentials.command.signedit`           | ✘       |
| `/mods [<mod>]`                                                                           |                           | `fabric-essentials.command.mods`               | ✘       |
| `/fly [<player>]`                                                                         |                           | `fabric-essentials.command.fly`                | ✘       |
| `/flyspeed [<flyspeed>] [<target>]`                                                       |                           | `fabric-essentials.command.flyspeed`           | ✘       |
| `/walkspeed [<walkspeed>] [<target>]`                                                     |                           | `fabric-essentials.command.walkspeed`          | ✘       |
| `/invulnerable [<player>]`                                                                | `/godmode`                | `fabric-essentials.command.invulnerable`       | ✘       |
| `/tellmessage <targets> <message>`                                                        |                           | `fabric-essentials.command.tellmessage`        | ✘       |
| `/message-to-vanilla quicktext <message>`, `/message-to-vanilla simplifiedtext <message>` |                           | `fabric-essentials.command.message-to-vanilla` | ✘       |

## Miscellaneous

### Commandspy

Run `/commandspy` to toggle spying. Any commands run by players will be sent to you.
Any commands listed in `ignoreCommandSpyCommands` config option will not be sent.

### Styled Input

Allows users to use [patboxs text format](https://placeholders.pb4.eu/user/text-format/) in anvils, books, signs, items.
They will require the following permission to do so `fabric-essentials.style.<type>.<tag>`, where `<type>` can be:
`anvil`, `book`, `sign`, `item.name`, `item.lore` and `<tag>` can be any of
the [available tags](https://placeholders.pb4.eu/user/text-format/#list-of-available-tags)!