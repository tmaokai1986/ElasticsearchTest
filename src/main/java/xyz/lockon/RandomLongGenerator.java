package xyz.lockon;

import java.util.Random;

public class RandomLongGenerator {
    private long startNumer;
    private long endNumber;
    private Random random;

    public RandomLongGenerator(long startNumer, long endNumber) {
        this.startNumer = startNumer;
        this.endNumber = endNumber;
        random = new Random();
    }

    public long nextNumber() {
        return startNumer + random.nextLong() % (endNumber - startNumer);
    }
}
