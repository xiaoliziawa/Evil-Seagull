# Evil Seagull

English | [简体中文](README.md)

A mod that makes seagulls from Alex's Mobs even "smarter"! Now they not only steal food from your hands, but also learned to snatch items from your Sophisticated Backpacks, AE2 ME Interfaces, RS Interfaces, and even Create conveyor belts.

## Features

That's right, those pesky seagulls will now steal your stuff from Sophisticated Backpacks, ME Interfaces, RS Interfaces, and conveyor belts!

### Core Features
- 🎒 **Steal from Backpacks**: Seagulls can steal items from Sophisticated Backpacks in player inventory
- 🏠 **Steal from Placed Backpacks**: Seagulls can steal items from Sophisticated Backpack blocks placed in the world
- ⚡ **Steal from ME Interface**: Seagulls can steal items from Applied Energistics 2 ME Interfaces
- 🔌 **Steal from RS Interface**: Seagulls can steal items from Refined Storage Interfaces
- ⚙️ **Steal from Conveyor Belts**: Seagulls can grab items from Create mod conveyor belts

### Smart Item Handling
- **Food Items**: Seagulls flee after stealing, then eat the food like vanilla behavior
- **Non-Food Items**: Seagulls fly to a random location (5-15 blocks away) and drop the item on the ground

### Special Behaviors
- **Belt Hovering**: Seagulls will hover in circles above conveyor belts before swooping down to grab items
- **Baked Potato Achievement**: When a seagull steals a baked potato, it triggers a special advancement for all nearby players

## Configuration

The mod provides rich configuration options in `config/evilseagull-common.toml`:

### Sophisticated Backpacks Settings
- `enableStealFromBackpacks`: Enable stealing from backpacks in player inventory (default: true)
- `backpackSearchRange`: Backpack search range (default: 10, range: 1-50)
- `enableStealFromPlacedBackpacks`: Enable stealing from placed backpack blocks (default: true)
- `placedBackpackSearchRange`: Placed backpack search range (default: 16, range: 1-32)
- `stealAnyItem`: Allow stealing any item, not just food (default: false)

### Applied Energistics Settings
- `enableStealFromMEInterface`: Enable stealing from ME Interface (default: true)
- `meInterfaceSearchRange`: ME Interface search range (default: 16, range: 1-32)
- `powerPerSteal`: AE energy cost per steal (default: 10.0, range: 0.0-1000.0)
- `stealAnyItem`: Allow stealing any item (default: false)

### Refined Storage Settings
- `enableStealFromRSInterface`: Enable stealing from RS Interface (default: true)
- `rsInterfaceSearchRange`: RS Interface search range (default: 16, range: 1-32)
- `energyPerSteal`: FE energy cost per steal (default: 10, range: 0-1000)
- `stealAnyItem`: Allow stealing any item (default: false)

### Create Settings
- `enableStealFromBelt`: Enable stealing from conveyor belts (default: true)
- `beltSearchRange`: Belt search range (default: 16, range: 1-32)
- `stealAnyItem`: Allow stealing any item (default: true)
- `hoverTimeMin/Max`: Hover duration range (default: 40-80 ticks)
- `dropRangeMin/Max`: Non-food item drop distance range (default: 5-15 blocks)

### General Settings
- `stealCooldownModifier`: Steal cooldown modifier (default: 100%, range: 50%-500%)
- `prioritizePlayerInventory`: Prioritize stealing from player's main inventory (default: true)
- `dropRangeMin/Max`: Non-food item drop distance range (default: 5-15 blocks)

## Dependencies

**Required:**
- Minecraft 1.20.1
- Forge 47.3.10+
- Alex's Mobs

**Optional (for enhanced features):**
- Sophisticated Backpacks - Enables backpack stealing feature
- Applied Energistics 2 - Enables ME Interface stealing feature
- Refined Storage - Enables RS Interface stealing feature
- Create - Enables conveyor belt stealing feature

## Installation

1. Ensure Minecraft 1.20.1 and Forge 47.3.10 or higher are installed
2. Install Alex's Mobs mod
3. Place this mod in the `mods` folder
4. (Optional) Install Sophisticated Backpacks, Applied Energistics 2, Refined Storage, and/or Create
5. Launch the game

## How It Works

The mod uses Mixin technology to extend the seagull's stealing behavior in Alex's Mobs:

1. **Target Detection**: Seagulls search within 16 blocks for valid targets (backpacks, ME interfaces, RS interfaces, conveyor belts)
2. **Smart Selection**: Seagulls choose the nearest target containing valid items
3. **Stealing Behavior**: Seagulls fly to the target, steal items, and behave differently based on item type
4. **Cooldown Mechanism**: After a successful steal, a cooldown (~75-150 seconds) prevents frequent stealing

## Technical Details

- Uses Mixin to modify seagull AI behavior
- Fully compatible with Alex's Mobs original steal blacklist configuration
- Stealing from ME Interface consumes ME network energy (AE)
- Stealing from RS Interface consumes RS network energy (FE)
- Optimized chunk-based block entity search algorithm for better performance
- Supports custom cooldown times and search ranges

## Changelog

### 1.4.0
- Added: Create mod conveyor belt support with unique hovering behavior
- Added: Configurable "steal any item" mode
- Added: Smart handling for non-food items (fly away and drop)
- Improved: Default search range increased to 16 blocks
- Improved: Chunk-based search algorithm for better performance

## License

This mod is released under GNU GPL 3.0 License

## Author

LirxOwO

## Feedback

If you encounter issues or have suggestions, feel free to submit an Issue on GitHub.

---

**Note**: This mod only enhances game fun and does not break game balance. All stealing behaviors can be fully customized or disabled through the configuration file.
