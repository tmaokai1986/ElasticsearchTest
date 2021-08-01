package xyz.lockon;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 客户ID省生成器
 */
public class CustomerIdGenerator {
    private long startPos;
    private long customerIdNum;
    private long currentIndex = 0;

    public CustomerIdGenerator(long startPos, long customerIdNum) {
        this.startPos = startPos;
        this.customerIdNum = customerIdNum;
    }

    public String nextCustomerId() {
        return stringToMD5(String.valueOf(startPos + (currentIndex++) % customerIdNum));
    }

    private static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
