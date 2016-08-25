package com.appetite;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.model.Recipe;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
//import com.dmfm.appetite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCookingStep.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCookingStep#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCookingStep extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_STEP_NUMBER = "com.appetite.FragmentCookingStep.ARG_STEP_NUMBER";
    private static final String ARG_RECIPE = "com.appetite.FragmentCookingStep.ARG_RECIPE";

    private final String bucket = "http://dima-mobilehub-516910810-category.s3.amazonaws.com/";

    // TODO: Rename and change types of parameters
    private int stepNumber;
    private Recipe recipe;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private OnFragmentInteractionListener mListener;

    public FragmentCookingStep() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stepNumber Parameter 1.
     * @param recipe Parameter 2.
     * @return A new instance of fragment FragmentCookingStep.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCookingStep newInstance(int stepNumber, Recipe recipe) {
        FragmentCookingStep fragment = new FragmentCookingStep();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_NUMBER, stepNumber);
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stepNumber = getArguments().getInt(ARG_STEP_NUMBER);
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cooking_step, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.fragment_cooking_step_text);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_cooking_step_image);
        textView.setText(recipe.getStep().get(stepNumber));


        //loads image
        String[] parts = recipe.getImage().split("\\.");
        String imageUri = ActivityMain.PATH_RECIPE_STEP + parts[0] + "_" + (stepNumber+1) + "." +parts[1];
            final File image = DiskCacheUtils.findInCache(imageUri, imageLoader.getDiskCache());
            if (image!= null && image.exists()) {
                Picasso.with(getContext()).load(image).fit().centerCrop().into(imageView);
            } else {
                imageLoader.loadImage(imageUri, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        Picasso.with(getContext()).load(s).fit().centerCrop().into(imageView);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
 /*       if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        } */
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
