package eu.codlab.fitit.service;

import android.os.Binder;
import android.os.IBinder;

/**
 * Created by kevinleperf on 28/04/15.
 */
public class DeviceService extends InternalMisfitShineService {
    private final IBinder binder = new LocalBinder();

    @Override
    protected IBinder getBinder() {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class LocalBinder extends Binder {
        public DeviceService getService() {
            return DeviceService.this;
        }
    }
}
