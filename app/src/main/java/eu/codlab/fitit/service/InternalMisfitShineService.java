package eu.codlab.fitit.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.misfitwearables.algorithm.model.SleepRawData;
import com.misfitwearables.algorithm.sleep.SleepSessionBuilder;
import com.misfitwearables.ble.setting.SDKSetting;
import com.misfitwearables.ble.shine.ShineAdapter;
import com.misfitwearables.ble.shine.ShineAdapter.ShineScanCallback;
import com.misfitwearables.ble.shine.ShineConfiguration;
import com.misfitwearables.ble.shine.ShineConnectionParameters;
import com.misfitwearables.ble.shine.ShineDevice;
import com.misfitwearables.ble.shine.ShineProfile;
import com.misfitwearables.ble.shine.ShineProfile.ShineCallback;
import com.misfitwearables.ble.shine.ShineStreamingConfiguration;
import com.misfitwearables.ble.shine.result.Activity;
import com.misfitwearables.ble.shine.result.SessionEvent;
import com.misfitwearables.ble.shine.result.SyncResult;
import com.misfitwearables.ble.shine.result.TapEventSummary;
import com.misfitwearables.ble.util.MutableBoolean;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import eu.codlab.fitit.FitItApplication;
import eu.codlab.fitit.events.NewDevice;

public abstract class InternalMisfitShineService extends Service {
    static final String TAG = "MISFIT_SHINE_SERVICE";

    public static final int SHINE_SERVICE_INITIALIZED = 0;
    public static final int SHINE_SERVICE_DISCOVERED = 1;
    public static final int SHINE_SERVICE_CONNECTED = 2;
    public static final int SHINE_SERVICE_DISCONNECTED = 3;
    public static final int SHINE_SERVICE_GET_ACTIVATION_STATE_COMPLETED = 4;
    public static final int SHINE_SERVICE_GET_ACTIVATION_STATE_FAILED = 5;
    public static final int SHINE_SERVICE_GET_CONFIGURATION_COMPLETED = 6;
    public static final int SHINE_SERVICE_GET_CONFIGURATION_FAILED = 7;
    public static final int SHINE_SERVICE_SYNC_COMPLETED = 8;
    public static final int SHINE_SERVICE_SYNC_FAILED = 9;
    public static final int SHINE_SERVICE_SET_CONFIGURATION_COMPLETED = 10;
    public static final int SHINE_SERVICE_SET_CONFIGURATION_FAILED = 11;
    public static final int SHINE_SERVICE_OTA_COMPLETED = 12;
    public static final int SHINE_SERVICE_OTA_FAILED = 13;
    public static final int SHINE_SERVICE_OTA_PROGRESS_CHANGED = 14;
    public static final int SHINE_SERVICE_RSSI_READ = 15;
    public static final int SHINE_SERVICE_PLAY_ANIMATION_SUCCEEDED = 16;
    public static final int SHINE_SERVICE_PLAY_ANIMATION_FAILED = 17;
    public static final int SHINE_SERVICE_ACTIVATE_SUCCEEDED = 18;
    public static final int SHINE_SERVICE_ACTIVATE_FAILED = 19;
    public static final int SHINE_SERVICE_CLOSED = 20;

    // Bundle Key
    public static final String EXTRA_DEVICE = "MisfitShineService.extra.device";
    public static final String EXTRA_RSSI = "MisfitShineService.extra.rssi";
    public static final String EXTRA_MESSAGE = "MisfitShineService.extra.message";
    public static final String EXTRA_SERIAL_STRING = "MisfitShineService.extra.serialstring";
    public static final String EXTRA_OTA_PROGRESS = "MisfitShineService.extra.ota.progress";

    protected Handler mHandler;
    protected Handler mDeviceDiscoveringHandler;

    private ShineProfile mShineProfile;
    private ShineAdapter mShineAdapter;
    private SyncResult mSummaryResult = null;

    /**
     * Service's Binder
     */
    protected abstract IBinder getBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return getBinder();
    }

    /**
     * Set Up
     */
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SDKSetting.setUp(FitItApplication.getInstance(), "user@example.com");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        mShineAdapter = ShineAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onDestroy() {
        if (mShineProfile != null) {
            mShineProfile.close();
        }
        super.onDestroy();
    }

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    public void setDeviceDiscoveringHandler(final Handler handler) {
        mDeviceDiscoveringHandler = handler;
    }

    /**
     * Public Interface - Scanning
     */
    public boolean startScanning() {
        if (mShineAdapter == null)
            return false;

        boolean result = true;
        try {
            mShineAdapter.startScanning(mShineScanCallback);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }

    public void stopScanning() {
        if (mShineAdapter == null)
            return;

        mShineAdapter.stopScanning(mShineScanCallback);
    }

    /**
     * Public Interface - Callback
     */
    private ShineScanCallback mShineScanCallback = new ShineScanCallback() {
        @Override
        public void onScanResult(ShineDevice device, int rssi) {
            Log.d("Shine", "having device " + device);
            Bundle mBundle = new Bundle();
            mBundle.putParcelable(InternalMisfitShineService.EXTRA_DEVICE, device);
            mBundle.putString(InternalMisfitShineService.EXTRA_SERIAL_STRING, device.getSerialNumber());
            mBundle.putInt(InternalMisfitShineService.EXTRA_RSSI, rssi);

            Message msg = Message.obtain(mDeviceDiscoveringHandler, SHINE_SERVICE_DISCOVERED);
            msg.setData(mBundle);
            msg.sendToTarget();

            //TODO CREATE
            FitItApplication.getInstance().getDevicesEventBus().post(new NewDevice(device, rssi));
        }

        ;
    };

    /**
     * Public Interface - Operate
     */
    public boolean connect(ShineDevice device) {
        try {
            Log.d("Shine", "connect " + mShineProfile);
            if (mShineProfile != null) {
                if (device.equals(mShineProfile.getDevice())) {
                    mShineProfile.connect();
                } else {
                    mShineProfile.close();
                    mShineProfile = device.connectProfile(this, false,
                            mShineCallback);
                }
            } else {
                mShineProfile = device.connectProfile(this, false,
                        mShineCallback);
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        if (mShineProfile == null) {
            return false;
        }

        startConnectionTimeOutTimer();
        return true;
    }

    public void disconnect() {
        mShineProfile.disconnect();
    }

    public int getDeviceFamily() {
        return mShineProfile.getDeviceFamily();
    }

    public void startGettingDeviceConfiguration() {
        mShineProfile.startGettingDeviceConfiguration();
    }

    public void stopGettingDeviceConfiguration() {
        mShineProfile.stopGettingDeviceConfiguration();
    }

    public void startSettingDeviceConfiguraion(String paramsString) {
        ShineConfiguration shineConfiguration = new ShineConfiguration();

        if (paramsString != null) {
            String[] params = paramsString.split(",");
            if (params.length == 3) {
                shineConfiguration.mActivityPoint = Long.parseLong(params[0].trim());
                shineConfiguration.mGoalValue = Long.parseLong(params[1].trim());
                shineConfiguration.mClockState = Byte.parseByte(params[2].trim());
            } else {
                Toast.makeText(this, "Please input the following fields: [point], [goal] and [clockState]", Toast.LENGTH_SHORT).show();
                mShineCallback.onSettingDeviceConfigurationFailed();
                return;
            }
        }

        mShineProfile.startSettingDeviceConfiguration(shineConfiguration);
    }

    public void stopSettingDeviceConfiguration() {
        mShineProfile.stopSettingDeviceConfiguration();
    }

    public void startSync() {
        mSummaryResult = new SyncResult();
        mShineProfile.startSync();
    }

    public void stopSync() {
        mShineProfile.stopSync();
    }

    public void startOTAing(byte[] firmwareData) {
        mShineProfile.startOTA(firmwareData);
    }

    public void stopOTAing() {
        mShineProfile.stopOTA();
    }

    public void readRssi() {
        mShineProfile.readRssi();
    }

    public void playAnimation() {
        mShineProfile.playAnimation();
    }

    public void startActivating() {
        mShineProfile.startActivating();
    }

    public void stopActivating() {
        mShineProfile.stopActivating();
    }

    public void startGettingActivationState() {
        mShineProfile.startGettingActivationState();
    }

    public void stopGettingActivationState() {
        mShineProfile.stopGettingActivationState();
    }

    public void close() {
        if (mShineProfile != null) {
            mShineProfile.close();
        }
    }

    /**
     * Public Interface - Callback
     */
    private ShineCallback mShineCallback = new ShineCallback() {

        @Override
        public void onSyncFailed() {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onSyncFailed:" + buildSyncResultString(mSummaryResult));

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_SYNC_FAILED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        ;

        @Override
        public void onSyncSucceeded() {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onSyncSucceeded:" + buildSyncResultString(mSummaryResult));

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_SYNC_COMPLETED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onSystemControlEventMappingFailed() {

        }

        @Override
        public void onSystemControlEventMappingSucceeded() {

        }

        @Override
        public void onUnmapAllEventAnimationFailed() {

        }

        @Override
        public void onUnmapAllEventAnimationSucceeded() {

        }

        ;

        @Override
        public void onSyncDataRead(SyncResult syncResult, Bundle bundle, MutableBoolean shouldStop) {
            shouldStop.setValue(false);
            for (SessionEvent sessionEvent : syncResult.mSessionEvents) {
                Log.d("Activity", "session_event : " + sessionEvent.mType + " " + sessionEvent.mTimestamp);
            }
            for (Activity activity : syncResult.mActivities) {
                Log.d("Activity", "activity : " + new Date(activity.mStartTimestamp * 1000) + " " + activity.mPoints + " " + activity.mBipedalCount + " ");
            }
            for (TapEventSummary eventTape : syncResult.mTapEventSummarys) {
                Log.d("Activity", "eventTape : " + eventTape.mCount + " " + eventTape.mTapType + " " + new Date(eventTape.mTimestamp * 1000));
            }

            mSummaryResult.mActivities.addAll(0, syncResult.mActivities);
            mSummaryResult.mSessionEvents.addAll(0, syncResult.mSessionEvents);
            mSummaryResult.mTapEventSummarys.addAll(0, syncResult.mTapEventSummarys);
        }

        @Override
        public void onGettingDeviceConfigurationFailed(ShineConfiguration configuration) {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onGettingDeviceConfigurationFailed:" + buildShineConfigurationString(configuration));

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_GET_CONFIGURATION_FAILED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onGettingDeviceConfigurationSucceeded(ShineConfiguration configuration) {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onGettingDeviceConfigurationSucceeded:" + buildShineConfigurationString(configuration));

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_GET_CONFIGURATION_COMPLETED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onGettingStreamingConfigurationFailed() {

        }

        @Override
        public void onGettingStreamingConfigurationSucceeded(ShineStreamingConfiguration shineStreamingConfiguration) {

        }

        @Override
        public void onMapEventAnimationFailed() {

        }

        @Override
        public void onMapEventAnimationSucceeded() {

        }

        @Override
        public void onSettingDeviceConfigurationFailed() {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onSettingDeviceConfigurationFailed");

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_SET_CONFIGURATION_FAILED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        ;

        @Override
        public void onSettingDeviceConfigurationSucceeded() {
            Bundle mBundle = new Bundle();
            mBundle.putString(EXTRA_MESSAGE, "onSettingDeviceConfigurationSucceeded");

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_SET_CONFIGURATION_COMPLETED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onSettingStreamingConfigurationFailed() {

        }

        @Override
        public void onSettingStreamingConfigurationSucceeded() {

        }

        @Override
        public void onStartButtonAnimationFailed() {

        }

        @Override
        public void onStartButtonAnimationSucceeded() {

        }

        @Override
        public void onStreamingUserInputEventsEnded() {

        }

        @Override
        public void onStreamingUserInputEventsFailed() {

        }

        @Override
        public void onStreamingUserInputEventsReceivedEvent(int i) {

        }

        ;

        @Override
        public void onOTAFailed() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_OTA_FAILED);
            msg.sendToTarget();
        }

        @Override
        public void onOTASucceeded() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_OTA_COMPLETED);
            msg.sendToTarget();
        }

        @Override
        public void onConnectionStateChanged(int newState) {
            stopConnectionTimeOutTimer();

            if (newState == ShineProfile.CONNECTION_STATE_CONNECTED) {
                String firmwareVersion = mShineProfile.getFirmwareVersion();
                String modelNumber = mShineProfile.getModelNumber();
                String deviceFamilyName = getDeviceFamilyName(mShineProfile.getDeviceFamily());

                Bundle mBundle = new Bundle();
                mBundle.putParcelable(InternalMisfitShineService.EXTRA_DEVICE, mShineProfile.getDevice());
                mBundle.putString(InternalMisfitShineService.EXTRA_MESSAGE, deviceFamilyName + " - " + firmwareVersion + " - " + modelNumber);

                Message msg = Message.obtain(mHandler, SHINE_SERVICE_CONNECTED);
                msg.setData(mBundle);
                msg.sendToTarget();
            } else if (newState == ShineProfile.CONNECTION_STATE_DISCONNECTED) {
                Message msg = Message.obtain(mHandler, SHINE_SERVICE_DISCONNECTED);
                msg.sendToTarget();
            } else if (newState == ShineProfile.CONNECTION_STATE_CLOSED) {
                mShineProfile = null;

                Message msg = Message.obtain(mHandler, SHINE_SERVICE_CLOSED);
                msg.sendToTarget();
            }
        }

        @Override
        public void onReadRssiSucceeded(int rssi) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(InternalMisfitShineService.EXTRA_RSSI, rssi);

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_RSSI_READ);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onSettingConnectionParametersFailed(ShineConnectionParameters shineConnectionParameters) {

        }

        @Override
        public void onSettingConnectionParametersSucceeded(ShineConnectionParameters shineConnectionParameters) {

        }

        ;

        public void onReadRssiFailed() {
            Bundle mBundle = new Bundle();
            mBundle.putInt(InternalMisfitShineService.EXTRA_RSSI, -1);

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_RSSI_READ);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        ;

        public void onPlayAnimationSucceeded() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_PLAY_ANIMATION_SUCCEEDED);
            msg.sendToTarget();
        }

        ;

        public void onPlayAnimationFailed() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_PLAY_ANIMATION_FAILED);
            msg.sendToTarget();
        }

        @Override
        public void onOTAProgressChanged(float progress) {
            Bundle mBundle = new Bundle();
            mBundle.putFloat(InternalMisfitShineService.EXTRA_OTA_PROGRESS, progress);

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_OTA_PROGRESS_CHANGED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onActivateSucceeded() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_ACTIVATE_SUCCEEDED);
            msg.sendToTarget();
        }

        @Override
        public void onChangingSerialNumberFailed() {

        }

        @Override
        public void onChangingSerialNumberSucceeded() {

        }

        @Override
        public void onActivateFailed() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_ACTIVATE_FAILED);
            msg.sendToTarget();
        }

        @Override
        public void onGettingActivationStateSucceeded(boolean isActivated) {
            Bundle mBundle = new Bundle();
            mBundle.putString(InternalMisfitShineService.EXTRA_MESSAGE, "Activated:" + String.valueOf(isActivated));

            Message msg = Message.obtain(mHandler, SHINE_SERVICE_GET_ACTIVATION_STATE_COMPLETED);
            msg.setData(mBundle);
            msg.sendToTarget();
        }

        @Override
        public void onGettingActivationStateFailed() {
            Message msg = Message.obtain(mHandler, SHINE_SERVICE_GET_ACTIVATION_STATE_FAILED);
            msg.sendToTarget();
        }
    };

    /**
     * Connecting TimeOut Timer
     */
    public static final int CONNECTING_TIMEOUT = 30000;

    private Timer mConnectingTimeOutTimer = new Timer();
    private ConnectingTimedOutTimerTask mCurrentConnectingTimeOutTimerTask = null;

    private class ConnectingTimedOutTimerTask extends TimerTask {
        public boolean mIsCancelled = false;

        public ConnectingTimedOutTimerTask() {
            mIsCancelled = false;
        }

        @Override
        public void run() {
            if (mIsCancelled == false) {
                InternalMisfitShineService.this.onConnectingTimedOut(this);
            }
        }
    }

    private void onConnectingTimedOut(ConnectingTimedOutTimerTask timerTask) {
        if (timerTask == mCurrentConnectingTimeOutTimerTask) {
            mCurrentConnectingTimeOutTimerTask = null;
            close();
        }
    }

    private void startConnectionTimeOutTimer() {
        stopConnectionTimeOutTimer();

        mCurrentConnectingTimeOutTimerTask = new ConnectingTimedOutTimerTask();
        mConnectingTimeOutTimer.schedule(mCurrentConnectingTimeOutTimerTask, CONNECTING_TIMEOUT);
    }

    private void stopConnectionTimeOutTimer() {
        if (mCurrentConnectingTimeOutTimerTask != null) {
            mCurrentConnectingTimeOutTimerTask.mIsCancelled = true;
            mCurrentConnectingTimeOutTimerTask.cancel();
        }
    }

    /**
     * Util
     */
    private String buildSyncResultString(SyncResult syncResult) {
        int numberOfActivities = 0;
        int totalSteps = 0;
        int totalPoints = 0;

        if (syncResult != null) {
            if (syncResult.mActivities != null) {
                numberOfActivities = syncResult.mActivities.size();

                for (Activity activity : syncResult.mActivities) {
                    totalPoints += activity.mPoints;
                    totalSteps += activity.mBipedalCount;
                }
            }
        }

        List<SleepRawData> sessions = SleepSessionBuilder.buildAutoSleepSessions(syncResult.mActivities);
        Log.d("Sleep", "having sleep values " + sessions);
        if (sessions != null) {
            for (SleepRawData session : sessions) {
                Log.d("Sleep", "new sleep information");
                Log.d("Sleep", "sleep deep duration " + session.mDeepSleepDuration);
                Log.d("Sleep", "sleep end time " + session.mEndTime);
                Log.d("Sleep", "sleep duration " + session.mSleepDuration);
                Log.d("Sleep", "sleep quality " + session.mSleepQuality);
                Log.d("Sleep", "sleep state changes " + Arrays.toString(session.mSleepStateChanges));
            }
        }
        return String.format(Locale.US, "\n%d activities\n  TotalSteps: %d\n  TotalPoints: %d", numberOfActivities, totalSteps, totalPoints);
    }

    private String buildShineConfigurationString(ShineConfiguration configuration) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("\nActivityPoint: " + configuration.mActivityPoint);
        stringBuffer.append("\nGoalValue: " + configuration.mGoalValue);
        stringBuffer.append("\nClockState: " + configuration.mClockState);
        stringBuffer.append("\nBatteryLevel: " + configuration.mBatteryLevel);

        return stringBuffer.toString();
    }

    private String getDeviceFamilyName(int deviceFamily) {
        String deviceFamilyName = "Unknown";

        switch (deviceFamily) {
            case ShineProfile.DEVICE_FAMILY_SHINE:
                deviceFamilyName = "Shine";
                break;
            case ShineProfile.DEVICE_FAMILY_FLASH:
                deviceFamilyName = "Flash";
                break;
            default:
                break;
        }
        return deviceFamilyName;
    }
}
