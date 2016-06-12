package com.example.prgpascal.bluerand_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

/**
 * Created by prgpascal on 12/06/2016.
 */
public class BlueRandActivity extends Activity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLayout();
    }

    private void createLayout() {
        setContentView(R.layout.activity_bluerand);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        //TODO instantiate Fragments
    }


}
