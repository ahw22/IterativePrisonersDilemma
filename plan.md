# Iterative Prisoner's Dilemma Simulator

A JavaFX desktop application that simulates Robert Axelrod's famous tournaments from *The Evolution of Cooperation*. Users can create tournaments between different strategies, analyze results, and explore how cooperation emerges in iterated games.

---

# 1. Project Overview

## Core Features

1. **Strategy Management**
   - Pre-built strategies (Tit For Tat, Always Defect, etc.)

2. **Tournament Execution**
   - Round-robin tournaments
   - Configurable rounds per match
   - Custom payoff matrices

3. **Results & Visualization**
   - Score rankings
   - Bar charts for scores
   - Cooperation rate statistics
   - Pairwise match results

4. **Advanced Features**
   - Strategy performance heatmaps
   - Export results to CSV/JSON

---

# Future Additions

## Noise
Simulates mistakes or miscommunications where a move is randomly flipped:
- Configurable probability (0-100%)
- Applied independently to each player's move
- Low noise (1-5%): Tit For Tat still performs well
- High noise (>10%): Generous strategies outperform

## Evolutionary Tournament
Simulates population dynamics over generations:
- Initial population with equal representation
- Each generation plays round-robin
- Reproduction proportional to score
- Track population share over time
- Visualize with line chart

## Custom Strategy Builder
GUI-based strategy creation:
- Select first move (Cooperate/Defect/Random)
- Configure response to opponent cooperation
- Configure response to opponent defection
- Set retaliation thresholds
- Test against built-in strategies

---

# 2. Domain Model

## 2.1 Action Enum
- **COOPERATE**: Player chooses to cooperate
- **DEFECT**: Player chooses to defect

## 2.2 Strategy Interface
Every strategy implements this interface:
- `getName()`: Display name for UI
- `nextMove(GameHistory history)`: Core decision logic, receives full game history
- `reset()`: Optional method to reset state between matches

## 2.3 GameHistory Class
Provides access to game history for decision-making:
- `isFirstMove()`: Check if this is the first round
- `getMyLastMove()` / `getOpponentLastMove()`: Get most recent moves
- `getMyMoves()` / `getOpponentMoves()`: Get all moves
- `getTotalRounds()`: Get current round number
- `recordMove(Action myMove, Action opponentMove)`: Record a move

## 2.4 PayoffMatrix Class
Configurable payoff matrix with four values:
- **Reward (R)**: Both cooperate - default 3
- **Temptation (T)**: You defect, they cooperate - default 5
- **Punishment (P)**: Both defect - default 1
- **Sucker (S)**: You cooperate, they defect - default 0

## 2.5 MatchResult Class
Contains results of a single match:
- Player A and B strategies
- Individual moves for each player
- Final scores for each player
- Cooperation rates for each player

## 2.6 TournamentResult Class
Contains results of an entire tournament:
- Total scores for each strategy
- Cooperation rates for each strategy
- List of all match results
- Ranked list of strategies by score

---

# 3. Strategy Implementations

## 3.1 Basic Strategies

| Strategy | Behavior |
|----------|----------|
| **Always Cooperate** | Always chooses COOPERATE |
| **Always Defect** | Always chooses DEFECT |
| **Random** | Randomly chooses COOPERATE or DEFECT |

## 3.2 Reciprocal Strategies

| Strategy | Behavior |
|----------|----------|
| **Tit For Tat** | Cooperates first, then copies opponent's last move |
| **Tit For Two Tats** | Like TFT but requires two defections before retaliating |
| **Generous Tit For Tat** | Like TFT but occasionally forgives defections |

## 3.3 Trigger Strategies

| Strategy | Behavior |
|----------|----------|
| **Grim Trigger** | Cooperates until opponent defects, then defects forever |
| **Pavlov (Win-Stay, Lose-Shift)** | Repeats last move after reward/temptation, switches after punishment/sucker |

## 3.4 Custom Strategy Builder
A UI-based tool allowing users to create strategies without coding:
- First move preference
- Response to opponent defection
- Response to opponent cooperation
- Retaliation threshold

---

# 4. Game Engine

## 4.1 Match Engine
Plays a repeated game between two strategies for N rounds.

Key responsibilities:
- Initialize fresh histories for both players
- Loop through configured number of rounds
- Get moves from both strategies
- Apply noise (if configured) to randomly flip moves
- Calculate payoffs using the payoff matrix
- Record all moves in histories
- Return MatchResult with scores and move history

## 4.2 Tournament Engine
Runs round-robin tournaments where every strategy plays every other strategy.

Key responsibilities:
- Create score tracking for all strategies
- For each pair of strategies, play matches (both home and away)
- Accumulate scores from all matches
- Calculate cooperation rates
- Generate final rankings
- Return TournamentResult

---

# 5. JavaFX UI Architecture

## 5.1 Project Structure

```
src/main/java/com/axelrod/
├── Main.java                    # Application entry point
├── model/                       # Domain classes
│   ├── Action.java
│   ├── Strategy.java
│   ├── GameHistory.java
│   ├── PayoffMatrix.java
│   ├── MatchResult.java
│   └── TournamentResult.java
├── strategies/                  # Built-in strategy implementations
├── engine/                      # Game and tournament engines
├── ui/                          # Controllers and views
│   ├── MainController.java
│   └── ...
├── view/                        # FXML layout files
│   └── MainView.fxml
└── util/                        # Utilities (export, etc.)
```

## 5.2 Main Window Layout

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Logo] Iterative Prisoner's Dilemma Simulator         [−][□][×]  │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────────┐  ┌───────────────┐   │
│  │   STRATEGIES    │  │   TOURNAMENT SETUP  │  │    RESULTS    │   │
│  │                 │  │                     │  │               │   │
│  │  ☑ Always C     │  │  Rounds: [200    ]   │  │  Rank Strategy│   │
│  │  ☑ Always D     │  │                     │  │   1. TFT   487│   │
│  │  ☑ Tit For Tat  │  │  Payoff Matrix:     │  │   2. AlwaysC 456│  │
│  │  ☑ Random       │  │  R:[3] T:[5] P:[1] S:[0]  │   3. Pavlov 423│  │
│  │                 │  │                     │  │   4. Random  312│  │
│  │                 │  │                     │  │               │   │
│  │                 │  │  [▶ Run Tournament]  │  │  [Chart]      │   │
│  │                 │  │                     │  │               │   │
│  └─────────────────┘  └─────────────────────┘  └───────────────┘   │
├─────────────────────────────────────────────────────────────────────┤
│  Status: Ready                                    Progress: ░░░░   │
└─────────────────────────────────────────────────────────────────────┘
```

**Left Panel - Strategy Selection:**
- Checkbox list of available strategies

**Center Panel - Tournament Settings:**
- Rounds per match (default 200)
- Payoff matrix configuration (R, T, P, S values)
- Run Tournament button

**Right Panel - Results:**
- Ranking table with strategy name and score
- Bar chart visualization
- Export button

## 5.3 Controller Design

**MainController:**
- Manages overall application state
- Coordinates between strategy list, settings, and results panels
- Runs tournament in background thread
- Updates progress bar and status label

**StrategyListController:**
- Populates strategy list from available implementations
- Handles selection state
- Manages add/remove operations

**ResultsController:**
- Updates table with tournament results
- Renders charts
- Handles sorting and filtering

---

# 6. Visualization Features

## 6.1 Score Bar Chart
Horizontal or vertical bar chart showing total scores for each strategy, sorted by rank.

## 6.2 Cooperation Rate Display
Shows what percentage of each strategy's moves were cooperative, helpful for understanding strategy behavior.

## 6.3 Pairwise Heatmap
Grid showing scores between each pair of strategies. Useful for understanding direct matchups.

## 6.4 Match Details
When clicking a result, show:
- All moves made in sequence
- Score progression over rounds
- Key decision points

---

# 7. Export Options

- **CSV**: Strategy, Score, Cooperation Rate
- **JSON**: Full tournament data with match details
- **Screenshot**: Save chart as PNG

---

# 8. Development Phases

## Phase 1: Core Simulation (Week 1)

| Task | Description |
|------|-------------|
| Create project structure | Maven/Gradle, JavaFX setup |
| Implement domain model | Action, Strategy, GameHistory, etc. |
| Implement 3-4 basic strategies | AlwaysC, AlwaysD, TFT, Random |
| Implement Match engine | Single match execution |
| Implement Tournament engine | Round-robin execution |
| Console testing | Verify tournament runs correctly |

**Deliverable:** Command-line tournament runner

## Phase 2: JavaFX UI (Week 2)

| Task | Description |
|------|-------------|
| Setup JavaFX project | Scene Builder, FXML |
| Main window layout | Three-panel design |
| Strategy selection list | Checkbox list with all strategies |
| Settings panel | Spinners for rounds, noise, payoff values |
| Results table | Sortable ranking table |
| Run button + progress | Async tournament execution |

**Deliverable:** Functional desktop app with basic UI

## Phase 3: Visualization (Week 3)

| Task | Description |
|------|-------------|
| Score bar chart | JavaFX BarChart integration |
| Cooperation rate display | Add column to results table |
| Heatmap view | Grid showing pairwise results |
| Match detail view | Click to see individual match data |

**Deliverable:** Visual analysis tools

## Phase 4: Polish & Export (Week 4)

| Task | Description |
|------|-------------|
| Export functionality | CSV/JSON export |
| Polish | Error handling, styling |

**Deliverable:** Production-ready application

---

# 9. Testing Plan

## Unit Tests
Test individual strategies and components:
- Strategy decision logic
- GameHistory tracking
- Payoff calculations
- Match result computation

## Integration Tests
Test component interactions:
- Tournament produces correct rankings
- Results match expected behavior from Axelrod's research
- UI updates correctly after tournament

## Key Test Cases
- Tit For Tat scores higher than Always Defect
- Grim Trigger cooperates with itself
- Random performs worse than reciprocal strategies
- Two generous strategies achieve mutual cooperation

---

# 10. Key Insights from Axelrod's Research

1. **Tit For Tat wins consistently** - Its success comes from being nice, retaliatory, and forgiving.

2. **Nice strategies win** - Never defect first. Being nice is evolutionarily stable in most environments.

3. **Retaliation maintains cooperation** - Once established, cooperation must be defended against exploitation.

4. **Forgiveness helps recover** - After accidental defection (noise), forgiving strategies recover cooperation faster.

5. **Clarity matters** - Strategies should be understandable to promote mutual cooperation.

---

This plan provides a complete roadmap for building a professional-quality Iterative Prisoner's Dilemma simulator with JavaFX.
