package com.appetite;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.model.Category;

import java.util.List;
/**
 * Created by Federica on 06/08/2016.
 */
public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnItemClickListener listener;

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

    public AdapterCategory(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_item, null);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.title.setText(category.getName());
        holder.image.setImageResource(Integer.parseInt(category.getImage()));
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
