package uz.azam.shopfront.util;

import java.math.BigDecimal;

public final class MoneyUtils {

    private MoneyUtils(){}

    public static String format(BigDecimal v) {
        if (v==null) return "—";
        return String.format("%,d so'm", v.longValue()).replace(',', ' ');

    }
}
