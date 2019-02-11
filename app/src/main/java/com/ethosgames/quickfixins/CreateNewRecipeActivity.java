package com.ethosgames.quickfixins;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateNewRecipeActivity extends BaseToolbarActivity {
    private RecyclerView stepInputsRecyclerView;
    private RecyclerView ingredientInputsRecyclerView;
    private StepInputsAdapter stepInputsAdapter;
    private IngredientInputsAdapter ingredientInputsAdapter;
    private RecyclerView.LayoutManager stepInputsLayoutManager;
    private RecyclerView.LayoutManager ingredientInputsLayoutManager;

    private TextInputEditText recipeName;
    private MaterialButton saveButton;

    private final ArrayList<String> steps = new ArrayList<>();
    private final ArrayList<String> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_recipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_recipe_toolbar_title);

        steps.add("");
        ingredients.add("");

        stepInputsLayoutManager = new LinearLayoutManager(this);
        ingredientInputsLayoutManager= new LinearLayoutManager(this);

        recipeName = findViewById(R.id.recipeName);
        saveButton = findViewById(R.id.saveRecipe);

        stepInputsRecyclerView = findViewById(R.id.stepsList);
        stepInputsRecyclerView.setLayoutManager(stepInputsLayoutManager);
        stepInputsAdapter = new StepInputsAdapter(steps);
        stepInputsRecyclerView.setAdapter(stepInputsAdapter);

        ingredientInputsRecyclerView = findViewById(R.id.ingredientsList);
        ingredientInputsRecyclerView.setLayoutManager(ingredientInputsLayoutManager);
        ingredientInputsAdapter = new IngredientInputsAdapter(ingredients);
        ingredientInputsRecyclerView.setAdapter(ingredientInputsAdapter);
    }

    @Override
    public void goToRandomActivity() {
        // TODO visually indicate loading before awaiting the request
        final Intent intent = new Intent(this, SingleRecipeActivity.class);

        RequestQueue queue = Volley.newRequestQueue(this);
        String randomIndexUrl =
                getApplicationContext().getString(R.string.backend_base_url) +
                        getApplicationContext().getString(R.string.backend_random_id_path);

        StringRequest randomIndexRequest = new StringRequest(Request.Method.GET, randomIndexUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        intent.putExtra(
                                getApplicationContext().getString(R.string.recipe_id_intent_message),
                                Integer.parseInt(response));
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(randomIndexRequest);
    }

    public void saveRecipe(View view) {
        try {
            JSONArray ingredientsArray = new JSONArray();
            int numberOfIngredients = ingredientInputsAdapter.getItemCount();
            for (int i = 0 ; i < numberOfIngredients ; i++) {
                ingredientsArray.put(ingredientInputsAdapter.getDataAt(i));
            }

            JSONArray stepsArray = new JSONArray();
            int numberOfSteps = stepInputsAdapter.getItemCount();
            for (int i = 0 ; i < numberOfSteps ; i++) {
                stepsArray.put(stepInputsAdapter.getDataAt(i));
            }

            JSONObject recipeJson = new JSONObject();
            recipeJson.put("Name", recipeName.getText().toString());
            recipeJson.put("Ingredients", ingredientsArray);
            recipeJson.put("Steps", stepsArray);

            String insertUrl = String.format("%s%s",
                    getApplicationContext().getString(R.string.backend_base_url),
                    getApplicationContext().getString(R.string.backend_insert_recipe_path));

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest insertRecipeRequest = new JsonObjectRequest(
                    Request.Method.POST, insertUrl, recipeJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {}
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    saveButton.setEnabled(true);
                    saveButton.setText(R.string.save_recipe_button_label);
                }
            });

            saveButton.setEnabled(false);
            saveButton.setText(R.string.save_recipe_disabled_button_label);
            queue.add(insertRecipeRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
