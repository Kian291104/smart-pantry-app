# Smart Pantry Manager

A Java Android app that tracks the ingredients you have at home and suggests recipes you can cook **using only those ingredients**, which helps reduce food waste.

Mobile App Development 700 – Kian Naidoo (402205512)

## Features
- **Pantry CRUD:** add, view, edit and delete ingredients (name, quantity, unit and an optional expiry date).
- **Suggested Recipes with strict matching:** a recipe only appears if *every* ingredient is in the pantry in *at least* the required quantity.
- **Forgiving matching:** plurals and capitals are ignored ("Tomatoes" = "tomato"), common synonyms are recognised, and units are converted (1 kg covers 200 g).
- **Almost There:** a separate, clearly labelled list of recipes that are missing only one ingredient.
- **Recipe detail:** each ingredient is marked ✓ or ✗ against your pantry, followed by the method.
- **Settings:** highlight items that expire soon, toggle the Almost There list, and clear the pantry.
- Input validation, a bottom navigation bar, and 19 recipes loaded on first run.

## Database: SQLite (SQLiteOpenHelper)
I chose SQLite because pantry data is personal, single-user data that must work offline. It is built into Android, so it needs no server, account or internet connection. It is also relational: `recipes` → `recipe_ingredients` are linked by a foreign key, which suits the matching logic.

Tables: `pantry_items`, `recipes`, `recipe_ingredients`.

## Setup / Run
1. Install **Android Studio** (2024 or newer) with Android SDK 34.
2. Clone the repository:
   `git clone https://github.com/Kian291104/smart-pantry-app.git`
3. In Android Studio, choose **File → Open** and select the cloned folder. Wait for the Gradle sync to finish.
4. In **Device Manager**, create an emulator (for example Pixel 7, API 34), or connect a phone with USB debugging enabled.
5. Click **Run ▶**.

The unit tests for the matching logic are in `app/src/test/.../RecipeMatcherTest`. To run them, right-click the file and choose **Run**.

## Project structure
```
app/src/main/java/com/example/smartpantry/
  model/   PantryItem, Recipe, RecipeIngredient
  data/    DatabaseHelper (SQLite CRUD), RecipeSeeder
  logic/   RecipeMatcher (strict rule), IngredientNormalizer, UnitConverter
  ui/      Activities, adapters, NavHelper
  util/    AppPrefs, Format
```
