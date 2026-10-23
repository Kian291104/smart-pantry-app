package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.AppPrefs;

import java.util.ArrayList;
import java.util.List;

/** Runs the strict-matching rule and lists only recipes the user can cook right now. */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter, almostAdapter;
    private TextView readyHeader, emptyView, almostHeader, almostHint;
    private RecyclerView almostRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = DatabaseHelper.getInstance(this);
        readyHeader = findViewById(R.id.text_ready_header);
        emptyView = findViewById(R.id.text_empty_suggestions);
        almostHeader = findViewById(R.id.text_almost_header);
        almostHint = findViewById(R.id.text_almost_hint);

        RecyclerView strictRecycler = findViewById(R.id.recycler_suggested);
        strictRecycler.setLayoutManager(new LinearLayoutManager(this));
        strictRecycler.setNestedScrollingEnabled(false);
        strictAdapter = new RecipeAdapter(this);
        strictRecycler.setAdapter(strictAdapter);

        almostRecycler = findViewById(R.id.recycler_almost);
        almostRecycler.setLayoutManager(new LinearLayoutManager(this));
        almostRecycler.setNestedScrollingEnabled(false);
        almostAdapter = new RecipeAdapter(this);
        almostRecycler.setAdapter(almostAdapter);

        NavHelper.setup(this, R.id.nav_recipes);
    }

    /** Re-run matching every time the screen is shown, so pantry changes appear immediately. */
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<Recipe> allRecipes = db.getAllRecipes();
        RecipeMatcher matcher = new RecipeMatcher(db.getAllPantryItems());

        // 1) Strict suggestions - every ingredient present in sufficient quantity
        List<RecipeAdapter.Row> strictRows = new ArrayList<>();
        for (Recipe r : matcher.suggest(allRecipes)) strictRows.add(new RecipeAdapter.Row(r, null));
        strictAdapter.setRows(strictRows);
        readyHeader.setText(getString(R.string.ready_to_cook, strictRows.size()));
        emptyView.setVisibility(strictRows.isEmpty() ? View.VISIBLE : View.GONE);

        // 2) Optional, clearly separated "Almost There" list (missing exactly one ingredient)
        boolean showAlmost = AppPrefs.showAlmostThere(this);
        List<RecipeAdapter.Row> almostRows = new ArrayList<>();
        if (showAlmost) {
            for (RecipeMatcher.MatchResult m : matcher.almostThere(allRecipes)) {
                almostRows.add(new RecipeAdapter.Row(m.recipe, m.missing.get(0)));
            }
        }
        almostAdapter.setRows(almostRows);
        int vis = showAlmost && !almostRows.isEmpty() ? View.VISIBLE : View.GONE;
        almostHeader.setVisibility(vis);
        almostHint.setVisibility(vis);
        almostRecycler.setVisibility(vis);
        almostHeader.setText(getString(R.string.almost_there, almostRows.size()));
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
