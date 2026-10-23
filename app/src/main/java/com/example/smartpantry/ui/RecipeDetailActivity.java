package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.Format;

/** Shows the full ingredient list (with have / missing marks) and the method for one recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = db.getRecipe(id);
        if (recipe == null) { // defensive: bad or missing extra
            finish();
            return;
        }

        setTitle(recipe.getName());
        ((TextView) findViewById(R.id.text_detail_name)).setText(recipe.getName());
        ((TextView) findViewById(R.id.text_detail_time)).setText(getString(R.string.minutes_fmt, recipe.getMinutes()));

        RecipeMatcher matcher = new RecipeMatcher(db.getAllPantryItems());
        StringBuilder ing = new StringBuilder();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            ing.append(matcher.hasEnough(ri) ? "✓  " : "✗  ")
               .append(Format.qtyWithUnit(ri.getQuantity(), ri.getUnit()))
               .append("  ").append(ri.getName()).append('\n');
        }
        ((TextView) findViewById(R.id.text_detail_ingredients)).setText(ing.toString().trim());

        StringBuilder steps = new StringBuilder();
        String[] lines = recipe.getSteps().split("\n");
        for (int i = 0; i < lines.length; i++) {
            steps.append(i + 1).append(". ").append(lines[i]).append("\n\n");
        }
        ((TextView) findViewById(R.id.text_detail_steps)).setText(steps.toString().trim());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
