package com.example.ge.gepaintapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by yuanda on 2015/5/9.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    int n;
    ArrayList<Fragment> af;

    public PagerAdapter(FragmentManager fm,ArrayList<Fragment> af){
        super(fm);
        this.n = af.size();
        this.af = af;
    }

    @Override
    public Fragment getItem(int i) {
        return af.get(i);
    }

    @Override
    public int getCount() {
        return n;
    }
}
