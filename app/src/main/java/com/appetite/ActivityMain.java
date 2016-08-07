//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//
package com.appetite;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Class name for log messages.
     */
    private final static String LOG_TAG = ActivityMain.class.getSimpleName();

    /**
     * Bundle key for saving/restoring the toolbar title.
     */
    private final static String BUNDLE_KEY_TOOLBAR_TITLE = "title";
    /**
     * The identity manager used to keep track of the current user account.
     */
    private IdentityManager identityManager;
    /**
     * The toolbar view control.
     */
    private Toolbar toolbar;
    /**
     * The navigation view for the drawer item.
     */
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    /**
     * The helper class used to toggle the left navigation drawer open and closed.
     */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);
        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.main_fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                FragmentHome fragmentHome = new FragmentHome();
                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                fragmentHome.setArguments(getIntent().getExtras());
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_fragment_container, fragmentHome).commit();
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //       getActionBar().setTitle(mTitle);
                //       invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //       getActionBar().setTitle(mDrawerTitle);
                //       invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        // Show the hamburger icon
        mDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTitle = mDrawerTitle = getTitle();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Class fragmentClass;
        Fragment fragment;

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {

            case R.id.drawer_item_home:
                Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                fragmentClass = FragmentHome.class;
                break;
            //Replacing the main content with ContentFragment Which is our Inbox View;
            case R.id.drawer_item_categories:
                Toast.makeText(getApplicationContext(), "Categories Selected", Toast.LENGTH_SHORT).show();
                fragmentClass = FragmentCategory.class;
                break;
            // For rest of the options we just show a toast on click
            case R.id.drawer_item_how_to:
                Toast.makeText(getApplicationContext(), "How to Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.drawer_item_shopping_list:
                Toast.makeText(getApplicationContext(), "Shopping list Selected", Toast.LENGTH_SHORT).show();
                fragmentClass = FragmentShoppingList.class;
                break;
            case R.id.drawer_item_favourite:
                Toast.makeText(getApplicationContext(), "Favourites Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                return true;
        }
        fragment = Fragment.instantiate(this, fragmentClass.getName());
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName());
        fragmentTransaction.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (fragmentManager.getBackStackEntryCount() == 0) {
            if (fragmentManager.findFragmentByTag(FragmentHome.class.getSimpleName()) == null) {
                final Class fragmentClass = FragmentHome.class;
                // if we aren't on the home fragment, navigate home.
                final Fragment fragment = Fragment.instantiate(this, fragmentClass.getName());

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                    // Set the title for the fragment.

                    final ActionBar actionBar = this.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(getString(R.string.app_name));
                    }
                    return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Amazon Methods
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        // Save the title so it will be restored properly to match the view loaded when rotation
        // was changed or in case the activity was destroyed.
        if (toolbar != null) {
            bundle.putCharSequence(BUNDLE_KEY_TOOLBAR_TITLE, toolbar.getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}