package com.ethosgames.quickfixins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class SavedRecipesListActivity extends BaseToolbarActivity {
    private RecyclerView recipesRecyclerView;
    private SavedRecipesAdapter recipesAdapter;
    private RecyclerView.LayoutManager recipesLayoutManager;

    private HashSet<Integer> savedRecipeIds;

    private final ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.saved_recipe_list_activity_title);

        savedRecipeIds = FileInteractor.readIntegerSetFromFile(getString(R.string.saved_recipes_file_path), getApplicationContext());

        recipesLayoutManager = new LinearLayoutManager(this);

        // TODO add loading indicator to layout, switch to actual layout on response
        RequestQueue queue = Volley.newRequestQueue(this);
        String baseUrl = String.format("%s%s?",
                getApplicationContext().getString(R.string.backend_base_url),
                getApplicationContext().getString(R.string.backend_get_multiple_by_id_path));

        for (Integer i : savedRecipeIds) {
            baseUrl += "ids[]=" + i.toString() + "&";
        }

        // Remove the last '&' character added above
        if (savedRecipeIds.size() > 0) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        JsonArrayRequest getRecipesRequest = new JsonArrayRequest(baseUrl,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0 ; i < response.length() ; i++) {
                        recipes.add(new Recipe((JSONObject) response.get(i)));
                    }

                    recipesRecyclerView = findViewById(R.id.savedRecipes);
                    recipesRecyclerView.setHasFixedSize(true);
                    recipesRecyclerView.setLayoutManager(recipesLayoutManager);
                    recipesAdapter = new SavedRecipesAdapter(recipes, savedRecipeIds);
                    recipesRecyclerView.setAdapter(recipesAdapter);
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        queue.add(getRecipesRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FileInteractor.writeSetToFile(savedRecipeIds, getString(R.string.saved_recipes_file_path), getApplicationContext());
    }

    @Override
    public void goToRandomActivity() {
        // TODO visually indicate loading before awaiting the request
        final Intent intent = new Intent(this, SingleRecipeActivity.class);

        RequestQueue queue = Volley.newRequestQueue(this);
        String validIDsUrl =
                getApplicationContext().getString(R.string.backend_base_url) +
                        getApplicationContext().getString(R.string.backend_all_valid_ids_path);

        JsonArrayRequest validIDsRequest = new JsonArrayRequest(validIDsUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int randomIndex = (int) Math.floor(Math.random() * response.length());
                        try {
                            int id = ((JSONObject) response.get(randomIndex)).getInt("ID");
                            intent.putExtra(
                                    getApplicationContext().getString(R.string.recipe_id_intent_message),
                                    id);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(validIDsRequest);
    }
}
