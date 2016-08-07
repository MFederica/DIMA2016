package com.appetite;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
/**
 * Created by Federica on 06/08/2016.
 */
public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private List<Category> categoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.fragment_category_title);
            image = (ImageView) view.findViewById(R.id.fragment_category_image);

        }
    }


    public AdapterCategory(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.title.setText(category.getTitle());
        holder.image.setImageResource(category.getImage());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
