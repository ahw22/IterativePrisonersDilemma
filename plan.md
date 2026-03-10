# Iterative Prisoner's Dilemma Simulator

A JavaFX desktop application that simulates Robert Axelrod's famous tournaments from *The Evolution of Cooperation*. Users can create tournaments between different strategies, analyze results, and explore how cooperation emerges in iterated games.

---

# 1. Project Overview

## Core Features

1. **Strategy Management**
   - Pre-built strategies (Tit For Tat, Always Defect, etc.)
   - Custom strategy creation via UI
   - Strategy parameter configuration

2. **Tournament Execution**
   - Round-robin tournaments
   - Configurable rounds per match
   - Noise/mistake probability
   - Custom payoff matrices

3. **Results & Visualization**
   - Score rankings
   - Bar charts for scores
   - Cooperation rate statistics
   - Pairwise match results

4. **Advanced Features**
   - Evolutionary tournaments (reproduction based on scores)
   - Strategy performance heatmaps
   - Export results to CSV/JSON

---

# 2. Domain Model

## 2.1 Action Enum

```java
public enum Action {
    COOPERATE,
    DEFECT
}
```

## 2.2 Strategy Interface

All strategies implement this interface:

```java
public interface Strategy {
    String getName();
    Action nextMove(GameHistory history);
    default void reset() { }
}
```

Key design decisions:
- `getName()`: Display name for UI
- `nextMove()`: Core decision logic, receives full history
- `reset()`: Optional reset between matches in a tournament

## 2.3 GameHistory Class

Provides access to the game history for decision-making:

```java
public class GameHistory {
    private List<Action> myMoves;
    private List<Action> opponentMoves;
    private int roundNumber;

    public boolean isFirstMove();
    public Action getMyLastMove();
    public Action getOpponentLastMove();
    public List<Action> getMyMoves();
    public List<Action> getOpponentMoves();
    public int getTotalRounds();
    
    public void recordMove(Action myMove, Action opponentMove);
}
```

## 2.4 PayoffMatrix Class

Configurable payoff matrix:

```java
public class PayoffMatrix {
    private int reward;        // R: both cooperate (default 3)
    private int temptation;    // T: you defect, they cooperate (default 5)
    private int punishment;    // P: both defect (default 1)
    private int sucker;        // S: you cooperate, they defect (default 0)
    
    public PayoffMatrix();
    public PayoffMatrix(int R, int T, int P, int S);
    
    public int[] getPayoff(Action myAction, Action opponentAction);
    public int getMyScore(Action myAction, Action opponentAction);
}
```

Standard payoff values (Axelrod's original):
|       | Cooperate | Defect   |
|-------|-----------|----------|
| **Cooperate** | R=3, R=3 | S=0, T=5 |
| **Defect**    | T=5, S=0 | P=1, P=1 |

## 2.5 MatchResult Class

```java
public class MatchResult {
    private Strategy playerA;
    private Strategy playerB;
    private List<Action> movesA;
    private List<Action> movesB;
    private int scoreA;
    private int scoreB;
    private double cooperationRateA;
    private double cooperationRateB;
    
    public int getScore(Strategy player);
    public double getCooperationRate(Strategy player);
}
```

## 2.6 TournamentResult Class

```java
public class TournamentResult {
    private List<Strategy> strategies;
    private Map<Strategy, Integer> totalScores;
    private Map<Strategy, Double> cooperationRates;
    private List<MatchResult> matchResults;
    private List<Strategy> ranking;
    
    public int getScore(Strategy strategy);
    public double getCooperationRate(Strategy strategy);
    public List<Strategy> getRanking();
    public List<MatchResult> getMatchesBetween(Strategy a, Strategy b);
}
```

---

# 3. Strategy Implementations

## 3.1 Always Cooperate

Always chooses COOPERATE regardless of opponent.

```java
public class AlwaysCooperate implements Strategy {
    @Override
    public String getName() { return "Always Cooperate"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        return Action.COOPERATE;
    }
}
```

## 3.2 Always Defect

Always chooses DEFECT regardless of opponent.

```java
public class AlwaysDefect implements Strategy {
    @Override
    public String getName() { return "Always Defect"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        return Action.DEFECT;
    }
}
```

## 3.3 Tit For Tat

**The most famous strategy.** Cooperates on first move, then copies opponent's last move.

```java
public class TitForTat implements Strategy {
    @Override
    public String getName() { return "Tit For Tat"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        if (history.isFirstMove()) {
            return Action.COOPERATE;
        }
        return history.getOpponentLastMove();
    }
}
```

## 3.4 Tit For Two Tats

Like Tit For Tat, but requires two defections to retaliate. More forgiving.

```java
public class TitForTwoTats implements Strategy {
    @Override
    public String getName() { return "Tit For Two Tats"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        if (history.isFirstMove() || history.getTotalRounds() < 2) {
            return Action.COOPERATE;
        }
        
        int defectionCount = 0;
        for (Action move : history.getOpponentMoves()) {
            if (move == Action.DEFECT) defectionCount++;
        }
        
        return defectionCount >= 2 ? Action.DEFECT : Action.COOPERATE;
    }
}
```

## 3.5 Grim Trigger

Cooperates until opponent defects once, then defects forever.

```java
public class GrimTrigger implements Strategy {
    private boolean triggered = false;
    
    @Override
    public String getName() { return "Grim Trigger"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        if (triggered) return Action.DEFECT;
        
        if (!history.isFirstMove() && history.getOpponentLastMove() == Action.DEFECT) {
            triggered = true;
            return Action.DEFECT;
        }
        return Action.COOPERATE;
    }
    
    @Override
    public void reset() { triggered = false; }
}
```

## 3.6 Random Strategy

Randomly chooses COOPERATE or DEFECT.

```java
public class RandomStrategy implements Strategy {
    private Random random = new Random();
    
    @Override
    public String getName() { return "Random"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        return random.nextBoolean() ? Action.COOPERATE : Action.DEFECT;
    }
}
```

## 3.7 Pavlov (Win-Stay, Lose-Shift)

If reward or temptation received, repeat last move. If punishment or sucker, switch.

```java
public class Pavlov implements Strategy {
    @Override
    public String getName() { return "Pavlov"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        if (history.isFirstMove()) return Action.COOPERATE;
        
        Action myLastMove = history.getMyLastMove();
        Action opponentLastMove = history.getOpponentLastMove();
        
        // Win (CC or DC) or Lose (DD or CD) - stay or shift accordingly
        boolean won = (myLastMove == Action.COOPERATE && opponentLastMove == Action.COOPERATE)
                   || (myLastMove == Action.DEFECT && opponentLastMove == Action.COOPERATE);
        
        return won ? myLastMove : (myLastMove == Action.COOPERATE ? Action.DEFECT : Action.COOPERATE);
    }
}
```

## 3.8 Generous Tit For Tat

Like Tit For Tat, but occasionally cooperates when opponent defected (to repair relations).

```java
public class GenerousTFT implements Strategy {
    private double generosity = 0.1; // 10% chance
    
    @Override
    public String getName() { return "Generous Tit For Tat"; }
    
    @Override
    public Action nextMove(GameHistory history) {
        if (history.isFirstMove()) return Action.COOPERATE;
        
        if (history.getOpponentLastMove() == Action.DEFECT) {
            return Math.random() < generosity ? Action.COOPERATE : Action.DEFECT;
        }
        return Action.COOPERATE;
    }
}
```

## 3.9 Custom Strategy Builder

For user-defined strategies without coding:

```java
public class CustomStrategyBuilder {
    public Strategy buildFromRules(
        Action firstMove,
        boolean copyOpponentOnDefect,
        boolean copyOpponentOnCooperate,
        int retaliationThreshold
    ) {
        // Build dynamic strategy
    }
}
```

---

# 4. Game Engine

## 4.1 Match Engine

Plays a single match between two strategies:

```java
public class Match {
    private PayoffMatrix payoffMatrix;
    private double noiseProbability = 0.0;
    
    public Match() {
        this.payoffMatrix = new PayoffMatrix();
    }
    
    public Match(PayoffMatrix payoffMatrix) {
        this.payoffMatrix = payoffMatrix;
    }
    
    public void setNoiseProbability(double probability) {
        this.noiseProbability = probability;
    }
    
    public MatchResult play(Strategy playerA, Strategy playerB, int rounds) {
        GameHistory historyA = new GameHistory();
        GameHistory historyB = new GameHistory();
        List<Action> movesA = new ArrayList<>();
        List<Action> movesB = new ArrayList<>();
        
        int scoreA = 0;
        int scoreB = 0;
        
        playerA.reset();
        playerB.reset();
        
        Random random = new Random();
        
        for (int i = 0; i < rounds; i++) {
            Action moveA = playerA.nextMove(historyA);
            Action moveB = playerB.nextMove(historyB);
            
            // Apply noise (randomly flip moves)
            if (noiseProbability > 0) {
                if (random.nextDouble() < noiseProbability) {
                    moveA = (moveA == Action.COOPERATE) ? Action.DEFECT : Action.COOPERATE;
                }
                if (random.nextDouble() < noiseProbability) {
                    moveB = (moveB == Action.COOPERATE) ? Action.DEFECT : Action.COOPERATE;
                }
            }
            
            // Calculate payoffs
            int[] payoffs = payoffMatrix.getPayoff(moveA, moveB);
            scoreA += payoffs[0];
            scoreB += payoffs[1];
            
            // Record moves
            movesA.add(moveA);
            movesB.add(moveB);
            historyA.recordMove(moveA, moveB);
            historyB.recordMove(moveB, moveA);
        }
        
        return new MatchResult(playerA, playerB, movesA, movesB, scoreA, scoreB);
    }
}
```

## 4.2 Tournament Engine

Runs round-robin tournaments:

```java
public class Tournament {
    private Match match;
    private int roundsPerMatch;
    
    public Tournament(int roundsPerMatch) {
        this.match = new Match();
        this.roundsPerMatch = roundsPerMatch;
    }
    
    public TournamentResult run(List<Strategy> strategies) {
        Map<Strategy, Integer> scores = new HashMap<>();
        Map<Strategy, List<Action>> allMoves = new HashMap<>();
        List<MatchResult> matchResults = new ArrayList<>();
        
        // Initialize scores
        for (Strategy s : strategies) {
            scores.put(s, 0);
            allMoves.put(s, new ArrayList<>());
        }
        
        // Round-robin: each pair plays each other twice (home and away)
        for (int i = 0; i < strategies.size(); i++) {
            for (int j = i + 1; j < strategies.size(); j++) {
                Strategy a = strategies.get(i);
                Strategy b = strategies.get(j);
                
                // Play once (A vs B)
                MatchResult result1 = match.play(a, b, roundsPerMatch);
                matchResults.add(result1);
                scores.put(a, scores.get(a) + result1.getScoreA());
                scores.put(b, scores.get(b) + result1.getScoreB());
                allMoves.get(a).addAll(result1.getMovesA());
                allMoves.get(b).addAll(result1.getMovesB());
                
                // Play again (B vs A) - swap roles
                MatchResult result2 = match.play(b, a, roundsPerMatch);
                matchResults.add(result2);
                scores.put(a, scores.get(a) + result2.getScoreB());
                scores.put(b, scores.get(b) + result2.getScoreA());
                allMoves.get(a).addAll(result2.getMovesB());
                allMoves.get(b).addAll(result2.getMovesA());
            }
        }
        
        return new TournamentResult(strategies, scores, matchResults);
    }
}
```

---

# 5. JavaFX UI Architecture

## 5.1 Project Structure

```
src/main/java/com/axelrod/
├── Main.java
├── AxelrodApplication.java
├── model/
│   ├── Action.java
│   ├── Strategy.java
│   ├── GameHistory.java
│   ├── PayoffMatrix.java
│   ├── MatchResult.java
│   └── TournamentResult.java
├── strategies/
│   ├── AlwaysCooperate.java
│   ├── AlwaysDefect.java
│   ├── TitForTat.java
│   ├── TitForTwoTats.java
│   ├── GrimTrigger.java
│   ├── RandomStrategy.java
│   ├── Pavlov.java
│   └── GenerousTFT.java
├── engine/
│   ├── Match.java
│   └── Tournament.java
├── ui/
│   ├── MainController.java
│   ├── StrategyListController.java
│   ├── SettingsController.java
│   └── ResultsController.java
├── view/
│   ├── MainView.fxml
│   └── components/
└── util/
    └── CsvExporter.java
```

## 5.2 Main Window Layout

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Logo] Iterative Prisoner's Dilemma Simulator         [−][□][×]  │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────────┐  ┌───────────────┐  │
│  │   STRATEGIES    │  │   TOURNAMENT SETUP  │  │    RESULTS    │  │
│  │                 │  │                     │  │               │  │
│  │  ☑ Always C     │  │  Rounds: [200    ]   │  │  Rank Strategy│  │
│  │  ☑ Always D     │  │  Noise:  [0.01  ]   │  │   1. TFT   487│  │
│  │  ☑ Tit For Tat  │  │                     │  │   2. AlwaysC 456│  │
│  │  ☑ Random       │  │  Payoff Matrix:     │  │   3. Pavlov 423│  │
│  │  ☐ Tit 2 Tats   │  │  R:[3] T:[5] P:[1] S:[0]  │   4. Random  312│  │
│  │  ☐ Grim Trigger │  │                     │  │               │  │
│  │                 │  │  [▶ Run Tournament] │  │  [Chart]      │  │
│  │  [+ Add Custom] │  │                     │  │               │  │
│  │  [Remove]       │  │                     │  │               │  │
│  └─────────────────┘  └─────────────────────┘  └───────────────┘  │
├─────────────────────────────────────────────────────────────────────┤
│  Status: Ready                                    Progress: ░░░░  │
└─────────────────────────────────────────────────────────────────────┘
```

## 5.3 Controller Classes

```java
public class MainController {
    @FXML private ListView<Strategy> strategyList;
    @FXML private Spinner<Integer> roundsSpinner;
    @FXML private Spinner<Double> noiseSpinner;
    @FXML private TableView<ScoreEntry> resultsTable;
    @FXML private BarChart<String, Number> scoreChart;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    
    private ObservableList<Strategy> selectedStrategies;
    private TournamentEngine tournamentEngine;
    
    @FXML
    public void initialize() {
        // Setup strategy list with checkboxes
        // Setup results table columns
        // Setup chart
    }
    
    @FXML
    public void runTournament() {
 Get        // selected strategies
        // Get settings
        // Run tournament in background thread
        // Update UI with results
    }
}
```

## 5.4 Strategy Selection ListView

```java
public class StrategyListCell extends ListCell<Strategy> {
    private CheckBox checkBox;
    
    @Override
    protected void updateItem(Strategy strategy, boolean empty) {
        super.updateItem(strategy, empty);
        if (strategy != null) {
            checkBox.setText(strategy.getName());
            checkBox.setSelected(isSelected());
        }
    }
}
```

---

# 6. Visualization Features

## 6.1 Score Bar Chart

```java
public class ScoreChartService {
    public BarChart<String, Number> createScoreChart(TournamentResult result) {
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Strategy s : result.getRanking()) {
            series.getData().add(new XYChart.Data<>(
                s.getName(), 
                result.getScore(s)
            ));
        }
        
        chart.getData().add(series);
        return chart;
    }
}
```

## 6.2 Cooperation Rate Chart

Shows what percentage of moves were cooperative:

```java
public double calculateCooperationRate(List<Action> moves) {
    long cooperations = moves.stream()
        .filter(m -> m == Action.COOPERATE)
        .count();
    return (double) cooperations / moves.size() * 100;
}
```

## 6.3 Pairwise Heatmap

Shows scores between each pair of strategies:

```
            TFT  AlwaysC  AlwaysD  Random
TFT         600   900      300      450
AlwaysC     900   900      200      500
AlwaysD     300   200      200      250
Random      450   500      250      350
```

---

# 7. Advanced Features

## 7.1 Noise Implementation

Noise models mistakes or miscommunications:

```java
public class NoisyMatch {
    private double noiseProbability;
    
    public Action applyNoise(Action move) {
        if (Math.random() < noiseProbability) {
            return move == Action.COOPERATE ? Action.DEFECT : Action.COOPERATE;
        }
        return move;
    }
}
```

Effects:
- Low noise (1-5%): Tit For Tat still performs well
- High noise (>10%): Generous strategies outperform

## 7.2 Evolutionary Tournament

Strategies reproduce proportional to their scores:

```java
public class EvolutionaryTournament {
    private int populationSize;
    private int generations;
    
    public EvolutionResult evolve(List<Strategy> strategies, int initialRounds) {
        Map<Strategy, Integer> population = new LinkedHashMap<>();
        
        // Initialize equal population
        for (Strategy s : strategies) {
            population.put(s, populationSize / strategies.size());
        }
        
        List<GenerationResult> history = new ArrayList<>();
        
        for (int gen = 0; gen < generations; gen++) {
            // Play tournament
            TournamentResult result = runRound(population);
            
            // Record generation
            history.add(new GenerationResult(population, result.getScores()));
            
            // Reproduce based on scores
            population = reproduce(result.getScores());
        }
        
        return new EvolutionResult(history);
    }
    
    private Map<Strategy, Integer> reproduce(Map<Strategy, Integer> scores) {
        int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
        Map<Strategy, Integer> newPopulation = new LinkedHashMap<>();
        
        for (Strategy s : scores.keySet()) {
            double proportion = (double) scores.get(s) / totalScore;
            newPopulation.put(s, (int) (proportion * populationSize));
        }
        
        return newPopulation;
    }
}
```

## 7.3 CSV Export

```java
public class CsvExporter {
    public void exportResults(TournamentResult result, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            // Header
            writer.println("Strategy,Score,Cooperation Rate");
            
            // Data rows
            for (Strategy s : result.getRanking()) {
                writer.printf("%s,%d,%.2f%n",
                    s.getName(),
                    result.getScore(s),
                    result.getCooperationRate(s) * 100
                );
            }
        }
    }
}
```

---

# 8. Development Phases

## Phase 1: Core Simulation (Week 1)

| Task | Description |
|------|-------------|
| Create project structure | Maven/Gradle, JavaFX setup |
| Implement Action enum | COOPERATE, DEFECT |
| Implement Strategy interface | Base interface |
| Implement GameHistory | History tracking |
| Implement 3 basic strategies | AlwaysC, AlwaysD, TFT |
| Implement Match engine | Single match execution |
| Implement Tournament engine | Round-robin execution |
| Console testing | Verify results |

**Deliverable:** Command-line tournament runner

## Phase 2: JavaFX UI (Week 2)

| Task | Description |
|------|-------------|
| Setup JavaFX project | Scene Builder, FXML |
| Main window layout | Three-panel design |
| Strategy selection list | Checkbox list |
| Settings panel | Spinners for rounds, noise |
| Results table | Sortable ranking table |
| Run button + progress | Async tournament execution |

**Deliverable:** Functional desktop app with UI

## Phase 3: Visualization (Week 3)

| Task | Description |
|------|-------------|
| Score bar chart | JavaFX BarChart |
| Cooperation rate chart | Second chart |
| Heatmap view | Pairwise results |
| Statistics panel | Detailed match info |

**Deliverable:** Visual analysis tools

## Phase 4: Advanced Features (Week 4)

| Task | Description |
|------|-------------|
| Custom strategy builder | UI-based strategy creation |
| Noise settings | Configurable mistake rate |
| Evolutionary mode | Population dynamics |
| Export functionality | CSV/JSON export |

**Deliverable:** Full-featured application

---

# 9. Testing Plan

## Unit Tests

```java
class TitForTatTest {
    @Test
    void cooperatesOnFirstMove() {
        Strategy tft = new TitForTat();
        GameHistory history = new GameHistory();
        
        assertEquals(Action.COOPERATE, tft.nextMove(history));
    }
    
    @Test
    void copiesOpponentLastMove() {
        Strategy tft = new TitForTat();
        GameHistory history = new GameHistory();
        
        history.recordMove(Action.COOPERATE, Action.DEFECT);
        
        assertEquals(Action.DEFECT, tft.nextMove(history));
    }
}
```

## Integration Tests

```java
class TournamentIntegrationTest {
    @Test
    void titForTatOutperformsAlwaysDefect() {
        List<Strategy> strategies = Arrays.asList(
            new TitForTat(),
            new AlwaysDefect()
        );
        
        Tournament tournament = new Tournament(200);
        TournamentResult result = tournament.run(strategies);
        
        assertTrue(result.getScore(new TitForTat()) > 
                   result.getScore(new AlwaysDefect()));
    }
}
```

---

# 10. Configuration

## pom.xml dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

# 11. Key Insights from Axelrod's Research

1. **Tit For Tat wins consistently** - Its success comes from being nice, retaliatory, and forgiving.

2. **Nice strategies win** - Never defect first. Being nice is evolutionarily stable.

3. **Retaliation maintains cooperation** - Once established, cooperation must be defended.

4. **Forgiveness helps recover** - After accidental defection (noise), forgiving strategies recover faster.

5. ** Clarity matters** - Strategies should be understandable to others.

---

This plan provides a complete roadmap for building a professional-quality Iterative Prisoner's Dilemma simulator with JavaFX.
