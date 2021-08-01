package xyz.lockon;

import org.junit.Assert;
import org.junit.Test;


public class CustomerIdGeneratorTest {

    @Test
    public void nextCustomerId() {
        CustomerIdGenerator customerIdGenerator = new CustomerIdGenerator(100, 10);
        String[] first = new String[10];
        for (int i = 0; i < 10; i++) {
            first[i] = customerIdGenerator.nextCustomerId();
        }
        String[] second = new String[10];
        for (int i = 0; i < 10; i++) {
            second[i] = customerIdGenerator.nextCustomerId();
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(first[i], second[i]);
        }
    }
}