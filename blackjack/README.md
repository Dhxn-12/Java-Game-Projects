# 🃏 Claude's Casino Blackjack

A **professional, casino-grade Blackjack game** built entirely in Java with Swing.
Designed as a portfolio-quality project demonstrating advanced OOP, design patterns,
clean architecture, and a polished UI — all without any external dependencies.

---

## 🎮 Features

### Core Gameplay
| Feature | Details |
|---|---|
| Standard Blackjack rules | Full Vegas Strip ruleset |
| Hit / Stand / Double Down | Full player actions |
| Split pairs | Up to 2 hands |
| Insurance | When dealer shows Ace |
| Late Surrender | Configurable |
| Dealer AI | S17 / H17 configurable |
| Ace counting | Auto 1 or 11 |
| Blackjack detection | 3:2 payout |
| Bust detection | Immediate resolution |
| Multi-deck shoe | 1 / 2 / 4 / 6 / 8 decks |

### Game Features
- 💰 Virtual chip betting system (8 denominations: $1–$1K)
- 💾 Auto-save & load player profile
- 📊 Full statistics dashboard (win rate, streaks, P&L, etc.)
- 🏆 21 unlockable achievements
- 📅 Daily login rewards (scaling streak bonus)
- 📋 Round history (last 100 hands, with table view)
- 🏅 Persistent leaderboard
- ⌨️ Full keyboard shortcuts (H / S / D / P / Enter / Esc)

### Casino Features
- 🎨 4 casino themes: Classic Green, Midnight Blue, Royal Red, Desert Gold
- 🔊 Synthesised sound effects (deal, win, lose, blackjack, chips, etc.)
- 🎵 Ambient background music (synthesised — no external files)
- ✨ Animated card dealing with smooth easing
- 🧮 Basic Strategy hints (mathematically correct)
- ⏱️ Optional timer mode (auto-stand on timeout)
- 🖥️ Fullscreen support

### Architecture
- **MVC** — Model / View / Controller fully separated
- **Singleton** — GameManager, SoundManager, AnimationManager, etc.
- **Observer** — GameObserver / GameEvent for decoupled event broadcasting
- **Factory** — Deck construction, theme application
- **State Machine** — GameState enum drives all screen transitions

---

## 📁 Project Structure

```
blackjack/
├── src/main/java/com/casino/blackjack/
│   ├── App.java                    ← Entry point
│   ├── model/
│   │   ├── Card.java               ← Immutable card (suit + rank)
│   │   ├── Hand.java               ← Card collection + scoring
│   │   ├── Deck.java               ← Multi-deck shoe + Hi-Lo count
│   │   ├── Player.java             ← Profile, wallet, statistics
│   │   ├── Dealer.java             ← Dealer with casino rules
│   │   ├── GameState.java          ← State machine enum
│   │   ├── GameEvent.java          ← Observer event types
│   │   ├── GameObserver.java       ← Observer interface
│   │   ├── GameSettings.java       ← All configurable settings
│   │   ├── HistoryEntry.java       ← Round snapshot for history
│   │   └── RoundResult.java        ← WIN / LOSE / PUSH / etc.
│   ├── controller/
│   │   └── GameManager.java        ← Central MVC controller (Singleton)
│   ├── manager/
│   │   ├── ChipManager.java        ← Betting chip denominations
│   │   ├── SoundManager.java       ← Audio synthesis (no WAV files)
│   │   └── AchievementManager.java ← Achievement registry + unlocking
│   ├── ai/
│   │   └── BasicStrategy.java      ← Mathematically correct strategy advisor
│   ├── save/
│   │   └── SaveSystem.java         ← Serialisation (player, settings, history)
│   └── view/
│       ├── MainWindow.java         ← Root JFrame + screen router
│       ├── CasinoTheme.java        ← Colors, fonts, 4 themes
│       ├── CasinoButton.java       ← Styled button with hover animations
│       ├── CardRenderer.java       ← Draws cards in code (no image files)
│       ├── AnimationManager.java   ← 60fps animation loop
│       ├── GameTablePanel.java     ← Main table view (felt, cards, chips)
│       ├── MainMenuPanel.java      ← Animated title screen
│       ├── SettingsPanel.java      ← Settings with tabbed panes
│       ├── StatisticsPanel.java    ← Stats + history + achievements
│       ├── LeaderboardPanel.java   ← Top scores table
│       ├── TutorialPanel.java      ← How to play guide
│       ├── InsuranceDialog.java    ← Insurance bet dialog
│       ├── DailyRewardDialog.java  ← Daily bonus popup
│       ├── AchievementToast.java   ← Slide-in achievement notification
│       └── GameOverPanel.java      ← Game over / restart screen
├── pom.xml                         ← Maven build (Java 17, fat JAR)
└── README.md
```

---

## 🚀 Setup & Running

### Requirements
- **Java 17+** (Java 21 also works)
- **Maven 3.8+** (optional — can also compile manually)

### Option A — Run the pre-built JAR
```bash
java -jar blackjack-1.0.0.jar
```

### Option B — Build with Maven
```bash
mvn clean package
java -jar target/blackjack-1.0.0.jar
```

### Option C — Compile manually
```bash
find src -name "*.java" > sources.txt
mkdir -p out
javac --release 17 -d out $(cat sources.txt)
cd out
jar cfm ../blackjack.jar <(echo "Main-Class: com.casino.blackjack.App") $(find . -name "*.class")
java -jar ../blackjack.jar
```

---

## ⌨️ Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `H` | Hit |
| `S` | Stand |
| `D` | Double Down |
| `P` | Split |
| `Enter` | Deal (confirm bet) |
| `Esc` | Clear Bet |

---

## 🎨 Screenshots

The game features 4 casino themes selectable from **View → Theme**:

- **Classic Green** — Traditional casino felt
- **Midnight Blue** — Sleek dark blue
- **Royal Red** — Rich crimson table
- **Desert Gold** — Warm amber tones

---

## 🏆 Achievements (21 total)

| Achievement | Condition |
|---|---|
| First Blood | Win your first hand |
| Natural! | First Blackjack |
| Blackjack Master | 25 Blackjacks |
| Unstoppable | 10-hand win streak |
| Casino King | Reach 10× starting balance |
| Grinder | Play 500 hands |
| All In | Win a max-bet hand |
| Week Warrior | 7-day login streak |
| …and 13 more | — |

---

## 🔧 Design Patterns Used

| Pattern | Where |
|---|---|
| **Singleton** | GameManager, SoundManager, AnimationManager, AchievementManager, SaveSystem |
| **Observer** | GameObserver interface + GameEvent enum → all views react to model changes |
| **MVC** | Model (model/), Controller (controller/), View (view/) |
| **Factory** | CasinoTheme.applyTheme(), Deck construction via GameSettings |
| **State Machine** | GameState enum controls all valid transitions and UI gating |

---

## 🔮 Future Improvements

- [ ] Multi-player support (local hot-seat)
- [ ] Networked multiplayer
- [ ] Card counting practice mode (Hi-Lo drill)
- [ ] Realistic card images (via embedded PNG atlas)
- [ ] Animated chip stacks on the felt
- [ ] Side bets (Perfect Pairs, 21+3)
- [ ] Tournament mode
- [ ] Export statistics to CSV
- [ ] Cloud save (Firebase)
- [ ] Android port (using libGDX)

---

## 📄 License

MIT License — free to use, modify, and distribute.

---

*Built with ☕ Java 17 + Swing — zero external runtime dependencies.*
