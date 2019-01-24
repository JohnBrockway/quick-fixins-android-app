package com.ethosgames.quickfixins;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private String[] steps;

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public StepViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public StepsAdapter(String[] steps) {
        this.steps = steps;
    }

    @Override
    public StepsAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_steps, parent, false);
        StepViewHolder viewHolder = new StepViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.textView.setText(steps[position]);
    }

    @Override
    public int getItemCount() {
        return steps.length;
    }
}
