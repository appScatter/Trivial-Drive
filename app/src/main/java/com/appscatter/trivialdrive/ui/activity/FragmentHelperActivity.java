package com.appscatter.trivialdrive.ui.activity;

import com.appscatter.trivialdrive.R;
import com.appscatter.trivialdrive.ui.fragment.TrivialFragment;

import android.os.Bundle;

public class FragmentHelperActivity extends TrivialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_content);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, TrivialFragment.newInstance())
                    .commit();
        }
    }
}
