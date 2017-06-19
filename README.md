# MunchausenFireworks

Munchausen Fireworks is a plugin for Minecraft (craftbukkit/spigot) that could bring a lot fun to your server.

The plugin, according to it's name, dedicated to my favorite (in childhood) book - **The Adventures of Baron Munchausen**. If you read this book you can remember the story about Baron Munchausen and the cannon ball. I remember this story and invent a new item - Munchausen Firework. This firework is a new item that allows players to ride the firework rocket and force other players (and mobs) to fly on it.

## Video
[![Recipe](http://img.youtube.com/vi/0nbcbQ56eNg/mqdefault.jpg)](https://youtu.be/0nbcbQ56eNg)

## Features
* New item: Munchausen Firework and new recipes (to create a Munchausen Firework and to power up it)
* Ride firework rocket and fly!
* You can control your flying direction!
* Use firework to bring made any mob flying!
* Use air bombs to destroy your enemies!
* Video demonstration

## How to use Munchausen Fireworks
* Craft the Munchausen Firework rocket
* Power up rocket if necessary
* Run, sprint and use your firework
* Yahoooo!!!!!

## Recipes
### Creating a Munchausen Firework
You need a Firework Rocket. Place it workbench and combine it with leash.

![Recipe](https://media-elerium.cursecdn.com/attachments/116/898/mf-craft-1.png)

### Increase power of Munchausen Firework
Place Munchausen Firework in workbench and add some ( 1 - 8 ) gunpowder. Every gunpowder will increase power of rocket by one.
 
![Recipe](https://media-elerium.cursecdn.com/attachments/116/899/mf-craft-2.png)

### Creating Carrier Firework
You need a Firework Rocket. Place it workbench and combine it with leash and furnace.

![Recipe](https://media-elerium.cursecdn.com/attachments/116/901/carrier-recipe.png)


### Creating Air bombs
You need TNT, stone button and flint-n-steel:
![Recipe](https://media-elerium.cursecdn.com/attachments/116/903/munchausen_airbomb.png)


## Commands
* `/munchausen help` — Mmm....
* `/munchausen give` — shows virtual inventory with Munchausen Fireworks (and fuel)
* `/munchausen reload` — reload plugin configuration
 

Command `/munchausen give` will open this inventory

![Recipe](https://media-elerium.cursecdn.com/attachments/116/902/Munchausen-give-command.png)


## Permissions
* `munchausen.give` — allows to use give and help commands
* `munchausen.config` — allows to use reload command
* `munchausen.firework` — allows to use Munchausen Fireworks
* `munchausen.carrier` — allows to use Carrier Fireworks
* `munchausen.firework.launchmob` — allows to launch mobs with Munchausen Fireworks
* `munchausen.firework.launchplayer` — allows to launch other players
* `munchausen.fireworks.craft` — allows to use crafting recipes
* `munchausen.bomberman` — allows to use Air bombs

## Configuration (config.yml)
```
general:
  check-updates: true # true - to enable update checker
  language: english # language (russian - included)
  language-save: false # save language file
firework:
  sprint-to-fly: true # Need sprinting to fly at firework
  item-craft-enable: true # Use crafting recipes
  decrease-takeoff-speed: true # if set to true the takeoff speed is lower than usual
  munchausen:
    item: '&6Munchausen_Firework$FIREWORK'   # Munchausen Firework item
    maxPower: 50 # Max power of rocket that could be reached using power-up recipe
    take-off-modifier: 0.8  # 1 - normal speed (100%), 0.8 = 80% of normal speed
    item-remove: true  # false - to infinitive fireworks
    randomize-firework: false # Ignore firework settings and every time create a random firework effect
  carrier:
    item: '&cCarrier_Firework$FIREWORK' # Carrier Firework item
    fuel-item: SULPHUR # Carrier fuel item
    take-off-modifier: 0.45   # 1 - normal speed (100%), 0.45 = 45% of normal speed
    base-time: 1.0   # time between reloads 
    explode-effect-on-reload: true # play small explosion effect when carrier is reloading
    need-fuel-to-fly: true # set to false if you need do disable fuel usage
fall-from-firework:
  damage-modifier: 100.0  # Fall-damage modifier (in percents). Set 0 to disable damage; 100 - no change; more than 100 - increased damage
  effects: # List of potion effects. Set "effects: []" to disable effects
  - DAMAGE_RESISTANCE time:3s level:3
```

## Update checker
Munchausen Fireworks include a update checker that use your server internet connection. Update checker will every hour check the dev.bukkit.org to find new released version of plugin and you can easy disable it: just set parameter "version-check" to "false" in config.yml.