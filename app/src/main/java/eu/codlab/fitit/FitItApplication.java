package eu.codlab.fitit;

import android.app.Application;

import de.greenrobot.event.EventBus;

/**
 * Created by kevinleperf on 28/04/15.
 */
public class FitItApplication extends Application {
    private static FitItApplication _this;

    public static final FitItApplication getInstance() {
        return _this;
    }

    private EventBus _devices_eventbus;

    @Override
    public void onCreate() {
        super.onCreate();

        _this = this;

        _devices_eventbus = EventBus.builder()
                .logNoSubscriberMessages(true)
                .sendNoSubscriberEvent(true)
                .sendSubscriberExceptionEvent(true)
                .build();
    }

    public EventBus getDevicesEventBus() {
        return _devices_eventbus;
    }
}
