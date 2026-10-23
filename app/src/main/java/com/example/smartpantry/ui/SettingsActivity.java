package com.example.smartpantry.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.util.AppPrefs;
import com.google.android.material.materialswitch.MaterialSwitch;

/** Settings: expiring-soon highlight toggle, Almost-There toggle, and clear pantry. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialSwitch expiry = findViewById(R.id.switch_expiry);
        expiry.setChecked(AppPrefs.expiryAlertsEnabled(this));
        expiry.setOnCheckedChangeListener((b, on) -> AppPrefs.setExpiryAlerts(this, on));

        MaterialSwitch almost = findViewById(R.id.switch_almost);
        almost.setChecked(AppPrefs.showAlmostThere(this));
        almost.setOnCheckedChangeListener((b, on) -> AppPrefs.setShowAlmostThere(this, on));

        Button clear = findViewById(R.id.button_clear_pantry);
        clear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.clear_pantry)
                .setMessage(R.string.confirm_clear_msg)
                .setPositiveButton(R.string.clear, (d, w) -> {
                    DatabaseHelper.getInstance(this).clearPantry();
                    Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show());

        NavHelper.setup(this, R.id.nav_settings);
    }
}
