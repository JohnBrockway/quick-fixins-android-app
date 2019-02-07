package com.ethosgames.quickfixins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class CreateNewRecipeActivity extends BaseToolbarActivity {
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_recipe_toolbar_title);

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
}
