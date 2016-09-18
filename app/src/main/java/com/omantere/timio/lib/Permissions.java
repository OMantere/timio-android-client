package com.omantere.timio.lib;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by omantere on 18/09/16.
 */
public class Permissions {
    public static boolean checkUsageStatsPermissions(final Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static void requestUsageStatsPermissions(final Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("This application requires you to grant access to application usage data on this device." +
                "Please enable the setting for this application.");

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
