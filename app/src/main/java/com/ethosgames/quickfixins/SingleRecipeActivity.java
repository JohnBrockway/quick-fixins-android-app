package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class SingleRecipeActivity extends AppCompatActivity {
    private RecyclerView stepsRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView.Adapter stepsAdapter;
    private RecyclerView.Adapter ingredientsAdapter;
    private RecyclerView.LayoutManager stepsLayoutManager;
    private RecyclerView.LayoutManager ingredientsLayoutManager;

    private Recipe recipe;
    private HashSet<Integer> savedRecipes;
    private HashSet<Integer> ratedRecipes;

    private final static int RATING_DIALOG_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO add loading indicator to layout, switch to actual layout on response
        int recipeId = getIntent().getIntExtra(
                getString(R.string.recipe_id_intent_message), 1);
        savedRecipes = FileInteractor.readIntegerSetFromFile(getString(R.string.saved_recipes_file_path), getApplicationContext());
        ratedRecipes = FileInteractor.readIntegerSetFromFile(getString(R.string.rated_recipes_file_path), getApplicationContext());

        stepsLayoutManager = new LinearLayoutManager(this);
        ingredientsLayoutManager= new LinearLayoutManager(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format("%s%s?id=%d",
                getApplicationContext().getString(R.string.backend_base_url),
                getApplicationContext().getString(R.string.backend_get_by_id_path),
                recipeId);

        JsonObjectRequest getRecipeRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            recipe = new Recipe(response);
                            if (savedRecipes.contains(recipe.id)) {
                                FloatingActionButton saveFab = findViewById(R.id.fabLike);
                                saveFab.setImageResource(R.drawable.favorite_border);
                            }
                            updateRateButtonStatus();
                            getSupportActionBar().setTitle(recipe.name);

                            stepsRecyclerView = findViewById(R.id.recipeSteps);
                            stepsRecyclerView.setHasFixedSize(true);
                            stepsRecyclerView.setLayoutManager(stepsLayoutManager);
                            stepsAdapter = new StepsAdapter(recipe.steps);
                            stepsRecyclerView.setAdapter(stepsAdapter);

                            ingredientsRecyclerView = findViewById(R.id.recipeIngredients);
                            ingredientsRecyclerView.setHasFixedSize(true);
                            ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
                            ingredientsAdapter = new IngredientsAdapter(recipe.ingredients);
                            ingredientsRecyclerView.setAdapter(ingredientsAdapter);
                        } catch (Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });

        queue.add(getRecipeRequest);
    }

    public void openRatingDialog(View view) {
        startActivityForResult(
                new Intent(this, RateRecipeDialogActivity.class),
                SingleRecipeActivity.RATING_DIALOG_REQUEST_CODE);
    }

    public void toggleSavedStatus(View view) {
        if (savedRecipes.contains(recipe.id)) {
            savedRecipes.remove(recipe.id);

            FloatingActionButton saveFab = findViewById(R.id.fabLike);
            saveFab.setImageResource(R.drawable.favorite);
        } else {
            savedRecipes.add(recipe.id);

            FloatingActionButton saveFab = findViewById(R.id.fabLike);
            saveFab.setImageResource(R.drawable.favorite_border);
        }
        FileInteractor.writeSetToFile(savedRecipes, getString(R.string.saved_recipes_file_path), getApplicationContext());
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SingleRecipeActivity.RATING_DIALOG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int easeRating = data.getIntExtra(getString(R.string.ease_rating_intent_tag), -1);
                int tasteRating = data.getIntExtra(getString(R.string.taste_rating_intent_tag), -1);

                if (easeRating >= 1 && easeRating <= 5) {
                    try {
                        JSONObject easeRatingJson = new JSONObject();
                        easeRatingJson.put("ID", recipe.id);
                        easeRatingJson.put("EaseRating", easeRating);

                        String easeUrl = String.format("%s%s",
                                getApplicationContext().getString(R.string.backend_base_url),
                                getApplicationContext().getString(R.string.backend_rate_recipe_ease_path));

                        sendPostWithJsonObject(easeUrl, easeRatingJson);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (tasteRating >= 1 && tasteRating <= 5) {
                    try {
                        JSONObject tasteRatingJson = new JSONObject();
                        tasteRatingJson.put("ID", recipe.id);
                        tasteRatingJson.put("TasteRating", tasteRating);

                        String tasteUrl = String.format("%s%s",
                                getApplicationContext().getString(R.string.backend_base_url),
                                getApplicationContext().getString(R.string.backend_rate_recipe_taste_path));

                        sendPostWithJsonObject(tasteUrl, tasteRatingJson);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ratedRecipes.add(recipe.id);
                updateRateButtonStatus();
                FileInteractor.writeSetToFile(ratedRecipes, getString(R.string.rated_recipes_file_path), getApplicationContext());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_create_new:
                startActivity(new Intent(this, CreateNewRecipeActivity.class));
                return true;
            case R.id.action_saved_recipes:

                return true;
            case R.id.action_random_recipe:
                goToRandomActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendPostWithJsonObject(String url, JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest rateEaseRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(rateEaseRequest);
    }
    private void updateRateButtonStatus() {
        if (ratedRecipes.contains(recipe.id)) {
            Button openRateDialogButton = findViewById(R.id.rateDialogOpen);
            openRateDialogButton.setEnabled(false);
            openRateDialogButton.setText(R.string.already_rated_recipe_button_label);
        }
    }
}
