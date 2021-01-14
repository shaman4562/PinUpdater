package com.delta22.pinup.confg;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;

public class SharedPrefHelper {

    private static SharedPreferences sPref;

    public static void init(SharedPreferences sharedPreferences) {
        sPref = sharedPreferences;
    }

    public static void saveUpdateTimeForDomain(@NonNull String domainName, int time) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(domainName, time);
        editor.apply();
    }

    public static int getUpdateTimeForDomain(@NonNull String domainName, int time) {
        return sPref.getInt(domainName, 0);
    }
}