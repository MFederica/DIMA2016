package com.appetite.style.filter;

import android.content.Context;

import com.appetite.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Federica on 26/08/2016.
 */
public class ExpandableListDataSource {

    /**
     * Returns fake data of films
     *
     * @param context
     * @return
     */
    public static Map<String, List<String>> getData(Context context) {
        Map<String, List<String>> expandableListData = new TreeMap<>();

        List<String> filmGenres = Arrays.asList(context.getResources().getStringArray(R.array.filter));

        List<String> difficultyFilter = Arrays.asList(context.getResources().getStringArray(R.array.difficultyFilter));
        List<String> timeFilter = Arrays.asList(context.getResources().getStringArray(R.array.timeFilter));
        List<String> categoryFilter = Arrays.asList(context.getResources().getStringArray(R.array.categoryFilter));
        List<String> countryFilter = Arrays.asList(context.getResources().getStringArray(R.array.countryFilter));

        expandableListData.put(filmGenres.get(0), categoryFilter);
        expandableListData.put(filmGenres.get(1), difficultyFilter);
        expandableListData.put(filmGenres.get(2), timeFilter);
        expandableListData.put(filmGenres.get(3), countryFilter);

        return expandableListData;
    }
}
