package ch.heigvd.dai.game;

/**
 * Implements the stats and the actions of a Nokemon.
 */
public class Nokemon {

    public final static int BASE_DAMAGE = 10;
    public final static int DEVIATION_DAMAGE = 10;

    public final static int BASE_HEAL = 5;
    public final static int DEVIATION_HEAL = 20;

    private final static int DEFAULT_HP = 80;

    private final int maxHp;
    private int hp;

    public Nokemon() {
        this.hp = DEFAULT_HP;
        this.maxHp = this.hp;
    }

    public Nokemon(int hp) {
        this.hp = hp;
        this.maxHp = this.hp;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
