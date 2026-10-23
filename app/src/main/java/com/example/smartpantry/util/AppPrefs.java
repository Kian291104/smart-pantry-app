package com.example.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Small wrapper around SharedPreferences for the Settings screen. */
public final class AppPrefs {
    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_ALMOST_THERE = "show_almost_there";

    private AppPrefs() { }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlertsEnabled(Context c) { return prefs(c).getBoolean(KEY_EXPIRY_ALERTS, true); }
    public static void setExpiryAlerts(Context c, boolean on) { prefs(c).edit().putBoolean(KEY_EXPIRY_ALERTS, on).apply(); }

    public static boolean showAlmostThere(Context c) { return prefs(c).getBoolean(KEY_ALMOST_THERE, true); }
    public static void setShowAlmostThere(Context c, boolean on) { prefs(c).edit().putBoolean(KEY_ALMOST_THERE, on).apply(); }
}
