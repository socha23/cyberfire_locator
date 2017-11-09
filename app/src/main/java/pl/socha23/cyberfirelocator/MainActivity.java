package pl.socha23.cyberfirelocator;

import android.content.Context;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectLocationSubscriber();
        setContentView(R.layout.activity_main);
    }

    private void connectLocationSubscriber() {
        LocationSubscriber.getInstance().connect(this);
    }

    @Override
    protected void onDestroy() {
        LocationSubscriber.getInstance().disconnect();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        setTextFieldsFromSettings();
        super.onResume();
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
        setIconColor(R.color.fireGreen);
    }

    private void setIconColor(int color) {
        ((ImageView)findViewById(R.id.icon_main)).setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationSubscriber.REQUEST_PERMISSION_CODE) {
            connectLocationSubscriber();
        }
    }
}
