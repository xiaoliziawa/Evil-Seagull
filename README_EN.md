# Evil Seagull

English | [简体中文](README.md)

A mod that makes seagulls from Alex's Mobs even "smarter"! Now they not only steal food from your hands, but also learned to snatch food from your Sophisticated Backpacks, AE2 ME Interfaces, and RS Interfaces.

## Features

That's right, those pesky seagulls will now steal your food from Sophisticated Backpacks, ME Interfaces, and RS Interfaces!

### Core Features
- **Steal from Backpacks**: Seagulls can now steal food from Sophisticated Backpacks in player inventory
- **Steal from Placed Backpacks**: Seagulls can steal food from Sophisticated Backpack blocks placed in the world
- **Steal from ME Interface**: Seagulls can steal food from Applied Energistics 2 ME Interfaces
- **Steal from RS Interface**: Seagulls can steal food from Refined Storage Interfaces

### Special Advancement
When a seagull steals a baked potato, it triggers a special advancement system that grants progress to all nearby players.

## Configuration

The mod provides rich configuration options in `config/evilseagull-common.toml`:

### Sophisticated Backpacks Settings
- `enableStealFromBackpacks`: Enable stealing from backpacks in player inventory (default: true)
- `backpackSearchRange`: Backpack search range (default: 10, range: 1-50)
- `enableStealFromPlacedBackpacks`: Enable stealing from placed backpack blocks (default: true)
- `placedBackpackSearchRange`: Placed backpack search range (default: 8, range: 1-32)

### Applied Energistics Settings
- `enableStealFromMEInterface`: Enable stealing from ME Interface (default: true)
- `meInterfaceSearchRange`: ME Interface search range (default: 8, range: 1-32)
- `powerPerSteal`: AE energy cost per steal (default: 10.0, range: 0.0-1000.0)

### Refined Storage Settings
- `enableStealFromRSInterface`: Enable stealing from RS Interface (default: true)
- `rsInterfaceSearchRange`: RS Interface search range (default: 8, range: 1-32)
- `energyPerSteal`: FE energy cost per steal (default: 10, range: 0-1000)

### General Settings
- `stealCooldownModifier`: Steal cooldown modifier (default: 100%, range: 50%-500%)
- `prioritizePlayerInventory`: Prioritize stealing from player's main inventory (default: true)

## Dependencies

**Required:**
- Minecraft 1.20.1
- Forge 47.3.10+
- Alex's Mobs

**Optional (for enhanced features):**
- Sophisticated Backpacks - Enables backpack stealing feature
- Applied Energistics 2 - Enables ME Interface stealing feature
- Refined Storage - Enables RS Interface stealing feature

## Installation

1. Ensure Minecraft 1.20.1 and Forge 47.3.10 or higher are installed
2. Install Alex's Mobs mod
3. Place this mod in the `mods` folder
4. (Optional) Install Sophisticated Backpacks, Applied Energistics 2, and/or Refined Storage
5. Launch the game

## How It Works

The mod uses Mixin technology to extend the seagull's stealing behavior in Alex's Mobs:

1. **Player Inventory Detection**: When a seagull approaches a player, it checks if they're carrying Sophisticated Backpacks
2. **World Block Detection**: Seagulls search for placed backpack blocks, ME Interfaces, and RS Interfaces within configured range
3. **Smart Target Selection**: Seagulls choose the nearest target containing food
4. **Stealing Behavior**: Seagulls fly to the target, steal food, and quickly escape
5. **Cooldown Mechanism**: After a successful steal, a cooldown prevents frequent stealing

## Technical Details

- Uses Mixin to modify seagull AI behavior
- Fully compatible with Alex's Mobs original steal blacklist configuration
- Stealing from ME Interface consumes ME network energy (AE)
- Stealing from RS Interface consumes RS network energy (FE)
- Supports custom cooldown times and search ranges

## License

This mod is released under GNU GPL 3.0 License

## Author

LirxOwO

## Feedback

If you encounter issues or have suggestions, feel free to submit an Issue on GitHub.

---

**Note**: This mod only enhances game fun and does not break game balance. All stealing behaviors can be fully customized or disabled through the configuration file.
