import java.io.Serializable;

public class Controlplane implements Serializable {
    int baseDamage;
    int baseDefense;
    int speedincrement;
    int level;
    int exp;
    int score;
    int tempDefense;
    boolean over;


    public Controlplane() {
        super();
        baseDamage = 20;
        baseDefense = 5;
        speedincrement = 0;
        over = false;
        level = 1;
        exp = 0;
        score = 100;
    }

}