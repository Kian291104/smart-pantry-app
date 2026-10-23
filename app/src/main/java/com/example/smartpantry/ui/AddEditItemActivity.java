package com.example.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.Format;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/** Add / Edit Ingredient form. Edit mode is used when an item id is passed in the Intent. */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp", "cup", "slice", "clove", "can"};

    private DatabaseHelper db;
    private TextInputLayout nameLayout, qtyLayout;
    private TextInputEditText nameInput, qtyInput, expiryInput;
    private Spinner unitSpinner;
    private PantryItem editingItem; // null = adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = DatabaseHelper.getInstance(this);
        nameLayout = findViewById(R.id.layout_name);
        qtyLayout = findViewById(R.id.layout_quantity);
        nameInput = findViewById(R.id.input_name);
        qtyInput = findViewById(R.id.input_quantity);
        expiryInput = findViewById(R.id.input_expiry);
        unitSpinner = findViewById(R.id.spinner_unit);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        expiryInput.setOnClickListener(v -> showDatePicker());
        Button clearExpiry = findViewById(R.id.button_clear_expiry);
        clearExpiry.setOnClickListener(v -> expiryInput.setText(""));

        Button save = findViewById(R.id.button_save);
        save.setOnClickListener(v -> save());

        // Read the id passed by MainActivity (-1 if none = add mode)
        long id = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (id != -1) {
            editingItem = db.getPantryItem(id);
        }
        if (editingItem != null) {
            setTitle(R.string.title_edit_item);
            nameInput.setText(editingItem.getName());
            qtyInput.setText(Format.qty(editingItem.getQuantity()));
            expiryInput.setText(editingItem.getExpiryDate());
            unitSpinner.setSelection(Math.max(0, indexOfUnit(editingItem.getUnit())));
        } else {
            setTitle(R.string.title_add_item);
        }
    }

    private int indexOfUnit(String unit) {
        for (int i = 0; i < UNITS.length; i++) if (UNITS[i].equalsIgnoreCase(unit)) return i;
        return -1;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) ->
                expiryInput.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Input validation: returns true only if every field is valid; shows errors inline otherwise. */
    private boolean validate() {
        boolean ok = true;
        String name = text(nameInput);
        String qty = text(qtyInput);

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.err_name_required));
            ok = false;
        } else if (!name.matches("[A-Za-z][A-Za-z '\\-]{0,39}")) {
            nameLayout.setError(getString(R.string.err_name_invalid));
            ok = false;
        } else {
            nameLayout.setError(null);
        }

        if (TextUtils.isEmpty(qty)) {
            qtyLayout.setError(getString(R.string.err_qty_required));
            ok = false;
        } else {
            try {
                double q = Double.parseDouble(qty);
                if (q <= 0 || q > 100000) throw new NumberFormatException();
                qtyLayout.setError(null);
            } catch (NumberFormatException e) {
                qtyLayout.setError(getString(R.string.err_qty_invalid));
                ok = false;
            }
        }
        return ok;
    }

    private void save() {
        if (!validate()) return;

        String name = text(nameInput);
        double qty = Double.parseDouble(text(qtyInput));
        String unit = (String) unitSpinner.getSelectedItem();
        String expiry = text(expiryInput);
        if (expiry.isEmpty()) expiry = null;

        if (editingItem == null) {
            db.addPantryItem(new PantryItem(name, qty, unit, expiry));
        } else {
            editingItem.setName(name);
            editingItem.setQuantity(qty);
            editingItem.setUnit(unit);
            editingItem.setExpiryDate(expiry);
            db.updatePantryItem(editingItem);
        }
        Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
        finish(); // back to the pantry list, whose onResume reloads the data
    }

    private static String text(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    // Toolbar menu: delete action only visible in edit mode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editingItem != null) {
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.delete)
                    .setIcon(R.drawable.ic_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == 1 && editingItem != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(getString(R.string.confirm_delete_msg, editingItem.getName()))
                    .setPositiveButton(R.string.delete, (d, w) -> {
                        db.deletePantryItem(editingItem.getId());
                        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
