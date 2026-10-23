package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.AppPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Pantry List screen (launcher). Shows every pantry item from SQLite in a RecyclerView. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private static final String TAG = "MainActivity";

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.text_empty);

        RecyclerView recycler = findViewById(R.id.recycler_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditItemActivity.class)));

        NavHelper.setup(this, R.id.nav_pantry);
    }

    /**
     * onResume runs every time this screen comes back to the foreground
     * (e.g. after saving in AddEditItemActivity), so the list is always fresh.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - reloading pantry");
        loadPantry();
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }

    private void loadPantry() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items, AppPrefs.expiryAlertsEnabled(this));
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(PantryItem item) {
        // Pass the row id to the edit screen through the Intent's extras
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_msg, item.getName()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    db.deletePantryItem(item.getId());
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
