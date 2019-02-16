package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class SingleRecipeActivity extends BaseToolbarActivity {
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
        final int recipeId = getIntent().getIntExtra(
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
                            updateSaveButtonStatus();
                            updateRateButtonStatus();
                            getSupportActionBar().setTitle(recipe.getName());

                            ImageView imageView = findViewById(R.id.recipePrimaryImageView);
                            if (recipe.getImageUrl().equals("null")) {
                                imageView.setImageDrawable(getDrawable(R.drawable.image_not_found));
                            }
                            else {
                                new DownloadImageTask(imageView).execute(recipe.getImageUrl());
                            }

                            stepsRecyclerView = findViewById(R.id.recipeSteps);
                            stepsRecyclerView.setHasFixedSize(true);
                            stepsRecyclerView.setLayoutManager(stepsLayoutManager);
                            stepsAdapter = new StepsAdapter(recipe.getSteps());
                            stepsRecyclerView.setAdapter(stepsAdapter);

                            ingredientsRecyclerView = findViewById(R.id.recipeIngredients);
                            ingredientsRecyclerView.setHasFixedSize(true);
                            ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
                            ingredientsAdapter = new IngredientsAdapter(recipe.getIngredients());
                            ingredientsRecyclerView.setAdapter(ingredientsAdapter);

                            showRecipeLayout();
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
        if (savedRecipes.contains(recipe.getId())) {
            savedRecipes.remove(recipe.getId());
        } else {
            savedRecipes.add(recipe.getId());
        }
        FileInteractor.writeSetToFile(savedRecipes, getString(R.string.saved_recipes_file_path), getApplicationContext());
        updateSaveButtonStatus();
    }

    public void remixRecipe(View view) {
        Intent intent = new Intent(this, CreateNewRecipeActivity.class);
        intent.putExtra(getString(R.string.recipe_name_intent_message), recipe.getName());
        intent.putExtra(getString(R.string.recipe_ingredients_intent_message), recipe.getIngredients());
        intent.putExtra(getString(R.string.recipe_steps_intent_message), recipe.getSteps());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_random_recipe:
                goToRandomActivity(recipe.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SingleRecipeActivity.RATING_DIALOG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int easeRating = data.getIntExtra(getString(R.string.ease_rating_intent_tag), -1);
                int tasteRating = data.getIntExtra(getString(R.string.taste_rating_intent_tag), -1);

                if (easeRating >= 1 && easeRating <= 5 && tasteRating >= 1 && tasteRating <= 5) {
                    try {
                        JSONObject ratingJson = new JSONObject();
                        ratingJson.put("ID", recipe.getId());
                        ratingJson.put("EaseRating", easeRating);
                        ratingJson.put("TasteRating", tasteRating);

                        String rateUrl = String.format("%s%s",
                                getApplicationContext().getString(R.string.backend_base_url),
                                getApplicationContext().getString(R.string.backend_rate_recipe_path));

                        ratedRecipes.add(recipe.getId());
                        updateRateButtonStatus();
                        FileInteractor.writeSetToFile(ratedRecipes, getString(R.string.rated_recipes_file_path), getApplicationContext());

                        RequestQueue queue = Volley.newRequestQueue(this);

                        JsonObjectRequest rateEaseRequest = new JsonObjectRequest(
                                Request.Method.POST, rateUrl, ratingJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {}
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), R.string.recipe_rate_server_failure, Toast.LENGTH_LONG).show();

                                ratedRecipes.remove(recipe.getId());
                                updateRateButtonStatus();
                                FileInteractor.writeSetToFile(ratedRecipes, getString(R.string.rated_recipes_file_path), getApplicationContext());
                            }
                        });
                        queue.add(rateEaseRequest);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateRateButtonStatus() {
        Button openRateDialogButton = findViewById(R.id.rateDialogOpen);

        if (ratedRecipes.contains(recipe.getId())) {
            openRateDialogButton.setEnabled(false);
            openRateDialogButton.setText(R.string.already_rated_recipe_button_label);
        }
        else {
            openRateDialogButton.setEnabled(true);
            openRateDialogButton.setText(R.string.rate_recipe_button_label);
        }
    }

    private void updateSaveButtonStatus() {
        FloatingActionButton saveFab = findViewById(R.id.fabLike);

        if (savedRecipes.contains(recipe.getId())) {
            saveFab.setImageResource(R.drawable.favorite_border);
        } else {
            saveFab.setImageResource(R.drawable.favorite);
        }
    }

    private void showRecipeLayout() {
        findViewById(R.id.loadingRecipeLayout).setVisibility(View.GONE);
        findViewById(R.id.recipeLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.fabLike).setVisibility(View.VISIBLE);
        findViewById(R.id.fabRemix).setVisibility(View.VISIBLE);
    }
}
