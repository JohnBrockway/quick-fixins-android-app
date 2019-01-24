package com.ethosgames.quickfixins;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private String[] ingredients;

    public static class IngredientsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public IngredientsViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public IngredientsAdapter(String[] ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_ingredients, parent, false);
        IngredientsViewHolder viewHolder = new IngredientsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        holder.textView.setText(ingredients[position]);
    }

    @Override
    public int getItemCount() {
        return ingredients.length;
    }
}
