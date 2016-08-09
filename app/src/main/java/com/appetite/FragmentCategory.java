package com.appetite;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.policy.Resource;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.appetite.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link FragmentCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variables for the recycler view
    private List<Category> categoryList = new ArrayList<Category>();
    private RecyclerView recyclerView;
    private AdapterCategory adapter;
    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

    //Variable to communicate to the activity
    OnCategorySelectedListener mCallback;

    public FragmentCategory() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * {} interface
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategory newInstance(String param1, String param2) {
        FragmentCategory fragment = new FragmentCategory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CategoryData data = new CategoryData();
        data.execute("");

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
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_category_recycler_view);

        //Use array adapter here

        //Use AdapterCategory with a custom object
        adapter = new AdapterCategory(getContext(), categoryList);
        int columns;
        //Set the grid layout manager
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
        adapter.setOnItemClickListener(new AdapterCategory.OnItemClickListener(){
            public void onItemClick(String textName){
                Toast.makeText(getContext(), textName, Toast.LENGTH_SHORT).show();
                mCallback.onCategorySelected(textName);
                Log.e("FragmentCategory", "onItemClick: " + textName);
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
    private class CategoryData extends AsyncTask<String, Void, ScanResult> {

        protected CategoryData() {

        }
        /**
         * This method runs in background to retrieve data from database
         * @param strings
         * @return
         */
        public ScanResult doInBackground(String...strings) {
            //set the uri of the string (now is equal for every entry)
            String tableName = "dima-mobilehub-516910810-Category";
            ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
            ScanResult result = dynamoDBClient.scan(scanRequest);
            return result;
        }

        protected void onPostExecute(ScanResult result) {
            List<String> names = new ArrayList<String>();
            List<String> images = new ArrayList<String>();
            for (Map<String, AttributeValue> item : result.getItems()) {
                String name = item.get("name").getS();
                String uri = "@drawable/" + item.get("image").getS();
                int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
                Category category = new Category(name, String.valueOf(imageResource));
                categoryList.add(category);

            }
            adapter.notifyDataSetChanged();
        }
    }


    public interface OnCategorySelectedListener {
        public void onCategorySelected(String textName);
    }
}
