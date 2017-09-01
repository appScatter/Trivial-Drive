package com.appscatter.trivialdrive;

import com.appscatter.iab.core.model.billing.Purchase;
import com.appscatter.iab.core.model.billing.SkuDetails;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;
import com.appscatter.iab.core.verification.VerificationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTrivialBilling {


    protected static final String NAME = "billing";

    protected static final String KEY_FIRST_LAUNCH = "first_launch";
    protected static final String KEY_HELPER = "helper";
    protected static final String KEY_PROVIDERS = "providers";
    protected static final String KEY_AUTO_RECOVER = "auto_recover";

    public static final String SKU_GAS = "sku_gas";
    public static final String SKU_PREMIUM = "sku_premium";
    public static final String SKU_SUBSCRIPTION = "sku_subscription";

    protected static Context context;
    protected static SharedPreferences preferences;

    // It's probably a good idea to store this values in a more secure way instead of boolean
    // Maybe some bits in int\long
    protected static boolean premium;
    protected static boolean subscription;
    protected static final Map<String, SkuDetails> DETAILS = new HashMap<>();

    public static void init(final Context context) {
        BaseTrivialBilling.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        if (preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            setHelper(Helper.ACTIVITY);
            setProviders(Arrays.asList(Provider.values()));
            setAutoRecover(true);
            TrivialData.resetGas();
        }
    }

    public static Helper getHelper() {
        return Helper.valueOf(preferences.getString(KEY_HELPER, null));
    }

    public static void setHelper(final Helper helper) {
        preferences.edit().putString(KEY_HELPER, helper.name()).apply();
    }

    public static Collection<Provider> getProviders() {
        try {
            final JSONObject jsonObject = new JSONObject(preferences.getString(KEY_PROVIDERS, ""));
            final JSONArray jsonArray = jsonObject.getJSONArray(KEY_PROVIDERS);
            final Collection<Provider> providers = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                providers.add(Provider.valueOf(jsonArray.getString(i)));
            }
            return providers;
        } catch (JSONException e) {
            return Collections.emptyList();
        }
    }

    public static void setProviders(final Iterable<Provider> providers) {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        for (final Provider provider : providers) {
            jsonArray.put(provider.name());
        }
        try {
            jsonObject.put(KEY_PROVIDERS, jsonArray);
            preferences.edit().putString(KEY_PROVIDERS, jsonObject.toString()).apply();
        } catch (JSONException ignore) { }
    }

    public static boolean isAutoRecover() {
        if (!preferences.contains(KEY_AUTO_RECOVER)) {
            throw new IllegalStateException();
        }
        return preferences.getBoolean(KEY_AUTO_RECOVER, false);
    }

    public static void setAutoRecover(final boolean autoRecover) {
        preferences.edit().putBoolean(KEY_AUTO_RECOVER, autoRecover).apply();
    }

    public static void updateSetup() {
        // clear all data if we change billing provider
        premium = false;
        subscription = false;
        DETAILS.clear();
    }

    private static Purchase getPurchase(final Map<Purchase, Integer> inventory,
            final String sku) {
        for (final Map.Entry<Purchase, Integer> entry : inventory.entrySet()) {
            @VerificationResult final int verificationResult = entry.getValue();
            if (verificationResult == VerificationResult.SUCCESS) {
                final Purchase purchase = entry.getKey();
                if (sku.equals(purchase.getSku())) {
                    return purchase;
                }
            }
        }
        return null;
    }

    public static void updateInventory(final InventoryResponse inventoryResponse) {
        final Map<Purchase, Integer> inventory = inventoryResponse.getInventory();
        if (!inventoryResponse.isSuccessful()) {
            // Leave current values intact if request failed
            return;
        }
        if (getPurchase(inventory, SKU_PREMIUM) != null) {
            premium = true;
        }
        final Purchase purchase = getPurchase(inventory, SKU_SUBSCRIPTION);
        if (purchase != null && !purchase.isCanceled()) {
            subscription = true;
        }
    }

    public static void updatePurchase(final PurchaseResponse purchaseResponse) {
        final Purchase purchase = purchaseResponse.getPurchase();
        if (!purchaseResponse.isSuccessful()) {
            // Leave current values intact if request failed
            return;
        }
        //noinspection ConstantConditions
        final String sku = purchase.getSku();
        if (SKU_PREMIUM.equals(sku)) {
            premium = true;
        } else if (SKU_SUBSCRIPTION.equals(sku)) {
            subscription = !purchase.isCanceled();
        }
    }

    public static void updateSkuDetails(final SkuDetailsResponse skuDetailsResponse) {
        final Collection<SkuDetails> skusDetails = skuDetailsResponse.getSkusDetails();
        if (!skuDetailsResponse.isSuccessful()) {
            // Leave current values intact if request failed
            return;
        }
        for (final SkuDetails skuDetails : skusDetails) {
            final String sku = skuDetails.getSku();
            if (!skuDetails.isEmpty()) {
                DETAILS.put(sku, skuDetails);
            }
        }
    }

    public static boolean hasPremium() {
        return premium;
    }

    public static boolean hasValidSubscription() {
        return subscription;
    }

    public static SkuDetails getDetails(final String sku) {
        return DETAILS.get(sku);
    }


}
