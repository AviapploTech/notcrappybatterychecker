package io.getcoffee.sightglass.notcrappy.background;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author HowardStark
 */
public class VoltageMeasure extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent baatteryIntent = getApplicationContext().registerReceiver(null, filter);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        return null;
    }
}
