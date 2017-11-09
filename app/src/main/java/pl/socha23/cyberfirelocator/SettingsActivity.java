package pl.socha23.cyberfirelocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            setPreferenceSummaries();
        }


        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            setPreferenceSummaries();
        }

        private void setPreferenceSummaries() {
            // we want current values in pref summaries
            EditTextPreference server = (EditTextPreference)getPreferenceScreen().findPreference("preferences_server");
            server.setSummary(server.getText());

            EditTextPreference name = (EditTextPreference)getPreferenceScreen().findPreference("preferences_name");
            name.setSummary(name.getText());

            ListPreference type = (ListPreference)getPreferenceScreen().findPreference("preferences_type");
            type.setSummary(type.getEntry());
        }
    }

    public static Intent createIntent(Context ctx) {
        return new Intent(ctx, SettingsActivity.class);
    }
}
