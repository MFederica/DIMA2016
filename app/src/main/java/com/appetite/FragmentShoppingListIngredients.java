package com.appetite;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;
//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentShoppingListIngredients.OnShoppingListIngredientFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentShoppingListIngredients#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShoppingListIngredients extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHOPPING_ITEM = "com.appetite.FragmentShoppingListIngredients.SHOPPING_ITEM";
    private static final String ARG_SHOPPING_POSITION = "com.appetite.FragmentShoppingListIngredients.SHOPPING_POSITION";

    // TODO: Rename and change types of parameters
    private ShoppingItem shoppingItem;
    private int position;

    private OnShoppingListIngredientFragmentInteractionListener mListener;

    private TextView mRecipeNameView;
    private RecyclerView mIngredientsListView;
    private Button mRemoveButton;

    public FragmentShoppingListIngredients() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param shoppingItem
     * @return A new instance of fragment FragmentShoppingListIngredients.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShoppingListIngredients newInstance(ShoppingItem shoppingItem, int position) {
        FragmentShoppingListIngredients fragment = new FragmentShoppingListIngredients();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHOPPING_ITEM, shoppingItem);
        args.putInt(ARG_SHOPPING_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shoppingItem = (ShoppingItem) getArguments().getSerializable(ARG_SHOPPING_ITEM);
            position = getArguments().getInt(ARG_SHOPPING_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.drawer_item_shopping_list));
        }

        View rootView = inflater.inflate(R.layout.fragment_shoppinglist_item_recipe, container, false);
        mIngredientsListView = (RecyclerView) rootView.findViewById(R.id.fragment_shoppinglist_ingredientslist);
        mRecipeNameView = (TextView) rootView.findViewById(R.id.fragment_shoppinglist_recipe);
        mRemoveButton = (Button) rootView.findViewById(R.id.fragment_shoppinglist_remove);
        mRecipeNameView.setText(shoppingItem.getRecipe());
        mIngredientsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mIngredientsListView.setAdapter(new AdapterShoppingListIngredient(getContext(), shoppingItem));
        mRecipeNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnShoppingListIngredientFragmentInteraction(shoppingItem);
                }
            }
        });
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShoppingListHelper.getInstance(getContext()).removeRecipe(shoppingItem)) {
                    ShoppingListHelper.saveShoppingList(getContext());

                    mListener.OnShoppingListIngredientFragmentDeletion(shoppingItem, position);
                }
            }
        });
        // Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShoppingListIngredientFragmentInteractionListener) {
            mListener = (OnShoppingListIngredientFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
    public interface OnShoppingListIngredientFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnShoppingListIngredientFragmentInteraction(ShoppingItem shoppingItem);
        void OnShoppingListIngredientFragmentDeletion(ShoppingItem shoppingItem, int position);
    }
}
