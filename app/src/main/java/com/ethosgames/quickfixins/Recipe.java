package com.ethosgames.quickfixins;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Recipe {
    private int id;
    private String imageUrl;
    private String name;
    private String[] ingredients;
    private String[] steps;
    private double easeRating;
    private int easeRatingCount;
    private double tasteRating;
    private int tasteRatingCount;

    public Recipe(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("ID");
            imageUrl = jsonObject.getString("ImageUrl");
            name = jsonObject.getString("Name");
            steps = convertJsonArrayToStringArray(jsonObject.getJSONArray("Steps"));
            ingredients = convertJsonArrayToStringArray(jsonObject.getJSONArray("Ingredients"));
            easeRating = jsonObject.getDouble("EaseRating");
            easeRatingCount = jsonObject.getInt("EaseRatingCount");
            tasteRating = jsonObject.getDouble("TasteRating");
            tasteRatingCount = jsonObject.getInt("TasteRatingCount");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String[] convertJsonArrayToStringArray(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0 ; i < stringArray.length ; i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getSteps() {
        return steps;
    }

    public double getEaseRating() {
        return easeRating;
    }

    public int getEaseRatingCount() {
        return easeRatingCount;
    }

    public double getTasteRating() {
        return tasteRating;
    }

    public int getTasteRatingCount() {
        return tasteRatingCount;
    }
}
