package pl.socha23.cyberfirelocator;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class NearbyDevicesActivity extends ListActivity {

    public static List<NearbyDevice> lastNearbyDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setDevices(lastNearbyDevices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static Intent createIntent(Context ctx) {
        return new Intent(ctx, NearbyDevicesActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNearbyDevicesFound(NearbyDevicesFoundEvent e) {
        setDevices(e.getDevices());
    }


    public void setDevices(List<NearbyDevice> devices) {
        List<String> rows = new ArrayList<>();
        for (NearbyDevice device : devices) {
            rows.add(device.getId() + "           " + device.getRssi());
        }
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows));
    }
}
