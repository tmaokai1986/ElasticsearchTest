package xyz.lockon;

import org.junit.Assert;
import org.junit.Test;

public class RandomLongGeneratorTest {

    @Test
    public void nextNumber() {
        RandomLongGenerator randomLongGenerator = new RandomLongGenerator(100L, 1000L);
        for (int i = 0; i < 10000; i++) {
            long num = randomLongGenerator.nextNumber();
            if (num < 100 || num > 1000) {
                Assert.assertTrue("Num =" + num, false);
            }
        }
        randomLongGenerator = new RandomLongGenerator(100000000L, 1000000000L);
        for (int i = 0; i < 10000; i++) {
            long num = randomLongGenerator.nextNumber();
            if (num < 100000000L || num > 1000000000L) {
                Assert.assertTrue("" + num, false);
            }
        }
    }
}