package io.getcoffee.sightglass.notcrappy.background;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

/**
 * @author HowardStark
 */
public class PowerAsyncTask extends AsyncTask<Void, Integer, Integer> {

    public static final String ASYNC_TASK_TAG = "PowerAsyncTask";
    private PowerService powerService;

    public PowerAsyncTask(PowerService powerService) {
        this.powerService = powerService;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        while(powerService.getPowerStatus() == PowerService.PowerStatus.CONNECTED) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = powerService.registerReceiver(null, filter);
            try {
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                if (voltage > 0)
                    powerService.updateVoltage(voltage);
            } catch(NullPointerException ex) {
                Log.wtf(PowerAsyncTask.ASYNC_TASK_TAG, Log.getStackTraceString(ex));
            }
        }
        return null;
    }
}
