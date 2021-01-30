import java.awt.*;
import java.io.*;

public class Bullettype implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String bulletFrom = "enemy";
    int power;
    transient Image bimage;

    public Bullettype(int power_in, int level, Image bimage_in) {
        super();
        power = power+5*(level-1);
        bimage = bimage_in;
    }

    public Bullettype(int power_in, String bulletFrom_in, Image bimage_in) {
        super();
        power = power_in;
        bimage = bimage_in;
        bulletFrom = bulletFrom_in;
    }

}

