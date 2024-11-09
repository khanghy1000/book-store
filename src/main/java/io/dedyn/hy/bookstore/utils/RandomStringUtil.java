package io.dedyn.hy.bookstore.utils;

public class RandomStringUtil {
    public static String generate(int length) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index
                = (int) (AlphaNumericString.length()
                * Math.random());

            sb.append(AlphaNumericString
                .charAt(index));
        }

        return sb.toString();
    }
}
