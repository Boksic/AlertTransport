package com.nlrd.alerttransport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by BOKSIC on 01/10/2016.
 */

public class SamplerPagerAdapter extends FragmentPagerAdapter
{
    public SamplerPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position == 0)
        {
            MapActivity mainActivity = new MapActivity();

            return (Fragment)mainActivity;
        }
        else
        {
            return new AlarmeActivity();
        }
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}
