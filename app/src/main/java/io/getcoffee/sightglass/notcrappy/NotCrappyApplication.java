package io.getcoffee.sightglass.notcrappy;

import android.app.Application;
import android.content.Intent;

import io.getcoffee.sightglass.notcrappy.background.PowerAsyncTask;
import io.getcoffee.sightglass.notcrappy.background.PowerService;

/**
 * @author HowardStark
 */
public class NotCrappyApplication extends Application {

    public final static String APPLICATION_TAG = "NotCrappy";
    public final static String ACTION_BATTERY_UPDATE = "io.getcoffee.sightglass.notcrappy.ACTION_BATTERY_UPDATE";

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, PowerService.class));
        startService(new Intent(this, PowerAsyncTask.class));
    }

}
