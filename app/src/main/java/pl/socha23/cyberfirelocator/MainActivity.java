package pl.socha23.cyberfirelocator;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private LocatorSynchronizer locatorSynchronizer = new LocatorSynchronizer();
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setTextFieldsFromSettings();
        connectLocationSubscriber();
    }

    private void connectLocationSubscriber() {
        LocationSubscriber.getInstance().connect(this);
    }

    private void connectLocatorSynchronizer() {
        try {
            locatorSynchronizer.connect(this);
        } catch (Exception e) {
            this.onSyncError(new SynchronizationErrorEvent(e.getMessage()));
        }
    }

    public void onSynchronize(View w) {
        locatorSynchronizer.sendLocation();
    }

    @Override
    protected void onDestroy() {
        LocationSubscriber.getInstance().disconnect();
        locatorSynchronizer.close();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        connectLocatorSynchronizer();
    }

    private void setTextFieldsFromSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTextField(R.id.label_type, prefs.getString("preferences_type", "<unknown type>"));
        setTextField(R.id.label_name, prefs.getString("preferences_name", "<unknown name>"));
        setTextField(R.id.label_id, LocatorID.get(this));
    }

    private void setTextField(int fieldId, String text) {
        ((TextView)findViewById(fieldId)).setText(text);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onPreferencesButtonClick(View view) {
        startActivity(SettingsActivity.createIntent(this));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationChangedEvent e) {
        setTextField(R.id.label_location, LocationFormatter.format(e.getLocation()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncSuccess(SynchronizationSuccessEvent e) {
        updateSyncStatus("last sync on " + e.getDate().toString(), Color.WHITE);
        setIconColor(R.color.fireGreen);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncError(SynchronizationErrorEvent e) {
        updateSyncStatus(e.getMessage(), Color.RED);
        setIconColor(Color.RED);
    }


    private void updateSyncStatus(String message, int color) {
        setTextField(R.id.label_synchronizationStatus, message);
        ((TextView)findViewById(R.id.label_synchronizationStatus)).setTextColor(color);
    }


    private void setIconColor(int color) {
        ((ImageView)findViewById(R.id.icon_main)).setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationSubscriber.REQUEST_PERMISSION_CODE) {
            connectLocationSubscriber();
            connectLocatorSynchronizer();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setTextFieldsFromSettings();
        connectLocatorSynchronizer();
    }
}
