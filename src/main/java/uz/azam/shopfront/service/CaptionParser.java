package uz.azam.shopfront.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CaptionParser {

    public record ParsedProduct(
            String name,
            BigDecimal price,
            boolean negotiable,
            String categorySlug,
            String description
    ) {
    }

    private static final Pattern PRICE_IN_TEXT = Pattern.compile(
            "(\\d[\\d\\s.,]{0,12}?)\\s*(mln|млн|million|миллион|ming|минг|мин|mig|so'm|som|sum|сум|сўм)",
            Pattern.CASE_INSENSITIVE);

    private record PriceHit(BigDecimal value, int start) {
    }

    public ParsedProduct parse(String caption) {
        if (caption == null || caption.isBlank()) {
            throw new IllegalArgumentException("Izoh bo'sh");
        }

        List<String> lines = caption.lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Izoh bo'sh");
        }

        String name = lines.get(0);
        BigDecimal price = null;
        boolean negotiable = false;
        String categorySlug = null;
        List<String> descLines = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("#")) {
                categorySlug = line.substring(1).toLowerCase();
                continue;
            }

            if (line.equalsIgnoreCase("kelishiladi")) {
                negotiable = true;
                continue;
            }

            BigDecimal parsed = tryParsePrice(line);
            if (parsed != null && price == null) {
                price = parsed;
                continue;
            }

            if (price == null) {
                PriceHit hit = findPrice(line);
                if (hit != null && hit.start() == 0) {
                    price = hit.value();
                    continue;
                }
            }

            descLines.add(line);
        }

        //Alohida qatorda topilmadi - nom ichidan qidiramiz
        if (price == null && !negotiable) {
            PriceHit hit = findPrice(name);
            if (hit != null) {
                price = hit.value();
            }
        }

        String description = descLines.isEmpty() ? null : String.join("\n", descLines);
        return new ParsedProduct(name, price, negotiable, categorySlug, description);

    }

    /**
     * Faqat raqamdan iborat qator: 650000 / 650 000 / 650,000 so'm
     */
    private BigDecimal tryParsePrice(String line) {
        String cleaned = line.replaceAll("[\\s ,]", "")
                .replaceAll("(?i)(so'm|som|sum|uzs|сум|сўм)", "");
        return cleaned.matches("\\d{3,12}") ? new BigDecimal(cleaned) : null;
    }

    /**
     * Matn ichidan "85 минг", "1.5 mln", "40ming" ni topadi
     */
    private PriceHit findPrice(String text) {
        Matcher m = PRICE_IN_TEXT.matcher(text);
        if (!m.find()) return null;

        String raw = m.group(1).replaceAll("[\\s ]", "").replace(",", ".");
        if (raw.isEmpty() || raw.equals(".")) return null;

        String unit = m.group(2).toLowerCase();
        BigDecimal v;
        try {
            v = new BigDecimal(raw);
        } catch (NumberFormatException e) {
            return null;
        }

        if (unit.startsWith("ml") || unit.startsWith("мл") || unit.startsWith("mil")
                || unit.startsWith("мил")) {
            v = v.multiply(BigDecimal.valueOf(1_000_000));
        } else if (unit.startsWith("min") || unit.startsWith("мин") || unit.startsWith("mig")) {
            v = v.multiply(BigDecimal.valueOf(1000));
        }

        return new PriceHit(v.setScale(0, RoundingMode.HALF_UP), m.start());
    }
}
