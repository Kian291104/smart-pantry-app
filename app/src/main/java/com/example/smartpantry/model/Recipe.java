package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/** A recipe with its required ingredients and preparation steps. */
public class Recipe {
    private final long id;
    private final String name;
    private final int minutes;
    private final String steps; // steps separated by '\n'
    private final List<RecipeIngredient> ingredients;

    public Recipe(long id, String name, int minutes, String steps, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
        this.steps = steps;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getMinutes() { return minutes; }
    public String getSteps() { return steps; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
