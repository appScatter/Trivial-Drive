package com.appscatter.trivialdrive;

import com.appscatter.iab.core.ASIab;
import com.appscatter.metrics.AppScatterBuilder;
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

        // Initializing the AppScatter SDK IAB capabilities
        ASIab.setLogEnabled(true, true);
        ASIab.init(this, TrivialBilling.getRelevantConfiguration(this));

        // Initializing the Metrics modules of the AppScatter SDK
        AppScatterBuilder.with("test@appscatter.com", this)
                .host("acceptor-service-staging.statful.com")
                .token("08b990c7-7a66-4adc-aaff-6cb58ae01fe5")
                .build();
        //http://acceptor-service-staging.statful.com/ingestion/data

        refWatcher = LeakCanary.install(this);
    }

}