package com.appscatter.trivialdrive;

import com.appscatter.iab.stores.amazon.AmazonBillingProvider;
import com.appscatter.iab.stores.aptoide.AptoideBillingProvider;
import com.appscatter.iab.stores.fortumo.FortumoBillingProvider;
import com.appscatter.iab.stores.google.GoogleBillingProvider;
import com.appscatter.iab.stores.openstore.providers.ApplandBillingProvider;
import com.appscatter.iab.stores.openstore.providers.SlideMEBillingProvider;
import com.appscatter.iab.stores.openstore.providers.YandexBillingProvider;
import com.appscatter.iab.stores.samsung.SamsungBillingProvider;

import android.support.annotation.StringRes;

public enum Provider {
    AMAZON(R.string.name_amazon, AmazonBillingProvider.NAME),
    GOOGLE(R.string.name_google, GoogleBillingProvider.NAME),
    SAMSUNG(R.string.name_samsung, SamsungBillingProvider.NAME),
    YANDEX(R.string.name_yandex, YandexBillingProvider.NAME),
    APPLAND(R.string.name_appland, ApplandBillingProvider.NAME),
    APTOIDE(R.string.name_aptoide, AptoideBillingProvider.NAME),
    SLIDEME(R.string.name_slideme, SlideMEBillingProvider.NAME),
    FORTUMO(R.string.name_fortumo, FortumoBillingProvider.NAME),
    OPENSTORE(R.string.name_openstore, "Open Store");


    public static Provider getByName(final String name) {
        for (final Provider provider : values()) {
            if (provider.name.equals(name)) {
                return provider;
            }
        }
        return null;
    }

    private final String name;
    @StringRes
    private final int nameId;

    Provider(final int nameId, final String name) {
        this.nameId = nameId;
        this.name = name;
    }

    public int getNameId() {
        return nameId;
    }
}
