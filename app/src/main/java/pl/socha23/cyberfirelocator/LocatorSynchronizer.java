package pl.socha23.cyberfirelocator;


import android.content.Context;
import android.location.Location;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LocatorSynchronizer {

    private  boolean connected;

    private Location lastLocation;

    private Serverside serverside;

    private Context context;

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
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLocationChanged(LocationChangedEvent e) {
        lastLocation = e.getLocation();
        sendLocation();
    }

    public void sendLocation() {
        LocatorState state = getCurrentState();
        Call call = serverside.post(state);
        EventBus.getDefault().post(new SynchronizationStartEvent());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                EventBus.getDefault().post(new SynchronizationSuccessEvent());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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
        return state;
    }

    public void close() {
        EventBus.getDefault().unregister(this);
        connected = false;
    }

    private interface Serverside {
        @POST("api/locators")
        Call<Boolean> post(@Body LocatorState locator);
    }
}
