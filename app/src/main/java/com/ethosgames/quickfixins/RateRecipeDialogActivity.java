package com.ethosgames.quickfixins;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class RateRecipeDialogActivity extends AppCompatActivity {
    private int[] easeButtonIds = new int[5];
    private int[] tasteButtonIds = new int[5];

    private int currentEaseRating = -1;
    private int currentTasteRating = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_recipe_dialog);
        easeButtonIds[0] = R.id.easeRating1;
        easeButtonIds[1] = R.id.easeRating2;
        easeButtonIds[2] = R.id.easeRating3;
        easeButtonIds[3] = R.id.easeRating4;
        easeButtonIds[4] = R.id.easeRating5;
        tasteButtonIds[0] = R.id.tasteRating1;
        tasteButtonIds[1] = R.id.tasteRating2;
        tasteButtonIds[2] = R.id.tasteRating3;
        tasteButtonIds[3] = R.id.tasteRating4;
        tasteButtonIds[4] = R.id.tasteRating5;
    }

    public void setRatingOptionSelected(View view) {
        int id = view.getId();

        boolean isEaseButton = false;
        for (int i = 0 ; i < easeButtonIds.length ; i++) {
            if (easeButtonIds[i] == id) {
                isEaseButton = true;
                break;
            }
        }

        int [] ids = isEaseButton ? easeButtonIds : tasteButtonIds;
        for (int i = 0 ; i < ids.length ; i++) {
            ImageButton button = findViewById(ids[i]);
            if (ids[i] == id) {
                if (isEaseButton) {
                    currentEaseRating = i + 1;
                    button.setBackground(
                            getDrawable(R.drawable.background_radial_gradient_accent_white));
                }
                else {
                    currentTasteRating = i + 1;
                    button.setBackground(
                            getDrawable(R.drawable.background_radial_gradient_primary_white));
                }
            }
            else {
                button.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            }
        }
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
}
