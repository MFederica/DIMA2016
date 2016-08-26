package com.appetite.style.filter;

import android.annotation.SuppressLint;
import android.support.design.BuildConfig;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.appetite.ActivityMain;
import com.appetite.R;

/**
 * Created by Federica on 26/08/2016.
 */
    public class FragmentNavigationManager implements NavigationManager {

        private static FragmentNavigationManager sInstance;

        private FragmentManager mFragmentManager;
        private ActivityMain mActivity;

        public static FragmentNavigationManager obtain(ActivityMain activity) {
            if (sInstance == null) {
                sInstance = new FragmentNavigationManager();
            }
            sInstance.configure(activity);
            return sInstance;
        }

        private void configure(ActivityMain activity) {
            mActivity = activity;
            mFragmentManager = mActivity.getSupportFragmentManager();
        }

        @Override
        public void showCategoryFilter(String title) {
            showFragment(CategoryFilter.newInstance(title), false);
            Toast.makeText(mActivity.getApplicationContext(), "Choosed Categories", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void showDifficultyFilter(String title) {
            Toast.makeText(mActivity.getApplicationContext(), "Choosed Difficulty", Toast.LENGTH_SHORT).show();
            //showFragment(FilterDifficulty.newInstance(title), false);
        }

        @Override
        public void showTimeFilter(String title) {
            Toast.makeText(mActivity.getApplicationContext(), "Choosed Time", Toast.LENGTH_SHORT).show();
            //showFragment(FilterTime.newInstance(title), false);
        }

        @Override
        public void showCountryFilter(String title) {
            Toast.makeText(mActivity.getApplicationContext(), "Choosed Country", Toast.LENGTH_SHORT).show();
            //showFragment(FilterCountry.newInstance(title), false);
        }

        private void showFragment(Fragment fragment, boolean allowStateLoss) {
            FragmentManager fm = mFragmentManager;

            @SuppressLint("CommitTransaction")
            FragmentTransaction ft = fm.beginTransaction()
                    .replace(R.id.container, fragment);

            ft.addToBackStack(null);

            if (allowStateLoss || !BuildConfig.DEBUG) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }

            fm.executePendingTransactions();
        }
    }

