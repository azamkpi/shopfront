package uz.azam.shopfront.util;

public final class PhoneUtils {

    private PhoneUtils(){}

    /**
     * +998 90 123-45-67, 998901234567, 901234567 → 901234567
     * Noto'g'ri raqam uchun null.
     */
    public static String normalize(String raw) {
        if (raw == null) return null;
        String d = raw.replaceAll("\\D", "");
        if (d.startsWith("998") && d.length() == 12) {
            d = d.substring(3);
        }
        return d.length() == 9 ? d : null;
    }

    public static boolean isValid(String raw) {
        return normalize(raw) != null;
    }

    /** 901234567 → +998 90 123 45 67 */
    public static String format(String raw) {
        String d = normalize(raw);
        if (d == null) return raw;
        return "+998 " + d.substring(0, 2) + " " + d.substring(2, 5)
                + " " + d.substring(5, 7) + " " + d.substring(7);
    }
}
