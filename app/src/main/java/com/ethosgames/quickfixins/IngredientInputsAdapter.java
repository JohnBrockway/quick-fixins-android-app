package com.ethosgames.quickfixins;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class IngredientInputsAdapter extends RepeatedTextInputsAdapter {

    public IngredientInputsAdapter(ArrayList<String> ingredients) {
        super(ingredients);
    }

    @Override
    public InputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_input, parent, false);
        InputViewHolder viewHolder = new InputViewHolder(view);
        return viewHolder;
    }
}
