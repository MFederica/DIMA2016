package com.appetite;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appetite.model.Recipe;

import java.util.List;
//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentRecipeInformation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRecipeInformation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRecipeInformation extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
 /*   private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2"; */
    private static final String ARG_RECIPE_NAME = "recipeName"; //TODO CHECK
    private static final String ARG_RECIPE = "com.appetite.RECIPE";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private String recipeName;
    private Recipe recipe;

    //TODO INTERFACCIA: decommenta 1/4
    //private OnFragmentInteractionListener mListener;

    public FragmentRecipeInformation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param recipe recipe selected
     * @return A new instance of fragment FragmentRecipeInformation.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRecipeInformation newInstance(Recipe recipe) {
        FragmentRecipeInformation fragment = new FragmentRecipeInformation();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2); */
        //  args.putString(ARG_RECIPE_NAME, recipeName);
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
            //recipeName = getArguments().getString(ARG_RECIPE_NAME);
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<String> ingredient_name = recipe.getIngredient_name();
        List<String> ingredient_quantity = recipe.getIngredient_quantity();
        List<String> ingredient_unit = recipe.getIngredient_unit();

        View rootView = inflater.inflate(
                R.layout.fragment_recipe_information, container, false);
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_name)).setText("NAME:  " + recipe.getName());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_category)).setText("CATEGORY:  " + recipe.getCategory());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_country)).setText("COUNTRY:  " + recipe.getCountry());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_advice)).setText("ADVICE:  " + recipe.getAdvice());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_introduction)).setText("INTRODUCTION:  " + recipe.getIntroduction());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_preparation_time)).setText("PREPARATION TIME:  " + recipe.getPreparationTime());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_cooking_time)).setText("COOKING TIME:  " + recipe.getCookingTime());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_difficulty)).setText("DIFFICULTY:  " + Integer.toString(recipe.getDifficulty()));
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_vegetarian)).setText("VEGETARIAN:  " + recipe.getVegetarian());
        String text = "\n";
        for(int i = 0; i < recipe.getIngredient_name().size(); i++) {
            Log.e("Ingredients", ingredient_name.get(i));
            Log.e("Ingredients", ingredient_quantity.get(i));
            Log.e("Ingredients", ingredient_unit.get(i));
            text = text.concat(ingredient_name.get(i) + " " +   ingredient_quantity.get(i) + ingredient_unit.get(i) + "\n");
        }
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_ingredient)).setText("INGREDIENTS:  " + text);
        Log.e("Ingredients", text);

        return rootView;
    }

    //TODO INTERFACCIA: decommenta 2/4
/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    } */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //TODO INTERFACCIA: decommenta 3/4 questo va in onAttach
    }
 /*       if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    } */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    //TODO INTERFACCIA: decommenta 4/4
        /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
}
