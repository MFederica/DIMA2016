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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.appetite.R;
import com.appetite.adapter.AdapterCategoriesList;
import com.appetite.model.Category;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 */
public class FragmentCategoriesList extends Fragment {
    private final static String TAG = FragmentCategoriesList.class.getSimpleName();
    private static final String DOWNLOAD_STATE = "com.appetite.fragment.FragmentCategoriesList.CATEGORIES_LIST.DOWNLOAD_STATE";
    private static final String CATEGORIES_LIST = "com.appetite.fragment.FragmentCategoriesList.CATEGORIES_LIST";

    //Variables for the recycler view
    private ArrayList<Category> categoryList = new ArrayList<Category>();
    private RecyclerView recyclerView;
    private AdapterCategoriesList adapter;
    private ImageLoader imageLoader;

    //Variables for the db
    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
    //Constans TODO: Create a Class that contans all the constants needed
    private final String categoryTable = "dima-mobilehub-516910810-Category";
    private final String categoryBucket = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/Category/";

    CategoryData data;

    //Variable to communicate to the activity
    OnCategorySelectedListener mCallback;

    public enum DownloadState {
        IDLE, DOWNLOADING, COMPLETED, ERROR, STOPPED
    }
    DownloadState downloadState = DownloadState.IDLE;

    public FragmentCategoriesList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "onCreate: savedInstanceState != null" );
            downloadState = (DownloadState) savedInstanceState.getSerializable(DOWNLOAD_STATE);
            categoryList = savedInstanceState.getParcelableArrayList(CATEGORIES_LIST);
            adapter = new AdapterCategoriesList(getContext(), categoryList);
        } else {
            Log.e(TAG, "onCreate: savedInstanceState == null (1st invocation)");
        }
        if(downloadState == DownloadState.IDLE || downloadState == DownloadState.DOWNLOADING) {
            data = new CategoryData();
            data.execute("");
        }

        /*
        Bundle bundle = ((ActivityMain) getActivity()).getCategoryBundle();
        if(bundle != null) {savedInstanceState = bundle;}
        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
                Log.e("FragmentCategoriesList:", "The first time that the fragment is called");
                CategoryData data = new CategoryData();
                data.execute("");
        } else {
            Log.e("FragmentCategoriesList:", "Other times the database is not queried anymore");
            //Use AdapterCategoriesList with a custom object

            adapter = new AdapterCategoriesList(getContext(), categoryList);
            categoryList = savedInstanceState.getParcelableArrayList("key");
            adapter.notifyDataSetChanged();
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        Activity activity = (Activity) context;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.drawer_item_categories));
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_category_recycler_view);

        int columns;
        //Set the grid layout manager

        //Use AdapterCategoriesList with a custom object
        adapter = new AdapterCategoriesList(getContext(), categoryList);
        //Calculate number of columns needed
        if(getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT)
            columns = 2;
        else
            columns = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterCategoriesList.OnItemClickListener(){
            public void onItemClick(String textName){
            //    Toast.makeText(getContext(), textName, Toast.LENGTH_SHORT).show();
                mCallback.onCategorySelected(textName);
                Log.e("FragmentCategoriesList", "onItemClick: " + textName);
                /*EventFragment eventFragment = EventFragment.newInstance();
                //replace content frame with your own view.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    ft.replace(R.id.content_frame, eventFragment).commit() */
            }
        });

        checkDownload(downloadState, rootView);
        return rootView;
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
    private class CategoryData extends AsyncTask<String, Void, ArrayList<Category>> {

        protected CategoryData() {

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
        public ArrayList<Category> doInBackground(String...strings) {
            //set the uri of the string (now is equal for every entry)
            try {
                ScanRequest scanRequest = new ScanRequest().withTableName(categoryTable);
                ScanResult result = dynamoDBClient.scan(scanRequest);
                if(result.getCount() != 0) {
                    // WE HAVE THE RESULT
                    for (Map<String, AttributeValue> item : result.getItems()) {

                        String name = item.get("name").getS();
                        String imageUri = categoryBucket + item.get("image").getS() + ".jpg";
                        Log.e("ImageURI:", imageUri);
                        //int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
                        Category category = new Category(name, imageUri);
                        categoryList.add(category);
                    }
                    downloadState = DownloadState.COMPLETED;
                    return categoryList;
                } else {
                    // WE DON'T HAVE THE RESULT
                    downloadState = DownloadState.COMPLETED; //TODO recipe non presente nel DB
                    return null;
                }
            } catch (RuntimeException e) {
                // CONNECTION ERROR
                downloadState = DownloadState.ERROR;
                Log.e(TAG, "doInBackground: RuntimeException: " + e.getMessage()); //TODO errore connessione
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Category> result) {
                adapter.notifyDataSetChanged();
                checkDownload(downloadState);
            }
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CATEGORIES_LIST, categoryList);
        outState.putSerializable(DOWNLOAD_STATE, downloadState);
        super.onSaveInstanceState(outState);
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(String textName);
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
            view.findViewById(R.id.fragment_category_recycler_view).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloading: NullPointerException container_full: " + e );
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
            view.findViewById(R.id.fragment_category_recycler_view).setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIDownloadError: NullPointerException container_full: " + e );
        }
        try {
            view.findViewById(R.id.download_error).setVisibility(View.VISIBLE);  //TODO errore connessione
            view.findViewById(R.id.download_error_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIDownloading(null);
                    data = new CategoryData();
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

        try {
            view.findViewById(R.id.fragment_category_recycler_view).setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.e(TAG, "UIShowList: NullPointerException container_full: " + e );
        }
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
}
