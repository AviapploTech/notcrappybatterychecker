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

import java.util.TimerTask;

import io.getcoffee.sightglass.notcrappy.NotCrappyApplication;

/**
 * @author HowardStark
 */
public class PowerService extends Service {

    public enum PowerStatus {
        CONNECTED,DISCONNECTED;
    }

    public static final String SERVICE_TAG = "PowerService";

    protected BatteryManager batteryManager;
    protected ChargingBroadcastReceiver chargingListener;
    private PowerStatus powerStatus = PowerStatus.DISCONNECTED;
    private int voltage = -1;

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

    protected void setBatteryManager(BatteryManager batteryManager) {
        this.batteryManager = batteryManager;
    }

    public int getVoltage() {
        return voltage;
    }

    protected void setVoltage(int voltage) {
        this.voltage = voltage;
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
                    powerService.setPowerStatus(PowerStatus.CONNECTED);
                    Log.i(PowerService.SERVICE_TAG, "Got 'Power Connected' broadcast.");
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

    protected static class PowerTimerTask extends TimerTask {

        public static final String TIMER_TASK_TAG = "PowerTimerTask";

        private PowerService powerService;

        public PowerTimerTask(PowerService powerService) {
            this.powerService = powerService;
        }

        public void run() {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = powerService.registerReceiver(null, filter);
            try {
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                if (voltage > 0) {
                    powerService.setVoltage(voltage);
                }
            } catch(NullPointerException ex) {
                Log.wtf(PowerTimerTask.TIMER_TASK_TAG, Log.getStackTraceString(ex));
            }
        }
    }

}
