package com.example.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/** Adapter for recipe cards. Used for both the strict list and the "Almost There" list. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener { void onRecipeClick(Recipe recipe); }

    /** One row: a recipe plus (optionally) the ingredient it is missing. */
    public static class Row {
        final Recipe recipe;
        final RecipeIngredient missing; // null for strict suggestions
        public Row(Recipe recipe, RecipeIngredient missing) { this.recipe = recipe; this.missing = missing; }
    }

    private final List<Row> rows = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) { this.listener = listener; }

    public void setRows(List<Row> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Row row = rows.get(position);
        h.name.setText(row.recipe.getName());
        h.meta.setText(h.itemView.getContext().getString(R.string.minutes_fmt, row.recipe.getMinutes())
                + " • " + row.recipe.getIngredients().size() + " ingredients");
        if (row.missing != null) {
            h.missing.setVisibility(View.VISIBLE);
            int total = row.recipe.getIngredients().size();
            h.missing.setText("Missing: " + row.missing.getName() + "  (" + (total - 1) + "/" + total + " available)");
        } else {
            h.missing.setVisibility(View.GONE);
        }
        h.itemView.setOnClickListener(v -> listener.onRecipeClick(row.recipe));
    }

    @Override
    public int getItemCount() { return rows.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, meta, missing;
        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.text_recipe_name);
            meta = v.findViewById(R.id.text_recipe_meta);
            missing = v.findViewById(R.id.text_recipe_missing);
        }
    }
}
