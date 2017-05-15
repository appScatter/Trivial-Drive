package com.appscatter.trivialdrive;

import com.appscatter.iab.core.ASIab;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import android.app.Application;
import android.content.Context;

public class TrivialApplication extends Application {

    public static RefWatcher getRefWatcher(final Context context) {
        final Context applicationContext = context.getApplicationContext();
        final TrivialApplication application = (TrivialApplication) applicationContext;
        return application.refWatcher;
    }


    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        TrivialData.init(this);
        TrivialBilling.init(this);

        ASIab.setLogEnabled(true, true);
//        ASIab.init(this, TrivialBilling.getRelevantConfiguration(this));
        ASIab.init("test@appscatter.com", this, new TrivialBillingListener(this));

        refWatcher = LeakCanary.install(this);
    }

}