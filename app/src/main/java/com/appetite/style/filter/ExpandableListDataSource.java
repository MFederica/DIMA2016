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

        List<String> filters = Arrays.asList(context.getResources().getStringArray(R.array.filter));

        List<String> difficultyFilter = Arrays.asList(context.getResources().getStringArray(R.array.Difficulty));
        List<String> timeFilter = Arrays.asList(context.getResources().getStringArray(R.array.Preparation_Time));
        List<String> countryFilter = Arrays.asList(context.getResources().getStringArray(R.array.Country));

        expandableListData.put(filters.get(0), difficultyFilter);
        expandableListData.put(filters.get(1), timeFilter);
        expandableListData.put(filters.get(2), countryFilter);

        return expandableListData;
    }
}
