package com.ethosgames.quickfixins;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleRecipeActivity extends AppCompatActivity {
    private RecyclerView stepsRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView.Adapter stepsAdapter;
    private RecyclerView.Adapter ingredientsAdapter;
    private RecyclerView.LayoutManager stepsLayoutManager;
    private RecyclerView.LayoutManager ingredientsLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recipe);

        // TODO add loading indicator to layout, switch to actual layout on response
        int recipeId = getIntent().getIntExtra(
                getApplicationContext().getString(R.string.recipe_id_intent_message),
                1);

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
                            JSONObject responseJson = new JSONObject(response);
                            Log.d("TEST", responseJson.toString());
                            stepsRecyclerView = findViewById(R.id.recipeSteps);
                            stepsRecyclerView.setHasFixedSize(true);
                            stepsRecyclerView.setLayoutManager(stepsLayoutManager);
                            JSONArray steps = responseJson.getJSONArray("Steps");
                            stepsAdapter = new StepsAdapter(convertJsonArrayToStringArray(steps));
                            stepsRecyclerView.setAdapter(stepsAdapter);

                            ingredientsRecyclerView = findViewById(R.id.recipeIngredients);
                            ingredientsRecyclerView.setHasFixedSize(true);
                            ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
                            JSONArray ingredients = responseJson.getJSONArray("Ingredients");
                            ingredientsAdapter = new IngredientsAdapter(convertJsonArrayToStringArray(ingredients));
                            ingredientsRecyclerView.setAdapter(ingredientsAdapter);
                        } catch (Exception e ) {
                            Log.d("TEST", "exception");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
        queue.add(getRecipeRequest);
    }

    private String[] convertJsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0 ; i < stringArray.length ; i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }
}
