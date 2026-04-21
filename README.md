# Tetris Game — Java Port

A Java port of the original Python Tetris game by **acedevph**, former **@JellyAce-69**, rebuilt
using Java Swing with all the original gameplay intact and additional features
from a real Tetris game added on top.

> **Original Python source:**
> [acedevph/Tetris-Game — main.py](https://github.com/acedevph/Tetris-Game/blob/main/main.py)

---

## Files

| File | Description |
|---|---|
| `Tetris.java` | Full Java Swing Tetris game (single file) |
| `main.py` | Original Python version using Pygame |

---

## How to Run

### Java version

**Step 1 — Compile:**
```bash
javac Tetris.java
```

**Step 2 — Run:**
```bash
java Tetris
```

> Requires Java 8 or higher. No external libraries needed — uses built-in Java Swing.

### Python version

```bash
pip install pygame
python main.py
```

> Requires Python 3 and Pygame.

---

## Controls

| Key | Action |
|---|---|
| `←` `→` | Move piece left / right |
| `↑` | Rotate piece |
| `↓` | Soft drop (move down faster) |
| `Space` | Hard drop (instant drop) |
| `P` | Pause / Resume |
| `R` | Restart (on Game Over screen) |

---

## Gameplay

- **10 × 20** board (same as the original)
- **7 tetrominoes** — I, O, T, J, L, S, Z
- Pieces fall automatically and speed up as your level increases
- Complete a full horizontal line to clear it and earn points
- Game ends when a new piece cannot spawn at the top

---

## Scoring

| Lines Cleared | Points (× Level) |
|---|---|
| 1 line | 100 |
| 2 lines | 300 |
| 3 lines | 500 |
| 4 lines (Tetris) | 800 |

Level increases every 10 lines cleared. Higher levels = faster fall speed.

---

## What Was Added in the Java Version

The Python version is a clean, minimal implementation. The Java version keeps
all of that logic and adds the following features:

| Feature | Python | Java |
|---|---|---|
| 7 tetrominoes | ✅ | ✅ |
| Movement & rotation | ✅ | ✅ |
| Line clearing | ✅ | ✅ |
| Score display | ✅ | ✅ |
| Ghost piece (drop preview) | ❌ | ✅ |
| Hard drop (Space) | ❌ | ✅ |
| Next piece preview | ❌ | ✅ |
| Level system | ❌ | ✅ |
| Lines counter | ❌ | ✅ |
| Speed increase per level | ❌ | ✅ |
| Classic Tetris scoring | ❌ | ✅ |
| Pause / Resume | ❌ | ✅ |
| Restart on Game Over | ❌ | ✅ |
| 3D shaded block rendering | ❌ | ✅ |
| Grid overlay lines | ❌ | ✅ |
| Controls hint panel | ❌ | ✅ |
| Game Over overlay | ❌ | ✅ |

---

## Author

**@JellyAce-69** | **acedevph**
Original Python game: [github.com/acedevph/Tetris-Game](https://github.com/acedevph/Tetris-Game)

---

## Requirements

| Version | Requirement |
|---|---|
| Java | Java 8 or higher |
| Python | Python 3 + Pygame |
