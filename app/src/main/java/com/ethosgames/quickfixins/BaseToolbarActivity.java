package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseToolbarActivity extends AppCompatActivity {

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
                startActivity(new Intent(this, SavedRecipesListActivity.class));
                return true;
            case R.id.action_random_recipe:
                goToRandomActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToRandomActivity(final int... idsToAvoid) {
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
                        try {
                            int idToAvoid = -1;
                            if (idsToAvoid.length > 0) {
                                idToAvoid = idsToAvoid[0];
                            }

                            int id = idToAvoid;
                            // If the random id selected is to be avoided, try to find a new one.
                            // Set a limit of retries to ensure the application doesn't hang.
                            for (int i = 0 ; i < 5 && id == idToAvoid ; i++) {
                                int randomIndex = (int) Math.floor(Math.random() * response.length());
                                id = ((JSONObject) response.get(randomIndex)).getInt("ID");
                            }
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
