package pl.socha23.cyberfirelocator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private TextView getStatusView() {
        return (TextView) findViewById(R.id.label_status);
    }

    public void onPreferencesButtonClick(View view) {
        startActivity(SettingsActivity.createIntent(this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationChangedEvent e) {
        getStatusView().setText(
                e.getLocation().getLatitude() + " x " + e.getLocation().getLongitude()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationSubscriber.REQUEST_PERMISSION_CODE) {
            connectLocationSubscriber();
        }

    }


}
