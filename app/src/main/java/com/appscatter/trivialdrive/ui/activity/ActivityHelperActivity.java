package com.appscatter.trivialdrive.ui.activity;

import com.appscatter.iab.core.ASIab;
import com.appscatter.iab.core.api.ActivityIabHelper;
import com.appscatter.iab.core.listener.OnInventoryListener;
import com.appscatter.iab.core.listener.OnPurchaseListener;
import com.appscatter.iab.core.listener.OnSetupListener;
import com.appscatter.iab.core.listener.OnSkuDetailsListener;
import com.appscatter.iab.core.model.event.SetupResponse;
import com.appscatter.iab.core.model.event.SetupStartedEvent;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;
import com.appscatter.trivialdrive.R;
import com.appscatter.trivialdrive.ui.view.TrivialView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class ActivityHelperActivity extends TrivialActivity
        implements OnSetupListener, OnPurchaseListener, OnInventoryListener, OnSkuDetailsListener {

    private ActivityIabHelper iabHelper;
    private TrivialView trivialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iabHelper = ASIab.getActivityHelper(this);
        setContentView(R.layout.include_trivial);
        trivialView = (TrivialView) findViewById(R.id.trivial);
        trivialView.setIabHelper(iabHelper);

        iabHelper.addPurchaseListener(this);
        iabHelper.addInventoryListener(this);
        iabHelper.addSkuDetailsListener(this);
        iabHelper.addSetupListener(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Some billing operations may involve another activities
        // It's a good idea to check for updates here
        trivialView.update();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iabHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onSetupStarted(@NonNull final SetupStartedEvent setupStartedEvent) {
        // might be a good place to show progress
        trivialView.update();
    }

    @Override
    public void onSetupResponse(@NonNull final SetupResponse setupResponse) {
        // good place to hide progress
    }

    @Override
    public void onPurchase(@NonNull final PurchaseResponse purchaseResponse) {
        trivialView.updatePremium();
        trivialView.updateSubscription();
    }

    @Override
    public void onInventory(@NonNull final InventoryResponse inventoryResponse) {
        trivialView.updatePremium();
        trivialView.updateSubscription();
    }

    @Override
    public void onSkuDetails(@NonNull final SkuDetailsResponse skuDetailsResponse) {
        trivialView.updateSkuDetails();
    }
}
