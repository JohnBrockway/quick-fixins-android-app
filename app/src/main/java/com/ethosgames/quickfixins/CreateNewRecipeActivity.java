package com.ethosgames.quickfixins;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CreateNewRecipeActivity extends AppCompatActivity {
    private RecyclerView stepInputsRecyclerView;
    private RecyclerView ingredientInputsRecyclerView;
    private RecyclerView.Adapter stepInputsAdapter;
    private RecyclerView.Adapter ingredientInputsAdapter;
    private RecyclerView.LayoutManager stepInputsLayoutManager;
    private RecyclerView.LayoutManager ingredientInputsLayoutManager;

    private final ArrayList<String> steps = new ArrayList<>();
    private final ArrayList<String> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_recipe);

        steps.add("");
        ingredients.add("");

        stepInputsLayoutManager = new LinearLayoutManager(this);
        ingredientInputsLayoutManager= new LinearLayoutManager(this);

        stepInputsRecyclerView = findViewById(R.id.stepsList);
        stepInputsRecyclerView.setLayoutManager(stepInputsLayoutManager);
        stepInputsAdapter = new StepInputsAdapter(steps);
        stepInputsRecyclerView.setAdapter(stepInputsAdapter);

        ingredientInputsRecyclerView = findViewById(R.id.ingredientsList);
        ingredientInputsRecyclerView.setLayoutManager(ingredientInputsLayoutManager);
        ingredientInputsAdapter = new IngredientInputsAdapter(ingredients);
        ingredientInputsRecyclerView.setAdapter(ingredientInputsAdapter);
    }
}
