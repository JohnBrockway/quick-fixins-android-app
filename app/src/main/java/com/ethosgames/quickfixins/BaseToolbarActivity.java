package com.ethosgames.quickfixins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

    public abstract void goToRandomActivity();
}
