package com.example.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Turns messy user-typed ingredient names into a canonical key so that
 * "Tomatoes", " tomato " and "TOMATO" all match the same recipe ingredient.
 * Pantry names and recipe names are BOTH passed through this, so the important
 * thing is consistency, not perfect English grammar.
 */
public final class IngredientNormalizer {

    /** Synonyms -> canonical name (keys are already singularised). */
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("scallion", "spring onion");
        ALIASES.put("green onion", "spring onion");
        ALIASES.put("capsicum", "bell pepper");
        ALIASES.put("green pepper", "bell pepper");
        ALIASES.put("red pepper", "bell pepper");
        ALIASES.put("ground beef", "beef mince");
        ALIASES.put("minced beef", "beef mince");
        ALIASES.put("mince", "beef mince");
        ALIASES.put("chili", "chilli");
        ALIASES.put("chile", "chilli");
        ALIASES.put("cheddar", "cheese");
        ALIASES.put("cheddar cheese", "cheese");
        ALIASES.put("spaghetti", "pasta");
        ALIASES.put("macaroni", "pasta");
        ALIASES.put("penne", "pasta");
        ALIASES.put("chicken fillet", "chicken breast");
        ALIASES.put("mayo", "mayonnaise");
        ALIASES.put("corn", "sweetcorn");
        ALIASES.put("rolled oat", "oat");
    }

    private IngredientNormalizer() { }

    public static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z ]", " ")   // drop digits/punctuation
                .replaceAll("\\s+", " ")      // collapse spaces
                .trim();
        if (s.isEmpty()) return s;

        // Singularise only the last word: "cherry tomatoes" -> "cherry tomato"
        int lastSpace = s.lastIndexOf(' ');
        String head = lastSpace >= 0 ? s.substring(0, lastSpace + 1) : "";
        String last = lastSpace >= 0 ? s.substring(lastSpace + 1) : s;
        s = head + singularize(last);

        String alias = ALIASES.get(s);
        return alias != null ? alias : s;
    }

    static String singularize(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes") || w.endsWith("sses"))
            return w.substring(0, w.length() - 2);                                   // peaches -> peach
        if (w.endsWith("s") && !w.endsWith("ss") && !w.endsWith("us"))
            return w.substring(0, w.length() - 1);                                   // eggs -> egg
        return w;
    }
}
