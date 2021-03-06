package com.appetite.recipe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

//import com.dmfm.appetite.R;
import com.appetite.R;
import com.appetite.model.Recipe;
import com.appetite.model.RecipeIngredient;
import com.appetite.style.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentRecipeIngredients extends Fragment {

    private static final String TAG = FragmentRecipeIngredients.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_RECIPE = "com.appetite.RECIPE";
    private static final String ARG_CURRENT_SERVINGS = "com.appetite.fragment_recipeingredients_CURRENT_SERVINGS";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private Recipe recipe;
    private List<RecipeIngredient> recipeIngredients = new ArrayList<RecipeIngredient>();
    private int currentServings;
    private AdapterRecipeIngredients adapter;
    private TextView servingsTextView;

    //TODO INTERFACCIA: decommenta 1/4
    //private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentRecipeIngredients() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentRecipeIngredients newInstance(Recipe recipe) {
        Log.d(TAG, "newInstance: ");
        FragmentRecipeIngredients fragment = new FragmentRecipeIngredients();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.toString(), "onCreate");
        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            recipe = getArguments().getParcelable(ARG_RECIPE);
            Log.d(TAG, "onCreate: received argument: "+String.valueOf(getArguments().getInt(ARG_CURRENT_SERVINGS)));

            if (savedInstanceState != null) {
                // Restore value of members from saved state
                currentServings = savedInstanceState.getInt(ARG_CURRENT_SERVINGS);
                Log.d(TAG, "onCreate: from savedInstanceState: "+String.valueOf(currentServings));
            }
            else {
                currentServings = Integer.valueOf(recipe.getAmount());
                Log.d(TAG, "onCreate: from RECIPE: "+String.valueOf(currentServings));
            }

            for(int i=0; i< recipe.getIngredient_name().size(); i++) {
                RecipeIngredient recipeIngredient = new RecipeIngredient(recipe.getIngredient_name().get(i),recipe.getIngredient_quantity().get(i), recipe.getIngredient_unit().get(i));
                recipeIngredients.add(recipeIngredient);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_recipe_ingredients_list, container, false);
        Button button = (Button) view.findViewById(R.id.fragment_recipeingredients_servings);
        button.setText(String.valueOf(currentServings));
        servingsTextView = (TextView) view.findViewById(R.id.fragment_recipeingredients_servingstext);
        if(currentServings > 1)
            servingsTextView.setText(getResources().getString(R.string.fragment_recipeingredients_servings));
        else
            servingsTextView.setText(getResources().getString(R.string.fragment_recipeingredients_serving));

        // Set the adapter
        final View rcView = view.findViewById(R.id.fragment_recipeingredients_list);
        if (rcView instanceof RecyclerView) {
            Context context = rcView.getContext();
            RecyclerView recyclerView = (RecyclerView) rcView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
            //TODO INTERFACCIA: decommenta 2/5 e cambia costruttore
            //recyclerView.setAdapter(new AdapterRecipeIngredients(DummyContent.ITEMS, mListener));
            adapter = new AdapterRecipeIngredients(getContext(), recipe, recipeIngredients, Integer.valueOf(currentServings));
            recyclerView.setAdapter(adapter);
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final View viewServings = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_recipe_ingredients_servingsdialog, null);

                final NumberPicker numberPicker = (NumberPicker) viewServings.findViewById(R.id.numberPicker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(100);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setValue(currentServings);

                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(viewServings);
                builder.setTitle(R.string.fragment_recipeingredients_dialog_title)
                        .setPositiveButton(R.string.servings_picker_done, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                currentServings = numberPicker.getValue();
                                if(currentServings > 1)
                                    servingsTextView.setText(getResources().getString(R.string.fragment_recipeingredients_servings));
                                else
                                    servingsTextView.setText(getResources().getString(R.string.fragment_recipeingredients_serving));

                                adapter.setAmount(currentServings);
                                adapter.notifyDataSetChanged();
                                ((Button) view.findViewById(R.id.fragment_recipeingredients_servings)).setText(String.valueOf(currentServings));
                                Log.d(TAG, "onClick (numberPicker): currentServings = "+String.valueOf(currentServings));
                            }
                        });
                // Create the AlertDialog object and return it
                builder.show();

            }
        });
        return view;
    }

    //TODO INTERFACCIA: decommenta 3/5
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        } */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO INTERFACCIA: decommenta 4/5
        //mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState: i'm going to save: "+ String.valueOf(savedInstanceState.getInt(ARG_CURRENT_SERVINGS)));
        savedInstanceState.putInt(ARG_CURRENT_SERVINGS, currentServings);
        Log.d(TAG, "onSaveInstanceState: i've just saved: "+ String.valueOf(savedInstanceState.getInt(ARG_CURRENT_SERVINGS)));
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    //TODO INTERFACCIA: decommenta 5/5
        /*
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    } */

    public int getCurrentServings() {
        return currentServings;
    }
}
