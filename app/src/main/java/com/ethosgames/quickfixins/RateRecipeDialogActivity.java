package com.ethosgames.quickfixins;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RateRecipeDialogActivity extends AppCompatActivity {
    private Button submitButton;

    private int[] easeButtonIds = {
            R.id.easeRating1,
            R.id.easeRating2,
            R.id.easeRating3,
            R.id.easeRating4,
            R.id.easeRating5
    };
    private int[] tasteButtonIds = {
            R.id.tasteRating1,
            R.id.tasteRating2,
            R.id.tasteRating3,
            R.id.tasteRating4,
            R.id.tasteRating5
    };

    private int currentEaseRating = -1;
    private int currentTasteRating = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_recipe_dialog);

        submitButton = findViewById(R.id.submitRatingButton);
    }

    public void easeRatingOptionSelected(View view) {
        int id = view.getId();

        for (int i = 0 ; i < easeButtonIds.length ; i++) {
            ImageButton button = findViewById(easeButtonIds[i]);
            if (easeButtonIds[i] == id) {
                currentEaseRating = i + 1;
                button.setBackground(
                        getDrawable(R.drawable.background_radial_gradient_accent_white));
            }
            else {
                button.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            }
        }

        maybeEnableSubmitButton();
    }

    public void tasteRatingOptionSelected(View view) {
        int id = view.getId();

        for (int i = 0 ; i < tasteButtonIds.length ; i++) {
            ImageButton button = findViewById(tasteButtonIds[i]);
            if (tasteButtonIds[i] == id) {
                currentTasteRating = i + 1;
                button.setBackground(
                        getDrawable(R.drawable.background_radial_gradient_primary_white));
            }
            else {
                button.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            }
        }

        maybeEnableSubmitButton();
    }

    public void submitRating(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.ease_rating_intent_tag), currentEaseRating);
        returnIntent.putExtra(getString(R.string.taste_rating_intent_tag), currentTasteRating);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancelDialog(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void maybeEnableSubmitButton() {
        if (currentEaseRating > 0 && currentTasteRating > 0) {
            submitButton.setEnabled(true);
            submitButton.setTextColor(getColor(R.color.colorAccent));
        }
    }
}
