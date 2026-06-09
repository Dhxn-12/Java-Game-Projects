<div align="center">

```
 ____  ____  __   ___  ____     __  __ _  _  __   ____  ____  ____  ____ 
/ ___)(  _ \/ _\ / __)(  __)   (  )(  ( \/ )/ _\ (    \(  __)(  _ \/ ___)
\___ \ ) __/    ( (__  ) _)     )( /    / \    /  ) D ( ) _)  )   /\___ \
(____/(__)  \_/\_/\___)(____)  (__)\_)__)\_/\_/  (____/(____)(__)\_)(____/
```

# 🚀 Space Invaders — Java Edition

**A complete, portfolio-ready arcade game built from scratch in pure Java.**  
No game engines. No external libraries. No image or audio files. Just clean Java.

[![Java](https://img.shields.io/badge/Java-8%2B-orange?style=flat-square&logo=java)](https://adoptium.net/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=flat-square)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Lines](https://img.shields.io/badge/Source%20Lines-3%2C384-green?style=flat-square)]()
[![Files](https://img.shields.io/badge/Java%20Files-19-brightgreen?style=flat-square)]()
[![License](https://img.shields.io/badge/License-Free%20%2F%20Educational-lightgrey?style=flat-square)]()

</div>

---

## 📖 Table of Contents

1. [Overview](#-overview)
2. [Screenshots & Features](#-screenshots--features)
3. [Project Structure](#-project-structure)
4. [Architecture & Design](#-architecture--design)
5. [Gameplay Mechanics](#-gameplay-mechanics)
6. [Controls](#-controls)
7. [Scoring System](#-scoring-system)
8. [Power-Ups](#-power-ups)
9. [Level Progression](#-level-progression)
10. [Technical Deep Dive](#-technical-deep-dive)
11. [Design Patterns](#-design-patterns)
12. [DSA Concepts](#-dsa--algorithms)
13. [Audio System](#-audio-system)
14. [Graphics System](#-graphics-system)
15. [How to Run](#-how-to-run)
16. [Building a JAR](#-building-a-jar)
17. [Extending the Game](#-extending-the-game)
18. [Java Concepts Index](#-java-concepts-index)

---

## 🎯 Overview

This is a **fully functional Space Invaders arcade game** built entirely in Java using Swing, demonstrating a wide range of software engineering skills — from basic OOP to advanced game architecture, design patterns, data structures, procedural graphics, and synthesized audio.

> **Zero external dependencies.** Every pixel is drawn with `Graphics2D`. Every sound is synthesized with `javax.sound.sampled`. High scores are saved to the local filesystem. The entire game runs from a single `javac` + `java` command.

### What makes this special for a portfolio?

| Aspect | What it shows |
|---|---|
| 19 Java files, 3,384 lines | Real project scale, not a toy |
| MVC + Singleton + Factory + Observer | Multiple design patterns in one codebase |
| Procedural sprite generation | Advanced `Graphics2D` mastery |
| Procedural audio synthesis | `javax.sound.sampled` expertise |
| Fixed-timestep game loop | Professional game engineering |
| AABB collision system | Practical algorithm application |
| Block-grid barrier destruction | 2D array manipulation |
| Particle physics system | Real-time simulation |
| File-based high score persistence | I/O and data management |
| State machine (6 states) | Control flow architecture |

---

## ✨ Screenshots & Features

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  SCORE   000420     HI-SCORE  001200       LEVEL  3     LIVES  ▲▲▲         │
├─────────────────────────────────────────────────────────────────────────────┤
│                    ★  ·   ·      ★    ·        ★                           │
│         ·    ★          ·             ★              ·                      │
│                                                                             │
│    👾👾👾👾👾👾👾👾👾👾👾    ← Row 4 (10 pts)                           │
│    👾👾👾👾👾👾👾👾👾👾👾    ← Row 3 (10 pts)                           │
│    👾👾👾👾👾👾👾👾👾👾👾    ← Row 2 (20 pts)                           │
│    👾👾👾👾👾👾👾👾👾👾👾    ← Row 1 (20 pts)                           │
│    👾👾👾👾👾👾👾👾👾👾👾    ← Row 0 (30 pts)                           │
│                                                                             │
│         ╔══╗   ╔══╗   ╔══╗   ╔══╗    ← 4 destructible barriers            │
│                                                                             │
│                      🚀                ← Player ship                       │
│ ─────────────────────────────────────────────────────────────────────────  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Core Features

- 🎮 **Smooth 60 FPS gameplay** via `javax.swing.Timer`
- 👾 **11 × 5 enemy formation** with 3 distinct alien types and 2-frame animation
- 💥 **Pixel-accurate destructible bunkers** — bullets carve real craters
- 🛸 **Boss enemy** every 3rd level with HP bar and animated engine lights
- 🌟 **18-particle explosion system** with physics (gravity + drag)
- 📳 **Screen shake** on hits and boss kills
- ⬆️ **Floating score popups** (+30, +20…) on every kill
- 🔋 **3 power-up types** that drop from enemies at 7% chance
- 💾 **Top 5 high scores** saved to `highscores.dat`
- 🎵 **Procedural background music** that speeds up as enemies die
- 🌌 **Parallax starfield** with 120 stars at varying speeds
- 📋 **Navigable main menu** with keyboard-driven option selection

---

## 📁 Project Structure

```
space-invaders/
│
├── src/
│   │
│   ├── main/                          ← Entry point & rendering
│   │   ├── Game.java                  ← JFrame setup, main() method
│   │   ├── GamePanel.java             ← Core: game loop, render, state machine (692 lines)
│   │   ├── GameLoop.java              ← Fixed-timestep loop reference implementation
│   │   └── MainMenu.java              ← Placeholder (menu embedded in GamePanel)
│   │
│   ├── model/                         ← Pure data objects (no rendering logic)
│   │   ├── Player.java                ← Ship position, lives, power-up timers (163 lines)
│   │   ├── Enemy.java                 ← Alien entity, grid coords, animation (118 lines)
│   │   ├── Bullet.java                ← Projectile (player or enemy owned)
│   │   ├── PowerUp.java               ← Collectible drop with bobbing animation
│   │   ├── Barrier.java               ← Destructible bunker via 8×6 block grid (108 lines)
│   │   └── Particle.java              ← Single explosion particle with physics
│   │
│   ├── controller/                    ← Input & collision
│   │   ├── InputHandler.java          ← KeyAdapter: hold state + single-press consumers
│   │   └── CollisionManager.java      ← AABB detection, event dispatch (165 lines)
│   │
│   ├── service/                       ← Stateful game services
│   │   ├── ScoreManager.java          ← Singleton: live score + high score
│   │   ├── SoundManager.java          ← Singleton: audio synthesis thread pool (151 lines)
│   │   ├── LevelManager.java          ← Swarm movement, enemy shooting, EnemyFactory
│   │   └── ParticleSystem.java        ← Particle pool + screen shake controller
│   │
│   └── util/                          ← Stateless utilities
│       ├── Constants.java             ← Single source of truth for all numbers (94 lines)
│       ├── AssetLoader.java           ← Procedural sprite cache via Graphics2D (310 lines)
│       └── SaveLoadManager.java       ← File-based high score persistence
│
├── run.sh                             ← Linux/macOS build + run script
├── run.bat                            ← Windows build + run script
└── README.md                          ← This file
```

---

## 🏗 Architecture & Design

### Game State Machine

The entire game is driven by a 6-state finite state machine inside `GamePanel`:

```
                    ┌─────────────────────────────────────┐
                    │                                     │
                    ▼                                     │
              ┌──────────┐   ENTER on "Controls"   ┌──────────────┐
              │   MENU   │ ───────────────────────► │   CONTROLS   │
              └──────────┘                          └──────────────┘
                    │                                     │
              ENTER on                              ENTER / ESC
              "Start Game"                               │
                    │                                     ▼
                    ▼                              back to MENU
              ┌──────────┐  ◄──── P / ESC ────  ┌──────────────┐
              │ RUNNING  │ ────── P / ESC ────►  │    PAUSED    │
              └──────────┘                       └──────────────┘
                    │
           ┌────────┴────────┐
           │                 │
     All enemies dead    0 lives left
           │                 │
           ▼                 ▼
    ┌─────────────┐    ┌───────────┐
    │  LEVEL_     │    │ GAME_OVER │
    │  TRANSITION │    └───────────┘
    └─────────────┘          │
           │            ENTER / SPACE
     (2s delay)              │
           │                 ▼
           └──────────► back to MENU
```

### Package Dependency Map

```
util ◄────── model ◄────── controller
  ▲               ▲              │
  │               │              │
  └────── service ◄─── main ────┘
              │           │
              └───────────┘
```

- `util` depends on nothing
- `model` depends only on `util`
- `controller` depends on `model`, `service`, `util`
- `service` depends on `model`, `util`
- `main` orchestrates everything

---

## 🕹 Gameplay Mechanics

### Enemy Swarm Behavior

The 11×5 enemy grid moves as a single unit managed by `LevelManager.updateSwarm()`:

1. Each frame, all alive enemies shift horizontally by `speed × direction`
2. When any enemy reaches the screen edge, **the entire formation reverses direction and drops** `18px`
3. Speed has two components that stack:
   - **Base speed** increases by `0.3px/frame` per level
   - **Kill bonus** adds `+0.04px/frame` per enemy killed (formation thins → speeds up)
4. Animation alternates between frame 0 and frame 1 every 30 game ticks

### Enemy Shooting

- Each frame, `LevelManager.getShootingEnemy()` is called
- It collects the **lowest alive enemy per column** (the ones that can actually shoot)
- One random shooter fires at most once per interval (starts at `1200ms`, shrinks by `80ms/level`, minimum `300ms`)
- Enemy bullets travel in a **zigzag pattern** for visual variety

### Destructible Barriers

Each of the 4 barriers is an **8×6 grid of 8×8px blocks**, each with `12 HP`:

```
  ████████
  ████████
  ████████
  ████████
  ██    ██   ← center arch cut out
  ██    ██
```

- Player and enemy bullets both damage barriers
- On impact, the hit block loses 3 HP; its neighbors lose 1 HP each (crater spreading)
- Blocks are rendered with alpha proportional to remaining HP (fade as damaged)

### Player Invincibility Frames

After being hit, the player has **2 seconds of invincibility** during which:
- The ship blinks (alternates visible/invisible every 100ms)
- Enemy bullets pass through
- A new hit cannot be registered

---

## 🎮 Controls

### Main Menu

| Key | Action |
|---|---|
| `↑` / `W` | Move selector up |
| `↓` / `S` | Move selector down |
| `ENTER` | Confirm selection |
| `M` | Toggle mute |

### In-Game

| Key | Action |
|---|---|
| `A` / `←` | Move ship left |
| `D` / `→` | Move ship right |
| `SPACE` | Shoot (hold for continuous fire) |
| `P` / `ESC` | Pause / Resume |
| `R` | Restart game immediately |
| `M` | Toggle mute |

### Pause Screen

| Key | Action |
|---|---|
| `P` / `ESC` | Resume |

### Game Over Screen

| Key | Action |
|---|---|
| `ENTER` or `SPACE` | Return to main menu |

---

## 🏆 Scoring System

Points are awarded based on which **row** the killed enemy occupied. Bottom rows are worth more because they're harder to reach (formation drops toward you):

| Enemy Row | Position | Points |
|---|---|---|
| Row 0 | Bottom | **30 pts** |
| Row 1 | Second from bottom | **20 pts** |
| Row 2 | Middle | **20 pts** |
| Row 3 | Second from top | **10 pts** |
| Row 4 | Top | **10 pts** |
| **Boss** | — | **200 pts** |

**Maximum possible score per level** (all 55 enemies):  
`(11 × 30) + (11 × 20) + (11 × 20) + (11 × 10) + (11 × 10) = 990 pts` + boss waves

The **top 5 high scores** are saved to `highscores.dat` in the game directory and displayed on the Game Over screen.

---

## ⚡ Power-Ups

Power-ups drop from killed enemies with a **7% chance per kill**. They fall at `2px/frame` with a bobbing animation and glow aura.

| Icon | Name | Effect | Duration |
|---|---|---|---|
| **R** (Blue) | Rapid Fire | Shoot cooldown drops from `350ms` → `120ms` | 8 seconds |
| **S** (Green) | Shield | Next hit absorbed; visible bubble around ship | 8 seconds |
| **♥** (Gold) | Extra Life | +1 life (max 5) | Permanent |

> **Shield interaction:** A shield absorbs exactly one hit, then disappears — even against boss bullets.

> **Rapid Fire stacking:** Collecting Rapid Fire while already active resets the timer to 8 seconds.

---

## 📈 Level Progression

| Level | Enemy Rows | Base Speed | Shoot Interval | Boss? |
|---|---|---|---|---|
| 1 | 3 | 1.20 px/frame | 1200ms | No |
| 2 | 3 | 1.50 px/frame | 1120ms | No |
| 3 | 4 | 1.80 px/frame | 1040ms | **Yes** |
| 4 | 4 | 2.10 px/frame | 960ms | No |
| 5 | 5 | 2.40 px/frame | 880ms | No |
| 6 | 5 | 2.70 px/frame | 800ms | **Yes** |
| 7+ | 5 | … | … (min 300ms) | Every 3rd |

> **Level Transition:** A 2-second "LEVEL N" splash is shown between levels. The player respawns centered with invincibility frames. Barriers carry over in their damaged state.

---

## 🔧 Technical Deep Dive

### Game Loop

The game uses `javax.swing.Timer` firing every **16ms (~62 FPS)**. The `GamePanel` class implements `ActionListener`, and `actionPerformed()` calls `update()` then `repaint()` every tick.

A reference **fixed-timestep loop** with catch-up logic is also provided in `GameLoop.java`:

```java
// Fixed-timestep loop (GameLoop.java)
while (running) {
    long now   = System.nanoTime();
    long delta = now - lastTime;
    lastTime   = now;
    lag       += delta;

    int steps = 0;
    while (lag >= frameNs && steps < 5) {  // max 5 catch-up frames
        callback.tick();
        lag  -= frameNs;
        steps++;
    }
    Thread.sleep(sleepNs / 1_000_000);     // sleep remainder of frame
}
```

### Double Buffering

Swing's `JPanel` with `paintComponent` provides automatic **double buffering** — the scene is drawn to an off-screen buffer, then swapped to screen in one operation, preventing tearing.

### Collision Detection (AABB)

All collisions use **Axis-Aligned Bounding Box** intersection via `java.awt.Rectangle.intersects()`:

```java
// Example: player bullet vs enemy
if (enemy.getBounds().intersects(bullet.getBounds())) {
    enemy.hit(1);
    bullet.deactivate();
}
```

Bounding boxes are **inset** by a few pixels from the sprite edge for a fair "feels right" hitbox — not pixel-perfect, but visually satisfying.

**Collision priority** (checked in order each frame):
1. Player bullets → Boss
2. Player bullets → Enemies
3. Player bullets → Barriers
4. Enemy bullets → Barriers
5. Enemy bullets → Player
6. Power-ups → Player
7. Enemies → Ground (instant death)

### Screen Shake

`ParticleSystem` tracks a `shakeUntil` timestamp and `shakeMag`. Each frame while shaking:

```java
shakeX = (int)((rand.nextFloat() * 2 - 1) * shakeMag);
shakeY = (int)((rand.nextFloat() * 2 - 1) * shakeMag);
```

`GamePanel.paintComponent()` applies this as a `Graphics2D.translate()` before drawing anything, shaking the entire scene.

---

## 🧩 Design Patterns

### 1. Singleton — `ScoreManager` & `SoundManager`

Both services use the classic lazy-initialized Singleton:

```java
public final class ScoreManager {
    private static ScoreManager instance;

    private ScoreManager() { /* private constructor */ }

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }
}
```

This ensures exactly one score tracker and one audio engine exist regardless of how many objects reference them.

### 2. Factory — `LevelManager.EnemyFactory`

A static inner Factory class creates enemy formations tailored to the current level:

```java
public static class EnemyFactory {
    public static List<Enemy> createWave(int level) {
        // Rows increase from 3 (level 1) up to max 5
        int rows = Math.min(ENEMY_ROWS, 3 + (level - 1) / 2);
        // ... populate grid
    }

    public static Enemy createBoss() {
        return new Enemy(-BOSS_WIDTH, BOSS_Y, BOSS_HP, Enemy.Type.BOSS);
    }
}
```

Adding new enemy types, formations, or mini-bosses only requires extending this class.

### 3. MVC-like Separation

| Layer | Classes | Role |
|---|---|---|
| **Model** | `Player`, `Enemy`, `Bullet`, `Barrier`, `PowerUp`, `Particle` | Data only — no rendering |
| **View** | `GamePanel.paintComponent()`, `drawMenu()`, `drawGame()`, `drawHUD()` | Rendering only — no logic |
| **Controller** | `InputHandler`, `CollisionManager` | Input processing and game-rule enforcement |

### 4. Observer / Callback — `GameEventListener`

`CollisionManager` fires events without knowing anything about `GamePanel`:

```java
public interface GameEventListener {
    void onEnemyKilled(int x, int y, int scoreValue);
    void onPlayerHit();
    void onBossKilled(int x, int y);
    void onPowerUpCollected(int type);
    void onAllEnemiesCleared();
}
```

`GamePanel` implements this interface and reacts — spawning particles, updating score, transitioning state — without `CollisionManager` being coupled to any of that.

---

## 📊 DSA & Algorithms

### Data Structures Used

| Structure | Where | Purpose |
|---|---|---|
| `ArrayList<Enemy>` | `GamePanel` | Dynamic enemy list; stream-filtered each frame |
| `ArrayList<Bullet>` | `GamePanel` | Active projectiles; `removeIf` each update |
| `ArrayList<Particle>` | `ParticleSystem` | Particle pool; dead particles pruned each frame |
| `ArrayList<Barrier>` | `GamePanel` | Bunker list |
| `int[][] hp` | `Barrier` | 2D grid of block HP values |
| `boolean[] keys` | `InputHandler` | O(1) lookup keycode → held state |
| `HashMap<String, BufferedImage>` | `AssetLoader` | Sprite cache — generate once, reuse |

### Algorithms

**Per-column bottom-enemy selection** (for enemy shooting):
```java
// O(enemies) — streams filter then find max row per column
for (int col = 0; col < ENEMY_COLS; col++) {
    enemies.stream()
           .filter(e -> e.isAlive() && e.getCol() == col)
           .max(Comparator.comparingInt(Enemy::getRow))
           .ifPresent(shooters::add);
}
```

**Particle physics** per frame:
```
position += velocity
velocity.y += 0.12f   // gravity
velocity.x *= 0.97f   // horizontal drag
size       *= 0.97f   // shrink over time
life       -= decay   // fade out
```

**Barrier crater spreading** on bullet impact:
```
hit_block.hp  -= 3
neighbors.hp  -= 1   // 4-directional chip
```

---

## 🔊 Audio System

`SoundManager` is a **Singleton** that synthesizes all audio at runtime using `javax.sound.sampled`. No audio files required. A fixed thread pool of 4 daemon threads handles playback to avoid blocking the game loop.

### Sound Synthesis

All sounds are generated as raw 8-bit PCM at 44,100 Hz sample rate:

| Sound | Synthesis Method | Formula |
|---|---|---|
| **Shoot** | Descending sine chirp | `sin(2π × (1200−600t/0.07) × t) × e^(−25t)` |
| **Explosion** | Noise burst | `random() × e^(−12t)` |
| **Power-up** | Ascending sine sweep | `sin(2π × (400+1200t/0.3) × t) × e^(−5t)` |
| **BG music** | Low-frequency pulse | 4-note sequence at C3, A2, G2, B2 |

### Background Music

The background heartbeat plays a 4-note bass sequence (C3 → A2 → G2 → B2) on a loop. The tempo starts at **600ms** between beats and decreases as enemies are killed:

```java
// Called on every enemy kill
soundManager.setTempo(Math.max(100, 600 - aliveCount * 4));
```

This creates authentic growing tension as the invasion thins.

---

## 🎨 Graphics System

`AssetLoader` draws **all sprites procedurally** using `java.awt.Graphics2D`. Every sprite is generated once, cached in a `HashMap<String, BufferedImage>`, and reused.

### Sprite Gallery

| Sprite | Technique |
|---|---|
| **Player ship** | Polygon hull + gradient cockpit oval + thrust flame `GradientPaint` |
| **Crab enemy** (rows 0–1) | Rounded rect body + oval eyes + leg rects (2-frame animation) |
| **Squid enemy** (rows 2–3) | Oval body + tentacle rects + single eye |
| **UFO enemy** (row 4) | Layered ovals + dome + animated porthole lights |
| **Boss** | Wide saucer oval + dome + rotating engine lights (8-frame animation) |
| **Player bullet** | Cyan gradient rounded rect with white tip |
| **Enemy bullet** | Red gradient polyline zigzag |
| **Power-ups** | Filled oval + monospaced label + glow stroke |
| **Barrier blocks** | 8×8 solid block with border, alpha from HP |

### Rendering Pipeline (per frame)

```
paintComponent()
│
├── Graphics2D.translate(shakeX, shakeY)    ← screen shake offset
├── drawBackground()                         ← gradient + 120 stars
│
└── switch(state)
    ├── MENU      → drawMenu()
    ├── RUNNING   → drawGame() + drawHUD()
    ├── PAUSED    → drawGame() + drawHUD() + drawPause() overlay
    ├── TRANSITION→ drawGame() + drawTransition() overlay
    └── GAME_OVER → drawGame() + drawGameOver() overlay
```

---

## 🚀 How to Run

### Prerequisites

You need a **Java Development Kit (JDK) version 8 or newer** — not just the JRE.

| OS | Install Command |
|---|---|
| **Ubuntu / Debian** | `sudo apt install default-jdk` |
| **macOS (Homebrew)** | `brew install openjdk` |
| **Windows** | Download from [adoptium.net](https://adoptium.net/) |
| **Fedora / RHEL** | `sudo dnf install java-latest-openjdk-devel` |

Verify your install: `javac -version` should print a version number.

### Option 1 — Shell Script (Linux / macOS)

```bash
# From the space-invaders/ directory:
chmod +x run.sh
./run.sh
```

### Option 2 — Batch Script (Windows)

```
Double-click run.bat
```
Or in PowerShell / Command Prompt:
```
.\run.bat
```

### Option 3 — Manual Commands

```bash
# From space-invaders/ directory

# 1. Create output directory
mkdir out

# 2. Compile all Java files
find src -name "*.java" | xargs javac -d out -sourcepath src

# 3. Run the game
java -cp out main.Game
```

**Windows PowerShell equivalent:**
```powershell
New-Item -ItemType Directory -Force out
Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Out-File sources.txt
javac -d out -sourcepath src "@sources.txt"
java -cp out main.Game
```

---

## 📦 Building a JAR

To distribute the game as a single executable JAR:

```bash
# Compile first (see above), then:
echo "Main-Class: main.Game" > manifest.txt
jar cfm SpaceInvaders.jar manifest.txt -C out .

# Run the JAR:
java -jar SpaceInvaders.jar
```

---

## 🛠 Extending the Game

The clean package separation makes extension straightforward:

### Add a New Enemy Type

1. Add a new `Type` constant to `Enemy.java`
2. Add a draw case to `AssetLoader.drawEnemy()`
3. Update `EnemyFactory.createWave()` to include the new type at certain levels

### Add a New Power-Up

1. Add a type constant and label to `AssetLoader.drawPowerUp()`
2. Add a case in `GamePanel.onPowerUpCollected()`
3. Add the corresponding method to `Player.java`

### Add a New Level Mechanic

1. Check `LevelManager.getLevel()` in `updateSwarm()` and add conditional behavior
2. Example: diagonal movement at level 10, or a circular orbit pattern

### Switch to JavaFX

1. Replace `JPanel` with `javafx.scene.canvas.Canvas`
2. Replace `javax.swing.Timer` with `AnimationTimer`
3. Replace `KeyAdapter` with `scene.setOnKeyPressed()`
4. All model/service/util/controller code is untouched

### Add Networked Leaderboard

1. Extend `SaveLoadManager` with HTTP POST/GET using `java.net.HttpURLConnection`
2. Serialize scores as JSON manually or with a lightweight library

---

## 📚 Java Concepts Index

A reference for educators and students — every major Java concept this project demonstrates:

| Concept | Where to find it |
|---|---|
| `abstract` classes | `Enemy` (type hierarchy) |
| `interface` | `CollisionManager.GameEventListener`, `GameLoop.TickCallback` |
| Generics | `List<Enemy>`, `Map<String, BufferedImage>` |
| `enum` | `GameState`, `Bullet.Owner`, `Enemy.Type` |
| `final` class | `Constants`, `AssetLoader`, `SaveLoadManager`, `ScoreManager` |
| Singleton pattern | `ScoreManager.getInstance()`, `SoundManager.getInstance()` |
| Factory pattern | `LevelManager.EnemyFactory` |
| Inner classes | `EnemyFactory` (static inner), `ScorePopup` (private inner) |
| Lambda expressions | `bullets.removeIf(b -> ...)`, `stream().filter(...)` |
| Stream API | `enemies.stream().filter().max()`, `Collectors.toList()` |
| `ArrayList` | Enemies, bullets, particles, barriers, power-ups |
| 2D arrays | `Barrier.hp[][]` block grid |
| `HashMap` | `AssetLoader` sprite cache |
| File I/O | `SaveLoadManager` (BufferedReader, PrintWriter) |
| Threading | `SoundManager` thread pool, `GameLoop` daemon thread |
| `ExecutorService` | `SoundManager.pool` — 4-thread audio playback pool |
| `volatile` | `SoundManager.bgRunning`, `InputHandler` flag fields |
| `Graphics2D` | Every sprite in `AssetLoader` |
| `GradientPaint` | Player thrust flame, bullet trails |
| `AlphaComposite` | Barrier block HP fading |
| `javax.sound.sampled` | Entire `SoundManager` audio pipeline |
| `javax.swing.Timer` | Main game loop in `GamePanel` |
| `KeyAdapter` | `InputHandler` |
| `JFrame` / `JPanel` | `Game.java`, `GamePanel.java` |
| `Rectangle.intersects()` | `CollisionManager` AABB collision |
| `System.currentTimeMillis()` | Invincibility timers, power-up timers, shoot cooldown |
| `System.nanoTime()` | `GameLoop` high-resolution timing |
| `Math.sin()` | Shield pulse, power-up glow, audio waveforms |
| `Random` | Particle scatter, enemy shooter selection, power-up drops |
| State machine | `GamePanel.GameState` enum + switch |
| Observer pattern | `CollisionManager.GameEventListener` interface |
| `computeIfAbsent()` | `AssetLoader` cache population |

---

## 📝 License

Free to use, modify, and distribute for educational and portfolio purposes.  
Credit appreciated but not required.

---

<div align="center">

**Built with ☕ Java — no engines, no shortcuts, no compromises.**

*"The best way to learn software architecture is to build something that actually runs."*

</div>
