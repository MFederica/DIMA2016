package com.appetite;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.model.FavoritesHelper;
import com.appetite.model.Recipe;
import com.appetite.model.ShoppingListHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AdapterRecipesList extends RecyclerView.Adapter<AdapterRecipesList.MyViewHolder> {

    private List<Recipe> recipesList;
    private Context context;
    private OnItemClickListener listener;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private FavoritesHelper favoritesHelper;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myView;
        public TextView title;
        public ImageView image;
        public Recipe recipe;
        public ImageView favorite;
        public ImageView difficulty;
        public TextView totalTime;
        public ImageView vegetarian;


        public MyViewHolder(View view) {
            super(view);
            myView = view;
            title = (TextView) view.findViewById(R.id.fragment_recipes_list_title);
            image = (ImageView) view.findViewById(R.id.fragment_recipes_list_image);
            favorite = (ImageView) view.findViewById(R.id.fragment_recipes_list_favorite);
            difficulty = (ImageView) view.findViewById(R.id.fragment_recipes_list_difficulty_image);
            totalTime = (TextView) view.findViewById(R.id.fragment_recipes_list_total_time);
            vegetarian = (ImageView) view.findViewById(R.id.fragment_recipes_list_vegetarian_image);
        }
    }

    public AdapterRecipesList(Context context, List<Recipe> recipesList) {
        this.recipesList = recipesList;
        this.context = context;
        favoritesHelper = FavoritesHelper.getInstance(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipes_list_item, null);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.recipe = recipesList.get(position);
        holder.title.setText(holder.recipe.getName());

        holder.image.setImageBitmap(null);

        if(favoritesHelper.isInFavorites(holder.recipe)) {
            holder.favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.favorite.setImageResource(R.drawable.ic_favorite_border);
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesHelper.favoriteChecked(holder.recipe);
                favoritesHelper.saveFavorites(context);
                //update view
                if(favoritesHelper.isInFavorites(holder.recipe)) {
                    holder.favorite.setImageResource(R.drawable.ic_favorite);
                } else {
                    holder.favorite.setImageResource(R.drawable.ic_favorite_border);
                }
            }
        });

        String imageUri = ActivityMain.PATH_RECIPE + holder.recipe.getImage();
        if (imageUri != null && !holder.recipe.getImage().equals("")) {
            final File image = DiskCacheUtils.findInCache(imageUri, imageLoader.getDiskCache());
            if (image!= null && image.exists()) {
                Picasso.with(context).load(image).fit().centerCrop().into(holder.image);
            } else {
                imageLoader.loadImage(imageUri, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        holder.image.setImageBitmap(null);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        Picasso.with(context).load(s).fit().centerCrop().into(holder.image);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }else {
            holder.image.setImageBitmap(null);
        }
        // difficulty?
        //TextView difficultyView = ((TextView) rootView.findViewById(R.id.fragment_recipe_information_difficulty));
        //String difficulty = "";
        switch(holder.recipe.getDifficulty()) {
            case 1:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_1);
                holder.difficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.difficulty_1));
                break;
            case 2:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_2);
                holder.difficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.difficulty_2));
                break;
            case 3:
                //difficulty = getString(R.string.fragment_recipe_information_difficulty_3);
                holder.difficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.difficulty_3));
                break;
        }
        //difficultyView.setText(difficulty);

        // total time?
        int totalTime = Integer.valueOf(holder.recipe.getPreparationTime()) + Integer.valueOf(holder.recipe.getCookingTime());
        holder.totalTime.setText(String.valueOf(totalTime));

        // vegetarian?
        if(holder.recipe.getVegetarian().compareTo("1") == 0) {
            holder.vegetarian.setVisibility(View.VISIBLE);
        } else {
            holder.vegetarian.setVisibility(View.GONE);
        }




        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                    listener.onItemClick(holder.recipe);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        public void onItemClick(Recipe recipe);
    }

}
