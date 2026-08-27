package uz.azam.shopfront.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaptionParser {

    public record ParsedProduct(
            String name,
            BigDecimal price,
            boolean negotiable,
            String categorySlug,
            String description
    ) { }

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
                categorySlug=line.substring(1).toLowerCase();
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

            descLines.add(line);
        }

        String description = descLines.isEmpty() ? null : String.join("\n", descLines);
        return new ParsedProduct(name, price, negotiable, categorySlug, description);

    }

    private BigDecimal tryParsePrice(String line) {
        String cleaned = line.replaceAll("[\\s ,]", "")
                .replaceAll("(?i)(so'm|som|sum|uzs)", "");
        if (!cleaned.matches("\\d{3,12}")) {
            return null;
        }
        return new BigDecimal(cleaned);
    }
}
