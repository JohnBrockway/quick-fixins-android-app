package com.ethosgames.quickfixins;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class FileInteractor {

    public static HashSet<Integer> readIntegerSetFromFile(String filename, Context context) {
        try {
            File file = new File(context.getFilesDir(), filename);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileInputStream fis = context.openFileInput(file.getName());
            InputStreamReader input = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(input);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String recipeString = sb.toString();

            HashSet<Integer> recipeIds = new HashSet<>();

            if (recipeString.length() != 0) {
                String[] values = recipeString.split(",");
                for (int i = 0 ; i < values.length ; i++) {
                    recipeIds.add(Integer.parseInt(values[i]));
                }
            }

            return recipeIds;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public static void writeSetToFile(HashSet<Integer> recipeSet, String filename, Context context) {
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        Integer[] recipeArray = recipeSet.toArray(new Integer[recipeSet.size()]);
        String recipeString = "";

        for (int i = 0 ; i < recipeArray.length ; i++) {
            recipeString += recipeArray[i].toString();
            if (i < recipeArray.length - 1) {
                recipeString += ",";
            }
        }

        try {
            FileOutputStream output = context.openFileOutput(file.getName(), context.MODE_PRIVATE);
            output.write(recipeString.getBytes());
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
