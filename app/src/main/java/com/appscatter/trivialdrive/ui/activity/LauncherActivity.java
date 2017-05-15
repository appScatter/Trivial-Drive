package com.appscatter.trivialdrive.ui.activity;

import com.appscatter.trivialdrive.Helper;
import com.appscatter.trivialdrive.TrivialBilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Helper helper = TrivialBilling.getHelper();
        startActivity(new Intent(this, helper.getActivityClass()));
        finish();
    }
}
