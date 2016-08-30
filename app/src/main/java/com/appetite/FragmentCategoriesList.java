package com.appetite;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
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
 * Use the {@link FragmentCategoriesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategoriesList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    //Variable to communicate to the activity
    OnCategorySelectedListener mCallback;

    public FragmentCategoriesList() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * {} interface
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategoriesList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategoriesList newInstance(String param1, String param2) {
        FragmentCategoriesList fragment = new FragmentCategoriesList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Toast.makeText(getContext(), textName, Toast.LENGTH_SHORT).show();
                mCallback.onCategorySelected(textName);
                Log.e("FragmentCategoriesList", "onItemClick: " + textName);
                /*EventFragment eventFragment = EventFragment.newInstance();
                //replace content frame with your own view.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    ft.replace(R.id.content_frame, eventFragment).commit() */
            }
        });

        return rootView;
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
                for (Map<String, AttributeValue> item : result.getItems()) {

                    String name = item.get("name").getS();
                    String imageUri = categoryBucket + item.get("image").getS() + ".jpg";
                    Log.e("ImageURI:", imageUri);
                    //int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
                    Category category = new Category(name, imageUri);
                    categoryList.add(category);
                }
                return categoryList;
            } catch (RuntimeException e) {
                Log.e("FragmentCategoriesList", "doInBackground: Error");
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Category> result) {
                adapter.notifyDataSetChanged();
            }
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", categoryList);
        super.onSaveInstanceState(outState);
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(String textName);
    }
}
