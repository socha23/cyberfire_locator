package pl.socha23.cyberfirelocator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

public class LocatorID {

    private final static String PREFS_KEY = "preferences_locatorId";

    public static String get(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (sp.getString(PREFS_KEY, "").equals("")) {
            sp.edit().putString(PREFS_KEY, UUID.randomUUID().toString()).commit();
        }
        return sp.getString(PREFS_KEY, "");
    }
}
