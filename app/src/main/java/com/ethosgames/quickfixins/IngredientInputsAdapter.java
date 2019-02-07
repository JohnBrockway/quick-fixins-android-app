package com.ethosgames.quickfixins;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

public class IngredientInputsAdapter extends RecyclerView.Adapter<IngredientInputsAdapter.IngredientInputViewHolder> {
    private ArrayList<String> ingredients;

    public class IngredientInputViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextInputEditText textInput;
        public ImageButton addButton;

        public IngredientInputViewHolder(View parentView) {
            super(parentView);

            textInput = parentView.findViewById(R.id.nameText);
            addButton = parentView.findViewById(R.id.addButton);

            addButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ingredients.add(getAdapterPosition() + 1, String.valueOf(getAdapterPosition() + 1));
            notifyItemInserted(getAdapterPosition() + 1);
        }
    }

    public IngredientInputsAdapter(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_input, parent, false);
        IngredientInputViewHolder viewHolder = new IngredientInputViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientInputViewHolder holder, int position) {
        holder.textInput.setText(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
