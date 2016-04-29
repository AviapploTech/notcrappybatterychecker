package io.getcoffee.sightglass.notcrappy;

import android.app.Application;

/**
 * @author HowardStark
 */
public class NotCrappyApplication extends Application {

    public static String APPLICATION_TAG = "NotCrappy";

    private static NotCrappyApplication instance = null;

    public NotCrappyApplication() {
        instance = this;
    }

    public static NotCrappyApplication getInstance() {
        if(instance != null) {
            return instance;
        }
        return new NotCrappyApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
