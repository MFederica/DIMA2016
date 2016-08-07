package com.appetite;

import android.content.res.Configuration;
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

import com.amazonaws.auth.policy.Resource;

import java.util.ArrayList;
import java.util.List;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_category_recycler_view);

        //Use array adapter here

        //Use AdapterCategory witha a custom object
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

        prepareCategoryData();

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

    //Function that populates the object that has to be inflated in the frame
    private void prepareCategoryData() {
        //set the uri of the string (now is equal for every entry)
        String uri = "@drawable/breakfast";
        int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());

        Category category = new Category("Breakfast", imageResource);
        categoryList.add(category);

        category = new Category("First Dishes", imageResource);
        categoryList.add(category);

        category = new Category("Second Dishes", imageResource);
        categoryList.add(category);

        category = new Category("Salad", imageResource);
        categoryList.add(category);

        category = new Category("Vegetarian", imageResource);
        categoryList.add(category);

        category = new Category("Dessert", imageResource);
        categoryList.add(category);

        category = new Category("Appetizers", imageResource);
        categoryList.add(category);

        category = new Category("Drinks & Beverages", imageResource);
        categoryList.add(category);

        category = new Category("Low Calories", imageResource);
        categoryList.add(category);

        adapter.notifyDataSetChanged();

    }



}
