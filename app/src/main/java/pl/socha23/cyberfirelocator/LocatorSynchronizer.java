package pl.socha23.cyberfirelocator;


import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LocatorSynchronizer {

    private final static int HEARTBEAT_MS = 30 * 1000;

    private final static long TIME_BETWEEN_SYNCES_MS = 5 * 1000;
    private long lastSyncOnMillis = 0;

    private final static String TAG = "LocatorSynchronizer";

    private  boolean connected;

    private Location lastLocation;
    private List<NearbyDevice> lastDevices = new ArrayList<>();

    private Serverside serverside;

    private Context context;
    private boolean syncOn = true;
    private ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(2);
    private ScheduledFuture scheduledFuture;

    public void connect(Context ctx) {
        if (connected) {
            return;
        }
        this.context = ctx;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PreferencesUtils.getServerUrl(ctx))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverside = retrofit.create(Serverside.class);
        EventBus.getDefault().register(this);
        connected = true;

        scheduledFuture = threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sync(false);
            }
        }, 0, HEARTBEAT_MS, TimeUnit.MILLISECONDS);

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLocationChanged(LocationChangedEvent e) {
        lastLocation = e.getLocation();
        sync(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNearbyDevicesFound(NearbyDevicesFoundEvent e) {
        lastDevices = e.getDevices();
        sync(false);
    }


    public void sync(boolean force) {
        if (!syncOn) {
            return;
        }

        if (!force && System.currentTimeMillis() - lastSyncOnMillis < TIME_BETWEEN_SYNCES_MS) {
            Log.d(TAG, "Not syncing, synced a moment ago...");
            return;
        }
        lastSyncOnMillis = System.currentTimeMillis();
        LocatorState state = getCurrentState();
        Call call = serverside.post(state);
        Log.d(TAG, "Running synchronization...");
        EventBus.getDefault().post(new SynchronizationStartEvent());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG, "...synchronization ok");
                EventBus.getDefault().post(new SynchronizationSuccessEvent());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, "...sync failure");
                EventBus.getDefault().post(new SynchronizationErrorEvent(t.getMessage()));
            }
        });
    }


    private LocatorState getCurrentState() {
        LocatorState state = new LocatorState();

        state.setId(LocatorID.get(context));
        state.setName(PreferencesUtils.getName(context));
        state.setType(PreferencesUtils.getType(context));
        if (lastLocation != null) {
            state.setLatitude(lastLocation.getLatitude());
            state.setLongitude(lastLocation.getLongitude());
        }
        state.setNearbyDevices(lastDevices);
        return state;
    }

    public void close() {
        EventBus.getDefault().unregister(this);
        if (scheduledFuture  != null) {
            scheduledFuture.cancel(false);
        }
        connected = false;
    }

    public void setSyncOn(boolean syncOn) {
        this.syncOn = syncOn;
    }

    private interface Serverside {
        @POST("api/locators")
        Call<Boolean> post(@Body LocatorState locator);
    }
}
