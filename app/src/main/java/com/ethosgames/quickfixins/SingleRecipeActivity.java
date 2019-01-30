package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

    private final static int RATING_DIALOG_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recipe);

        // TODO add loading indicator to layout, switch to actual layout on response
        int recipeId = getIntent().getIntExtra(
                getString(R.string.recipe_id_intent_message), 1);
        savedRecipes = readSavedRecipesFromFile();

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

    public void homeButtonPressed(View view) {
        startActivity(new Intent(this, SplashPageActivity.class));
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
        }
        else {
            savedRecipes.add(recipe.id);

            FloatingActionButton saveFab = findViewById(R.id.fabLike);
            saveFab.setImageResource(R.drawable.favorite_border);
        }
        writeSavedRecipesToFile();
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
            }
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

    private HashSet<Integer> readSavedRecipesFromFile() {
        try {
            File savedRecipesFile = new File(getFilesDir(), getString(R.string.saved_recipes_file_path));
            FileInputStream fis = openFileInput(savedRecipesFile.getName());
            InputStreamReader input = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(input);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String savedRecipesString = sb.toString();

            HashSet<Integer> recipeIds = new HashSet<>();
            String[] values = savedRecipesString.split(",");
            for (int i = 0 ; i < values.length ; i++) {
                recipeIds.add(Integer.parseInt(values[i]));
            }
            return recipeIds;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    private void writeSavedRecipesToFile() {
        File savedRecipesFile = new File(getFilesDir(), getString(R.string.saved_recipes_file_path));
        if (!savedRecipesFile.exists()) {
            try {
                savedRecipesFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        Integer[] savedRecipesArray = savedRecipes.toArray(new Integer[savedRecipes.size()]);
        String savedRecipesString = "";

        for (int i = 0 ; i < savedRecipesArray.length ; i++) {
            savedRecipesString += savedRecipesArray[i].toString();
            if (i < savedRecipesArray.length - 1) {
                savedRecipesString += ",";
            }
        }

        try {
            FileOutputStream output = openFileOutput(savedRecipesFile.getName(), MODE_PRIVATE);
            output.write(savedRecipesString.getBytes());
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
