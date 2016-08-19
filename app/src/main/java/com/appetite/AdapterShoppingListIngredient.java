package com.appetite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TODO INTERFACCIA: decommenta 1/3
//import com.appetite.FragmentRecipeIngredients.OnListFragmentInteractionListener;
import com.appetite.model.Recipe;
import com.appetite.model.RecipeIngredient;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RecipeIngredient} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterShoppingListIngredient extends RecyclerView.Adapter<AdapterShoppingListIngredient.ViewHolder> {

    private final static String TAG = AdapterShoppingListIngredient.class.getSimpleName();

    private final List<String> mValues;
    private Context context;

    //TODO INTERFACCIA: decommenta 2/3 e cancella il costruttore usato ora
    /*private final OnListFragmentInteractionListener mListener;

    public AdapterRecipeIngredients(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    } */

    public AdapterShoppingListIngredient(Context context, List<String> ingredientsList) {
        this.context = context;
        mValues = ingredientsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglist_item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mNameView.setText(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mNameView = (TextView) view.findViewById(R.id.fragment_shoppinglist_ingredient);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
