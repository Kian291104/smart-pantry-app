package com.example.smartpantry.logic;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core business logic: the STRICT-MATCHING RULE.
 * A recipe is "suggested" only if EVERY required ingredient is in the pantry
 * in AT LEAST the required quantity. Anything less is excluded.
 *
 * Pure Java (no Android classes) so it can be unit tested on the JVM.
 */
public class RecipeMatcher {

    private static final double EPSILON = 1e-9; // tolerance for floating-point rounding

    /** Result of checking one recipe against the pantry. */
    public static class MatchResult {
        public final Recipe recipe;
        public final List<RecipeIngredient> missing;

        MatchResult(Recipe recipe, List<RecipeIngredient> missing) {
            this.recipe = recipe;
            this.missing = missing;
        }

        public boolean isFullMatch() { return missing.isEmpty(); }
    }

    /** Pantry grouped by normalised name, so two "milk" rows are added together. */
    private final Map<String, List<PantryItem>> pantryByName = new HashMap<>();

    public RecipeMatcher(List<PantryItem> pantry) {
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            pantryByName.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
    }

    /** True if the pantry holds enough of this single ingredient. */
    public boolean hasEnough(RecipeIngredient needed) {
        List<PantryItem> matches = pantryByName.get(IngredientNormalizer.normalize(needed.getName()));
        if (matches == null) return false; // ingredient not in pantry at all

        UnitConverter.Dimension neededDim = UnitConverter.dimensionOf(needed.getUnit());
        Double neededBase = UnitConverter.toBase(needed.getQuantity(), needed.getUnit());

        double available = 0;
        for (PantryItem item : matches) {
            if (neededBase != null && neededDim == UnitConverter.dimensionOf(item.getUnit())) {
                // Same kind of unit (e.g. kg vs g): convert both to the base unit
                available += UnitConverter.toBase(item.getQuantity(), item.getUnit());
            } else if (item.getUnit() != null && item.getUnit().equalsIgnoreCase(needed.getUnit())) {
                // Unknown unit, but spelled identically on both sides
                available += item.getQuantity();
            }
            // Different dimensions (e.g. "ml" vs "g") cannot be compared safely -> ignored
        }
        double required = neededBase != null ? neededBase : needed.getQuantity();
        return available + EPSILON >= required;
    }

    public MatchResult check(Recipe recipe) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            if (!hasEnough(ing)) missing.add(ing);
        }
        return new MatchResult(recipe, missing);
    }

    /** STRICT suggestions: only recipes with zero missing ingredients. */
    public List<Recipe> suggest(List<Recipe> recipes) {
        List<Recipe> out = new ArrayList<>();
        for (Recipe r : recipes) {
            if (!r.getIngredients().isEmpty() && check(r).isFullMatch()) out.add(r);
        }
        return out;
    }

    /** Bonus: recipes missing exactly ONE ingredient (kept separate from suggestions). */
    public List<MatchResult> almostThere(List<Recipe> recipes) {
        List<MatchResult> out = new ArrayList<>();
        for (Recipe r : recipes) {
            MatchResult m = check(r);
            if (m.missing.size() == 1 && r.getIngredients().size() > 1) out.add(m);
        }
        return out;
    }
}
