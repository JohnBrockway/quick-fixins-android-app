package com.ethosgames.quickfixins;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Recipe {
    public int id;
    public String name;
    public String[] ingredients;
    public String[] steps;
    public double easeRating;
    public int easeRatingCount;
    public double tasteRating;
    public int tasteRatingCount;

    public Recipe(String jsonRepresentation) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRepresentation);
            id = jsonObject.getInt("ID");
            name = jsonObject.getString("Name");
            JSONArray stepsJson = jsonObject.getJSONArray("Steps");
            steps = convertJsonArrayToStringArray(stepsJson);
            JSONArray ingredientsJson = jsonObject.getJSONArray("Ingredients");
            ingredients = convertJsonArrayToStringArray(ingredientsJson);
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
}
