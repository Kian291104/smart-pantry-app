package com.example.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Wires the shared bottom navigation bar used on the three top-level screens. */
final class NavHelper {

    private NavHelper() { }

    static void setup(Activity activity, int selectedItemId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(selectedItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) return true;

            Class<?> target;
            if (id == R.id.nav_pantry) target = MainActivity.class;
            else if (id == R.id.nav_recipes) target = SuggestedRecipesActivity.class;
            else target = SettingsActivity.class;

            // Explicit Intent. REORDER_TO_FRONT re-uses an existing instance instead of stacking copies.
            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
            return false;
        });
    }
}
