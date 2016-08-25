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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.appetite.model.Category;
import com.appetite.model.Recipe;

import java.lang.reflect.Array;
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CATEGORY_SELECTED_NAME = "com.appetite.CATEGORY_SELECTED";

    private String categorySelectedName;

    //Variables for the recycler view
    private ArrayList<Recipe> recipesList = new ArrayList<Recipe>();
    private RecyclerView recyclerView;
    private AdapterRecipesList adapter;

    //Variable to communicate to the activity
    OnRecipeSelectedListener mCallBack;

    //Variables for the db
    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
    //Constans TODO: Create a Class that contans all the constants needed
    private final String recipeTable = "dima-mobilehub-516910810-Recipe";
    private final String bucket = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/";

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
        Bundle bundle = ((ActivityMain) getActivity()).getCategoryBundle();
        if(bundle != null) {savedInstanceState = bundle;}
        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            Log.e("FragmentCategoriesList:", "The first time that the fragment is called");
            RecipeData data = new RecipeData();
            data.execute("");
        } else {
            Log.e("FragmentCategoriesList:", "Other times the database is not queried anymore");
            //Use AdapterCategoriesList with a custom object

            adapter = new AdapterRecipesList(getContext(), recipesList);
            recipesList = savedInstanceState.getParcelableArrayList("key");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterRecipesList.OnItemClickListener(){
            public void onItemClick(Recipe recipe){
                Toast.makeText(getContext(), recipe.getName(), Toast.LENGTH_SHORT).show();
                mCallBack.onRecipeSelected(recipe);
                Log.e("FragmentRecipesList", "onItemClick: " + recipe.getName());
                /*EventFragment eventFragment = EventFragment.newInstance();
                //replace content frame with your own view.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    ft.replace(R.id.content_frame, eventFragment).commit() */
            }
        });

        return rootView;
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
    private class RecipeData extends AsyncTask<String, Void, ArrayList<Recipe>> {

        protected RecipeData() {

        }
        /**
         * This method runs in background to retrieve data from database
         * @param strings
         * @return
         */
        public ArrayList<Recipe> doInBackground(String...strings) {
            //set the uri of the string (now is equal for every entry)

                Log.e("RecipeList:", "The name of the category is:" + categorySelectedName);
                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":category", new AttributeValue().withS(categorySelectedName));

                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                        .withFilterExpression("Category = :category")
                        .withExpressionAttributeValues(eav);

                List<Recipe> result = mapper.scan(Recipe.class, scanExpression);

                Log.e("RecipeList", "The results are:" + result.toString());

                for (Recipe item : result) {
                    //Get all attributes from the DB
                    //modify the image uri , save it back and save in list
                    String imageUri = categorySelectedName + "/" + item.getImage() + ".jpg";
                    item.setImage(imageUri);
                    recipesList.add(item);
                }
                return recipesList;
        }

        protected void onPostExecute(ArrayList<Recipe> result) {adapter.notifyDataSetChanged();}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", recipesList);
        super.onSaveInstanceState(outState);
    }

    public interface OnRecipeSelectedListener {
        public void onRecipeSelected(Recipe recipeSelected);
    }

}
