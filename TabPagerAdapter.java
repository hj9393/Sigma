package com.example.win10.sigma;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm,int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        // Returning the current tabs
        switch (position) {
            case 0:
           //     Log.i("TabFragment",Integer.toString(position));
                fragment = new TabFragment1();
                break;
          //      return new TabFragment1();
            case 1:
          //      Log.i("TabFragment",Integer.toString(position));
                fragment = new TabFragment2();
                break;
      //          return new TabFragment2();
            case 2:
         //       Log.i("TabFragment",Integer.toString(position));
                fragment = new TabFragment3();
                break;
           //     return new TabFragment3();
  /*          case 3:
         //       Log.i("TabFragment",Integer.toString(position));
                fragment = new TabFragment4();
                break;
          //      return new TabFragment4();*/
            case 3:
      //          Log.i("TabFragment",Integer.toString(position));
                fragment = new TabFragment5();
                break;
         //       return new TabFragment5();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
