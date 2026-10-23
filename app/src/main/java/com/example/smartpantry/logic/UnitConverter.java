package com.example.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities to a common base unit so "1 kg" and "500 g" can be compared.
 * Mass -> grams, volume -> millilitres, countable items -> pieces.
 */
public final class UnitConverter {

    public enum Dimension { MASS, VOLUME, COUNT }

    private static final class UnitInfo {
        final Dimension dimension;
        final double toBase; // multiply by this to get the base unit
        UnitInfo(Dimension d, double f) { dimension = d; toBase = f; }
    }

    private static final Map<String, UnitInfo> UNITS = new HashMap<>();

    static {
        // Mass (base: gram)
        put(Dimension.MASS, 1, "g", "gram", "grams");
        put(Dimension.MASS, 1000, "kg", "kilogram", "kilograms");
        put(Dimension.MASS, 0.001, "mg");
        put(Dimension.MASS, 28.35, "oz", "ounce", "ounces");
        put(Dimension.MASS, 453.6, "lb", "lbs", "pound", "pounds");
        // Volume (base: millilitre)
        put(Dimension.VOLUME, 1, "ml", "millilitre", "milliliter", "millilitres", "milliliters");
        put(Dimension.VOLUME, 1000, "l", "litre", "liter", "litres", "liters");
        put(Dimension.VOLUME, 5, "tsp", "teaspoon", "teaspoons");
        put(Dimension.VOLUME, 15, "tbsp", "tablespoon", "tablespoons");
        put(Dimension.VOLUME, 250, "cup", "cups");
        // Countable items (base: piece)
        put(Dimension.COUNT, 1, "", "pc", "pcs", "piece", "pieces", "whole", "each", "item", "items",
                "slice", "slices", "clove", "cloves", "can", "cans", "tin", "tins");
    }

    private static void put(Dimension d, double factor, String... names) {
        for (String n : names) UNITS.put(n, new UnitInfo(d, factor));
    }

    private UnitConverter() { }

    private static UnitInfo info(String unit) {
        String key = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT).replace(".", "");
        return UNITS.get(key);
    }

    /** @return the unit's dimension, or null if the unit is unknown. */
    public static Dimension dimensionOf(String unit) {
        UnitInfo i = info(unit);
        return i == null ? null : i.dimension;
    }

    /** @return quantity in the base unit (g / ml / pcs), or null if the unit is unknown. */
    public static Double toBase(double quantity, String unit) {
        UnitInfo i = info(unit);
        return i == null ? null : quantity * i.toBase;
    }
}
