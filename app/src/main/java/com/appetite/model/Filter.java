package com.appetite.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.appetite.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Federica on 27/08/2016.
 */
public  class Filter {

    private Map<String, String> filters;
    private Map<String, String> filterGroup;
    private Map<String, ArrayList<String>> activePerGroup;

    private static int FILTER_TYPES = 3;
    private String[] filterTypes;

    private static Filter instance;
    private final String ACTIVATE = "true";
    private final String DEACTIVATE = "false";

    private Filter(Context contex) {
        //Get al filter types and then in the for we are going to populate the dictionary of filters
        filterTypes = contex.getResources().getStringArray(R.array.filter);
        activePerGroup = new HashMap<>();
        filterGroup = new HashMap<String, String>();
        filters = new HashMap<>();
        String[] f;
        //Here Populates the Map of filters with the necessary key
        for(int i = 0; i<FILTER_TYPES; i++) {
            activePerGroup.put(filterTypes[i], null);
            int resourceId= contex.getResources().getIdentifier(filterTypes[i], "array", contex.getPackageName());
            f = contex.getResources().getStringArray(resourceId);
            String value = "false";
            for(int j = 0; j < f.length; j++) {
                filters.put(f[j], value);
                filterGroup.put(f[j], filterTypes[i]);
            }
        }
        Log.e("FilterGroup ", filterGroup.toString());
        Log.e("activePerGroup: ", activePerGroup.toString());
        Log.e("Filters map", filters.toString());
    }

    /**
     * Returns singleton class instance
     * @param context
     * @return singleton class instance
     */
    public static Filter getInstance(Context context) {
        if (instance == null) {
           instance = new Filter(context);
        }
        return instance;
    }

    /**
     * Set to true a specific filter in the dictionary
     * @param filterKey
     */
    public void activateFilter(String filterKey) {
        filters.put(filterKey, ACTIVATE);
    }

    /**
     * Set to false a specific filter in the dictionary
     * @param filterKey
     */
    public void deactivateFilter(String filterKey) {
        filters.put(filterKey, DEACTIVATE);
    }

    /**
     * Return the value of the specified filterKey
     * @param filterKey
     * @return
     */
    public String getFilterStatus(String filterKey) {
        return filters.get(filterKey);
    }

    /**
     * Return the list of active filters
     * @return
     */
    public ArrayList<String> getActivatedFilters() {
        ArrayList<String> activated = new ArrayList<String>();
        Set<String> keys = filters.keySet();
        for(String key : keys) {
            if(filters.get(key).equals(ACTIVATE))
                activated.add(key);
        }
        return activated;
    }

    /**
     * Return the list of inactive filters
     * @return
     */
    public ArrayList<String> getInactiveFilters() {
        ArrayList<String> deactivated = new ArrayList<String>();
        Set<String> keys = filters.keySet();
        for(String key : keys) {
            if(filters.get(key).equals(DEACTIVATE))
                deactivated.add(key);
        }
        return deactivated;
    }

    /**
     * Get the group dictionary
     * @return
     */
    public Map<String, String> getFilterGroup() {
        return filterGroup;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public Map<String, ArrayList<String>> getActivePerGroup() {
        return activePerGroup;
    }

    public void setGroupFilterActivation(String filterKey) {
        String filterCategory = filterGroup.get(filterKey);
        ArrayList<String> dictObject = activePerGroup.get(filterCategory);
        if(dictObject ==  null)
            dictObject = new ArrayList<>();
        dictObject.add(filterKey);
        activePerGroup.put(filterCategory, dictObject);
    }

    public void setGroupFilterDeactivation(String filterKey) {
        String filterCategory = filterGroup.get(filterKey);
        ArrayList<String> dictObject = activePerGroup.get(filterCategory);
        dictObject.remove(filterKey);
        if(dictObject.isEmpty()) {
            activePerGroup.put(filterCategory, null);
        } else {
            activePerGroup.put(filterCategory, dictObject);
        }
    }

    public void resetFilters() {
        for(String filter: filters.keySet()) {
            filters.put(filter, DEACTIVATE);
        }
        for(String key : activePerGroup.keySet()) {
            activePerGroup.put(key, null);
        }
        Log.e("resetFilters: ", filters.toString());
    }
}
