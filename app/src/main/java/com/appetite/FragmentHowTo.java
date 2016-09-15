package com.appetite;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appetite.model.HowToItem;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentHowTo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHowTo extends android.support.v4.app.Fragment implements YouTubePlayer.OnFullscreenListener {
    /** The duration of the animation sliding up the video in portrait. */
    private static final int ANIMATION_DURATION_MILLIS = 300;
    /** The padding between the video list and the video in landscape orientation. */
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

    /** The request code when calling startActivityForResult to recover from an API service error. */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static final String API_KEY = "AIzaSyBRUwFqzeE4ROAcNn3iDR4g3DOG_uGfy5o";

    private View videoBox;
    private View closeButton;
    private View rootView;
    private RecyclerView recyclerView;

    private AdapterHowToList adapter;
    private ArrayList<VideoEntry> videoList;

    private boolean isFullscreen;
    private boolean isViewCreated;

    public FragmentHowTo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHowTo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHowTo newInstance(String param1, String param2) {
        FragmentHowTo fragment = new FragmentHowTo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Log.e("Main view", rootView.toString());
        isViewCreated = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.drawer_item_how_to));
        }
        View view = inflater.inflate(R.layout.fragment_how_to_list, container, true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_how_to_recycler_view);

        //adapter = new AdapterHowToList(getContext(), null ,videoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        //recyclerView.setAdapter(adapter);

        //videoBox = rootView.findViewById(R.id.video_box);
        //closeButton = rootView.findViewById(R.id.close_button);

        videoBox.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onFullscreen(boolean b) {

    }

    /**
     * A fragment that shows a static list of videos.
     */
    public static final class VideoListFragment extends android.support.v4.app.ListFragment {

        private static final List<VideoEntry> VIDEO_LIST;
        static {
            List<VideoEntry> list = new ArrayList<VideoEntry>();
            list.add(new VideoEntry("YouTube Collection", "Y_UmWdcTrrc"));
            list.add(new VideoEntry("GMail Tap", "1KhZKNZO8mQ"));
            list.add(new VideoEntry("Chrome Multitask", "UiLSiqyDf4Y"));
            list.add(new VideoEntry("Google Fiber", "re0VRK6ouwI"));
            list.add(new VideoEntry("Autocompleter", "blB_X38YSxQ"));
            list.add(new VideoEntry("GMail Motion", "Bu927_ul_X0"));
            list.add(new VideoEntry("Translate for Animals", "3I24bSteJpw"));
            VIDEO_LIST = Collections.unmodifiableList(list);
        }

        private PageAdapter adapter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            adapter = new PageAdapter(getActivity(), VIDEO_LIST);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            //videoBox = getActivity().findViewById(R.id.video_box);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String videoId = VIDEO_LIST.get(position).videoId;

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

            adapter.releaseLoaders();
        }

    }

    /**
     * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
     * of them only once and keeping track of the loader of each one. When the ListFragment gets
     * destroyed it releases all the loaders.
     */
    private static final class PageAdapter extends BaseAdapter {

        private final List<VideoEntry> entries;
        private final List<View> entryViews;
        private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        private final LayoutInflater inflater;
        private final ThumbnailListener thumbnailListener;

        private boolean labelsVisible;

        public PageAdapter(Context context, List<VideoEntry> entries) {
            this.entries = entries;

            entryViews = new ArrayList<View>();
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            inflater = LayoutInflater.from(context);
            thumbnailListener = new ThumbnailListener();

            labelsVisible = true;
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        public void setLabelVisibility(boolean visible) {
            labelsVisible = visible;
            for (View view : entryViews) {
                view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public VideoEntry getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            VideoEntry entry = entries.get(position);

            // There are three cases here
            if (view == null) {
                // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.

                view = inflater.inflate(R.layout.fragment_how_to_list_item, parent, false);
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                thumbnail.setTag(entry.videoId);
                thumbnail.initialize(API_KEY, thumbnailListener);
            } else {
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    thumbnail.setTag(entry.videoId);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.
                    thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entry.videoId);
                }
            }
            TextView label = ((TextView) view.findViewById(R.id.text));
            label.setText(entry.text);
            label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
            return view;
        }

        private final class ThumbnailListener implements
                YouTubeThumbnailView.OnInitializedListener,
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onInitializationSuccess(
                    YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
                loader.setOnThumbnailLoadedListener(this);
                thumbnailViewToLoaderMap.put(view, loader);
                view.setImageResource(R.drawable.loading_thumbnail);
                String videoId = (String) view.getTag();
                loader.setVideo(videoId);
            }

            @Override
            public void onInitializationFailure(
                    YouTubeThumbnailView view, YouTubeInitializationResult loader) {
                view.setImageResource(R.drawable.no_thumbnail);
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
                view.setImageResource(R.drawable.no_thumbnail);
            }
        }

    }

    private static final class VideoEntry {
        private final String text;
        private final String videoId;

        public VideoEntry(String text, String videoId) {
            this.text = text;
            this.videoId = videoId;
        }
    }

}
