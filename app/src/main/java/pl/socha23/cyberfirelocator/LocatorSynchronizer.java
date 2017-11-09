package pl.socha23.cyberfirelocator;


import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LocatorSynchronizer {

    private Serverside serverside;

    public void connect(Context ctx) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PreferencesUtils.getServerUrl(ctx))
                .build();
        serverside = retrofit.create(Serverside.class);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLocationChanged(LocationChangedEvent e) {
        EventBus.getDefault().post(new SynchronizationSuccessEvent());
    }

    public void close() {
        EventBus.getDefault().unregister(this);
    }

    private interface Serverside {
        @POST("api/locators")
        void post(@Body LocatorState locator);
    }
}
