//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//
package com.appetite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.ArgumentMarshaller;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.appetite.model.Filter;
import com.appetite.model.Recipe;
import com.appetite.model.ShoppingItem;
import com.appetite.style.filter.CustomExpandableListAdapter;
import com.appetite.style.filter.ExpandableListDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCategoriesList.OnCategorySelectedListener, FragmentRecipesList.OnRecipeSelectedListener,
        FragmentShoppingList.OnShoppingListFragmentInteractionListener, FragmentShoppingListIngredients.OnShoppingListIngredientFragmentInteractionListener, FragmentFavoritesList.OnFavoritesListFragmentInteractionListener {
    /**
     * Class name for log messages.
     */
    private final static String TAG = ActivityMain.class.getSimpleName();

    public final static String RECIPE_SELECTED = "com.appetite.ActivityMain.RECIPE_SELECTED";
    public final static String FRAGMENT = "com.appetite.ActivityMain.FRAGMENT";
    public final static String fileShoppingListName = "shopping_list";
    public final static String fileFavoritesName = "favorites";

    public final static String PATH_RECIPE = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/";
    public final static String PATH_RECIPE_STEP = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/Steps/";
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
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private boolean isSearchViewOpen;

    /**
     * The navigation view for the drawer item for filters.
     */
    private ExpandableListView expandableFilterList;
    private Map<String, List<String>> expandableListData;
    private ArrayList expandableListTitle;
    private ExpandableListAdapter expandableListAdapter;
    private Filter filterDictionary;
    private String[] items;
    private final String ACTIVATE = "true";
    private boolean isDrawerCreatedFirstTime = false;

    /**
     * All the bundle saved from the fragments
     */
    private Bundle categoryBundle;

    /**
     * The helper class used to toggle the left navigation drawer open and closed.
     */
    private ActionBarDrawerToggle drawerToggle;

    private ArrayList<String> recipeNameList;

    private SimpleCursorAdapter mAdapter;

    private String query = "";

    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
    //Constants TODO: Create a Class that contains all the constants needed (ci sono pure in FragmentRecipesList)
    private final String recipeTable = "dima-mobilehub-516910810-Recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);
        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setSharedPref();

        if(recipeNameList != null) {
            Log.e("MainActivity:", recipeNameList.toString());
        }
        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.main_fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                Class fragmentClass = null;
                Fragment fragment = null;
                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments and instantiate
                // the right fragment
                if (getIntent().getStringExtra(FRAGMENT) != null) {
                    if (getIntent().getStringExtra(FRAGMENT).equals(FragmentShoppingList.class.getSimpleName())) {
                        Log.i(TAG, "onCreate (intent): " + FragmentShoppingList.class.getSimpleName());
                        fragmentClass = FragmentShoppingList.class;
                        fragment = Fragment.instantiate(this, fragmentClass.getName());
                    }
                } else {
                    fragmentClass = FragmentHome.class;
                    fragment = Fragment.instantiate(this, fragmentClass.getName());
                    fragment.setArguments(getIntent().getExtras());
                }
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_fragment_container, fragment, fragmentClass.getSimpleName()).commit();
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set things for the research
        final String[] from = new String[] {"recipeName"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,null, from, to,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


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
        navigationView.getMenu().findItem(R.id.drawer_item_home).setChecked(true);

        mTitle = mDrawerTitle = getTitle();

        /**
         * FILTRI SONO QUA
         */
        expandableFilterList = (ExpandableListView) findViewById(R.id.nav_view_filters);
        View listHeaderView = getLayoutInflater().inflate(R.layout.nav_header, null, false);

        initItems();

        expandableFilterList.addHeaderView(listHeaderView);
        expandableListData = ExpandableListDataSource.getData(getApplication().getApplicationContext());
        expandableListTitle = new ArrayList(expandableListData.keySet());

        addDrawerItems();
        setupDrawer();
        if(savedInstanceState == null) {
           // selectFirstItemAsDefault();
        }
        /**
         * E FINISCONO QUI
         */
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        // Clear back stack when navigating from the Nav Drawer.
        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.i(TAG, "onNavigationItemSelected: CANCELLATO IL BACK STACK");

        android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
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
                fragmentClass = FragmentCategoriesList.class;
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
                fragmentClass = FragmentFavoritesList.class;
                break;
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
        Log.e(TAG, "onBackPressed: BackStackEntryCount = " + fragmentManager.getBackStackEntryCount());
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

                ///////TODO considerare se tenere questo metodo anzichè le righe sopra
                ///////onNavigationItemSelected(navigationView.getMenu().findItem(R.id.drawer_item_home));

                navigationView.getMenu().findItem(R.id.drawer_item_home).setChecked(true);
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
        Log.e(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        // Add SearchWidget.
        Log.e(TAG, "onPrepareOptionsMenu: ");
        searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.search));
        searchView.setQueryHint("Search recipe..");
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth( Integer.MAX_VALUE );
        searchMenuItem = menu.findItem(R.id.search);
        if(!query.equals("")) {
            searchMenuItem.expandActionView();
            searchView.setQuery(query, false);
        }

        // Getting selected (clicked) item suggestions
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor searchCursor = mAdapter.getCursor();
                String selected = searchCursor.toString();
                if(searchCursor.moveToPosition(position)) {
                    selected = searchCursor.getString(1);
                }
                RecipeData data = new RecipeData(selected);
                data.execute("");
                if (searchView != null) {
                    searchView.setIconified(true);

                }
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("SearchView:", "inside onQueryTextSubmit" + s);
                RecipeData data = new RecipeData(s);
                data.execute("");
                if (searchView != null) {
                    searchView.setIconified(true);

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });


        super.onPrepareOptionsMenu(menu);
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

        if(id == R.id.button_filters) {
            DrawerLayout drawer_filters = (DrawerLayout) this.findViewById(R.id.drawer_layout);
           drawer_filters.openDrawer(Gravity.RIGHT);
        }

        //if(mDrawerToggle.onOptionsItemSelected(item)) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if(navigationView.getMenu().findItem(R.id.search)!=null) {
        searchView = (SearchView) MenuItemCompat
                .getActionView(navigationView.getMenu().findItem(R.id.search));
        searchMenuItem = navigationView.getMenu().findItem(R.id.search);
        }

        if(searchMenuItem!= null && searchView!=null) {
            searchMenuItem.collapseActionView();
        }
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        // state for the first SearchView
        isSearchViewOpen = searchMenuItem.isActionViewExpanded();
        Log.e(TAG, "SearchView, onSaveInstanca" + isSearchViewOpen);
        bundle.putString("search", searchView.getQuery().toString());
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "searchView onRestoreInstanceState " + isSearchViewOpen);
        // properly set the state to balance Android's own restore mechanism
        if(isSearchViewOpen) {
            searchMenuItem.expandActionView();
        }
        query = savedInstanceState.getString("search");
    }

    @Override
    public void onCategorySelected(String categorySelectedName) {
        // The user selected a category from FragmentCategoriesList
        Log.e(TAG, "onCategorySelected: " + categorySelectedName);

        // Do something here to display that article

        //TODO checkare 2 pane layout or not
                /*
            FragmentRecipesList articleFrag = (FragmentRecipesList)
                    getSupportFragmentManager().findFragmentById(R.id.article_fragment);

            if (articleFrag != null) {
                // If article frag is available, we're in two-pane layout...

                // Call a method in the ArticleFragment to update its content
                articleFrag.updateArticleView(position);

            } else  */
        {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected category
            FragmentRecipesList newFragment = FragmentRecipesList.newInstance(categorySelectedName);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the main_fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.main_fragment_container, newFragment);
            transaction.addToBackStack(FragmentRecipesList.class.getName());

            // Commit the transaction
            transaction.commit();
        }
    }

    public Bundle getCategoryBundle() {
        return categoryBundle;
    }

    @Override
    public void onRecipeSelected(Recipe recipeSelected) {
        // The user selected a recipe from FragmentRecipesList
        Log.e(TAG, "onRecipeSelected: " + recipeSelected.getName());

        Intent intent = new Intent(this, ActivityRecipe.class);
        intent.putExtra(RECIPE_SELECTED, recipeSelected);
        startActivity(intent);
    }

    @Override
    public void onShoppingListFragmentInteraction(ShoppingItem item, int position) {
        //TODO checkare 2 pane layout or not
                /*
            FragmentRecipesList articleFrag = (FragmentRecipesList)
                    getSupportFragmentManager().findFragmentById(R.id.article_fragment);

            if (articleFrag != null) {
                // If article frag is available, we're in two-pane layout...

                // Call a method in the ArticleFragment to update its content
                articleFrag.updateArticleView(position);

            } else
        { */
        // Otherwise, we're in the one-pane layout and must swap frags...

        // Create fragment and give it an argument for the selected category
        FragmentShoppingListIngredients newFragment = FragmentShoppingListIngredients.newInstance(item, position);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the main_fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_fragment_container, newFragment);
        transaction.addToBackStack(FragmentRecipesList.class.getName());

        // Commit the transaction
        transaction.commit();


    }

    @Override
    public void OnShoppingListIngredientFragmentInteraction(ShoppingItem shoppingItem) {
        Log.e(TAG, "OnShoppingListIngredientFragmentInteraction: SONO DENTRO ACTIVITYMAIN");
        RecipeData data = new RecipeData(shoppingItem.getRecipe());
        data.execute("");
    }

    @Override
    public void OnShoppingListIngredientFragmentDeletion(final ShoppingItem shoppingItem, final int position) {
        //TODO checkare 2 pane layout or not
                /*
            FragmentRecipesList articleFrag = (FragmentRecipesList)
                    getSupportFragmentManager().findFragmentById(R.id.article_fragment);

            if (articleFrag != null) {
                // If article frag is available, we're in two-pane layout...

                // Call a method in the ArticleFragment to update its content
                articleFrag.updateArticleView(position);

            } else
        { */
        // Otherwise, we're in the one-pane layout and must swap frags...
        // Create fragment and give it an argument for the selected category

        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        Class fragmentClass = FragmentShoppingList.class;
        final Fragment newFragment = Fragment.instantiate(this, fragmentClass.getName());
        // Replace whatever is in the main_fragment_container view with this fragment
        transaction.replace(R.id.main_fragment_container, newFragment, fragmentClass.getSimpleName());


        // Commit the transaction
        transaction.commit();

        Snackbar.make(findViewById(R.id.drawer_layout), R.string.fragment_shoppinglist_snackbar_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.fragment_shoppinglist_snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((FragmentShoppingList)newFragment).addElement(shoppingItem, position);
                    }
                }).show();
    }

    @Override
    public void onFavoritesListFragmentInteraction(String item) {
        Log.e(TAG, "OnFavoritesListIngredientFragmentInteraction: SONO DENTRO ACTIVITYMAIN");
        RecipeData data = new RecipeData(item);
        data.execute("");
    }

    /**
     * TODO: make the search more general using the single words
     * @param query
     */
    private void populateAdapter(String query) {
        //TODO: leggi sopra e implementalo qui
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "recipeName" });
        for (int i=0; i< recipeNameList.size(); i++) {
            if (recipeNameList.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, recipeNameList.get(i)});
        }
        mAdapter.changeCursor(c);
    }

    /**
     * Private class that performs task of retrieving data in background
     */
    private class RecipeData extends AsyncTask<String, Void, Recipe> {

        String recipe;

        protected RecipeData(String recipe) {
            this.recipe = wellFormattedString(recipe);
        }
        /**
         * This method runs in background to retrieve data from database
         * @param
         * @return
         */
        public Recipe doInBackground(String...strings) {
            Log.e(TAG, "doInBackground: SONO DENTRO ACTIVITYMAIN BACKGROUND");
            Recipe recipeItem = new Recipe();
            recipeItem.setName(recipe);
            DynamoDBQueryExpression<Recipe> queryExpression = new DynamoDBQueryExpression<Recipe>()
                    .withHashKeyValues(recipeItem);

            List<Recipe> result = mapper.query(Recipe.class, queryExpression);
            if(result.size() != 0) {
                String imageUri = result.get(0).getCategory() + "/" + result.get(0).getImage() + ".jpg";
                result.get(0).setImage(imageUri);
                return result.get(0);
            }  else {
                return null;
            }
        }

        protected void onPostExecute(Recipe result) {
            if(result != null) {
                Log.e(TAG, "onShoppingListFragmentInteraction: recipe selected from the shopping list FOUND in DB, called " + result.getName());
                //..we start its activity
                Intent intent = new Intent(getApplication(), ActivityRecipe.class);
                intent.putExtra(RECIPE_SELECTED, result);
                startActivity(intent);
            } else {
                Log.e(TAG, "onShoppingListFragmentInteraction: recipe selected from the shopping list not found in DB" );
            }
        }
    }


    /**
     * Method needed to retireve shared preferences when they are set
     */
    public void setSharedPref() {

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean sharedNotNull = false;
                SharedPreferences sharedPref = null;
                 while(!sharedNotNull) {
                     sharedPref = getSharedPreferences(
                             "recipeNameList", Context.MODE_PRIVATE);
                     if (sharedPref != null) {
                         sharedNotNull = true;
                     }
                 }
                    Set<String> s = sharedPref.getStringSet("recipeNameList", null);
                    recipeNameList = new ArrayList<String>(s);
                }
        });
        thread2.start();
    }

    /**
     * Needed to format correct strings to pass to the search query
     * @param s
     * @return
     */
    private String wellFormattedString(String s) {
        String[] temp = s.split(" ");
        String output = Character.toUpperCase(temp[0].charAt(0)) + temp[0].substring(1) + " ";
        for(int i = 1; i < temp.length; i++) {
            output = output.concat(temp[i] + " ");
        }
        output = output.trim();
        return output;
    }


    /**
     * QUI I mETODI PER IL DRAWER DEI FILTRI
     */

   /* private void selectFirstItemAsDefault() {
        if (navigationManager != null) {
            String firstFilter = getResources().getStringArray(R.array.Category)[0];
            navigationManager.showCategoryFilter(firstFilter);
        }
    }*/

    private void initItems() {
        items = getResources().getStringArray(R.array.filter);
    }

    private void addDrawerItems() {
        isDrawerCreatedFirstTime = true;
        filterDictionary = Filter.getInstance(this.getApplicationContext());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListData);
        expandableFilterList.setAdapter(expandableListAdapter);
        //Here to refresh... but doesn't work still
        checkFilterStatus();

        expandableFilterList.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (expandableListData.get(expandableListTitle.get(groupPosition))))
                        .get(childPosition).toString();

                if (items[0].equals(expandableListTitle.get(groupPosition))) {

                    changeActivationStatus(parent, v, groupPosition, selectedItem, childPosition);

                } else if (items[1].equals(expandableListTitle.get(groupPosition))) {

                    changeActivationStatus(parent, v, groupPosition, selectedItem, childPosition);

                } else if(items[2].equals(expandableListTitle.get(groupPosition))) {

                    changeActivationStatus(parent, v, groupPosition, selectedItem, childPosition);

                }  else if(items[3].equals(expandableListTitle.get(groupPosition))) {

                    changeActivationStatus(parent, v, groupPosition, selectedItem, childPosition);

                } else {

                    throw new IllegalArgumentException("Not supported type");
                }

                ArrayList<String> activated = filterDictionary.getActivatedFilters();
                if(activated != null) {
                    for (int i = 0; i < activated.size(); i++){
                        Log.e("ActivatedFilter", activated.get(i));
                    }

                }
                return false;

            }
        });

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        mDrawerToggle.onConfigurationChanged(configuration);
    }

    /**
     * method used to change the color of the text of the filters
     * @param parent
     * @param v
     * @param groupPosition
     * @param selectedItem
     * @param childPosition
     */
    private void changeActivationStatus(ExpandableListView parent, View v, int groupPosition, String selectedItem, int childPosition) {
        //get child view
        Log.e("changeActivationSt: ", "parameters");
        Log.e("ParentPassed: ", parent.toString());
        Log.e("View passed: ", v.toString() );
        List group = expandableListData.get(expandableListTitle.get(groupPosition));
        boolean isLast = false;
        int lastIndex = group.size()-1;
        if(group.get(lastIndex).equals(selectedItem)) {
            isLast = true;
            Log.e("isLast ", "yes" );
        }
        View view = expandableListAdapter.getChildView(groupPosition, childPosition, isLast, v , parent);
        TextView text = (TextView) view.findViewById(R.id.expandedListItem);
        if(filterDictionary.getFilterStatus(selectedItem).equals(ACTIVATE)) {
            filterDictionary.deactivateFilter(selectedItem);
            text.setTextColor(Color.parseColor("#FFA000"));
            //Do something to change text
        } else {
            filterDictionary.activateFilter(selectedItem);
            text.setTextColor(Color.parseColor("#FF4081"));
        }
        //Things to do with the selected item
    }

    /**
     * Color as active a filter when is called)
     * @param parent
     * @param groupPosition
     * @param selectedItem
     * @param childPosition
     */
    private void activateSingleFilter(ViewGroup parent, int groupPosition, String selectedItem, int childPosition) {
        Log.e("activateSingleFilter: ","parent: " + parent.toString());
        List group = expandableListData.get(expandableListTitle.get(groupPosition));
        boolean isLast = false;
        View childView = parent.findViewById(R.id.listTitle);
        //Log.e("ChildView: ", childView.toString() );
        int lastIndex = group.size()-1;
        if(group.get(lastIndex).equals(selectedItem)) {
            isLast = true;
            Log.e("isLast", "yes");
        }
        View view = expandableListAdapter.getChildView(groupPosition, childPosition, isLast, childView , parent);
        Log.e("ChildView: ", view.toString());
        TextView text = (TextView) view.findViewById(R.id.expandedListItem);
        text.setTextColor(Color.parseColor("#FF4081"));

    }

    /**
     * Called to properly display the active filters even at screen rotation
     */
    private void checkFilterStatus() {
        filterDictionary = Filter.getInstance(this.getApplicationContext());
        ArrayList<String> activated = filterDictionary.getActivatedFilters();
        ExpandableListView parent = (ExpandableListView) getWindow().getDecorView().getRootView().findViewById(R.id.nav_view_filters);
        Log.e("expandableListView: ", parent.toString() );
        String selectedItem;
        int indexOfGroups = 4;
        if(activated != null) {
            //for all titles
            for (int i = 0; i < indexOfGroups; i++) {
                //View parent = expandableListAdapter.getGroupView(i, true, null, null);
                int numberOfFilters = ((List) expandableListData.get(expandableListTitle.get(i))).size();
                //For alla filters inside the menu
                for (int j = 0; j < numberOfFilters; j++) {
                    selectedItem = ((List) (expandableListData.get(expandableListTitle.get(i))))
                            .get(j).toString();
                    //Check if the filter is active, in that case we change its color
                    if (activated.contains(selectedItem)) {
                        activateSingleFilter( parent, i, selectedItem, j);
                    }
                }

            }
        }

    }


}





