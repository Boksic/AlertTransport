package com.nlrd.alerttransport;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    ViewPager viewPagers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPagers = (ViewPager) findViewById(R.id.viewPager);
        viewPagers.setAdapter(new SamplerPagerAdapter(getSupportFragmentManager()));
    }
}
