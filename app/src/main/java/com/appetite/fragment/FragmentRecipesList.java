package com.appetite.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.appetite.R;
import com.appetite.adapter.AdapterRecipesList;
import com.appetite.model.Filter;
import com.appetite.model.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link FragmentRecipesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRecipesList extends Fragment {
    private final static String TAG = FragmentRecipesList.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CATEGORY_SELECTED_NAME = "com.appetite.CATEGORY_SELECTED";

    private static final String DOWNLOAD_STATE = "com.appetite.fragment.FragmentRecipesList.DOWNLOAD_STATE";
    private static final String RECIPES_LIST = "com.appetite.fragment.FragmentRecipesList.RECIPES_LIST";
    private static final String RECIPES_DISPLAYER = "com.appetite.fragment.FragmentRecipesList.RECIPES_DISPLAYER";

    private String categorySelectedName;

    //Variables for the recycler view
    private ArrayList<Recipe> recipesList = new ArrayList<Recipe>();
    private RecyclerView recyclerView;
    private AdapterRecipesList adapter;
    private HashMap<Recipe, String> recipeDisplayer = new HashMap<>();

    RecipeData data;

    //Variable to communicate to the activity
    OnRecipeSelectedListener mCallBack;

    public enum DownloadState {
        IDLE, DOWNLOADING, COMPLETED, ERROR, STOPPED
    }

    DownloadState downloadState = DownloadState.IDLE;


    //Variables for the db
    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
    //Constans TODO: Create a Class that contans all the constants needed
    private final String recipeTable = "dima-mobilehub-516910810-Recipe";
    private final String bucket = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/";
    private final String DISPLAY = "true";
    private final String NOT_DISPLAY = "false";

    public FragmentRecipesList() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * {} interface
     * @param categorySelectedName name of the category selected.
     * @return A new instance of fragment FragmentCategoriesList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRecipesList newInstance(String categorySelectedName) {
        FragmentRecipesList fragment = new FragmentRecipesList();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_SELECTED_NAME, categorySelectedName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categorySelectedName = getArguments().getString(ARG_CATEGORY_SELECTED_NAME);
        }
        if (savedInstanceState != null) {
            Log.e(TAG, "onCreate: savedInstanceState != null" );
            downloadState = (DownloadState) savedInstanceState.getSerializable(DOWNLOAD_STATE);
            recipesList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
            adapter = new AdapterRecipesList(getContext(), recipesList);
            recipeDisplayer = (HashMap<Recipe, String>) savedInstanceState.getSerializable(RECIPES_DISPLAYER);
        } else {
            Log.e(TAG, "onCreate: savedInstanceState == null (1st invocation)");
        }
        if(downloadState == DownloadState.IDLE || downloadState == DownloadState.DOWNLOADING) {
            data = new RecipeData();
            data.execute("");
        }
    /*


        Bundle bundle = ((ActivityMain) getActivity()).getCategoryBundle();
        if(bundle != null) {savedInstanceState = bundle;}
        if(savedInstanceState == null || !savedInstanceState.containsKey(RECIPES_LIST)) {
            Log.e(TAG, "The first time that the fragment is called");
            data = new RecipeData();
            data.execute("");
        } else {
            Log.e(TAG, "Other times the database is not queried anymore");
            //Use AdapterCategoriesList with a custom object
            recipesList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
            adapter = new AdapterRecipesList(getContext(), recipesList);
            recipeDisplayer = (HashMap<Recipe, String>) savedInstanceState.getSerializable(RECIPES_DISPLAYER);
            Log.e("DisplayerResored", recipeDisplayer.toString());
        } */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(categorySelectedName);
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recipes_list_recycler_view);

        //Use array adapter here

        //Use AdapterCategoriesList with a custom object
        adapter = new AdapterRecipesList(getContext(), recipesList);
        int columns;
        //Set the grid layout manager
        //Calculate number of columns needed
        if(getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT)
            columns = 2;
        else
            columns = 3;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterRecipesList.OnItemClickListener(){
            public void onItemClick(Recipe recipe){
              //  Toast.makeText(getContext(), recipe.getName(), Toast.LENGTH_SHORT).show();
                mCallBack.onRecipeSelected(recipe);
                Log.e("FragmentRecipesList", "onItemClick: " + recipe.getName());
                /*EventFragment eventFragment = EventFragment.newInstance();
                //replace content frame with your own view.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    ft.replace(R.id.content_frame, eventFragment).commit() */
            }
        });

        checkDownload(downloadState, rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    /*    if(data != null) {
            data.cancel(true);
            downloadState = DownloadState.STOPPED; //TODO verificare se serve
            Log.e(TAG, "onPause: DownloadState = " + downloadState.toString() );
        } */
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if ((data != null) && (data.getStatus() == AsyncTask.Status.RUNNING)) {
            data.cancel(true);
            Log.d(TAG, "onDestroy: TASK CANCELLATO");
            downloadState = DownloadState.DOWNLOADING; //TODO verificare se serve
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        //This makes sure that the container activity has implemented
        //the callback interface. If not, it thows an exception
        try {
            mCallBack = (OnRecipeSelectedListener) activity;
        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + "must implement OnCategorySelectedListener");
        }
    }


    /**
     * This function will check the orientation of the screen
     * @return orientation in witch the device is
     */
    public int getScreenOrientation()
    {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    /**
     * Private class that performs task of retrieving data in background
     */
    private class RecipeData extends AsyncTask<String, Void, ArrayList<Recipe>> implements Serializable {

        protected RecipeData() {
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                downloadState = DownloadState.DOWNLOADING;
            }
            else {
                downloadState = DownloadState.ERROR;
                this.cancel(true);
                checkDownload(downloadState);
            }
        }

        /**
         * This method runs in background to retrieve data from database
         * @param strings
         * @return
         */
        public ArrayList<Recipe> doInBackground(String...strings) {
            //set the uri of the string (now is equal for every entry)
            try {
                List<Recipe> result;
                String pathImage;
                if (!categorySelectedName.equals("Vegetarian")) {
                    Log.e("RecipeList:", "The name of the category is:" + categorySelectedName);
                    Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                    eav.put(":category", new AttributeValue().withS(categorySelectedName));

                    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                            .withFilterExpression("Category = :category")
                            .withExpressionAttributeValues(eav);

                    result = mapper.scan(Recipe.class, scanExpression); //TODO handle timeout (manda eccezione a volte)

                    Log.e("RecipeList", "The results are:" + result.toString());

                } else {

                    Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                    eav.put(":vegetarian", new AttributeValue().withS("1"));

                    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                            .withFilterExpression("Vegetarian = :vegetarian")
                            .withExpressionAttributeValues(eav);

                    result = mapper.scan(Recipe.class, scanExpression);

                }
                    if (result.size() != 0) {
                        // WE HAVE THE RESULT
                        for (Recipe item : result) {
                            //Get all attributes from the DB
                            //modify the image uri , save it back and save in list
                            String imageUri = item.getCategory() + "/" + item.getImage() + ".jpg";
                            item.setImage(imageUri);
                            recipesList.add(item);
                            recipeDisplayer.put(item, DISPLAY);

                        }
                        Log.e("recipeDisplayer: ", recipeDisplayer.toString());
                        downloadState = DownloadState.COMPLETED;
                        return recipesList;
                    } else {
                        // WE DON'T HAVE THE RESULT
                        downloadState = DownloadState.COMPLETED; //TODO recipe non presente nel DB
                        return null;
                    }
                }catch(RuntimeException e){
                    // CONNECTION ERROR
                    downloadState = DownloadState.ERROR;
                    Log.e(TAG, "doInBackground: RuntimeException: " + e.getMessage()); //TODO errore connessione
                    return null;
                }
        }


        protected void onPostExecute(ArrayList<Recipe> result) {
            checkDownload(downloadState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPES_LIST, recipesList);
        outState.putSerializable(RECIPES_DISPLAYER, recipeDisplayer);
        outState.putSerializable(DOWNLOAD_STATE, downloadState);
        super.onSaveInstanceState(outState);
    }

    public interface OnRecipeSelectedListener {
        public void onRecipeSelected(Recipe recipeSelected);
    }


    /**
     * Method called to display the filter at single click when we are in the fragment were is possible to
     * perform this operation
     */
    public void onFilterChange() {
        Filter filter = Filter.getInstance(getContext());
        ArrayList<String> active = filter.getActivatedFilters();

            //For every recipe we go to change the adapter accordingly
            for (Recipe r : recipeDisplayer.keySet()) {
                ArrayList<String> activeDifficulty = filter.getActivePerGroup().get("Difficulty");
                ArrayList<String> activeTime = filter.getActivePerGroup().get("Preparation_Time");
                ArrayList<String> activeCountry = filter.getActivePerGroup().get("Country");
                boolean firstCheck = true;
                boolean secondCheck = true;
                boolean thirdCheck = true;
                if(activeDifficulty != null) {
                    Log.e("CheckDifficulty: ", Integer.toString(r.getDifficulty()));
                    String difficulty = "";
                    if (r.getDifficulty() == 1)
                        difficulty = "Easy";
                    if (r.getDifficulty() == 2)
                        difficulty = "Medium";
                    if (r.getDifficulty() == 3)
                        difficulty = "Hard";
                    //Vede se ha quel valore
                    if (!active.contains(difficulty))
                        firstCheck = false;
                }

                if(activeTime != null) {
                    Log.e("CheckTime: ", r.getPreparationTime());
                    String[] temp = activeTime.get(0).split(" ");
                    int min = Integer.parseInt(temp[2]);
                    int preparationTime = Integer.parseInt(r.getPreparationTime());
                    int cookingTime = Integer.parseInt(r.getCookingTime());
                    if (!((preparationTime + cookingTime) <= min))
                        secondCheck = false;
                }


                if(activeCountry != null) {
                    Log.e("CheckCountry: ", r.getCountry() );
                    if(!(active.contains(r.getCountry())))
                        thirdCheck = false;
                }

                //We set finally if the recipe is going to be seen or not
                if(firstCheck && secondCheck && thirdCheck) {
                    Log.e("displayRecipe: ", r.getName());
                    recipeDisplayer.put(r, DISPLAY);
                    if(!recipesList.contains(r))
                        recipesList.add(r);
                } else {
                    Log.e("deactivateRecipe: ", r.getName());
                    recipeDisplayer.put(r, NOT_DISPLAY);
                    if(recipesList.contains(r))
                        recipesList.remove(r);
                }
            }

        recipeDisplayer.toString();
        recipesList.toString();
        adapter.notifyDataSetChanged();

        if(downloadState == DownloadState.COMPLETED) {
            UIShowList(null);
        }

    }

    /**
     * Updates the UI w.r.t. the status of the download of the recipe
     * @param mState status of the download of the recipe
     */
    private void checkDownload(DownloadState mState, View view) {
        Log.d(TAG, "checkDownload: downloadingState = " + mState.toString());

        if(mState == DownloadState.DOWNLOADING) {
            UIDownloading(view);
        } else
        if(mState == DownloadState.COMPLETED) {
            UIDownloadCompleted(view);
        } else
        if(mState == DownloadState.ERROR) {
            UIDownloadError(view );
        }
    }

    /**
     * Updates the UI w.r.t. the status of the download of the recipe
     * @param mState status of the download of the recipe
     */
    private void checkDownload(DownloadState mState) {
        checkDownload(mState, null);
    }

    /**
     * Show a progress bar
     */
    private void UIDownloading(View view) {
        if(view == null)
            view = getView();
        Log.e(TAG, "UIDownloading" );
        try {
            view.findViewById(R.id.fragment_recipes_list_recycler_view).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException container_full: " + e );
        }
        try {
            view.findViewById(R.id.empty_recipes_list).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException container_empty: " + e );
        }
        try {
            view.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException progress_bar_stub: " + e );
        }
        try {
            view.findViewById(R.id.download_error).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException download_error: " + e );
        }
    }

    /**
     * Show a message of connection error
     */
    private void UIDownloadError(View view) {
        if(view == null)
            view = getView();
        Log.e(TAG, "UIDownloadError" );
        // Toast.makeText(getApplicationContext(), "ERRORE INTERNET PROVA TODO", Toast.LENGTH_SHORT).show();
        try {
            view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException progress_bar: " + e );
        }
        try {
            view.findViewById(R.id.empty_recipes_list).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException container_empty: " + e );
        }
        try {
            view.findViewById(R.id.fragment_recipes_list_recycler_view).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException container_full: " + e );
        }
        try {
            view.findViewById(R.id.download_error).setVisibility(View.VISIBLE);  //TODO errore connessione
            view.findViewById(R.id.download_error_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIDownloading(null);
                    data = new RecipeData();
                    data.execute("");
                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException download_error_stub: " + e );
        }
    }

    /**
     * Show the recipe with all its content
     */
    private void UIDownloadCompleted(View view) {
        if(view == null)
            view = getView();
        Log.e(TAG, "UIDownloadCompleted" );
        onFilterChange();
        UIShowList(view);
        try {
            view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadCompleted: NullPointerException progress_bar: " + e );
        }
        try {
            view.findViewById(R.id.download_error).setVisibility(View.GONE);
        }
        catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadCompleted: NullPointerException download_error: " + e );
        }
    }

    private void UIShowList(View view) {
        Log.e(TAG, "UIShowList: ");
        String print = "";
        for(int i=0; i < recipesList.size(); i ++) {
            print+=recipesList.get(i).getName() + " ";
        }
        Log.e(TAG, "UIShowList: " + print );
        if(view == null)
            view = getView();
        // DISPLAY EMPTY MESSAGE OR NOT
        if(recipesList.size() == 0) {
            Log.d(TAG, "onItemsChanged: the list is EMPTY" );
            try {
                view.findViewById(R.id.fragment_recipes_list_recycler_view).setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.e(TAG, "UIShowList: NullPointerException container_full: " + e );
            }
            try {
                view.findViewById(R.id.empty_recipes_list).setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                Log.e(TAG, "UIShowList: NullPointerException container_empty: " + e );
            }
        } else {
            Log.d(TAG, "onItemsChanged: the list contains ITEMS" );
            try {
                view.findViewById(R.id.fragment_recipes_list_recycler_view).setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                Log.e(TAG, "UIShowList: NullPointerException container_full: " + e );
            }
            try {
                view.findViewById(R.id.empty_recipes_list).setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.e(TAG, "UIShowList: NullPointerException container_empty: " + e );
            }
        }
    }
}
