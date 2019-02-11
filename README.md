# Tournament

This plugin can generate several types of tournament brackets and run the tournaments automatically with minimal setup.

## Setup Spawn

Players a teleported back to this location at the end of the tournament.

### Command Method

Use the command `/tournament config spawn_location` to set the spawn to your current location.

### Config Method

```yml
spawn_location:
  world: spawn
  x: 0
  y: 0
  z: 0
  yaw: 0
  pitch: 0
```

## Setup Spectator Spawn

Players are telported to this location at the start of the tournament and after they play a match.

### Command Method

Use the command `/tournament config spectator_spawn_location` to set the spectator spawn to your current location.

### Config Method

```yml
spectator_spawn_location:
  world: spawn
  x: -1545
  y: 99
  z: -356
  yaw: 271
  pitch: 19
```

## Setting up Arenas

### Command Method

Use the command `/tournament config arenas <arena_name> <team_1_spawn_location|team_2_spawn_location>` to save the arena and team spawns to the config.

### Config Method

```yml
arenas:
  '1':
    world: spawn
    team_1_spawn_location:
      x: -1544
      y: 70
      z: -357
      yaw: 224
      pitch: -1
    team_2_spawn_location:
      x: -1496
      y: 71
      z: -405
      yaw: 44
      pitch: 0
```

Make sure to change the world and coordinates to adjust for your arena. Make sure that each arena has a unique name; in this case, the name of the arena is `1`.

## Kit Setup

```yml
kits:
  Iron:
    inventory:
      0:
        material: IRON_SWORD
        enchantments:
          DAMAGE_ALL: 1
    armor:
      helmet:
        material: IRON_HELMET
      chestplate:
        material: IRON_CHESTPLATE
      leggings:
        material: IRON_LEGGINGS
      boots:
        material: IRON_BOOTS
```

## Tournament Types

**SINGLE_ELIMINATION**: Players are placed into a tournament bracket randomly and are eliminated after a loss.

**ROUND_ROBIN**: Each player plays a match against every other player, the player with the most wins wins the tournament.

## Starting a tournament

Use the command `/tournament start <type> <kit>`