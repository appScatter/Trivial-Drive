package com.appscatter.trivialdrive.ui.activity;

import com.appscatter.iab.core.ASIab;
import com.appscatter.metrics.AppScatterBuilder;
import com.appscatter.trivialdrive.R;
import com.appscatter.trivialdrive.TrivialBilling;
import com.appscatter.trivialdrive.TrivialBillingListener;
import com.appscatter.trivialdrive.ui.view.TrivialView;

abstract class TrivialActivity extends BaseTrivialActivity {

    @Override
    protected void onInitButtonClicked(BaseTrivialActivity.HeaderViewHolder headerViewHolder) {
        // initializing the AppScatter SDK IAB capabilities
        ASIab.init("test@appscatter.com", getApplication(), new TrivialBillingListener(getApplication()));

        // Initializing the Metrics modules of the AppScatter SDK
        AppScatterBuilder.with("test@appscatter.com", getApplication())
                .host("acceptor-service-staging.statful.com")
                .token("08b990c7-7a66-4adc-aaff-6cb58ae01fe5")
                .isDryRun(true)
                .build();
        //https://acceptor-service-staging.statful.com/ingestion/data

        headerViewHolder.setSetupResponse(null);

        // strictly for demo purposes
        TrivialBilling.updateSetup();
        final TrivialView trivialView = (TrivialView) findViewById(R.id.trivial);
        trivialView.updatePremium();
        trivialView.updateSubscription();
        trivialView.updateSkuDetails();
    }

    @Override
    protected void onFortumoButtonClicked(HeaderViewHolder headerViewHolder) {

    }
}
