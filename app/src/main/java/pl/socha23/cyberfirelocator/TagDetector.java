package pl.socha23.cyberfirelocator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class TagDetector {

    public final static int REQUEST_ENABLE_BT = 12;
    private final static String TAG = "TagDetector";
    private final static int SCAN_PERIOD = 200;
    private final static int SCAN_PAUSE = 10000;

    private ForgettingStore devices = new ForgettingStore(3 * SCAN_PAUSE);

    private BluetoothAdapter.LeScanCallback scanCallback = new MyScanCallback();
    private Handler handler = new Handler();
    private boolean scanningOn = false;


    public void connect(MainActivity mainActivity) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            startScan(mainActivity);
        }
    }

    public void disconnect(Context ctx) {
        stopScan();
    }

    public void onBluetoothEnabled(Context ctx) {
        startScan(ctx);
    }


    private void stopScan() {
        scanningOn = false;
    }


    private void startScan(Context ctx) {
        if (scanningOn) {
            return;
        }
        scanningOn = true;
        postScan();
    }

    private void postScan() {
        if (scanningOn) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Pausing BLE scan");
                    broadcastScanResults();
                    BluetoothAdapter.getDefaultAdapter().stopLeScan(scanCallback);
                }
            }, SCAN_PERIOD);
            Log.d(TAG, "Resuming BLE scan");
            BluetoothAdapter.getDefaultAdapter().startLeScan(scanCallback);
            postScanPause();
        }
    }

    private void broadcastScanResults() {
        EventBus.getDefault().post(new NearbyDevicesFoundEvent(devices.list()));
        NearbyDevicesActivity.lastNearbyDevices = devices.list();
    }

    private void postScanPause() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postScan();
            }
        }, SCAN_PAUSE);
    }

    private void onDeviceFound(BluetoothDevice device, int rssi) {
        Log.d(TAG, "device found: " + device.getAddress() + " " + rssi);
        String id = device.getAddress().replaceAll(":", "").toLowerCase();
        devices.put(id, new NearbyDevice(id, rssi));
    }

    class MyScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            onDeviceFound(device, rssi);
        }
    }
}
