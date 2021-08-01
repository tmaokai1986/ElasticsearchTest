package xyz.lockon;

public class HashMain {
    public static void main(String[] args) {
        System.out.println(DJB_HASH("0") % 5);
        System.out.println(DJB_HASH("1") % 5);
        System.out.println(DJB_HASH("2") % 5);

    }

    public static int DJB_HASH(String value) {
        long hash = 5381;
        for (int i = 0; i < value.length(); i++) {
            hash = ((hash << 5) + hash) + value.charAt(i);
        }
        return (int)hash;
    }
}
