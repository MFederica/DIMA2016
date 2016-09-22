package com.appetite.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.R;
import com.appetite.model.Category;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
/**
 * Created by Federica on 06/08/2016.
 */
public class AdapterCategoriesList extends RecyclerView.Adapter<AdapterCategoriesList.MyViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnItemClickListener listener;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.fragment_category_title);
            image = (ImageView) view.findViewById(R.id.fragment_category_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                        listener.onItemClick(title.getText().toString());
                }
            });
        }
    }

    public AdapterCategoriesList(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_categories_list_item, null);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.title.setText(category.getName());

        //This code is from the image and the image loader
        //imageLoader.displayImage(category.getImage(), holder.image);
        //holder.image.setImageResource(Integer.parseInt(category.getImage()));

        holder.image.setImageBitmap(null);

        if (category.getImage() != null && !category.getImage().equals("")) {
            final File image = DiskCacheUtils.findInCache(category.getImage(), imageLoader.getDiskCache());
            if (image!= null && image.exists()) {
                Picasso.with(context).load(image).fit().centerCrop().into(holder.image);
            } else {
                imageLoader.loadImage(category.getImage(), new ImageLoadingListener() {
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        public void onItemClick(String textName);
    }

}
