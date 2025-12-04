# Evil Seagull - 邪恶海鸥

[English](README_EN.md) | 简体中文

一个让 Alex's Mobs 的海鸥变得更加"聪明"的模组！现在它们不仅会从玩家手里偷食物，还学会了从你的精妙背包、AE2 ME 接口、RS 接口，甚至机械动力传送带上顺走物品。

## 功能特性

是的没错，byd海鸥会从你的精妙背包、ME接口、RS接口和传送带里面偷走你的东西了😈

### 核心功能
- 🎒 **从背包偷窃**：海鸥可以从玩家背包里的精妙背包（Sophisticated Backpacks）中偷取物品
- 🏠 **从放置的背包偷窃**：海鸥可以从世界中放置的精妙背包方块中偷取物品
- ⚡ **从 ME 接口偷窃**：海鸥可以从应用能源 2（Applied Energistics 2）的 ME 接口中偷取物品
- 🔌 **从 RS 接口偷窃**：海鸥可以从精致存储（Refined Storage）的接口中偷取物品
- ⚙️ **从传送带偷窃**：海鸥可以从机械动力（Create）的传送带上抓取物品

### 智能物品处理
- **食物类物品**：海鸥偷走后会逃跑，然后像原版一样吃掉
- **非食物类物品**：海鸥偷走后会飞到随机位置（5-15格外），然后丢弃在地上

### 特殊行为
- **传送带悬停**：海鸥会在传送带上方盘旋一段时间，然后俯冲抓取物品
- **烤土豆成就**：当海鸥偷走烤土豆时，会触发特殊成就，让附近的玩家都能获得成就进度

## 配置选项

模组提供了丰富的配置选项，可以在 `config/evilseagull-common.toml` 中调整：

### 精妙背包设置
- `enableStealFromBackpacks`：是否允许从玩家背包中的精妙背包偷窃（默认：true）
- `backpackSearchRange`：背包搜索范围（默认：10，范围：1-50）
- `enableStealFromPlacedBackpacks`：是否允许从放置的背包方块偷窃（默认：true）
- `placedBackpackSearchRange`：放置背包搜索范围（默认：16，范围：1-32）
- `stealAnyItem`：是否允许偷取任意物品，而不仅是食物（默认：false）

### 应用能源设置
- `enableStealFromMEInterface`：是否允许从 ME 接口偷窃（默认：true）
- `meInterfaceSearchRange`：ME 接口搜索范围（默认：16，范围：1-32）
- `powerPerSteal`：每次偷窃消耗的 AE 能量（默认：10.0，范围：0.0-1000.0）
- `stealAnyItem`：是否允许偷取任意物品（默认：false）

### 精致存储设置
- `enableStealFromRSInterface`：是否允许从 RS 接口偷窃（默认：true）
- `rsInterfaceSearchRange`：RS 接口搜索范围（默认：16，范围：1-32）
- `energyPerSteal`：每次偷窃消耗的 FE 能量（默认：10，范围：0-1000）
- `stealAnyItem`：是否允许偷取任意物品（默认：false）

### 机械动力设置
- `enableStealFromBelt`：是否允许从传送带偷窃（默认：true）
- `beltSearchRange`：传送带搜索范围（默认：16，范围：1-32）
- `stealAnyItem`：是否允许偷取任意物品（默认：true）
- `hoverTimeMin/Max`：悬停时间范围（默认：40-80 ticks）
- `dropRangeMin/Max`：非食物物品丢弃距离范围（默认：5-15格）

### 通用设置
- `stealCooldownModifier`：偷窃冷却时间修正（默认：100%，范围：50%-500%）
- `prioritizePlayerInventory`：是否优先从玩家主背包偷窃（默认：true）
- `dropRangeMin/Max`：非食物物品丢弃距离范围（默认：5-15格）

## 依赖模组

**必需：**
- Minecraft 1.20.1
- Forge 47.3.10+
- Alex's Mobs

**可选（用于增强功能）：**
- Sophisticated Backpacks - 启用从背包偷窃功能
- Applied Energistics 2 - 启用从 ME 接口偷窃功能
- Refined Storage - 启用从 RS 接口偷窃功能
- Create - 启用从传送带偷窃功能

## 安装方法

1. 确保已安装 Minecraft 1.20.1 和 Forge 47.3.10 或更高版本
2. 安装 Alex's Mobs 模组
3. 将本模组放入 `mods` 文件夹
4. （可选）安装 Sophisticated Backpacks、Applied Energistics 2、Refined Storage 和/或 Create
5. 启动游戏

## 工作原理

模组通过 Mixin 技术扩展了 Alex's Mobs 中海鸥的偷窃行为：

1. **目标检测**：海鸥会在16格范围内搜索有效目标（背包、ME接口、RS接口、传送带）
2. **智能选择**：海鸥会选择距离最近且包含有效物品的目标
3. **偷窃行为**：海鸥会飞向目标，偷取物品后根据物品类型决定后续行为
4. **冷却机制**：偷窃成功后会进入冷却时间（约75-150秒），防止频繁偷窃

## 技术细节

- 使用 Mixin 修改海鸥 AI 行为
- 完全兼容 Alex's Mobs 原版偷窃黑名单配置
- 从 ME 接口偷窃时会消耗 ME 网络能量（AE）
- 从 RS 接口偷窃时会消耗 RS 网络能量（FE）
- 优化的区块级方块实体搜索算法，性能友好
- 支持自定义冷却时间和搜索范围

## 更新日志

### 1.4.0
- 新增：机械动力（Create）传送带支持，海鸥会悬停后抓取物品
- 新增：可配置的"偷取任意物品"模式
- 新增：非食物物品的智能处理（飞走后丢弃）
- 优化：默认搜索范围提升至16格
- 优化：使用区块级搜索算法提升性能

## 许可证

本模组采用 GNU GPL 3.0 许可证发布

## 作者

LirxOwO

## 问题反馈

如果遇到问题或有建议，欢迎在 GitHub 上提交 Issue。

---

**注意**：本模组仅增强游戏趣味性，不会破坏游戏平衡。所有偷窃行为都可以通过配置文件完全自定义或关闭。
