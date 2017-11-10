package pl.socha23.cyberfirelocator;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    private final static String KEY_SERVER = "preferences_server";
    private final static String KEY_NAME = "preferences_name";
    private final static String KEY_TYPE = "preferences_type";

    public static String getServerUrl(Context ctx) {
        return getString(ctx, KEY_SERVER);
    }

    public static String getType(Context ctx) {
        return getString(ctx, KEY_TYPE);
    }

    public static String getName(Context ctx) {
        return getString(ctx, KEY_NAME);
    }

    private static String getString(Context ctx, String key) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, "");
    }

}
