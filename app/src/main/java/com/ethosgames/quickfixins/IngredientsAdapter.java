package com.ethosgames.quickfixins;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private String[] ingredients;

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public IngredientViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public IngredientsAdapter(String[] ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_ingredients, parent, false);
        IngredientViewHolder viewHolder = new IngredientViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.textView.setText(ingredients[position]);
    }

    @Override
    public int getItemCount() {
        return ingredients.length;
    }
}
