package com.ethosgames.quickfixins;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import java.util.ArrayList;

public class StepInputsAdapter extends RecyclerView.Adapter<StepInputsAdapter.StepInputViewHolder> {
    private ArrayList<String> steps;
    private RecyclerView recyclerView;

    public class StepInputViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextInputEditText textInput;
        public ImageButton addButton;
        public ImageButton removeButton;

        public StepInputViewHolder(View parentView) {
            super(parentView);

            textInput = parentView.findViewById(R.id.nameText);
            addButton = parentView.findViewById(R.id.addButton);
            removeButton = parentView.findViewById(R.id.removeButton);

            textInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    steps.set(getAdapterPosition(), s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {}
            });
            addButton.setOnClickListener(this);
            removeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch(id) {
                case R.id.addButton:
                    final int nextAdapterPosition = getAdapterPosition() + 1;
                    steps.add(nextAdapterPosition, "");
                    notifyItemInserted(nextAdapterPosition);

                    // Set focus on the newly created text input
                    // Need to wrap in wait for the RecyclerView to finish calculating the new layout
                    // or else the call to find the Adapter position (getAdapterPosition() + 1) will
                    // fail as it is not yet created.
                    recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            StepInputsAdapter.StepInputViewHolder viewHolder =
                                    (StepInputsAdapter.StepInputViewHolder) recyclerView.findViewHolderForAdapterPosition(nextAdapterPosition);
                            viewHolder.textInput.requestFocus();
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                    break;
                case R.id.removeButton:
                    if (getItemCount() > 1) {
                        steps.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                    break;
            }
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
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public String getDataAt(int position) {
        return steps.get(position);
    }

}
