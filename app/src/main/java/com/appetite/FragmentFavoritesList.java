package com.appetite;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.dmfm.appetite.R;
import com.appetite.model.FavoritesHelper;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFavoritesListFragmentInteractionListener}
 * interface.
 */
public class FragmentFavoritesList extends Fragment {

    //TODO implementare la possibilità di cambiare il numero di servings nella shopping list
    //TODO (o perlomeno mostrarlo perchè ora si moltiplica..)!!

    //TODO fare un fragment di lista di ricette e poi una volta dentro vedere gli ingredienti comprati

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnFavoritesListFragmentInteractionListener mListener;
    private AdapterFavoritesList adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentFavoritesList() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentFavoritesList newInstance(int columnCount) {
        FragmentFavoritesList fragment = new FragmentFavoritesList();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.drawer_item_favourite));
        }

        View view = inflater.inflate(R.layout.fragment_favoriteslist, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new AdapterFavoritesList(context, FavoritesHelper.getInstance(context).favoritesList, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoritesListFragmentInteractionListener) {
            mListener = (OnFavoritesListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnShoppingListFragmentInteractionListener");
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
    public interface OnFavoritesListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFavoritesListFragmentInteraction(String item);
    }

    public void addElement(String favorite, int position) {
        FavoritesHelper.getInstance(getContext()).favoritesList.add(position, favorite);
        FavoritesHelper.saveFavorites(getContext());
        adapter.notifyItemInserted(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
    }
}
