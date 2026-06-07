<div align="center">

```
██████╗ ██╗███╗   ██╗ ██████╗     ██████╗ ██╗   ██╗███╗   ██╗
██╔══██╗██║████╗  ██║██╔═══██╗    ██╔══██╗██║   ██║████╗  ██║
██║  ██║██║██╔██╗ ██║██║   ██║    ██████╔╝██║   ██║██╔██╗ ██║
██║  ██║██║██║╚██╗██║██║   ██║    ██╔══██╗██║   ██║██║╚██╗██║
██████╔╝██║██║ ╚████║╚██████╔╝    ██║  ██║╚██████╔╝██║ ╚████║
╚═════╝ ╚═╝╚═╝  ╚═══╝ ╚═════╝     ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝
```

# 🦖 Dino Run

**A production-grade Chrome Dinosaur Game clone built entirely in Java Swing**

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)](/)
[![Patterns](https://img.shields.io/badge/Design%20Patterns-6-green?style=for-the-badge)](/)
[![FPS](https://img.shields.io/badge/Game%20Loop-60%20FPS-red?style=for-the-badge)](/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](/)

*Designed for portfolio showcase · Interview-ready codebase · Clean architecture*

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Gameplay Preview](#-gameplay-preview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Design Patterns](#-design-patterns)
- [Java Concepts](#-java-concepts-demonstrated)
- [DSA Concepts](#-data-structures--algorithms)
- [Getting Started](#-getting-started)
- [Controls](#-controls)
- [Power-Ups](#-power-ups)
- [Achievements](#-achievements)
- [Save System](#-save-system)
- [Difficulty Scaling](#-difficulty-scaling)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

**Dino Run** is a fully featured, production-level endless runner game inspired by the Chrome offline dinosaur game. Built from scratch using **Java Swing** with zero external dependencies, this project demonstrates mastery of:

- Clean **MVC architecture** with strict layer separation
- **6 classic design patterns** applied naturally in a game context
- **Core Java OOP** — inheritance, polymorphism, abstraction, interfaces
- **Data Structures** — ArrayList, HashMap, Iterator used as real game internals
- **Multithreading** — custom 60 FPS daemon game loop
- **Persistence** — Java Serialization for cross-session save data

This is not a tutorial project. Every class has a clear responsibility, every pattern solves a real problem, and every DSA choice is justified by its time complexity.

---

## 🎮 Gameplay Preview

```
┌──────────────────────────────────────────────────────────────────┐
│                                    HI 01250  00847               │
│         ★                                                        │
│                    🌵        🦅                                   │
│   🦖                                                              │
│▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄│
│ SHIELD 3s                                                         │
└──────────────────────────────────────────────────────────────────┘
```

The game features:
- 🌅 **Day/Night cycle** — sky darkens and stars appear after score 500
- 🌵 **Procedural obstacles** — 3 cactus variants + pterodactyls at 3 heights
- ✨ **Particle effects** — dust on landing, explosion on death
- 🛡 **Power-up system** — 3 collectibles with visible timers
- 🏆 **Achievement popups** — animated overlays on unlock

---

## ✨ Features

### Core Gameplay
| Feature | Description |
|---|---|
| 🏃 Endless running | Procedurally generated obstacles, infinite scroll |
| ⬆️ Double jump | Collect a power-up to earn second mid-air jump |
| ⬇️ Duck | Lower hitbox to dodge pterodactyls |
| 💥 Collision | Pixel-perfect AABB hitbox detection |
| 📈 Scoring | Score increases every frame; persisted across sessions |

### Advanced Features
| Feature | Description |
|---|---|
| 🛡 Shield | Absorbs one collision; 320-frame duration |
| ⏱ Slow Motion | Reduces world speed to 40%; 260-frame duration |
| ×2 Multiplier | Doubles score gain; 320-frame duration |
| 🌙 Night Mode | Visual theme switch at score 500 with star field |
| ✨ Particles | Dust plume on jump/land; burst explosion on death |
| 🏆 Achievements | 9 unlockable badges with animated popup notifications |
| 💾 Persistence | High score, total games, total jumps saved to disk |
| ⏸ Pause | Mid-game pause/resume with dim overlay |

---

## 🏗 Architecture

The project follows a strict **Model-View-Controller (MVC)** architecture:

```
┌─────────────────────────────────────────────────────────────────┐
│                        VIEW LAYER                               │
│   GameWindow (JFrame Singleton)  ←→  GamePanel (Renderer)      │
└───────────────────────┬─────────────────────────────────────────┘
                        │ reads state / calls draw()
┌───────────────────────▼─────────────────────────────────────────┐
│                     CONTROLLER LAYER                            │
│              GameController (bridges input → model)             │
└──────────┬────────────────────────────────────────┬─────────────┘
           │ delegates to                           │
┌──────────▼────────────┐              ┌────────────▼─────────────┐
│     INPUT LAYER       │              │       ENGINE LAYER        │
│  InputHandler         │              │  GameEngine (core state)  │
│  KeyAdapter + lambdas │              │  GameLoop  (60 FPS thread)│
└───────────────────────┘              │  DifficultyManager        │
                                       │  AchievementManager       │
┌──────────────────────────────────────▼──────────────────────────┐
│                        MODEL LAYER                              │
│  Dino  Obstacle  Cactus  Ptero  PowerUp  Particle  GameStats    │
└─────────────────────────────────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────┐
│                      UTILITY / SUPPORT                          │
│  ObstacleFactory  PowerUpFactory  SaveManager  CollisionDetector│
│  ParticleSystem   ScoreFormatter                                │
└─────────────────────────────────────────────────────────────────┘
```

**Data flows in one direction:** Input → Controller → Engine → View. The view never mutates state; the engine never touches Swing.

---

## 📁 Project Structure

```
DinoGame/
├── src/
│   ├── app/
│   │   └── Main.java                  ← Entry point (SwingUtilities EDT)
│   │
│   ├── model/                         ← Pure game data, no Swing
│   │   ├── Dino.java                  ← Player: physics, animation, hitbox
│   │   ├── Obstacle.java              ← Abstract base (Polymorphism)
│   │   ├── Cactus.java                ← extends Obstacle (3 variants)
│   │   ├── Ptero.java                 ← extends Obstacle (3 heights)
│   │   ├── PowerUp.java               ← Collectible with pulse animation
│   │   ├── PowerUpType.java           ← Enum: SHIELD, SLOW_MOTION, SCORE_X2
│   │   ├── Particle.java              ← Single dust/debris particle
│   │   ├── Achievement.java           ← Achievement data record
│   │   └── GameStats.java             ← Serializable persistent stats
│   │
│   ├── engine/
│   │   ├── GameEngine.java            ← Core: state machine, update, draw
│   │   ├── GameLoop.java              ← 60 FPS daemon thread
│   │   ├── DifficultyManager.java     ← Strategy: speed/spawn scaling
│   │   └── AchievementManager.java    ← HashMap registry + Observer callback
│   │
│   ├── view/
│   │   ├── GameWindow.java            ← Singleton JFrame, wires all layers
│   │   └── GamePanel.java             ← JPanel renderer, HUD, overlays
│   │
│   ├── controller/
│   │   └── GameController.java        ← Bridges input events to GameEngine
│   │
│   ├── input/
│   │   └── InputHandler.java          ← KeyAdapter + functional interfaces
│   │
│   ├── factory/
│   │   ├── ObstacleFactory.java       ← Creates Cactus or Ptero by score
│   │   └── PowerUpFactory.java        ← Creates random PowerUp
│   │
│   ├── save/
│   │   └── SaveManager.java           ← ObjectOutputStream/InputStream
│   │
│   ├── state/
│   │   └── GameState.java             ← Enum: MENU, PLAYING, PAUSED, GAME_OVER
│   │
│   └── util/
│       ├── CollisionDetector.java     ← AABB overlap check O(1)
│       ├── ParticleSystem.java        ← ArrayList particle pool
│       └── ScoreFormatter.java        ← Zero-padded score strings
│
├── README.md
└── .gitignore
```

**Total: 25 Java source files across 10 packages.**

---

## 🎨 Design Patterns

### 1. Singleton — `GameWindow`
Only one game window can exist. `getInstance()` ensures a single JFrame instance throughout the application lifecycle.
```java
public static GameWindow getInstance() {
    if (instance == null) instance = new GameWindow();
    return instance;
}
```

### 2. Factory Method — `ObstacleFactory`, `PowerUpFactory`
Decouples creation logic from the engine. The engine calls `ObstacleFactory.create(score, width, groundY)` without knowing whether it gets a `Cactus` or `Ptero`. Difficulty-weighted probability is fully contained in the factory.
```java
// Engine doesn't know what type it's getting:
obstacles.add(ObstacleFactory.create(score, WIDTH, GROUND_Y));
```

### 3. Strategy — `DifficultyManager`
The difficulty algorithm is encapsulated as a swappable object. To change how speed scales with score, you replace `DifficultyManager` — the engine is untouched.
```java
speed = difficulty.getSpeed(score);           // strategy call
spawnTimer = difficulty.getSpawnInterval(score);
```

### 4. Observer — `InputHandler` + `AchievementManager`
Input events are broadcast to registered listener lambdas. The achievement system fires a `Consumer<Achievement>` callback to the view without knowing anything about Swing.
```java
// Register (wiring):
input.setJumpListener(controller::onJump);
achievements.setOnUnlock(a -> showPopup(a.getName()));
```

### 5. State Machine — `GameState` enum + `GameEngine`
The game transitions between `MENU → PLAYING → PAUSED → GAME_OVER` states. Each state determines what `update()` and `draw()` do, cleanly preventing logic from running in the wrong context.

### 6. MVC (Architectural Pattern)
- **Model**: `GameEngine`, all `model.*` classes — pure logic, no UI
- **View**: `GamePanel`, `GameWindow` — reads model state, renders it
- **Controller**: `GameController` — translates input events into model method calls

---

## ☕ Java Concepts Demonstrated

| Concept | File(s) | How It's Used |
|---|---|---|
| **Inheritance** | `Cactus`, `Ptero` | Both extend abstract `Obstacle` |
| **Polymorphism** | `GameEngine.draw()` | `obstacle.draw(g2d)` dispatches per subclass |
| **Abstraction** | `Obstacle.java` | Abstract class with `abstract draw()` |
| **Encapsulation** | `Dino.java` | All state private; exposed via getters only |
| **Interfaces** | `InputHandler.java` | `InputListener`, `DuckListener` functional interfaces |
| **Enums** | `GameState`, `PowerUpType` | Type-safe state and category constants |
| **Generics** | Throughout | `ArrayList<Obstacle>`, `Consumer<Achievement>`, `Map<String, Achievement>` |
| **Lambda Expressions** | `InputHandler`, `AchievementManager` | `controller::onJump`, `a -> showPopup(...)` |
| **Method References** | `GameWindow` | `controller::onJump`, `panel::repaint` |
| **Serialization** | `GameStats`, `SaveManager` | `implements Serializable`, `ObjectOutputStream` |
| **File Handling** | `SaveManager` | `FileOutputStream`, `Files.createDirectories` |
| **Exception Handling** | `SaveManager` | `try-catch` for `IOException`, `ClassNotFoundException` |
| **Multithreading** | `GameLoop` | `implements Runnable`, daemon thread, `volatile boolean` |
| **Functional Interfaces** | `InputHandler` | `@FunctionalInterface` with `onJump()`, `onDuck()` |
| **Collections Framework** | `GameEngine` | `ArrayList`, `Iterator`, `HashMap` |
| **switch expressions** | `GameEngine`, `GamePanel` | Java 14+ arrow switch on enums |
| **var / type inference** | Throughout | Where it improves readability |
| **packages** | All files | 10 logically separated packages |
| **static utility classes** | `CollisionDetector`, `ScoreFormatter` | Private constructors, all-static methods |
| **final fields** | Throughout | Immutability where appropriate |

---

## 📊 Data Structures & Algorithms

### ArrayList — Active Obstacles & Power-Ups
```
obstacles: ArrayList<Obstacle>
powerUps:  ArrayList<PowerUp>
particles: ArrayList<Particle>  (via ParticleSystem)
```
- **Add** (spawn): `O(1)` amortized
- **Update all**: `O(n)` per frame — iterates every active object
- **Remove dead**: `O(n)` via `Iterator` (avoids `ConcurrentModificationException`)
- **Collision check**: `O(n)` — one AABB test per obstacle per frame

### HashMap — Achievement Registry
```
Map<String, Achievement>  achievements
```
- **Lookup** by ID: `O(1)` — e.g. `achievements.get("score_100")`
- **Unlock check**: `O(1)` per named achievement
- **Reset all**: `O(n)` — iterates values once per game start

### Iterator Pattern — Safe Removal
```java
Iterator<Obstacle> it = obstacles.iterator();
while (it.hasNext()) {
    Obstacle o = it.next();
    o.update(speed, slowMode);
    if (o.isDead()) it.remove();  // safe O(1) removal mid-iteration
}
```

### AABB Collision Detection — O(1) Per Pair
```java
// Axis-Aligned Bounding Box — four inequality checks
return a.x < b.x + b.width
    && a.x + a.width  > b.x
    && a.y < b.y + b.height
    && a.y + a.height > b.y;
```
Total per-frame complexity: `O(n)` where `n` = active obstacles (typically 1–4).

### Difficulty Scaling — Linear Step Function
```
speed(score)         = 5.0 + ⌊score / 200⌋ × 0.7    (capped at 14.0)
spawnInterval(score) = 90  − ⌊score / 100⌋ × 4      (min 38 frames)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 or later** — uses switch expressions and `instanceof` patterns
  ```bash
  java -version   # should print 17+
  ```

### Clone the Repository
```bash
git clone https://github.com/yourusername/dino-run.git
cd dino-run
```

### Compile
```bash
# Generate source file list
find src -name "*.java" > sources.txt

# Compile all to out/ directory
javac --release 17 -d out @sources.txt
```

### Run
```bash
java -cp out app.Main
```

### Create Runnable JAR (optional)
```bash
# Package into a self-contained JAR
jar cfe DinoRun.jar app.Main -C out .

# Run the JAR
java -jar DinoRun.jar
```

### One-liner (compile + run)
```bash
find src -name "*.java" | xargs javac --release 17 -d out && java -cp out app.Main
```

---

## 🎮 Controls

| Input | Action |
|---|---|
| `Space` | Jump (first jump) |
| `↑ Up Arrow` | Jump (same as Space) |
| `Space` / `↑` (mid-air) | Double jump |
| `↓ Down Arrow` | Duck (hold) |
| `P` / `Escape` | Pause / Resume |
| `Mouse Click` | Jump |
| `Space` (Game Over) | Restart immediately |

---

## ⚡ Power-Ups

Power-ups spawn as glowing collectible orbs above the ground. Walk into one to activate it.

| Icon | Name | Effect | Duration |
|---|---|---|---|
| `S` (Blue) | **Shield** | Next collision is absorbed; dino blinks | 320 frames (~5s) |
| `T` (Amber) | **Slow Motion** | World speed drops to 40% | 260 frames (~4s) |
| `×2` (Orange) | **Score ×2** | Score gain doubled | 320 frames (~5s) |

Active power-ups show a countdown timer in the bottom-left HUD.

Power-ups only begin spawning after **score 50**, with a 13% chance per obstacle spawn cycle.

---

## 🏆 Achievements

Achievements unlock once per session and trigger an animated popup overlay.

| ID | Name | Condition |
|---|---|---|
| `first_jump` | First Jump! | Perform your first jump |
| `score_100` | 100 Club | Reach score 100 |
| `score_500` | 500 Runner | Reach score 500 |
| `score_1000` | Master Runner | Reach score 1000 |
| `score_2000` | Legendary | Reach score 2000 |
| `night_mode` | Night Owl | Survive until night mode (score 500) |
| `shield_used` | Shield Bearer | Collect a shield power-up |
| `double_jump` | Double Jumper | Perform a mid-air second jump |
| `no_crash_100` | Clean Run | Reach score 100 without being hit |

Achievements are stored in a `HashMap<String, Achievement>` keyed by ID for `O(1)` lookup and unlock.

---

## 💾 Save System

Game statistics persist between sessions using **Java Object Serialization**.

**What is saved:**
- High score
- Total games played
- Total jumps performed
- Total play time (milliseconds)
- Total collision count

**Save file location:**

| OS | Path |
|---|---|
| Linux / macOS | `~/.dinorun/stats.dat` |
| Windows | `C:\Users\<username>\.dinorun\stats.dat` |

**How it works:**
```java
// Saving — ObjectOutputStream wraps a FileOutputStream
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
    oos.writeObject(stats);  // GameStats implements Serializable
}

// Loading — ObjectInputStream reads the object back
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
    return (GameStats) ois.readObject();
}
```

SaveManager handles all `IOException` gracefully — if the file is missing or corrupted, a fresh `GameStats` is returned.

---

## 📈 Difficulty Scaling

The game gets progressively harder as your score increases:

```
Score     Speed    Spawn Interval    Obstacle Types
──────────────────────────────────────────────────
0–199     5.0      90 frames         Cacti only
200–399   5.7      86 frames         Cacti + rare birds
400–599   6.4      82 frames         More birds
600–799   7.1      78 frames         Faster spawn
800–999   7.8      74 frames         High bird frequency
1000+     8.5+     70 frames–min38   Maximum chaos
(cap)     14.0     38 frames
```

Bird (pterodactyl) spawn chance scales from 0% at score 150 to a maximum of 38% — capped to ensure the game remains playable.

---

## 🤝 Contributing

Pull requests are welcome. Suggested extensions:

- [ ] **Sound effects** — `javax.sound.sampled` for jump/death/power-up audio
- [ ] **Leaderboard** — `TreeMap<Integer, String>` sorted by score descending
- [ ] **Replay system** — `LinkedList<GameFrame>` to record and replay runs
- [ ] **Custom skins** — alternate dino sprites selectable from a menu
- [ ] **AI bot mode** — rule-based agent that plays automatically
- [ ] **Settings screen** — volume, key rebinding, resolution toggle
- [ ] **Sprite sheet support** — replace Graphics2D shapes with PNG sprite atlas

### Code Style
- All classes fully Javadoc-commented
- Package names lowercase, class names PascalCase
- No magic numbers — all constants are named `static final` fields
- Each class has a single, clearly stated responsibility

---

## 📄 License

```
MIT License

Copyright (c) 2025 Dino Run Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

<div align="center">

Built with ☕ Java · No external dependencies · ~1,400 lines of clean code

*If this project helped you learn or landed you an interview, drop a ⭐*

</div>
