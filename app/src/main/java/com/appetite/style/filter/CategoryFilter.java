package com.appetite.style.filter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.R;

/**
 * Created by Federica on 26/08/2016.
 */
public class CategoryFilter extends Fragment {
/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
    private static final String KEY_FILTER = "key_filter";

    public CategoryFilter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment FragmentAction.
     */
    public static CategoryFilter newInstance(String filterTitle) {
        CategoryFilter categoryFilter = new CategoryFilter();
        Bundle args = new Bundle();
        args.putString(KEY_FILTER, filterTitle);
        categoryFilter.setArguments(args);
        return categoryFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.filter_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_book, getActivity().getApplicationContext().getTheme());
        if (icon != null) {
            icon.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        ((ImageView) view.findViewById(R.id.filter_icon)).setImageDrawable(icon);

        String movieTitle = getArguments().getString(KEY_FILTER);
        ((TextView) view.findViewById(R.id.filter_title)).setText(movieTitle);
    }
}
