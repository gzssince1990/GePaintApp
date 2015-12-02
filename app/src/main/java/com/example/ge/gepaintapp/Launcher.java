package com.example.ge.gepaintapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;


public class Launcher extends FragmentActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Log.w("watching","I am onCreate");

        //call reset function to add fragments to this page
        reset();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w("watching","I am onRestart");
        reset();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.w("watching","I am onResume");
        reset();
    }



    /**
     * Add fragments to PageViewer
     * Using Fragments Adapter
     */
    public void reset(){

        /**
         * 1. Open the db manager for stories;
         * 2. Retrieve Story objects from the the db manager;
         * 3. Close the db manager;
         */
        DatabaseManager myManager = new DatabaseManager(this);
        myManager.openReadable();
        ArrayList<Story> storyList = myManager.retrieveRows();
        myManager.close();

        /**
         * Add home page to the fragments array list;
         * Then add painting preview page to the fragments
         * array list(loop);
         */
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MyFragment().newInstance(
                MyFragment.Type.HOME_PAGE, null));
        for (int i=0;i<storyList.size(); i++){
            if (storyList.get(i).getStoryStatus() == 1){
                fragments.add(new MyFragment().newInstance(
                        MyFragment.Type.PREVIEW_PAGE,
                        storyList.get(i).getStoryName()));
            }
        }

        /**
         * Fragments array list -> Pager Adapter;
         * Pager Adapter -> View Pager;
         * View Pager -> Main Layout;
         */
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

}
