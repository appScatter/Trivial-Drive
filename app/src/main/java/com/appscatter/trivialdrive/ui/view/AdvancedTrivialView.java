package com.appscatter.trivialdrive.ui.view;

import com.appscatter.iab.core.ASIab;
import com.appscatter.iab.core.api.AdvancedIabHelper;
import com.appscatter.iab.core.listener.OnInventoryListener;
import com.appscatter.iab.core.listener.OnPurchaseListener;
import com.appscatter.iab.core.listener.OnSetupListener;
import com.appscatter.iab.core.listener.OnSkuDetailsListener;
import com.appscatter.iab.core.model.event.SetupResponse;
import com.appscatter.iab.core.model.event.SetupStartedEvent;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class AdvancedTrivialView extends TrivialView
        implements OnSetupListener, OnPurchaseListener, OnInventoryListener, OnSkuDetailsListener {

    private AdvancedIabHelper iabHelper;

    public AdvancedTrivialView(final Context context) {
        super(context);
    }

    public AdvancedTrivialView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedTrivialView(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdvancedTrivialView(final Context context, final AttributeSet attrs,
            final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        super.init();
        iabHelper = ASIab.getAdvancedHelper();
        setIabHelper(iabHelper);
        iabHelper.addPurchaseListener(this);
        iabHelper.addInventoryListener(this);
        iabHelper.addSkuDetailsListener(this);
        iabHelper.addSetupListener(this, false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        iabHelper.register();
    }

    @Override
    protected void onDetachedFromWindow() {
        iabHelper.unregister();
        super.onDetachedFromWindow();
    }

    @Override
    public void onSetupStarted(@NonNull final SetupStartedEvent setupStartedEvent) {
        update();
    }

    @Override
    public void onSetupResponse(@NonNull final SetupResponse setupResponse) { }

    @Override
    public void onPurchase(@NonNull final PurchaseResponse purchaseResponse) {
        updatePremium();
        updateSubscription();
    }

    @Override
    public void onInventory(@NonNull final InventoryResponse inventoryResponse) {
        updatePremium();
        updateSubscription();
    }

    @Override
    public void onSkuDetails(@NonNull final SkuDetailsResponse skuDetailsResponse) {
        updateSkuDetails();
    }
}
