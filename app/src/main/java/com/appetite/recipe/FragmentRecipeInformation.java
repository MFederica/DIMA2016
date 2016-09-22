package com.appetite.recipe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.R;
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
    private final static String TAG = FragmentRecipeInformation.class.getSimpleName();
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


        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_country)).setText(recipe.getCountry());
        String countryImageName = "drawable/flag_" + recipe.getCountry().toLowerCase().replace(" ", "_");
        int countryImageId = getResources().getIdentifier(countryImageName, null, getContext().getPackageName());
        Log.e(TAG, "onCreateView: "+ countryImageName );
        ((ImageView) rootView.findViewById(R.id.fragment_recipe_information_country_image)).setImageDrawable(getResources().getDrawable(countryImageId));
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_advice)).setText(recipe.getAdvice());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_introduction)).setText(recipe.getIntroduction());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_preparation_time)).setText(recipe.getPreparationTime());
        ((TextView) rootView.findViewById(R.id.fragment_recipe_information_cooking_time)).setText(recipe.getCookingTime());


        //TextView difficultyView = ((TextView) rootView.findViewById(R.id.fragment_recipe_information_difficulty));
        //String difficulty = "";
        switch(recipe.getDifficulty()) {
            case 1:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_1);
                ((ImageView) rootView.findViewById(R.id.fragment_recipe_information_difficulty_image)).setImageDrawable(getResources().getDrawable(R.drawable.difficulty_1));
                break;
            case 2:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_2);
                ((ImageView) rootView.findViewById(R.id.fragment_recipe_information_difficulty_image)).setImageDrawable(getResources().getDrawable(R.drawable.difficulty_2));
                break;
            case 3:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_3);
                ((ImageView) rootView.findViewById(R.id.fragment_recipe_information_difficulty_image)).setImageDrawable(getResources().getDrawable(R.drawable.difficulty_3));
                break;
        }
        //difficultyView.setText(difficulty);

        if(recipe.getVegetarian().compareTo("1") == 0) {
            rootView.findViewById(R.id.fragment_recipe_information_vegetarian).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.fragment_recipe_information_vegetarian).setVisibility(View.GONE);
        }

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
