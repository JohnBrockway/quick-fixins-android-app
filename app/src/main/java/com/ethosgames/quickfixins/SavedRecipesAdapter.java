package com.ethosgames.quickfixins;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class SavedRecipesAdapter extends RecyclerView.Adapter<SavedRecipesAdapter.RecipeViewHolder> {
    private ArrayList<Recipe> data;
    private HashSet<Integer> savedRecipes;

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView recipeNameTextView;
        public ImageButton unsaveButton;

        public RecipeViewHolder(View parentView) {
            super(parentView);

            recipeNameTextView = parentView.findViewById(R.id.recipeName);
            unsaveButton = parentView.findViewById(R.id.unsaveButton);

            unsaveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = data.get(getAdapterPosition()).id;
            if (savedRecipes.contains(id)) {
                savedRecipes.remove(id);
                unsaveButton.setImageResource(R.drawable.favorite_border);
            }
            else {
                unsaveButton.setImageResource(R.drawable.favorite);
                savedRecipes.add(id);
            }
        }
    }

    public SavedRecipesAdapter(ArrayList<Recipe> data, HashSet<Integer> savedRecipes) {
        this.data = data;
        this.savedRecipes = savedRecipes;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.recipeNameTextView.setText(data.get(position).name);
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipe, parent, false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
