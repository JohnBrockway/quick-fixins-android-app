package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SplashPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_page);
        RequestQueue queue = Volley.newRequestQueue(this);
        String upcheckUrl =
                getApplicationContext().getString(R.string.backend_base_url) +
                getApplicationContext().getString(R.string.backend_upcheck_path);

        StringRequest upcheckRequest = new StringRequest(Request.Method.GET, upcheckUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
        queue.add(upcheckRequest);
    }

    public void goToRandomActivity(View view) {
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
