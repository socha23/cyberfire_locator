package pl.socha23.cyberfirelocator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class LocationSubscriber {

    public final static int REQUEST_PERMISSION_CODE = 2432;
    private final static LocationSubscriber INSTANCE = new LocationSubscriber();
    private final static String TAG = "LocationSubscriber";
    private final static int LOCATION_INTERVAL = 1000;
    private final static float LOCATION_DISTANCE_M = 1;

    private boolean connected = false;

    private LocationManager locationManager = null;

    private LocationListener[] locationListeners = new LocationListener[]{
            new LocationListener(),
            new LocationListener()
    };

    private LocationSubscriber() {
    }


    public boolean connect(Activity ctx) {
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        String[] PROVIDERS = {LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER};
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE);
            return false;
        }
        for (String provider : PROVIDERS) {
            locationManager.requestLocationUpdates(
                    provider, LOCATION_INTERVAL, LOCATION_DISTANCE_M,
                    locationListeners[1]);
        }
        connected = true;
        return true;
    }

    public void disconnect() {
        if (locationManager != null) {
            for (int i = 0; i < locationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(locationListeners[i]);
                } catch (SecurityException sex) {
                    Log.e(TAG, "Security exception", sex);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public static LocationSubscriber getInstance() {
        return INSTANCE;
    }

    private static class LocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "location changed");
            EventBus.getDefault().post(new LocationChangedEvent(location));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
