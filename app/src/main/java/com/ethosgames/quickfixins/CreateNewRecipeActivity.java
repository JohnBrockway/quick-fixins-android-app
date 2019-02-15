package com.ethosgames.quickfixins;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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

    private Bitmap selectedImageBitmap = null;

    private final static int IMAGE_SELECTION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_recipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_recipe_toolbar_title);

        stepInputsLayoutManager = new LinearLayoutManager(this);
        ingredientInputsLayoutManager= new LinearLayoutManager(this);

        recipeName = findViewById(R.id.recipeName);
        saveButton = findViewById(R.id.saveRecipe);

        String name = getIntent().getStringExtra(getString(R.string.recipe_name_intent_message));
        String[] defaultIngredients = getIntent().getStringArrayExtra(
                getString(R.string.recipe_ingredients_intent_message));
        String[] defaultSteps = getIntent().getStringArrayExtra(
                getString(R.string.recipe_steps_intent_message));

        recipeName.setText(name);
        populateArrayListWithArray(ingredients, defaultIngredients);
        populateArrayListWithArray(steps, defaultSteps);

        stepInputsRecyclerView = findViewById(R.id.stepsList);
        stepInputsRecyclerView.setLayoutManager(stepInputsLayoutManager);
        stepInputsAdapter = new StepInputsAdapter(steps);
        stepInputsRecyclerView.setAdapter(stepInputsAdapter);

        ingredientInputsRecyclerView = findViewById(R.id.ingredientsList);
        ingredientInputsRecyclerView.setLayoutManager(ingredientInputsLayoutManager);
        ingredientInputsAdapter = new IngredientInputsAdapter(ingredients);
        ingredientInputsRecyclerView.setAdapter(ingredientInputsAdapter);
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

            String encodedImageData = null;
            if (selectedImageBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedImageData = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }

            JSONObject recipeJson = new JSONObject();
            recipeJson.put("ImageData", encodedImageData);
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
                public void onResponse(JSONObject response) {
                    try {
                        int id = response.getInt("ID");
                        HashSet<Integer> savedRecipes = FileInteractor.readIntegerSetFromFile(getString(R.string.saved_recipes_file_path), getApplicationContext());
                        savedRecipes.add(id);
                        FileInteractor.writeSetToFile(savedRecipes, getString(R.string.saved_recipes_file_path), getApplicationContext());
                        startActivityWithId(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), R.string.recipe_insert_server_failure, Toast.LENGTH_LONG).show();
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

    public void startActivityWithId(int id) {
        final Intent intent = new Intent(this, SingleRecipeActivity.class);
        intent.putExtra(
                getApplicationContext().getString(R.string.recipe_id_intent_message),
                id);
        startActivity(intent);
    }

    public void populateArrayListWithArray(ArrayList<String> list, String[] array) {
        if (array == null || array.length == 0) {
            list.add("");
        }
        else {
            for (int i = 0 ; i < array.length ; i++) {
                list.add(array[i]);
            }
        }
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECTION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMAGE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            ImageButton imageButton = findViewById(R.id.addImageButton);
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                double scaleFactor = selectedImageBitmap.getWidth() / 500.0;
                selectedImageBitmap = Bitmap.createScaledBitmap(
                        selectedImageBitmap,
                        (int) (selectedImageBitmap.getWidth() / scaleFactor),
                        (int) (selectedImageBitmap.getHeight() / scaleFactor),
                        false);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), selectedImageBitmap);
                imageButton.setImageDrawable(bitmapDrawable);
                imageButton.setPadding(0, 0, 0, 0);
                imageButton.setBackgroundTintList(null);
                imageButton.setBackground(new ColorDrawable(getResources().getColor(R.color.lightGray)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
