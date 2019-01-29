package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SingleRecipeActivity extends AppCompatActivity {
    private RecyclerView stepsRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView.Adapter stepsAdapter;
    private RecyclerView.Adapter ingredientsAdapter;
    private RecyclerView.LayoutManager stepsLayoutManager;
    private RecyclerView.LayoutManager ingredientsLayoutManager;

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recipe);

        // TODO add loading indicator to layout, switch to actual layout on response
        int recipeId = getIntent().getIntExtra(
                getString(R.string.recipe_id_intent_message), 1);

        stepsLayoutManager = new LinearLayoutManager(this);
        ingredientsLayoutManager= new LinearLayoutManager(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format("%s%s?id=%d",
                getApplicationContext().getString(R.string.backend_base_url),
                getApplicationContext().getString(R.string.backend_get_by_id_path),
                recipeId);

        StringRequest getRecipeRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            recipe = new Recipe(response);
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

    public void saveRecipe(View view) {
        File savedRecipesFile = new File(getFilesDir(), getString(R.string.saved_recipes_file_path));
        String idToAppend = "";
        if (!savedRecipesFile.exists()) {
            try {
                savedRecipesFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (savedRecipesFile.length() != 0) {
            idToAppend += ",";
        }
        
        idToAppend += String.valueOf(recipe.id);
        try {
            FileOutputStream output = openFileOutput(savedRecipesFile.getName(), MODE_APPEND);
            output.write(idToAppend.getBytes());
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
