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

    public static final String SERVICE_TAG = "PowerService";

    BatteryManager batteryManager;
    ChargingBroadcastReceiver chargingListener;

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

        return START_STICKY_COMPATIBILITY;
    }

    protected void startChargingListener() {
        IntentFilter chargingFilter = new IntentFilter();
        chargingFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        chargingFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        chargingListener = new ChargingBroadcastReceiver();
        registerReceiver(chargingListener, chargingFilter);
    }

    protected static class ChargingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case Intent.ACTION_POWER_CONNECTED:
                    Log.i(PowerService.SERVICE_TAG, "Got 'Power Connected' broadcast.");
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    Log.i(PowerService.SERVICE_TAG, "Got 'Power Disconnected' broadcast.");
                    break;
                default:
                    throw new IllegalStateException("ChargingBroadcastReceiver received unacceptable intent action.");
            }
        }
    }

}
