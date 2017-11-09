package pl.socha23.cyberfirelocator;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    public static String getServerUrl(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("preferences_server", "");
    }

}
