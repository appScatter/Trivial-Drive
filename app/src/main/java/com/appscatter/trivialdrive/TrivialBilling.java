package com.appscatter.trivialdrive;

import com.appscatter.iab.core.model.billing.Purchase;
import com.appscatter.iab.core.model.billing.SkuDetails;
import com.appscatter.iab.core.model.event.billing.InventoryResponse;
import com.appscatter.iab.core.model.event.billing.PurchaseResponse;
import com.appscatter.iab.core.model.event.billing.SkuDetailsResponse;
import com.appscatter.iab.core.verification.VerificationResult;
import com.appscatter.iab.stores.amazon.AmazonBillingProvider;
import com.appscatter.iab.stores.aptoide.AptoideBillingProvider;
import com.appscatter.iab.stores.fortumo.FortumoBillingProvider;
import com.appscatter.iab.stores.fortumo.FortumoMapedSkuResolver;
import com.appscatter.iab.stores.fortumo.model.FortumoSkuDetails;
import com.appscatter.iab.stores.google.GoogleBillingProvider;
import com.appscatter.iab.stores.openstore.providers.ApplandBillingProvider;
import com.appscatter.iab.stores.openstore.providers.OpenStoreBillingProvider;
import com.appscatter.iab.stores.samsung.BillingMode;
import com.appscatter.iab.stores.samsung.SamsungBillingProvider;
import com.appscatter.iab.stores.samsung.SamsungMapSkuResolver;
import com.appscatter.iab.stores.samsung.SamsungPurchaseVerifier;
import com.appscatter.iab.stores.slideme.SlideMeBillingProvider;
import com.appscatter.iab.utils.permissions.ASPermissionsConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TrivialBilling {

    private static final String NAME = "billing";

    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_HELPER = "helper";
    private static final String KEY_PROVIDERS = "providers";
    private static final String KEY_AUTO_RECOVER = "auto_recover";

    private static final String AMAZON_SKU_GAS = "com.appscatter.trivialdrive.sku_gas";
    private static final String AMAZON_SKU_PREMIUM = "com.appscatter.trivialdrive.sku_premium";
    private static final String AMAZON_SKU_SUBSCRIPTION = "com.appscatter.trivialdrive.sku_infinite_gas";

    private static final String GOOGLE_SKU_GAS = "sku_gas";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GOOGLE_SKU_PREMIUM = "sku_premium";
    private static final String GOOGLE_SKU_SUBSCRIPTION = "sku_subscription";  //sku_inifinte_gas
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GOOGLE_PLAY_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9OkAeu/9lvQPNOKB/JfiW/OwYyZwMeOxfhNpPhz6" +
            "HpDHpzzYvzu0xFMDgIMl7RDSt5PhfDQOZ7QIym0TRML9uB3j93Vo6z02dn9LYCDZ02tmPi7iIDkWsPDYSJOO" +
            "975XJsqxpIC5Ysrmq5Wo75mb5DJoGyVFDYi4CE2Fy6WgPJ+9dcx4NiJT5CeiAaG7P9u+dE9HTEF0zgLYIOm1" +
            "QptY8gWbgSKPlBr32fn/NsQ2cxGD1BKMtvPHXIOKyIdWYZhIoLq+HlmEqAibMxYwzIekK64fTcPs9HkFTlhi" +
            "u1NPCQYngnRZf3BbcSOwQrq43EvnWir+QyPH2s48V1c68sxovQIDAQAB";

    public static final String SAMSUNG_SKU_GAS = "gas";
    public static final String SAMSUNG_SKU_PREMIUM = "premium";
    public static final String SAMSUNG_SKU_SUBSCRIPTION = "subscription";
    public static final String SAMSUNG_GROUP_ID = "P10000101309";

    public static final String YANDEX_SKU_GAS = "com.appscatter.trivialdrive.sku_gas";
    public static final String YANDEX_SKU_PREMIUM = "com.appscatter.trivialdrive.sku_premium";
    public static final String YANDEX_SKU_SUBSCRIPTION = "com.appscatter.trivialdrive.sku_infinite_gas";
    public static final String YANDEX_PUBLIC_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvNxmLxDiUKzFc8E2XvbLdGrjhc31Q"
            + "gxRErxDGhAE5QCMSgFKMfAPxZ7xf2sJ8v6Jh/rvtZHq8+h5XIJ+XCltNRdtf6273YkYRNpHb7"
            + "KVL0WFMNe02LTfmjlXsFOqSHRF4/l7O+iDf3LA/VtbPdmPdGi92m42LqIkFPqnXjZu1NTYzLN"
            + "kxD24Zs2DcCvQOIf67ErQHXfujwrLVejln1TBWZzlQR7Cpjv1AhW0UPfY8YggFnuH2DvtMN67"
            + "anZxMRDKCxXOEFO5a0MNEqDnTAfR2dmc5TGbRqX88CLNqgm3qnqYReNmYJWEsIkNSMnwIhl3M"
            + "ZS3BL8C6Ilivm9khYzjLQIDAQAB";

    public static final String APTOIDE_SKU_GAS = "sku_gas";
    public static final String APTOIDE_SKU_PREMIUM = "sku_premium";
    public static final String APTOIDE_SKU_SUBSCRIPTION = "sku_infinite_gas";
    public static final String APTOIDE_PUBLIC_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw1yQC6to81pRD+kUF9Hsj2ALLOh9Ja3I8GCa/Mrq" +
            "rA5MsSbLRUGH3Vot/Gb2XrOat7Xs1EKBXMmKR85iZCZ0ujqnLMgxZFM936b0MRr6TiQQRBmhM5+SJICd9vdUb" +
            "ENTUg6Dtb9YluERcr7ZsVgE4fAeHerkpUEo4F0BF0HIBR5FvcqzG6AX2BgCE7EthvA0yV/D726TArnT3ntBhd" +
            "tfVtcZWUltZpuav9sDtxm0M2i9FtJawZa13/bsIijYJJ1YXvLKiA11D/2zu6fQa7+EV0IDeMBwe7a31QTmM8f" +
            "H8HxysYQ3rPK4MCyh0qQ+Mq00N6LDIdfVQTk9l8zxwvSGDQIDAQAB";

    public static final String APPLAND_GAS = "appland.sku_gas";
    public static final String APPLAND_PREMIUM = "appland.sku_premium";
//    public static final String APPLAND_SUBSCRIPTION = "appland.sku_infinite_gas";
    public static final String APPLAND_PUBLIC_KEY
            = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDhXNlPlj29O7uiINlwEF8Ivqa7" +
            "R/4Y0/x5PaBnBtLvXBg/p2V4OmKPHXbbE8GJgfKkqmRMTd1HKlTBRCZRC8CCoJDY" +
            "lAMikubM6IeejTLLjPR5P5E0cQRb5cOncak3tTLCMb0QASZC5Kb/68dyYiRWjoG6" +
            "NbSVRhmpjCCI/4hY3QIDAQAB";

    public static final String SLIDEME_GAS = "sku_hamburger";
    public static final String SLIDEME_PREMIUM = "sku_gourmethamburger";
    public static final String SLIDEME_SUBSCRIPTION = "slideme.sku_infinite_gas";
    public static final String SLIDEME_PUBLIC_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyuVYkCeiQQuFpNYND1jUaNP8WOSxZc172VzYHqEa"
            + "kCfSupMIhVQeAM7xkTZ6lZiBMwagw58wHxpbO65+6h3sKuiTGP+2Ax2wclinVgwNiLQbdl8RdZtf3aJIx/dr"
            + "+gIK+QsbqCfSxBrT/8qnWeSS1JcJlTbYmqCvBtiT4n3ZjpL2TYTGDlGXBoqzKpVwQguTk6Cikg4cCsLB++Hz"
            + "aupAhN+WcurjTV4jighy6KQsIQSP8+sdLHHAPK2+cOZ5py6cL55GAVu2cpkTsnWoEr5wseMTRwZqDePBN77P"
            + "DVdwjyOT5INvXd3/AHrT4B1WEHCnrjfCqIaGIxJrzNK2OvqKwwIDAQAB";

    private static final String FORTUMO_GAS_TITLE = "sku_gas";
    private static final String FORTUMO_GAS_SERVICEID = "61d3a1a88bdd651fe73f833bc2edeef5";
    private static final String FORTUMO_GAS_INAPPSECRET = "350489f757da89f89cf6e1f65470d192";

//    private static final String FORTUMO_PREMIUM_TITLE = "sku_premium";
//    private static final String FORTUMO_PREMIUM_SERVICEID = "e5319c6cafb255d4b9fcff58fb3bbab1";
//    private static final String FORTUMO_PREMIUM_INAPPSECRET = "c88e5cd99bea2f0a87959ae6b496b006";

    private static final String FORTUMO_PREMIUM_TITLE = "sku_test_#1";
    private static final String FORTUMO_PREMIUM_SERVICEID = "47358560fe71fa36763a59f3d20b8b37";
    private static final String FORTUMO_PREMIUM_INAPPSECRET = "2107c2ab327555e89b001c826da63107";

    public static final String SKU_GAS = "sku_gas";
    public static final String SKU_PREMIUM = "sku_premium";
    public static final String SKU_SUBSCRIPTION = "sku_subscription";

    private static Context context;
    private static SharedPreferences preferences;

    // It's probably a good idea to store this values in a more secure way instead of boolean
    // Maybe some bits in int\long
    private static boolean premium;
    private static boolean subscription;
    private static final Map<String, SkuDetails> DETAILS = new HashMap<>();

    private static BillingProvider newProvider(final Provider provider, Context context) {
        switch (provider) {
            case AMAZON:
                return newAmazonProvider();
            case GOOGLE:
                return newGoogleProvider();
            case SAMSUNG:
                return newSamsungProvider();
            case YANDEX:
                return newYandexProvider();
            case APPLAND:
                return newApplandProvider();
            case APTOIDE:
                return newAptoideProvider();
            case SLIDEME:
                return newSlideMEProvider(context);
            case OPENSTORE:
                return newOpenStoreProvider();
            case FORTUMO:
                return newFortumoProvider();
            default:
                throw new IllegalStateException();
        }
    }

    private static BillingProvider newGoogleProvider() {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        skuResolver.add(SKU_GAS, GOOGLE_SKU_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, GOOGLE_SKU_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, GOOGLE_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new GoogleBillingProvider.Builder(context)
                .setPurchaseVerifier(new SimplePublicKeyPurchaseVerifier(GOOGLE_PLAY_KEY))
                .setSkuResolver(skuResolver)
                .setDebugMode(true)
                .build();
    }

    private static BillingProvider newAmazonProvider() {
        final MapSkuResolver skuResolver = new MapSkuResolver();
        skuResolver.add(SKU_GAS, AMAZON_SKU_GAS);
        skuResolver.add(SKU_PREMIUM, AMAZON_SKU_PREMIUM);
        skuResolver.add(SKU_SUBSCRIPTION, AMAZON_SKU_SUBSCRIPTION);

        return new AmazonBillingProvider.Builder(context)
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newSamsungProvider() {
        final SamsungMapSkuResolver skuResolver = new SamsungMapSkuResolver(SAMSUNG_GROUP_ID);
        skuResolver.add(SKU_GAS, SAMSUNG_SKU_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, SAMSUNG_SKU_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, SAMSUNG_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new SamsungBillingProvider.Builder(context)
                .setBillingMode(BillingMode.TEST_SUCCESS)
                .setPurchaseVerifier(new SamsungPurchaseVerifier(context, BillingMode.TEST_SUCCESS))
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newYandexProvider() {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        skuResolver.add(SKU_GAS, YANDEX_SKU_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, YANDEX_SKU_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, YANDEX_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new OpenStoreBillingProvider.Builder(context)
                .setPurchaseVerifier(new SimplePublicKeyPurchaseVerifier(YANDEX_PUBLIC_KEY))
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newAptoideProvider() {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        skuResolver.add(SKU_GAS, APTOIDE_SKU_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, APTOIDE_SKU_PREMIUM, SkuType.ENTITLEMENT);
//        skuResolver.add(SKU_SUBSCRIPTION, APTOIDE_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new AptoideBillingProvider.Builder(context)
                .setPurchaseVerifier(new SimplePublicKeyPurchaseVerifier(APTOIDE_PUBLIC_KEY))
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newApplandProvider() {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        skuResolver.add(SKU_GAS, APPLAND_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, APPLAND_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, APPLAND_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new ApplandBillingProvider.Builder(context)
                .setPurchaseVerifier(new SimplePublicKeyPurchaseVerifier(APPLAND_PUBLIC_KEY))
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newSlideMEProvider(Context context) {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        skuResolver.add(SKU_GAS, SLIDEME_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, SLIDEME_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, SLIDEME_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        if (context instanceof Activity) {
            Activity activity = (Activity)context;
            return new SlideMeBillingProvider.Builder(TrivialBilling.context, activity)
                    .setPurchaseVerifier(new SimplePublicKeyPurchaseVerifier(SLIDEME_PUBLIC_KEY))
                    .setSkuResolver(skuResolver)
                    .build();
        } else {
            return null;
        }
    }

    private static BillingProvider newOpenStoreProvider() {
        final TypedMapSkuResolver skuResolver = new TypedMapSkuResolver();
        //         skuResolver.add(SKU_GAS, YANDEX_SKU_GAS, SkuType.CONSUMABLE);
        //        skuResolver.add(SKU_PREMIUM, YANDEX_SKU_PREMIUM, SkuType.ENTITLEMENT);
        //        skuResolver.add(SKU_SUBSCRIPTION, YANDEX_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new OpenStoreBillingProvider.Builder(context)
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newFortumoProvider() {
        final FortumoMapedSkuResolver skuResolver = new FortumoMapedSkuResolver();

        FortumoSkuDetails prodGas = new FortumoSkuDetails.Builder()
                .setTitle(FORTUMO_GAS_TITLE)
                .setServiceId(FORTUMO_GAS_SERVICEID)
                .setInAppSecret(FORTUMO_GAS_INAPPSECRET)
                .setItemType(SkuType.CONSUMABLE)
                .build();

        FortumoSkuDetails prodPremium = new FortumoSkuDetails.Builder()
                .setTitle(FORTUMO_PREMIUM_TITLE)
                .setServiceId(FORTUMO_PREMIUM_SERVICEID)
                .setInAppSecret(FORTUMO_PREMIUM_INAPPSECRET)
                .setItemType(SkuType.ENTITLEMENT)
                .build();

        skuResolver.add(SKU_GAS, prodGas);
        skuResolver.add(SKU_PREMIUM, prodPremium);
//        skuResolver.add(SKU_SUBSCRIPTION, AMAZON_SKU_SUBSCRIPTION);

        return new FortumoBillingProvider.Builder(context)
                .setSkuResolver(skuResolver)
                .build();
    }

    private TrivialBilling() {
        throw new UnsupportedOperationException();
    }

    public static void init(final Context context) {
        TrivialBilling.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        if (preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            setHelper(Helper.ACTIVITY);
            setProviders(Arrays.asList(Provider.values()));
            setAutoRecover(true);
            TrivialData.resetGas();
        }
    }

    public static Configuration getRelevantConfiguration(final Context context) {
        final Configuration.Builder builder = new Configuration.Builder();
        builder.setBillingListener(new TrivialBillingListener(context));
        for (final Provider provider : getProviders()) {
            BillingProvider billingProvider = newProvider(provider, context);
            if (billingProvider != null) {
                builder.addBillingProvider(billingProvider);
            }
        }
        builder.setAutoRecover(preferences.getBoolean(KEY_AUTO_RECOVER, false));
        builder.setPermissionsConfig(new ASPermissionsConfig.Builder().build());
        return builder.build();
    }

    public static Configuration getFortumoConfiguration(final Context context) {
        final Configuration.Builder builder = new Configuration.Builder();
        builder.setBillingListener(new TrivialBillingListener(context));
        builder.addBillingProvider(newFortumoProvider());
        builder.setAutoRecover(preferences.getBoolean(KEY_AUTO_RECOVER, false));
        builder.setPermissionsConfig(new ASPermissionsConfig.Builder().build());
        return builder.build();
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
        } catch (JSONException ignore) {
        }
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
