package com.ryanwahle.birthprep;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position + 1) {
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AppointmentsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BloodPressureFragment.newInstance(position + 1))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, KickTimesFragment.newInstance(position + 1))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContractionsFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        Log.v("onSectionAttached", "" + number);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_appointments);
                break;
            case 2:
                mTitle = getString(R.string.title_blood_pressure);
                break;
            case 3:
                mTitle = getString(R.string.title_kick_times);
                break;
            case 4:
                mTitle = getString(R.string.title_contractions);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
