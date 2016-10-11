package com.nlrd.alerttransport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by BOKSIC on 01/10/2016.
 */

public class SamplerPagerAdapter extends FragmentStatePagerAdapter
{
    private String []tabs = new String[]{"Acceuil", "Carte", "Liste Alarme"};
    public SamplerPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position == 0)
        {
            return new AcceuilActivity();
        }
        else if (position == 1)
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tabs[position];
    }
}
