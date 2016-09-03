package com.appetite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.appetite.R;
import com.appetite.model.Recipe;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ActivityRecipe extends AppCompatActivity implements NetworkRecipeRequestFragment.NetworkRecipeRequestListener {
    private final static String TAG = ActivityRecipe.class.getSimpleName(); //logger tag for debugging
    static final String RECIPE = "com.appetite.ActivityRecipe.RECIPE"; //used to save the recipe in the instance state
    static final String RECIPE_NAME = "com.appetite.ActivityRecipe.RECIPE_NAME"; //used to save the recipe in the instance state
    static final String DOWNLOADING_STATE = "com.appetite.ActivityRecipe.DOWNLOADING_STATE";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

    private String recipeNameSelected;
    private Recipe recipeSelected;
    private FloatingActionButton fab;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    NetworkRecipeRequestFragment networkRecipeRequestFragment;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: " );
        super.onCreate(savedInstanceState);

        android.app.FragmentManager fm = getFragmentManager();

        // initialize part of the layout (independent from the recipe selected)
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        // Check to see if we have retained the worker fragment.
        networkRecipeRequestFragment = (NetworkRecipeRequestFragment)fm.findFragmentByTag("recipeDownloader");
        if(networkRecipeRequestFragment != null) {
            Log.d(TAG, "onCreate: we have retained the worker fragment");
            // If retained, check download status
            checkDownload(networkRecipeRequestFragment.getDownloadState());
        }

        // Restore the recipe (Recipe or String) from saved state..
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: savedInstanceState != null");

            recipeSelected = savedInstanceState.getParcelable(RECIPE);
            if (recipeSelected != null) {
                Log.d(TAG, "onCreate: restore recipe from saved state");
                recipeNameSelected = recipeSelected.getName();
                initializeView();
            } else {
                Log.d(TAG, "onCreate: restore recipe_name from saved state");
                recipeNameSelected = savedInstanceState.getString(RECIPE_NAME);
            }
        } else {
            // ..otherwise get the recipe (Recipe or String) from the intent
            Intent intent = getIntent();
            if (intent.hasExtra(ActivityMain.RECIPE_SELECTED)) {
                // received recipe via intent
                Log.d(TAG, "onCreate: received recipe via intent");
                recipeSelected = intent.getParcelableExtra(ActivityMain.RECIPE_SELECTED);
                recipeNameSelected = recipeSelected.getName();
                initializeView();
            } else {
                // received recipe_name via intent
                Log.d(TAG, "onCreate: received recipe_name via intent");
                recipeNameSelected = intent.getStringExtra(ActivityMain.RECIPE_NAME_SELECTED);
                NetworkRecipeRequestFragment recipeRequest = new NetworkRecipeRequestFragment();

                // If the worker fragment has not been retained (or first time running), we need to create it.
                if (networkRecipeRequestFragment == null) {
                    Log.d(TAG, "onCreate: worker fragment not retained, creating it!");
                    networkRecipeRequestFragment = NetworkRecipeRequestFragment.newInstance(recipeNameSelected);
                    fm.beginTransaction().add(networkRecipeRequestFragment, "recipeDownloader").commit();
                }
            }
        }
        getSupportActionBar().setTitle(recipeNameSelected);
    }

    /**
     * initialize the view that depends on the recipe selected
     */
    private void initializeView() {

        // Load the image
        setAppBarImage(recipeSelected.getImage());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Initialize the FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityCooking.class);
                intent.putExtra(ActivityMain.RECIPE_SELECTED, recipeSelected);
                startActivity(intent);
            }
        });

        // Define viewPager behavior (hide or show the FAB)
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                switch (position) {
                    // show the FAB if we are in page n.2 ("recipe preparation")
                    case 2:
                        p.setBehavior(new ScrollAwareFABBehavior(getApplicationContext(), null));
                        fab.setLayoutParams(p);
                        ((FloatingActionButton) findViewById(R.id.fab)).show();
                        break;

                    // hide the FAB otherwise
                    default:
                        p.setBehavior(null);
                        fab.setLayoutParams(p);
                        ((FloatingActionButton) findViewById(R.id.fab)).hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu: " );
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: " );
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            Toast.makeText(ActivityRecipe.this, "invoco onBackPressed()", Toast.LENGTH_SHORT).show(); //TODO delete this line
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position) {
                case 0:
                    return FragmentRecipeInformation.newInstance(recipeSelected);
                case 1:
                    return FragmentRecipeIngredients.newInstance(recipeSelected);
                case 2:
                    return FragmentRecipePreparation.newInstance(recipeSelected);
                default:
                    Log.e(TAG, "SectionsPagerAdapter.getItem: ERROR default in switch (invalid position = " + position + ")" );
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Titles of the pages
            switch (position) {
                case 0:
                    return "INFO";
                case 1:
                    return "INGREDIENTS";
                case 2:
                    return "PREPARATION";
            }
            return null;
        }
    }

    /**
     * Loads the recipe image (from cache or, if not found, from DB) and sets it in the app bar
     */
    private void setAppBarImage(String imageString) {
        final ImageView image = (ImageView) findViewById(R.id.activity_recipe_appbar_image);
        String stringUri = ActivityMain.PATH_RECIPE + imageString;
        if (stringUri != null && !imageString.equals("")) {
            final File imageFile = DiskCacheUtils.findInCache(stringUri, imageLoader.getDiskCache());
            if (imageFile!= null && imageFile.exists()) {
                Picasso.with(getApplicationContext()).load(imageFile).fit().centerCrop().into(image);
            } else {
                imageLoader.loadImage(stringUri, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        image.setImageBitmap(null);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        Picasso.with(getApplicationContext()).load(s).fit().centerCrop().into(image);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        } else {
            image.setImageBitmap(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        // Save the recipe (in order to avoid other requests to the DB)
        if(recipeSelected != null)
            bundle.putParcelable(RECIPE, recipeSelected);
        // Save the recipe_name
        if(recipeNameSelected != null)
            bundle.putString(RECIPE_NAME, recipeNameSelected);

        super.onSaveInstanceState(bundle);
    }

    /**
     * Updates the UI w.r.t. the status of the download of the recipe
     * @param mState status of the download of the recipe
     */
    private void checkDownload(NetworkRecipeRequestFragment.DownloadState mState) {
        Log.d(TAG, "checkDownload: downloadingState = " + mState.toString());

        if(mState == NetworkRecipeRequestFragment.DownloadState.DOWNLOADING) {
            UIDownloading();
        } else
        if(mState == NetworkRecipeRequestFragment.DownloadState.COMPLETED) {
            UIDownloadCompleted();
        } else
        if(mState == NetworkRecipeRequestFragment.DownloadState.ERROR) {
            UIDownloadError();
        }
    }

    /**
     * Show a progress bar
     */
    private void UIDownloading() {
        Log.e(TAG, "UIDownloading" );
        try {
            findViewById(R.id.container).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException container: " + e );
        }
        try {
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException progress_bar_stub: " + e );
        }
        try {
            findViewById(R.id.download_error).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException download_error: " + e );
        }
    }

    /**
     * Show a message of connection error
     */
    private void UIDownloadError() {
        Log.e(TAG, "UIDownloadError" );
        Toast.makeText(getApplicationContext(), "ERRORE INTERNET PROVA TODO", Toast.LENGTH_SHORT).show();
        try {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException progress_bar: " + e );
        }

        try {
            findViewById(R.id.container).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException container: " + e );
        }
        try {
            findViewById(R.id.download_error).setVisibility(View.VISIBLE);  //TODO errore connessione
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException download_error_stub: " + e );
        }
    }

    /**
     * Show the recipe with all its content
     */
    private void UIDownloadCompleted() {
        Log.e(TAG, "UIDownloadCompleted" );
        try {
            findViewById(R.id.container).setVisibility(View.VISIBLE);
        }catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadCompleted: NullPointerException container: " + e );
        }
        try {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadCompleted: NullPointerException progress_bar: " + e );
        }
        try {
            findViewById(R.id.download_error).setVisibility(View.GONE);
        }
        catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadCompleted: NullPointerException download_error: " + e );
        }
    }

    @Override
    public void onRequestStarted() {
        UIDownloading();
    };

    @Override
    public void onRequestProgressUpdate(int progress) {

    };

    @Override
    public void onRequestFinished(Recipe result) {
        if(result != null) {
            // recipe downloaded
            UIDownloadCompleted();
            Log.d(TAG, "recipe selected FOUND in DB, called " + result.getName());
            recipeSelected = result;
            initializeView();
        } else {
            // recipe not downloaded
            UIDownloadError();
            Log.e(TAG, "recipe selected NOT FOUND in DB" );
        }
    };
}
