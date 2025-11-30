# Evil Seagull - 邪恶海鸥

一个让 Alex's Mobs 的海鸥变得更加"聪明"的模组！现在它们不仅会从玩家手里偷食物，还学会了从你的精妙背包和 AE2 ME 接口里顺走食物。

## 功能特性

是的没错,byd海鸥会从你的精妙背包和ME接口里面偷走你的食物了😈

### 核心功能
- **从背包偷窃**：海鸥现在可以从玩家背包里的精妙背包（Sophisticated Backpacks）中偷取食物
- **从放置的背包偷窃**：海鸥可以从世界中放置的精妙背包方块中偷取食物
- **从 ME 接口偷窃**：海鸥可以从应用能源 2（Applied Energistics 2）的 ME 接口中偷取食物

### 特殊成就
当海鸥偷走烤土豆时，会触发一个特殊的成就系统，让附近的玩家都能获得成就进度。

## 配置选项

模组提供了丰富的配置选项，可以在 `config/evilseagull-common.toml` 中调整：

### 精妙背包设置
- `enableStealFromBackpacks`：是否允许从玩家背包中的精妙背包偷窃（默认：true）
- `backpackSearchRange`：背包搜索范围（默认：10，范围：1-50）
- `enableStealFromPlacedBackpacks`：是否允许从放置的背包方块偷窃（默认：true）
- `placedBackpackSearchRange`：放置背包搜索范围（默认：8，范围：1-32）

### 应用能源设置
- `enableStealFromMEInterface`：是否允许从 ME 接口偷窃（默认：true）
- `meInterfaceSearchRange`：ME 接口搜索范围（默认：8，范围：1-32）
- `powerPerSteal`：每次偷窃消耗的能量（默认：10.0，范围：0.0-1000.0）

### 通用设置
- `stealCooldownModifier`：偷窃冷却时间修正（默认：100%，范围：50%-500%）
- `prioritizePlayerInventory`：是否优先从玩家主背包偷窃（默认：true）

## 依赖模组

**必需：**
- Minecraft 1.20.1
- Forge 47.3.10+
- Alex's Mobs

**可选（用于增强功能）：**
- Sophisticated Backpacks - 启用从背包偷窃功能
- Applied Energistics 2 - 启用从 ME 接口偷窃功能

## 安装方法

1. 确保已安装 Minecraft 1.20.1 和 Forge 47.3.10 或更高版本
2. 安装 Alex's Mobs 模组
3. 将本模组放入 `mods` 文件夹
4. （可选）安装 Sophisticated Backpacks 和/或 Applied Energistics 2
5. 启动游戏

## 工作原理

模组通过 Mixin 技术扩展了 Alex's Mobs 中海鸥的偷窃行为：

1. **玩家背包检测**：当海鸥靠近玩家时，会检查玩家是否携带精妙背包
2. **世界方块检测**：海鸥会在配置范围内搜索放置的背包方块和 ME 接口
3. **智能选择目标**：海鸥会选择距离最近且包含食物的目标
4. **偷窃行为**：海鸥会飞向目标，偷取食物后快速逃离
5. **冷却机制**：偷窃成功后会进入冷却时间，防止频繁偷窃

## 技术细节

- 使用 Mixin 修改海鸥 AI 行为
- 完全兼容 Alex's Mobs 原版偷窃黑名单配置
- 从 ME 接口偷窃时会消耗 ME 网络能量
- 支持自定义冷却时间和搜索范围

## 许可证

本模组采用 GNU GPL 3.0 许可证发布

## 作者

LirxOwO

## 问题反馈

如果遇到问题或有建议，欢迎在 GitHub 上提交 Issue。

---

**注意**：本模组仅增强游戏趣味性，不会破坏游戏平衡。所有偷窃行为都可以通过配置文件完全自定义或关闭。
