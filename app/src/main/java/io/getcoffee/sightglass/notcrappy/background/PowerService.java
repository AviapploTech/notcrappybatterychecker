package io.getcoffee.sightglass.notcrappy.background;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import io.getcoffee.sightglass.notcrappy.NotCrappyApplication;

/**
 * @author HowardStark
 */
public class PowerService extends Service {

    public enum PowerStatus {
        CONNECTED,DISCONNECTED;
    }

    public static final String SERVICE_TAG = "PowerService";
    public static final String EXTRA_VOLTAGE = "voltage";

    protected BatteryManager batteryManager;
    protected ChargingBroadcastReceiver chargingListener;
    private PowerStatus powerStatus = PowerStatus.DISCONNECTED;
    private volatile int voltage = -1;

    @Override
    public void onCreate() {
        return;
    }

    /**
     *
     * @param intent
     * @return null
     * PowerService shouldn't be bound to
     * as it could cause unpredictable
     * behavior.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startChargingListener();
        setBatteryManager((BatteryManager) getSystemService(BATTERY_SERVICE));

        return START_STICKY_COMPATIBILITY;
    }

    protected void startChargingListener() {
        IntentFilter chargingFilter = new IntentFilter();
        chargingFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        chargingFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        chargingListener = new ChargingBroadcastReceiver(this);
        registerReceiver(chargingListener, chargingFilter);
    }

    public PowerStatus getPowerStatus() {
        return powerStatus;
    }

    protected void setPowerStatus(PowerStatus powerStatus) {
        this.powerStatus = powerStatus;
    }

    public BatteryManager getBatteryManager() {
        return batteryManager;
    }

    public void setBatteryManager(BatteryManager batteryManager) {
        this.batteryManager = batteryManager;
    }

    public int getVoltage() {
        return voltage;
    }

    public synchronized void updateVoltage(int voltage) {
        this.voltage = voltage;
        Intent update = new Intent();
        update.setAction(NotCrappyApplication.ACTION_BATTERY_UPDATE);
        update.putExtra(PowerService.EXTRA_VOLTAGE, this.voltage);
        sendBroadcast(update);
    }

    protected static class ChargingBroadcastReceiver extends BroadcastReceiver {

        private PowerService powerService;

        public ChargingBroadcastReceiver(PowerService powerService) {
            this.powerService = powerService;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case Intent.ACTION_POWER_CONNECTED:
                    int whichPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    Log.i(PowerService.SERVICE_TAG, "Got 'Power Connected' broadcast.");
                    if(whichPlugged == BatteryManager.BATTERY_PLUGGED_USB) {
                        powerService.setPowerStatus(PowerStatus.CONNECTED);
                        new PowerAsyncTask(powerService).execute();
                    }
                    Log.i(PowerService.SERVICE_TAG, "Got charging type of " + ((whichPlugged == BatteryManager.BATTERY_PLUGGED_USB) ? "USB" : "AC"));
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    powerService.setPowerStatus(PowerStatus.CONNECTED);
                    Log.i(PowerService.SERVICE_TAG, "Got 'Power Disconnected' broadcast.");
                    break;
                default:
                    throw new IllegalStateException("ChargingBroadcastReceiver received unacceptable intent action.");
            }
        }
    }

}
