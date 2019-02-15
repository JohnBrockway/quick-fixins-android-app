package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class SavedRecipesAdapter extends RecyclerView.Adapter<SavedRecipesAdapter.RecipeViewHolder> {
    private ArrayList<Recipe> data;
    private HashSet<Integer> savedRecipes;

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout recipeRow;
        public TextView recipeNameTextView;
        public ImageButton unsaveButton;

        public RecipeViewHolder(View parentView) {
            super(parentView);

            recipeRow = parentView.findViewById(R.id.recipeRow);
            recipeNameTextView = parentView.findViewById(R.id.recipeName);
            unsaveButton = parentView.findViewById(R.id.unsaveButton);

            recipeRow.setOnClickListener(this);
            unsaveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int recipeId = data.get(getAdapterPosition()).getId();
            switch(id) {
                case R.id.recipeRow:
                    Intent intent = new Intent(view.getContext(), SingleRecipeActivity.class);
                    intent.putExtra(
                            view.getContext().getString(R.string.recipe_id_intent_message),
                            recipeId);
                    view.getContext().startActivity(intent);
                    break;
                case R.id.unsaveButton:
                    if (savedRecipes.contains(recipeId)) {
                        savedRecipes.remove(recipeId);
                        unsaveButton.setImageResource(R.drawable.favorite_border);
                    }
                    else {
                        unsaveButton.setImageResource(R.drawable.favorite);
                        savedRecipes.add(recipeId);
                    }
                    break;
            }
        }
    }

    public SavedRecipesAdapter(ArrayList<Recipe> data, HashSet<Integer> savedRecipes) {
        this.data = data;
        this.savedRecipes = savedRecipes;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.recipeNameTextView.setText(data.get(position).getName());
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
