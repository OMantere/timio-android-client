package com.omantere.timio.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by omantere on 18/09/16.
 */
public class User {
    public static String getClientToken(Context context) {
        final String DEFAULT = "DEFAULT";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String clientToken = sharedPreferences.getString("client_token", DEFAULT);
        return clientToken.equals(DEFAULT) ? null : clientToken;
    }

    public static String getEmail(Context context) {
        final String DEFAULT = "DEFAULT";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String clientToken = sharedPreferences.getString("email", DEFAULT);
        return clientToken.equals(DEFAULT) ? null : clientToken;
    }

    public static void storeClientToken(Context context, String clientToken) {
        Helpers.writeToSharedPreferences(context, "client_token", clientToken);
    }

    public static void storeEmail(Context context, String email) {
        Helpers.writeToSharedPreferences(context, "email", email);
    }

    public static void removeClientToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("client_token");
        editor.commit();
    }
}
