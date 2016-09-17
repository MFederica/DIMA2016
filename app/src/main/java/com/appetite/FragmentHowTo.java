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
import com.appetite.model.HowToItem;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;
import com.appetite.style.SimpleDividerItemDecoration;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHowToListFragmentInteractionListener}
 * interface.
 */
public class FragmentHowTo extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnHowToListFragmentInteractionListener mListener;
    private AdapterHowToList adapter;

    //List of videos
    private static final List<HowToItem> VIDEO_LIST;
    static {
        ArrayList<HowToItem> list = new ArrayList<HowToItem>();
        list.add(new HowToItem("How to Apply Fondant ", "EYFLLlOtGYI", "http://img.youtube.com/vi/EYFLLlOtGYI/1.jpg"));
        list.add(new HowToItem("Assemble a piping bag", "J4eJj2SAjk4","http://img.youtube.com/vi/J4eJj2SAjk4/2.jpg"));
        list.add(new HowToItem("How to Caramelize Sugar", "vxTLy7hUkeU", "http://img.youtube.com/vi/vxTLy7hUkeU/1.jpg"));
        list.add(new HowToItem("Make Creamy Mashed Potatoes", "5N6AMGf0G88", "http://img.youtube.com/vi/5N6AMGf0G88/1.jpg"));
        list.add(new HowToItem("Crack and Separate Eggs", "aJH2l5x7o3s", "http://img.youtube.com/vi/aJH2l5x7o3s/2.jpg"));
        list.add(new HowToItem("Cook the Pasta Like a Pro", "uDmyDjaC9DA", "http://img.youtube.com/vi/uDmyDjaC9DA/2.jpg"));
        list.add(new HowToItem("Master Risotto", "fCWp6CSeXB4", "http://img.youtube.com/vi/fCWp6CSeXB4/1.jpg"));
        list.add(new HowToItem("Cook the Perfect Steak", "nE4xh6VDZhU", "http://img.youtube.com/vi/nE4xh6VDZhU/2.jpg"));
        list.add(new HowToItem("Reach the Perfect Temperature for Frying", "ntbpM5_B9vY", "http://img.youtube.com/vi/ntbpM5_B9vY/2.jpg"));
        VIDEO_LIST = Collections.unmodifiableList(list);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentHowTo() {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentHowTo newInstance(int columnCount) {
        FragmentHowTo fragment = new FragmentHowTo();
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
            actionBar.setTitle(getString(R.string.drawer_item_how_to));
        }

        // Set the recycler view..
        View rootView = inflater.inflate(R.layout.fragment_how_to_list, container, false);
        View view = rootView.findViewById(R.id.fragment_how_to_recycler_view);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        //..and the adapter
            adapter = new AdapterHowToList(context, VIDEO_LIST, mListener, this);
            recyclerView.setAdapter(adapter);
        }

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHowToListFragmentInteractionListener) {
            mListener = (OnHowToListFragmentInteractionListener) context;
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
    public interface OnHowToListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHowToListFragmentInteraction(HowToItem item, int position);
    }
}
