package com.example.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite database (via SQLiteOpenHelper).
 * Tables:
 *   pantry_items        - the user's ingredients (full CRUD)
 *   recipes             - seeded recipe collection
 *   recipe_ingredients  - one row per ingredient a recipe needs (FK -> recipes)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // pantry_items
    public static final String T_PANTRY = "pantry_items";
    public static final String C_ID = "id";
    public static final String C_NAME = "name";
    public static final String C_QTY = "quantity";
    public static final String C_UNIT = "unit";
    public static final String C_EXPIRY = "expiry_date";

    // recipes
    public static final String T_RECIPES = "recipes";
    public static final String C_MINUTES = "minutes";
    public static final String C_STEPS = "steps";

    // recipe_ingredients
    public static final String T_RECIPE_ING = "recipe_ingredients";
    public static final String C_RECIPE_ID = "recipe_id";

    private static DatabaseHelper instance;

    /** Single shared instance avoids opening multiple connections (and leaks). */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /** Runs once, the first time the database file is created. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_NAME + " TEXT NOT NULL, "
                + C_QTY + " REAL NOT NULL CHECK(" + C_QTY + " > 0), "
                + C_UNIT + " TEXT NOT NULL, "
                + C_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_NAME + " TEXT NOT NULL, "
                + C_MINUTES + " INTEGER NOT NULL, "
                + C_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_RECIPE_ING + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_RECIPE_ID + " INTEGER NOT NULL REFERENCES " + T_RECIPES + "(" + C_ID + ") ON DELETE CASCADE, "
                + C_NAME + " TEXT NOT NULL, "
                + C_QTY + " REAL NOT NULL, "
                + C_UNIT + " TEXT NOT NULL)");

        RecipeSeeder.seed(db); // pre-load the recipe collection on first run
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ---------------- Pantry CRUD ----------------

    /** CREATE */
    public long addPantryItem(PantryItem item) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(item));
    }

    /** READ (all) - sorted alphabetically for the list screen. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null, null, null,
                C_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) items.add(fromCursor(c));
        }
        return items;
    }

    /** READ (one) */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, C_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    /** UPDATE */
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update(T_PANTRY, toValues(item), C_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE */
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(T_PANTRY, C_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        getWritableDatabase().delete(T_PANTRY, null, null);
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(C_NAME, item.getName().trim());
        v.put(C_QTY, item.getQuantity());
        v.put(C_UNIT, item.getUnit());
        v.put(C_EXPIRY, item.getExpiryDate());
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(C_ID)),
                c.getString(c.getColumnIndexOrThrow(C_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(C_QTY)),
                c.getString(c.getColumnIndexOrThrow(C_UNIT)),
                c.getString(c.getColumnIndexOrThrow(C_EXPIRY)));
    }

    // ---------------- Recipes (read-only) ----------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null, C_NAME + " ASC")) {
            while (c.moveToNext()) recipes.add(recipeFromCursor(db, c));
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_RECIPES, null, C_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return c.moveToFirst() ? recipeFromCursor(db, c) : null;
        }
    }

    private Recipe recipeFromCursor(SQLiteDatabase db, Cursor c) {
        long id = c.getLong(c.getColumnIndexOrThrow(C_ID));
        return new Recipe(id,
                c.getString(c.getColumnIndexOrThrow(C_NAME)),
                c.getInt(c.getColumnIndexOrThrow(C_MINUTES)),
                c.getString(c.getColumnIndexOrThrow(C_STEPS)),
                getIngredientsForRecipe(db, id));
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        try (Cursor c = db.query(T_RECIPE_ING, null, C_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, C_ID + " ASC")) {
            while (c.moveToNext()) {
                list.add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow(C_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(C_QTY)),
                        c.getString(c.getColumnIndexOrThrow(C_UNIT))));
            }
        }
        return list;
    }
}
