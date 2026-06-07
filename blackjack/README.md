# 🃏 Professional Casino Blackjack

> A feature-rich, desktop Blackjack game built in Java with a polished casino aesthetic, AI strategy hints, achievement system, persistent save data, and immersive sound effects.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation & Running](#installation--running)
- [Gameplay Guide](#gameplay-guide)
- [Game Modes & Difficulty](#game-modes--difficulty)
- [Settings](#settings)
- [Achievements](#achievements)
- [Statistics & Leaderboard](#statistics--leaderboard)
- [Save System](#save-system)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Technical Details](#technical-details)

---

## Overview

**Professional Casino Blackjack** (`blackjack-1.0.0.jar`) is a fully-featured desktop Blackjack simulator built with **Java Swing**. It faithfully implements casino Blackjack rules including splitting, doubling down, insurance, and surrender — while layering on modern game features like achievement tracking, a daily reward system, a leaderboard, card-count display, and a built-in strategy advisor.

The game uses a **Nimbus Look & Feel** with a custom casino theme for a premium feel, and generates synthesized sound effects in real time using the Java Sound API (no external audio files needed).

---

## Features

### Core Gameplay
- Standard casino Blackjack with full rule support: **Hit, Stand, Double Down, Split, Surrender, Insurance**
- Multi-deck shoe (configurable 1–8 decks) with a cut card and automatic reshuffling
- Configurable Blackjack payout (e.g. 3:2 or 6:5)
- Dealer peek and soft-17 rules (configurable)
- Split hands with independent play (`PLAYER_TURN_SPLIT` state)
- Insurance side bet with resolution tracking

### AI Strategy Advisor
- Built-in **Basic Strategy** engine covering hard totals, soft totals, and pair splitting
- Provides move recommendations: **Hit, Stand, Double Down, Double-or-Stand, Split, Surrender, Surrender-or-Stand**
- Shows human-readable explanations (e.g. *"Strategy: Double Down on soft 17 vs dealer 5"*)
- Optional **edge estimation** per hand
- Toggle hints on/off in Settings; optional probability overlay

### Chip & Betting System
- Visual chip tray with 8 denominations: **$1 (white), $5 (red), $25 (blue), $100 (green), $500 (orange), $1,000 (black), $5,000 (purple), $10,000 (gold)**
- Re-Bet and Double Bet shortcuts
- Configurable minimum and maximum bet limits
- Configurable starting balance

### Achievement System (21 achievements)
| Achievement | Condition |
|---|---|
| First Blood | Win your first hand |
| Natural! | Get your first Blackjack |
| Blackjack Pro | Get 5 Blackjacks |
| Blackjack Master | Get 25 Blackjacks |
| Hot Streak | Win 3 hands in a row |
| On Fire! | Win 5 hands in a row |
| Unstoppable | Win 10 hands in a row |
| Doubling Up | Double your starting balance |
| High Roller | Reach 5× starting balance |
| Casino King | Reach 10× starting balance |
| Regular | Play 50 hands |
| Veteran | Play 200 hands |
| Grinder | Play 500 hands |
| Double Trouble | Win a Double Down hand |
| Divide and Conquer | Win after splitting |
| Covered! | Win an insurance bet |
| Week Warrior | Log in 7 days in a row |
| Monthly Devotee | Log in 30 days in a row |
| Jackpot! | Win $500+ in a single hand |
| All In | Win a max-bet hand |
| Skilled Player | Reach 60% win rate (50+ hands) |

Achievements pop up as **toast notifications** and award points tracked on your profile.

### Daily Reward System
- Claim a bonus reward once per calendar day
- Tracks consecutive login days (up to 30-day streak achievements)
- Shown via a dedicated dialog on login when available

### Statistics Panel
Tracks and displays:
- Hands played / won / lost / pushed
- Blackjacks, busts, win rate (%)
- Current & best win streak
- Total wagered, won, lost, net profit
- Biggest single-hand win
- Double Down wins, Split wins, Insurance wins

### Leaderboard
- Persistent local leaderboard sorted by balance
- Records player name, balance, hands played, and timestamp
- Viewable from the main menu

### Sound & Music
Synthesized audio (no external files) for:
`CARD_DEAL`, `CARD_FLIP`, `CHIP_BET`, `CHIP_COLLECT`, `WIN`, `LOSE`, `BLACKJACK`, `PUSH`, `BUST`, `BUTTON_CLICK`, `SHUFFLE`, `DEAL_START`, `ACHIEVEMENT`, `DAILY_REWARD`
- Separate **sound** and **music** volume controls
- Toggle sound and music independently
- Ambient background music

### Visual Themes
Three casino table themes selectable in Settings:
- **Classic Green** (default)
- Vegas (additional theme)
- Dark (additional theme)

Custom `CasinoTheme` with tokens for: table felt, trim, rail, gold accents, card backgrounds, button states, win/lose/push overlay colours, chip shadow, and panel backgrounds.

### Animations
- Card deal animations (`CardAnimation`)
- Pulsing UI effects (`PulseAnimation`)
- Floating text overlays (`TextAnimation`)
- Configurable animation speed; can be disabled entirely

### Tutorial
Built-in **How to Play** panel explaining rules, hand values, and available actions — accessible from the main menu.

---

## Requirements

| Requirement | Minimum |
|---|---|
| Java Runtime | Java 21 (JRE or JDK) |
| OS | Windows / macOS / Linux |
| Display | 800 × 600 or larger |
| Memory | ~128 MB heap (default JVM settings are fine) |

> The JAR was compiled with `javac` from **Ubuntu, JDK 21.0.10**. Java 17+ should also work; Java 8/11 will not.

---

## Installation & Running

1. **Download** `blackjack-1.0.0.jar` to any folder.

2. **Verify Java version:**
   ```bash
   java -version
   # Should print: openjdk 21 ... or similar Java 21+
   ```

3. **Run the game:**
   ```bash
   java -jar blackjack-1.0.0.jar
   ```

4. *(Optional)* Increase memory for smoother animations:
   ```bash
   java -Xmx256m -jar blackjack-1.0.0.jar
   ```

5. *(Optional)* Run fullscreen (toggle in Settings, or launch with the in-game option):
   The game supports a fullscreen toggle managed via `GameSettings`.

> **Save data** is stored in `~/.blackjack/` (your home directory) automatically. No installation is required.

---

## Gameplay Guide

### Starting a Game
1. Launch the JAR — the **Main Menu** appears.
2. Enter your **player name** (default: `Player`).
3. Click **NEW GAME** to start fresh, or **CONTINUE** if save data exists (shown with your name and balance).

### Round Flow

```
PLACING_BET → DEALING → [INSURANCE_OFFER] → PLAYER_TURN → [PLAYER_TURN_SPLIT] → DEALER_TURN → ROUND_OVER
```

| Phase | What Happens |
|---|---|
| **Placing Bet** | Click chips to add to your bet; use Re-Bet or Double Bet for speed |
| **Dealing** | Two cards each to player and dealer (dealer's second card face-down) |
| **Insurance Offer** | If dealer shows an Ace, you may buy insurance for half your bet |
| **Player Turn** | Choose Hit, Stand, Double Down, Split (if pair), or Surrender |
| **Split Turn** | Play each split hand independently |
| **Dealer Turn** | Dealer reveals hole card and draws to 17+ (soft-17 rule configurable) |
| **Round Over** | Result displayed; winnings paid; achievements checked |

### Actions Reference

| Action | Condition | Effect |
|---|---|---|
| **Hit** | Always available | Draw one card |
| **Stand** | Always available | End your turn |
| **Double Down** | First two cards only | Double the bet, draw exactly one card |
| **Split** | Pair of same-rank cards | Split into two hands, each gets a new card |
| **Surrender** | First two cards (if enabled) | Forfeit half your bet, end the hand |
| **Insurance** | Dealer shows Ace | Bet up to half your original wager against dealer Blackjack |

### Card Values
- **2–10** → face value
- **J, Q, K** → 10
- **Ace** → 1 or 11 (whichever doesn't bust)
- **Blackjack** → Ace + 10-value on first two cards → pays 3:2 (default)

---

## Game Modes & Difficulty

Select difficulty from **Settings → Difficulty**:

| Difficulty | Effect |
|---|---|
| **Easy** | Relaxed rules; longer deal delay for readability |
| **Normal** | Standard casino rules (default) |
| **Hard** | Stricter rules; affects AI edge estimates |

Deck count, payout ratio, and rule variants (dealer peek, stand-on-soft-17, double-after-split, surrender) are all independently configurable.

---

## Settings

All settings are persisted to `~/.blackjack/settings.dat`.

| Setting | Default | Description |
|---|---|---|
| Deck Count | 6 | Decks in the shoe (1–8) |
| Blackjack Payout | 1.5 (3:2) | Multiplier for natural Blackjack |
| Dealer Peek | On | Dealer checks for Blackjack before player acts |
| Stand on Soft 17 | On | Dealer stands on soft 17 |
| Double After Split | On | Allow doubling on split hands |
| Surrender Allowed | On | Allow late surrender |
| Minimum Bet | $5 | Minimum wager per hand |
| Maximum Bet | $1,000 | Maximum wager per hand |
| Starting Balance | $1,000 | New player starting chips |
| Sound Enabled | On | Toggle all sound effects |
| Music Enabled | On | Toggle ambient music |
| Sound Volume | 75% | SFX volume (0–100%) |
| Music Volume | 50% | Music volume (0–100%) |
| Theme | Classic Green | Table colour theme |
| Fullscreen | Off | Fullscreen window mode |
| Show Hints | On | Display Basic Strategy recommendations |
| Show Probabilities | Off | Show edge/probability overlay |
| Animations Enabled | On | Card deal and UI animations |
| Animation Speed | Normal | Speed multiplier for animations |
| Timer Enabled | Off | Per-turn decision timer |
| Timer Seconds | 30 | Seconds per turn when timer is on |
| Difficulty | Normal | Easy / Normal / Hard |

---

## Achievements

Achievements are stored per player profile. Each achievement awards **points** visible on the Statistics panel. Unlocks trigger an animated **toast notification** in-game.

See the full list in the [Features → Achievement System](#achievement-system-21-achievements) section above.

---

## Statistics & Leaderboard

### Statistics Panel
Accessed from the main menu (**STATISTICS**). Shows a full breakdown of your session and lifetime stats.

### Leaderboard
Accessed from **LEADERBOARD**. Lists the top players by balance saved locally in `~/.blackjack/leaderboard.dat`. A new entry is automatically added when you finish a game.

---

## Save System

All data is serialised via Java's `ObjectOutputStream` and stored in **`~/.blackjack/`**:

| File | Contents |
|---|---|
| `player.dat` | Player profile, balance, stats, achievements |
| `settings.dat` | All game settings |
| `history.dat` | Round history (capped to most recent N entries) |
| `leaderboard.dat` | Leaderboard entries (name, balance, hands, timestamp) |
| `backups/` | Automatic backups created before saves (timestamped `yyyyMMdd_HHmmss`) |

> Backups are created automatically. If your save is corrupted, copy a file from `~/.blackjack/backups/` back to `~/.blackjack/`.

To **reset all data**, delete the `~/.blackjack/` directory.

---

## Project Structure

```
com.casino.blackjack
├── App.java                         # Entry point; applies Nimbus L&F and launches MainWindow
├── controller/
│   └── GameManager.java             # Singleton; owns game state machine and event dispatch
├── model/
│   ├── Card.java                    # Card with Suit & Rank enums; face-up/down state
│   ├── Deck.java                    # Multi-deck shoe; Hi-Lo running & true count; cut card
│   ├── Hand.java                    # A player or dealer hand; split/double/insurance flags
│   ├── Player.java                  # Full player profile; stats; achievement list; daily reward
│   ├── Dealer.java                  # Dealer hand wrapper
│   ├── GameSettings.java            # All configurable settings (serialisable)
│   ├── GameState.java               # Enum of game phases (state machine states)
│   ├── GameEvent.java               # Events fired through the observer bus
│   ├── GameObserver.java            # Observer interface implemented by view panels
│   ├── HistoryEntry.java            # Single round history record
│   └── RoundResult.java             # Enum: WIN, BLACKJACK, LOSE, PUSH, BUST, SURRENDER, INSURANCE_WIN
├── ai/
│   └── BasicStrategy.java           # Full basic strategy lookup; explain() and estimateEdge()
├── manager/
│   ├── ChipManager.java             # Chip tray state; bet validation; chip denominations
│   ├── AchievementManager.java      # Singleton; checks and unlocks all 21 achievements
│   └── SoundManager.java            # Synthesised audio via javax.sound; ambient music thread
├── save/
│   └── SaveSystem.java              # Serialisation to ~/.blackjack/; backup management
└── view/
    ├── MainWindow.java              # Top-level JFrame; panel router; GameObserver
    ├── MainMenuPanel.java           # Start screen; player name input; saved game display
    ├── GameTablePanel.java          # Main play surface; card rendering; action buttons
    ├── SettingsPanel.java           # All settings controls
    ├── StatisticsPanel.java         # Lifetime stats display
    ├── LeaderboardPanel.java        # Local leaderboard table
    ├── TutorialPanel.java           # How-to-play guide
    ├── GameOverPanel.java           # End-of-game summary screen
    ├── CardRenderer.java            # Custom card drawing (suits, ranks, faces)
    ├── AnimationManager.java        # Card, pulse, and text animations
    ├── CasinoButton.java            # Styled JButton (PRIMARY, NEUTRAL, DANGER styles)
    ├── CasinoTheme.java             # Colour tokens and fonts for all themes
    ├── AchievementToast.java        # Animated achievement pop-up overlay
    ├── InsuranceDialog.java         # Insurance offer modal
    └── DailyRewardDialog.java       # Daily reward claim modal
```

---

## Architecture

The game follows a classic **MVC + Observer** pattern:

```
  ┌─────────────────────────────┐
  │        MainWindow           │  ← Top-level frame / panel router
  │  (implements GameObserver)  │
  └────────────┬────────────────┘
               │ observes events
  ┌────────────▼────────────────┐
  │        GameManager          │  ← Singleton controller / state machine
  │   (controller package)      │    Fires GameEvents to all registered observers
  └────┬──────────┬─────────────┘
       │          │
  ┌────▼───┐  ┌───▼──────┐
  │ Model  │  │ Managers │
  │ Player │  │ Chip     │
  │ Deck   │  │ Sound    │
  │ Hand   │  │ Achieve. │
  │  ...   │  │ Save     │
  └────────┘  └──────────┘
```

- **GameState** drives the state machine: `MAIN_MENU → PLACING_BET → DEALING → PLAYER_TURN → DEALER_TURN → ROUND_OVER → GAME_OVER`
- **GameEvent** carries event type + payload; all view panels subscribe as `GameObserver`
- **SaveSystem** serialises the full `Player` and `GameSettings` objects to disk
- **SoundManager** runs audio on a cached thread pool (daemon threads); no audio files needed — tones are synthesised

---

## Technical Details

- **Language:** Java 21
- **UI Toolkit:** Java Swing with Nimbus Look & Feel
- **Audio:** `javax.sound.sampled` — synthesised PCM tones, no bundled audio assets
- **Persistence:** Java Object Serialisation (`ObjectOutputStream` / `ObjectInputStream`)
- **Build:** Standard `jar` with `Main-Class: com.casino.blackjack.App` in `MANIFEST.MF`
- **JAR size:** ~128 KB (compiled classes only, no dependencies)
- **Dependencies:** None — pure Java SE standard library

---

## License

This project is provided as-is. No external libraries or runtime dependencies are required beyond a standard Java 21 JRE.
