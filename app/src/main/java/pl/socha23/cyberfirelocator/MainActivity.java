package pl.socha23.cyberfirelocator;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private LocatorSynchronizer locatorSynchronizer = new LocatorSynchronizer();
    private final static String TAG = "MainActivity";

    private boolean syncOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setTextFieldsFromSettings();
        connectLocationSubscriber();
        connectLocatorSynchronizer();

        ((SwitchCompat)findViewById(R.id.switch_sync)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSyncOn(isChecked);
            }
        });
        EventBus.getDefault().register(this);
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
        locatorSynchronizer.sync(true);
    }

    @Override
    protected void onDestroy() {
        LocationSubscriber.getInstance().disconnect();
        locatorSynchronizer.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void setSyncOn(boolean on) {
        this.syncOn = on;
        locatorSynchronizer.setSyncOn(on);
        if (on) {
            locatorSynchronizer.sync(true);
        } else {
            setIconColor(R.color.fireGray);
        }
    }

    private void setTextFieldsFromSettings() {
        setTextField(R.id.label_type, PreferencesUtils.getType(this));
        setTextField(R.id.label_name, PreferencesUtils.getName(this));
        setTextField(R.id.label_id, LocatorID.get(this));
    }

    private void setTextField(int fieldId, String text) {
        ((TextView)findViewById(fieldId)).setText(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationChangedEvent e) {
        setTextField(R.id.label_location, LocationFormatter.format(e.getLocation()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncSuccess(SynchronizationSuccessEvent e) {
        if (syncOn) {
            updateSyncStatus("last sync on " + e.getDate().toString(), Color.WHITE);
            setIconColor(R.color.fireGreen);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncError(SynchronizationErrorEvent e) {
        if (syncOn) {
            updateSyncStatus(e.getMessage(), Color.RED);
            setIconColor(R.color.fireRed);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncStart(SynchronizationStartEvent e) {

        if (syncOn) {
            setIconColor(R.color.fireOrange);
        }
    }

    private void updateSyncStatus(String message, int color) {
        setTextField(R.id.label_synchronizationStatus, message);
        ((TextView)findViewById(R.id.label_synchronizationStatus)).setTextColor(color);
    }


    private void setIconColor(int color) {
        ((ImageView)findViewById(R.id.icon_main)).setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setTextFieldsFromSettings();
        connectLocatorSynchronizer();
    }

    public void onPreferencesButtonClick(View view) {
        startActivity(SettingsActivity.createIntent(this));
    }

    public void onSwitchClick(View view) {
        setSyncOn(((Switch)view).isChecked());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationSubscriber.REQUEST_PERMISSION_CODE) {
            connectLocationSubscriber();
            connectLocatorSynchronizer();
        }
    }

}
