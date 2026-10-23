package com.example.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** JVM unit tests for the strict-matching rule (run with: Right-click > Run 'RecipeMatcherTest'). */
public class RecipeMatcherTest {

    private static Recipe tomatoPasta() {
        return new Recipe(1, "Tomato Pasta", 25, "Cook.", Arrays.asList(
                new RecipeIngredient("pasta", 200, "g"),
                new RecipeIngredient("tomato", 3, "pcs"),
                new RecipeIngredient("onion", 1, "pcs"),
                new RecipeIngredient("garlic", 2, "clove"),
                new RecipeIngredient("olive oil", 15, "ml")));
    }

    private static List<PantryItem> fullPantry() {
        return new ArrayList<>(Arrays.asList(
                new PantryItem("Pasta", 500, "g", null),
                new PantryItem("Tomatoes", 3, "pcs", null),   // plural + capital
                new PantryItem("onion", 2, "pcs", null),
                new PantryItem("Garlic", 4, "clove", null),
                new PantryItem("Olive Oil", 1, "l", null)));  // litres vs ml
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        RecipeMatcher m = new RecipeMatcher(fullPantry());
        assertEquals(1, m.suggest(Arrays.asList(tomatoPasta())).size());
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.remove(4); // remove olive oil
        RecipeMatcher m = new RecipeMatcher(pantry);
        assertTrue(m.suggest(Arrays.asList(tomatoPasta())).isEmpty());
        assertEquals(1, m.almostThere(Arrays.asList(tomatoPasta())).size());
    }

    @Test
    public void insufficientQuantity_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.set(1, new PantryItem("tomato", 2, "pcs", null)); // need 3
        assertTrue(new RecipeMatcher(pantry).suggest(Arrays.asList(tomatoPasta())).isEmpty());
    }

    @Test
    public void unitConversion_kgCoversGrams() {
        assertTrue(new RecipeMatcher(Arrays.asList(new PantryItem("pasta", 0.25, "kg", null)))
                .hasEnough(new RecipeIngredient("pasta", 200, "g")));
        assertFalse(new RecipeMatcher(Arrays.asList(new PantryItem("pasta", 0.1, "kg", null)))
                .hasEnough(new RecipeIngredient("pasta", 200, "g")));
    }

    @Test
    public void duplicateRowsAreSummed() {
        RecipeMatcher m = new RecipeMatcher(Arrays.asList(
                new PantryItem("milk", 100, "ml", null),
                new PantryItem("Milk", 200, "ml", null)));
        assertTrue(m.hasEnough(new RecipeIngredient("milk", 250, "ml")));
    }

    @Test
    public void incompatibleUnits_doNotCount() {
        RecipeMatcher m = new RecipeMatcher(Arrays.asList(new PantryItem("sugar", 500, "ml", null)));
        assertFalse(m.hasEnough(new RecipeIngredient("sugar", 15, "g")));
    }

    @Test
    public void normalizer_handlesPluralsCaseAndAliases() {
        assertEquals("tomato", IngredientNormalizer.normalize("  Tomatoes "));
        assertEquals("egg", IngredientNormalizer.normalize("EGGS"));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
        assertEquals("spring onion", IngredientNormalizer.normalize("Scallions"));
        assertEquals("beef mince", IngredientNormalizer.normalize("ground beef"));
        assertEquals("pasta", IngredientNormalizer.normalize("Spaghetti"));
    }
    @Test
    public void expiredItems_areIgnored() {
        RecipeMatcher m = new RecipeMatcher(Arrays.asList(
                new PantryItem("milk", 500, "ml", "2000-01-01")));   // expired long ago
        assertFalse(m.hasEnough(new RecipeIngredient("milk", 100, "ml")));
    }
}
