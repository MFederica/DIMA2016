package com.appetite;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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

import java.util.ArrayList;
import java.util.List;

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
    private static final String ARG_RECIPENAME = "recipeName";

    private String recipeName;

    //Variables for the recycler view
    private List<Recipe> recipesList = new ArrayList<Recipe>();
    private RecyclerView recyclerView;
    private AdapterRecipesList adapter;

    public FragmentRecipesList() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * {} interface
     * @param recipeName name of the recipe.
     * @return A new instance of fragment FragmentCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRecipesList newInstance(String recipeName) {
        FragmentRecipesList fragment = new FragmentRecipesList();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, recipeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(ARG_RECIPENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recipes_list_recycler_view);

        //Use array adapter here

        //Use AdapterCategory with a custom object
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
            public void onItemClick(String textName){
                Toast.makeText(getContext(), textName, Toast.LENGTH_SHORT).show();
                Log.e("FragmentRecipesList", "onItemClick: " + textName);
                /*EventFragment eventFragment = EventFragment.newInstance();
                //replace content frame with your own view.
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();    ft.replace(R.id.content_frame, eventFragment).commit() */
            }
        });
        prepareRecipesListData();

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
    private void prepareRecipesListData() {
        //TODO set the uri of the string (now is equal for every entry)
        String uri = "@drawable/breakfast";
        int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());

        Recipe recipe = new Recipe(recipeName + " 0", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 1", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 2", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 3", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 4", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 5", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 6", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 7", imageResource);
        recipesList.add(recipe);

        recipe = new Recipe(recipeName + " 8", imageResource);
        recipesList.add(recipe);


        adapter.notifyDataSetChanged();

    }

}
