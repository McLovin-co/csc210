# Lab 09 Written Answers

---

## Question 5 — CircleDisplay.java

**a. What is `CircleDisplay()` doing?**
`CircleDisplay()` is the constructor. It sets the preferred size of the panel to 400×400 pixels and sets the background color to white. It runs once when the program starts to initialize the component before it appears on screen.

**b. When/how often does `paintComponent()` run?**
`paintComponent()` runs whenever Swing decides the component needs to be redrawn — when the window first appears, when it's resized, when it's uncovered after being hidden, or any time `repaint()` is called explicitly. You don't call it directly; Swing calls it for you on the Event Dispatch Thread.

**c. What is the JPanel class?**
`JPanel` is a generic lightweight container in the `javax.swing` package. It is a blank panel you can add components to or override `paintComponent()` on to draw custom graphics. It handles double-buffering automatically, which prevents flickering during redraws.

**d.** (Code change — alter color/size and recompile.) Example: change `g.setColor(Color.BLUE)` to `g.setColor(Color.RED)` and `int diameter = 100` to `int diameter = 200`.

---

## Question 6 — ContinuousLogisticGrowth.java

**a. What term makes this different from exponential growth?**
The term `(1 - x/K)` — the logistic damping factor. In pure exponential growth the equation is `dx/dt = r*x`. Here it's `dx/dt = r*x*(1 - x/K)`, so as `x` approaches the carrying capacity `K`, the factor approaches zero and growth slows to a halt.

**b. Is the behavior unpredictable and chaotic like the discrete model?**
No. The continuous logistic model is smooth and predictable. No matter how you adjust `r`, `K`, or `x0`, the population follows a clean S-shaped (sigmoid) curve up to `K`. Chaos is a feature of the *discrete* logistic map, not the continuous ODE version.

**c. Does the population always converge to a value?**
Yes — it always converges to `K` (the carrying capacity), as long as `r > 0` and `x0 > 0`.

**d. Does the population ever collapse?**
No. The continuous logistic model never collapses to zero from a positive starting population; zero is an unstable equilibrium. The population can only approach zero if `x0 = 0`.

**e. Does the population ever hit runaway exponential growth to infinity?**
No. The `(1 - x/K)` term prevents this. Growth slows as the population approaches `K` and the model is bounded.

**f. Comparison with RabbitDiscreteSimulationGUI.java:**
The discrete simulation shows much more varied behavior. The rabbit population can oscillate, overshoot, crash, and stabilize — or remain noisy — depending on the carrot supply. The continuous model always produces a smooth sigmoid. The discrete model adds stochastic agent-level effects (random foraging order, discrete mating pairs) that the ODE cannot capture.

---

## Question 7 — SierpinskiTriangle.java

**a. How does the program draw the same pattern at ever smaller scales?**
Through recursion. `drawSierpinski()` calls itself three times with half the side length each time. Each call draws the same triangle shape at a smaller scale, creating self-similarity automatically.

**b. What function is responsible for the shape of any particular triangle (not painting)?**
`fillTriangle()` — it calculates the three vertex coordinates from an apex point `(x, y)` and side length, then calls `g.fillPolygon()` to draw one solid triangle.

**c. What function is responsible for the shape of the entire image?**
`drawSierpinski()` — it orchestrates the recursive structure, deciding where each sub-triangle's apex goes and recursing until depth = 0.

**d. How is the recursive nature similar to the discrete logistic map?**
Both apply the same rule repeatedly to their own output. The logistic map feeds `x_n` back into the formula to produce `x_{n+1}`. `drawSierpinski()` feeds its own output (smaller triangles) back into itself. Both produce complex, self-similar behavior from a very simple rule applied iteratively.

---

## Question 8 — RandomHexPrinter.java & RawRandomHexStreamer.java

**a. What are these programs doing?**
Both request random bytes from the random.org web API and print them as hexadecimal. `RandomHexPrinter` makes one request and prints the result. `RawRandomHexStreamer` loops forever, making requests every second and streaming the output continuously (hence needing CTRL-C to stop).

**b. How are they getting their randomness?**
From `random.org`, which generates randomness from atmospheric noise — a physical, hardware-based source external to the computer.

**c. Is the randomness truly random?**
Yes — random.org uses atmospheric noise, which is considered a true (non-deterministic) random source, unlike `java.util.Random` which is pseudorandom (deterministic given a seed).

**d. GetWebPage.java** — see the file `GetWebPage.java` included in this project.

---

## Question 9 — CarrotGenerator.java

**a. Are the random numbers pseudorandom or random?**
Pseudorandom — they come from `java.util.Random`, which is a deterministic algorithm seeded by the system clock. The numbers look random but are fully deterministic given the seed.

**b. What do the random values get used as?**
As the `x` and `y` coordinates (screen position) for each new carrot that is placed on the panel.

**c. How often does the timer execute?**
Every 2000 milliseconds (2 seconds).

**d. What code executes when the timer goes off?**
The `actionPerformed()` method — it generates a random position, creates a new `Carrot` object at that position, adds it to the `carrots` list, and calls `repaint()`.

**e. What variable keeps track of all the Carrots?**
`carrots` — a `java.util.List<Carrot>` (`ArrayList`).

---

## Question 10 — RabbitsMoving.java

**a. What determines a rabbit's starting location?**
`rand.nextInt(WIDTH)` and `rand.nextInt(HEIGHT)` — random positions within the panel bounds, passed to the `Rabbit` constructor.

**b. What line initializes the rabbit's velocity?**
```java
dx = rand.nextInt(5) - 2;
dy = rand.nextInt(5) - 2;
```
This gives a random integer velocity between -2 and +2 in each axis.

**c. How many times per second are positions updated?**
The timer fires every 30 ms, so approximately **33 times per second** (1000 / 30 ≈ 33 fps).

**d. What function updates a rabbit's position?**
`move(int width, int height)` in the `Rabbit` inner class.

**e. How do rabbits bounce off walls?**
```java
if (x < 0 || x > width - size)  dx = -dx;
if (y < 0 || y > height - size) dy = -dy;
```
When a rabbit hits a boundary, its velocity component in that direction is negated (flipped), reversing it. Removing these two lines causes rabbits to fly off-screen and disappear.

**f.** (Code change — adjust speed.) Change `dx = rand.nextInt(5) - 2` to `dx = rand.nextInt(15) - 7` for fast, or `dx = (rand.nextInt(3) - 1)` for slow.

---

## Question 11 — WolfMover.java

**a. How often does the program update the wolf's position?**
Every 30 ms (the timer delay). The line `timer = new javax.swing.Timer(30, this)` sets this.

**b. What code in the constructor enables keyboard input?**
```java
addKeyListener(this);
setFocusable(true);
requestFocusInWindow();
```

**c. What method does the timer execute every tick?**
`actionPerformed(ActionEvent e)` — it reads the key flags and updates `wolf.x` / `wolf.y`, then calls `repaint()`.

**d.** (Code changes — faster/bigger/different color/inverted controls.) See the modified `WolfMover.java` file included in this project.

---

## Question 12 — SpriteLoader, SpriteLoader2, SpriteAnimationPanel

**a. What type holds the image data in SpriteLoader.java?**
`BufferedImage` — a `java.awt.image.BufferedImage`.

**b. Where is the image loaded from, and what is it?**
`resources/sprite.png` — a small pixel-art humanoid character (a person with a sword, blue shirt, brown hair/legs).

**c. In SpriteLoader2.java, what type is loaded onto the window to be painted?**
`BufferedImage` — the same type, passed into the `SpritePanel` constructor and drawn with `g.drawImage()`.

**d. What do you see when loading spriteSheet.png?**
All four animation frames displayed side by side in a single wide image — four stick figures with different arm positions, laid out horizontally as one 256×64 image.

**e. What is different in SpriteAnimationPanel.java that causes animation?**
A `Timer` fires every 100 ms, incrementing `currentFrame` and calling `repaint()`. In `paintComponent()`, the source rectangle `sx = currentFrame * frameWidth` shifts across the spritesheet, so each repaint shows the next frame. This cycles through the 4 frames repeatedly, creating animation. The previous programs just drew the entire image at once with no frame cycling.

---

## Question 13 — MultispeciesContinuousSimulation.java

**a. Why doesn't the program use integration/differentiation directly?**
Because numerical ODE integration using Euler's method is much simpler to implement than symbolic calculus. Instead of solving the differential equations analytically, it approximates them by computing the derivative at each time step and adding a small increment (`x += dx * DT`). This is called **Euler's method** — a first-order numerical integrator.

**b. What does each initial value represent?**
- `C = 0.9`: initial carrot population (normalized, where 1.0 = carrying capacity)
- `R = 0.5`: initial rabbit population
- `W = 0.2`: initial wolf population
- `rC, rR, rW`: intrinsic growth rates for each species
- `KC, KR, KW`: carrying capacities (max sustainable population) for each species
- `a`: rate at which rabbits consume carrots (negative for carrots)
- `b`: rate at which rabbits benefit from eating carrots (positive for rabbits)
- `c`: rate at which wolves consume rabbits (negative for rabbits)
- `d`: rate at which wolves benefit from eating rabbits (positive for wolves)

**c. Why are the curves so smooth?**
Because the model is continuous and uses very small time steps (`DT = 0.01`). With tiny increments, the Euler approximation closely follows the true smooth ODE solution with no discrete jumps or stochastic noise.

---

## Question 14 — CarrotRabbitSimulation.java

**a. What is the main limiting factor on rabbit population?**
The carrying capacity `rabbitCarryingCapacity = 50`. The logistic factor `(1 - rabbits.size() / rabbitCarryingCapacity)` directly caps how many offspring are produced per carrot eaten — when the population exceeds 50, the factor goes negative, producing zero offspring.

**b. Does it more closely follow discrete or continuous logistic map? Why?**
Discrete — because rabbits reproduce in bursts each time they eat a carrot (event-driven), the population jumps in steps rather than growing smoothly. The carrot refill events create discrete "generations," matching the generation-based structure of difference equations.

**c. How often does the carrot population repopulate, and what is the maximum?**
Every `carrotRefillInterval = 500` ticks (adjustable in setup). The maximum number of carrots is `carrotMax = 20` (also adjustable).

**d. With faster carrot repopulation (~every second), does it match discrete or continuous?**
It more closely matches the **continuous** logistic model. With constant food availability, rabbits reproduce frequently in small amounts, producing the smooth sigmoid growth curve characteristic of the continuous ODE rather than the bursty discrete map.

**e. With a very high population cap and high carrots with short refill interval:**
The rabbit population converges to a stable equilibrium determined by the logistic factor — it doesn't go to infinity or go extinct. The carrying capacity term still limits growth mathematically even without the artificial `maxPop` cap. This matches the continuous logistic model's behavior: always converging to a stable value.

**f.** (ArcadeEcosystemSimulation tuning — see that file's notes.) Best match to the multispecies continuous graphs is achieved with moderate carrot counts (~100), medium refill intervals (~300 ticks), and starting populations close to the ODE initial values (`C ≈ 0.9*capacity`, `R ≈ 0.5*capacity`, `W ≈ 0.2*capacity`).
