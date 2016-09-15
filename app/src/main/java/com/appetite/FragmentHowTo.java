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

import java.util.ArrayList;
import java.util.Collections;
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
        List<HowToItem> list = new ArrayList<HowToItem>();
        list.add(new HowToItem("YouTube Collection", "Y_UmWdcTrrc")); //TODO nomi in R.string, URL anche no..imho
        list.add(new HowToItem("GMail Tap", "1KhZKNZO8mQ"));
        list.add(new HowToItem("Chrome Multitask", "UiLSiqyDf4Y"));
        list.add(new HowToItem("Google Fiber", "re0VRK6ouwI"));
        list.add(new HowToItem("Autocompleter", "blB_X38YSxQ"));
        list.add(new HowToItem("GMail Motion", "Bu927_ul_X0"));
        list.add(new HowToItem("Translate for Animals", "3I24bSteJpw"));
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
