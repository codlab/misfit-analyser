package eu.codlab.fitit.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import eu.codlab.fitit.FitItApplication;
import eu.codlab.fitit.UserInputEvent;
import eu.codlab.fitit.events.NewDevice;
import eu.codlab.fitit.service.DeviceService;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DeviceService mService;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            String mMessage = "";
            switch (message.what) {
                case DeviceService.SHINE_SERVICE_INITIALIZED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_INITIALIZED");
                    break;
                case DeviceService.SHINE_SERVICE_CONNECTED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_CONNECTED");
                    String firmwareVersion = message.getData().getString(DeviceService.EXTRA_MESSAGE);
                    mMessage = "CONNECTED - " + firmwareVersion;
                    break;
                case DeviceService.SHINE_SERVICE_DISCONNECTED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_DISCONNECTED");
                    mMessage = "DISCONNECTED";
                    break;
                case DeviceService.SHINE_SERVICE_SYNC_COMPLETED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_SYNC_COMPLETED");
                    mMessage = message.getData().getString(DeviceService.EXTRA_MESSAGE);
                    break;
                case DeviceService.SHINE_SERVICE_SYNC_FAILED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_SYNC_FAILED");
                    mMessage = message.getData().getString(DeviceService.EXTRA_MESSAGE);
                    break;
                case DeviceService.SHINE_SERVICE_RSSI_READ:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_RSSI_READ");
                    int rssi = message.getData().getInt(DeviceService.EXTRA_RSSI);
                    break;
                case DeviceService.SHINE_SERVICE_PLAY_ANIMATION_SUCCEEDED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_PLAY_ANIMATION_SUCCEEDED");
                    mMessage = "PLAY ANIMATION SUCCEEDED";
                    break;
                case DeviceService.SHINE_SERVICE_PLAY_ANIMATION_FAILED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_PLAY_ANIMATION_FAILED");
                    mMessage = "PLAY ANIMATION FAILED";
                    break;
                case DeviceService.SHINE_SERVICE_CLOSED:
                    Log.d(TAG, "mHandler.SHINE_SERVICE_CLOSED");
                    mMessage = "CLOSED";
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    };

    private void setUpBTLEService() {
        Log.d(TAG, "init() mService= " + mService);
        Intent bindIntent = new Intent(this, DeviceService.class);
        startService(bindIntent);

        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((DeviceService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            mService.setHandler(mHandler);
            mService.setDeviceDiscoveringHandler(mHandler);
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    public void refreshDevices() {
        mService.startScanning();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        setUpBTLEService();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpBTLEService();
        FitItApplication.getInstance().getDevicesEventBus().register(this);
    }

    @Override
    public void onPause() {
        mService.stopScanning();
        FitItApplication.getInstance().getDevicesEventBus().unregister(this);
        unbindService(mServiceConnection);
        super.onPause();
    }

    public void onEventMainThread(NewDevice event) {
        Toast.makeText(this, event.rssi + " " + event.device.getName() + " " + event.device.getAddress() + " " + event.device.getSerialNumber(), Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(UserInputEvent event) {
        Toast.makeText(this, event.input, Toast.LENGTH_SHORT).show();
    }
}