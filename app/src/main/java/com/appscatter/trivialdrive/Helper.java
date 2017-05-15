package com.appscatter.trivialdrive;

import com.appscatter.trivialdrive.ui.activity.ActivityHelperActivity;
import com.appscatter.trivialdrive.ui.activity.AdvancedHelperActivity;
import com.appscatter.trivialdrive.ui.activity.FragmentHelperActivity;

import android.app.Activity;
import android.support.annotation.StringRes;

public enum Helper {

    ACTIVITY(R.string.helper_activity, ActivityHelperActivity.class),
    FRAGMENT(R.string.helper_fragment, FragmentHelperActivity.class),
    ADVANCED(R.string.helper_advanced, AdvancedHelperActivity.class),;

    @StringRes
    private final int nameId;
    private final Class<? extends Activity> activityClass;

    Helper(final int nameId, final Class<? extends Activity> activityClass) {
        this.nameId = nameId;
        this.activityClass = activityClass;
    }

    public int getNameId() {
        return nameId;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }
}
