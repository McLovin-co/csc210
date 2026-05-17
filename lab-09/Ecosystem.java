import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Ecosystem — owns all game entities (Wolf, Rabbits, Carrots, Fungus) and
 * runs one update tick: move everything, check collisions, spawn offspring.
 *
 * Separation of concerns: this class contains only simulation logic.
 * Rendering is handled by the caller (ArcadeGame / GamePanel).
 */
public class Ecosystem {

    // ── World dimensions ─────────────────────────────────────────────────────
    private final int worldW, worldH;

    // ── Entities ─────────────────────────────────────────────────────────────
    public final Wolf wolf;
    public final List<Rabbit> rabbits = new ArrayList<>();
    public final List<Carrot> carrots = new ArrayList<>();
    public final List<Fungus> fungi   = new ArrayList<>();

    // ── Score (rabbits caught by wolf) ───────────────────────────────────────
    private int score = 0;

    // ── Carrot spawner (uses random.org with java.util.Random fallback) ──────
    private static final int MAX_CARROTS = 30;
    private static final Random RAND = new Random();

    // ── Constructor ──────────────────────────────────────────────────────────

    public Ecosystem(int worldW, int worldH) {
        this.worldW = worldW;
        this.worldH = worldH;

        wolf = new Wolf(worldW / 2, worldH / 2);

        for (int i = 0; i < 5; i++) {
            rabbits.add(new Rabbit(RAND.nextInt(worldW), RAND.nextInt(worldH)));
        }

        // Seed a few fungus patches
        for (int i = 0; i < 3; i++) {
            fungi.add(new Fungus(RAND.nextInt(worldW), RAND.nextInt(worldH)));
        }
    }

    // ── Per-tick update ──────────────────────────────────────────────────────

    public void update(InputHandler input) {
        wolf.update(input, worldW, worldH);
        for (Rabbit r  : rabbits) r.move(worldW, worldH);
        for (Fungus f  : fungi)   f.update(worldW, worldH);

        resolveCollisions();
        pruneDeadEntities();
    }

    // ── Spawn a new carrot (called by CarrotSpawner thread via invokeLater) ──

    public void spawnCarrot(int x, int y) {
        if (carrots.size() < MAX_CARROTS) {
            carrots.add(new Carrot(x, y));
        }
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public int getScore()        { return score; }
    public int getRabbitCount()  { return rabbits.size(); }
    public int getCarrotCount()  { return carrots.size(); }
    public int getFungusCount()  { return fungi.size(); }
    public int getWorldW()       { return worldW; }
    public int getWorldH()       { return worldH; }

    // ── Collision resolution ─────────────────────────────────────────────────

    private void resolveCollisions() {
        wolfEatsRabbits();
        rabbitsEatCarrots();
        fungusEatsCarrots();
        fungusEatsWolves();
    }

    /** Wolf catches rabbits → score increases, rabbit removed. */
    private void wolfEatsRabbits() {
        Iterator<Rabbit> it = rabbits.iterator();
        while (it.hasNext()) {
            Rabbit r = it.next();
            if (wolf.getBounds().intersects(r.getBounds())) {
                it.remove();
                score++;
            }
        }
    }

    /** Rabbit eats a carrot → one new rabbit spawns nearby. */
    private void rabbitsEatCarrots() {
        List<Rabbit> newRabbits = new ArrayList<>();
        Iterator<Carrot> cit = carrots.iterator();
        while (cit.hasNext()) {
            Carrot c = cit.next();
            boolean eaten = false;
            for (Rabbit r : rabbits) {
                if (r.getBounds().intersects(c.getBounds())) {
                    newRabbits.add(new Rabbit(r.x + 10, r.y + 10));
                    cit.remove();
                    eaten = true;
                    break;
                }
            }
        }
        rabbits.addAll(newRabbits);
    }

    /** Fungus eats a carrot → may reproduce. */
    private void fungusEatsCarrots() {
        List<Fungus> newFungi = new ArrayList<>();
        Iterator<Carrot> cit = carrots.iterator();
        while (cit.hasNext()) {
            Carrot c = cit.next();
            for (Fungus f : fungi) {
                if (f.getBounds().intersects(c.getBounds())) {
                    cit.remove();
                    Fungus offspring = f.tryReproduce();
                    if (offspring != null) newFungi.add(offspring);
                    break;
                }
            }
        }
        fungi.addAll(newFungi);
    }

    /**
     * Fungus eats the wolf → wolf teleports back to centre (respawn) and
     * score resets, simulating the wolf being "infected" and retreating.
     * (Permanently killing the player-wolf would end the game with no
     * recovery, so a respawn penalty is used instead.)
     */
    private void fungusEatsWolves() {
        for (Fungus f : fungi) {
            if (f.getBounds().intersects(wolf.getBounds())) {
                // Penalty: lose 3 score points and respawn wolf at centre
                score = Math.max(0, score - 3);
                wolf.x = worldW / 2;
                wolf.y = worldH / 2;
                break;
            }
        }
    }

    /** Remove dead fungus from the list. */
    private void pruneDeadEntities() {
        fungi.removeIf(f -> !f.isAlive());
    }
}
