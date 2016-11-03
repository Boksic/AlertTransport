package com.nlrd.alerttransport;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends AppCompatActivity
{
    private ViewPager viewPagers;
    public static MainActivity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPagers = (ViewPager) findViewById(R.id.viewPager);
        viewPagers.setAdapter(new SamplerPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPagers);

        mainActivity = this;
    }
}
