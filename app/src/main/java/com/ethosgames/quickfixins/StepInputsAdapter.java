package com.ethosgames.quickfixins;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

public class StepInputsAdapter extends RecyclerView.Adapter<StepInputsAdapter.StepInputViewHolder> {
    private ArrayList<String> steps;

    public class StepInputViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextInputEditText textInput;
        public ImageButton addButton;

        public StepInputViewHolder(View parentView) {
            super(parentView);

            textInput = parentView.findViewById(R.id.nameText);
            addButton = parentView.findViewById(R.id.addButton);

            addButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            steps.add(getAdapterPosition() + 1, "");
            notifyItemInserted(getAdapterPosition() + 1);
        }
    }

    public StepInputsAdapter(ArrayList<String> steps) {
        this.steps = steps;
    }

    @Override
    public StepInputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_input, parent, false);
        StepInputViewHolder viewHolder = new StepInputViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StepInputViewHolder holder, int position) {
        holder.textInput.setText(steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
