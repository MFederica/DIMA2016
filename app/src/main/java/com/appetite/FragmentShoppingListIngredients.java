package com.appetite;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;
import com.appetite.style.SimpleDividerItemDecoration;
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
 * {@link FragmentShoppingListIngredients.OnShoppingListIngredientFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentShoppingListIngredients#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShoppingListIngredients extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHOPPING_ITEM = "com.appetite.FragmentShoppingListIngredients.SHOPPING_ITEM";
    private static final String ARG_SHOPPING_POSITION = "com.appetite.FragmentShoppingListIngredients.SHOPPING_POSITION";

    // TODO: Rename and change types of parameters
    private ShoppingItem shoppingItem;
    private int position;

    private OnShoppingListIngredientFragmentInteractionListener mListener;

    private ImageView mRecipeImageView;
    private TextView mRecipeNameView;
    private TextView mRecipeServingsView;
    private RecyclerView mIngredientsListView;
    private Button mRemoveButton;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public FragmentShoppingListIngredients() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param shoppingItem
     * @return A new instance of fragment FragmentShoppingListIngredients.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShoppingListIngredients newInstance(ShoppingItem shoppingItem, int position) {
        FragmentShoppingListIngredients fragment = new FragmentShoppingListIngredients();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SHOPPING_ITEM, shoppingItem);
        args.putInt(ARG_SHOPPING_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shoppingItem = (ShoppingItem) getArguments().getSerializable(ARG_SHOPPING_ITEM);
            position = getArguments().getInt(ARG_SHOPPING_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Change the action bar title
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.drawer_item_shopping_list));
        }

        View rootView = inflater.inflate(R.layout.fragment_shoppinglist_item_recipe, container, false);
        mIngredientsListView = (RecyclerView) rootView.findViewById(R.id.fragment_shoppinglist_ingredientslist);
        mIngredientsListView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mRecipeNameView = (TextView) rootView.findViewById(R.id.fragment_shoppinglist_recipe_name);
        mRecipeServingsView = (TextView) rootView.findViewById(R.id.fragment_shoppinglist_recipe_servings);
        mRemoveButton = (Button) rootView.findViewById(R.id.fragment_shoppinglist_remove);
        mRecipeImageView = (ImageView) rootView.findViewById(R.id.fragment_shoppinglist_item_recipe_image);

        mRecipeNameView.setText(shoppingItem.getRecipe());
        String servings = String.valueOf(shoppingItem.getServings()) + " ";
        if(shoppingItem.getServings() > 1)
            servings += getResources().getString(R.string.fragment_recipeingredients_servings);
        else
            servings += getResources().getString(R.string.fragment_recipeingredients_serving);
        Log.e("TAG", "onCreateView: " + servings );
        mRecipeServingsView.setText(servings);
        mIngredientsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mIngredientsListView.setAdapter(new AdapterShoppingListIngredient(getContext(), shoppingItem));
        mIngredientsListView.setNestedScrollingEnabled(false);
        rootView.findViewById(R.id.fragment_shoppinglist_item_recipe_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnShoppingListIngredientFragmentInteraction(shoppingItem);
                }
            }
        });

        rootView.findViewById(R.id.fragment_shoppinglist_item_recipeAndServings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnShoppingListIngredientFragmentInteraction(shoppingItem);
                }
            }
        });
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShoppingListHelper.getInstance(getContext()).removeRecipe(shoppingItem)) {
                    ShoppingListHelper.saveShoppingList(getContext());

                    mListener.OnShoppingListIngredientFragmentDeletion(shoppingItem, position);
                }
            }
        });

        String imageUri = ActivityMain.PATH_RECIPE + shoppingItem.getImage();
        mRecipeImageView.setImageBitmap(null);

        if (imageUri != null && !shoppingItem.getImage().equals("")) {
            final File image = DiskCacheUtils.findInCache(imageUri, imageLoader.getDiskCache());
            if (image!= null && image.exists()) {
                Picasso.with(getContext()).load(image).fit().centerCrop().into(mRecipeImageView);
            } else {
                imageLoader.loadImage(imageUri, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        mRecipeImageView.setImageBitmap(null);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        Picasso.with(getContext()).load(s).fit().centerCrop().into(mRecipeImageView);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }else {
            mRecipeImageView.setImageBitmap(null);
        }
        // Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShoppingListIngredientFragmentInteractionListener) {
            mListener = (OnShoppingListIngredientFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnShoppingListIngredientFragmentInteractionListener {
        // TODO: Update argument type and name
        void OnShoppingListIngredientFragmentInteraction(ShoppingItem shoppingItem);
        void OnShoppingListIngredientFragmentDeletion(ShoppingItem shoppingItem, int position);
    }
}
