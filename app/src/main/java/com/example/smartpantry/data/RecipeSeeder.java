package com.example.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Pre-loads the recipe collection the first time the database is created. */
final class RecipeSeeder {

    private RecipeSeeder() { }

    static void seed(SQLiteDatabase db) {
        db.beginTransaction(); // one transaction = fast and all-or-nothing
        try {
            recipe(db, "Cheese Omelette", 10,
                    "Whisk the eggs with a pinch of salt.\nMelt the butter in a pan over medium heat.\nPour in the eggs and cook until almost set.\nSprinkle cheese over half, fold and serve.",
                    "egg", 3, "pcs", "cheese", 50, "g", "butter", 10, "g");
            recipe(db, "Scrambled Eggs on Toast", 10,
                    "Whisk eggs and milk together.\nMelt butter in a pan and add the egg mixture.\nStir gently until softly set.\nToast the bread and spoon the eggs on top.",
                    "egg", 2, "pcs", "bread", 2, "slice", "milk", 50, "ml", "butter", 10, "g");
            recipe(db, "French Toast", 15,
                    "Whisk eggs, milk and sugar in a shallow bowl.\nDip each slice of bread in the mixture.\nFry in a hot pan until golden on both sides.",
                    "bread", 4, "slice", "egg", 2, "pcs", "milk", 100, "ml", "sugar", 15, "g");
            recipe(db, "Pancakes", 25,
                    "Mix flour and sugar in a bowl.\nWhisk in eggs and milk to a smooth batter.\nRest the batter for 10 minutes.\nCook ladlefuls in a hot greased pan, flipping once.",
                    "flour", 200, "g", "milk", 300, "ml", "egg", 2, "pcs", "sugar", 25, "g");
            recipe(db, "Tomato Pasta", 25,
                    "Boil the pasta in salted water until al dente.\nFry chopped onion and garlic in olive oil.\nAdd chopped tomatoes and simmer for 10 minutes.\nToss the sauce with the drained pasta.",
                    "pasta", 200, "g", "tomato", 3, "pcs", "onion", 1, "pcs", "garlic", 2, "clove", "olive oil", 15, "ml");
            recipe(db, "Garlic Butter Pasta", 20,
                    "Cook the pasta until al dente.\nMelt butter and gently fry crushed garlic.\nToss pasta in the garlic butter.\nTop with grated cheese.",
                    "pasta", 200, "g", "butter", 30, "g", "garlic", 3, "clove", "cheese", 30, "g");
            recipe(db, "Egg Fried Rice", 20,
                    "Fry chopped onion until soft.\nAdd peas and cooked rice and stir-fry for 3 minutes.\nPush rice aside, scramble the eggs, then mix through.\nSeason with soy sauce.",
                    "rice", 200, "g", "egg", 2, "pcs", "onion", 1, "pcs", "peas", 100, "g", "soy sauce", 30, "ml");
            recipe(db, "Vegetable Stir-Fry", 25,
                    "Cook the rice.\nSlice carrots, pepper and onion thinly.\nStir-fry vegetables on high heat for 5 minutes.\nAdd soy sauce and serve over rice.",
                    "carrot", 2, "pcs", "bell pepper", 1, "pcs", "onion", 1, "pcs", "soy sauce", 30, "ml", "rice", 200, "g");
            recipe(db, "Potato Wedges", 40,
                    "Preheat oven to 200°C.\nCut potatoes into wedges.\nToss with olive oil and salt.\nRoast for 30–35 minutes until crisp.",
                    "potato", 4, "pcs", "olive oil", 30, "ml", "salt", 5, "g");
            recipe(db, "Mashed Potatoes", 30,
                    "Peel and boil potatoes until soft.\nDrain and mash.\nBeat in butter and warm milk.\nSeason with salt.",
                    "potato", 5, "pcs", "butter", 30, "g", "milk", 100, "ml", "salt", 5, "g");
            recipe(db, "Tomato & Onion Salad", 10,
                    "Slice the tomatoes and onion.\nArrange on a plate.\nDrizzle with olive oil and season with salt.",
                    "tomato", 3, "pcs", "onion", 1, "pcs", "olive oil", 15, "ml", "salt", 2, "g");
            recipe(db, "Chicken & Rice", 35,
                    "Cook the rice.\nDice chicken and fry until golden.\nAdd chopped onion and garlic and cook until soft.\nServe the chicken over the rice.",
                    "chicken breast", 2, "pcs", "rice", 200, "g", "onion", 1, "pcs", "garlic", 2, "clove");
            recipe(db, "Chicken Curry", 45,
                    "Fry chopped onion and garlic until soft.\nAdd curry powder and cook for 1 minute.\nAdd diced chicken and brown it.\nAdd chopped tomatoes and simmer 20 minutes.\nServe with rice.",
                    "chicken breast", 2, "pcs", "onion", 1, "pcs", "tomato", 2, "pcs", "curry powder", 15, "g", "garlic", 2, "clove", "rice", 200, "g");
            recipe(db, "Spaghetti Bolognese", 45,
                    "Brown the mince in a pan.\nAdd chopped onion and garlic and cook until soft.\nAdd chopped tomatoes and simmer for 20 minutes.\nServe over cooked pasta.",
                    "beef mince", 500, "g", "pasta", 250, "g", "tomato", 4, "pcs", "onion", 1, "pcs", "garlic", 2, "clove");
            recipe(db, "Toasted Cheese Sandwich", 10,
                    "Butter the outside of both slices of bread.\nPlace cheese between the slices.\nToast in a pan until golden and melted.",
                    "bread", 2, "slice", "cheese", 50, "g", "butter", 10, "g");
            recipe(db, "Banana Smoothie", 5,
                    "Peel and chop the bananas.\nBlend with milk and honey until smooth.",
                    "banana", 2, "pcs", "milk", 250, "ml", "honey", 15, "ml");
            recipe(db, "Overnight Oats", 5,
                    "Mix oats and milk in a jar.\nTop with sliced banana and honey.\nRefrigerate overnight.",
                    "oats", 80, "g", "milk", 200, "ml", "banana", 1, "pcs", "honey", 10, "ml");
            recipe(db, "Vegetable Soup", 45,
                    "Chop carrots, potatoes, onion and tomatoes.\nFry onion until soft.\nAdd remaining vegetables and stock.\nSimmer for 30 minutes and blend if desired.",
                    "carrot", 3, "pcs", "potato", 2, "pcs", "onion", 1, "pcs", "tomato", 2, "pcs", "vegetable stock", 1, "l");
            recipe(db, "Tuna Pasta Salad", 20,
                    "Cook the pasta and let it cool.\nDrain the tuna.\nMix pasta, tuna, sweetcorn and mayonnaise.",
                    "pasta", 200, "g", "tuna", 1, "can", "sweetcorn", 100, "g", "mayonnaise", 45, "ml");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** ingredients: repeating triples of (name, quantity, unit). */
    private static void recipe(SQLiteDatabase db, String name, int minutes, String steps, Object... ingredients) {
        ContentValues r = new ContentValues();
        r.put(DatabaseHelper.C_NAME, name);
        r.put(DatabaseHelper.C_MINUTES, minutes);
        r.put(DatabaseHelper.C_STEPS, steps);
        long recipeId = db.insert(DatabaseHelper.T_RECIPES, null, r);

        for (int i = 0; i < ingredients.length; i += 3) {
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.C_RECIPE_ID, recipeId);
            v.put(DatabaseHelper.C_NAME, (String) ingredients[i]);
            v.put(DatabaseHelper.C_QTY, ((Number) ingredients[i + 1]).doubleValue());
            v.put(DatabaseHelper.C_UNIT, (String) ingredients[i + 2]);
            db.insert(DatabaseHelper.T_RECIPE_ING, null, v);
        }
    }
}
