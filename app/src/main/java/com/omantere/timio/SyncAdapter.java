package com.omantere.timio;

/**
 * Created by omantere on 06/09/16.
 */

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.usage.UsageStatsManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.omantere.timio.lib.User;
import com.omantere.timio.lib.Permissions;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String clientDataEndpoint;

    UsageStatsManager usageStatsManager;
    PackageManager packageManager;
    Context context;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        packageManager = context.getPackageManager();
        this.context = context;
        clientDataEndpoint = context.getString(R.string.server_api_url) + "/push_client_data";
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
        Context context,
        boolean autoInitialize,
        boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        packageManager = context.getPackageManager();
        this.context = context;
        clientDataEndpoint = context.getString(R.string.server_api_url);
    }

    private ApplicationInfo getApplicationInfo(String packageName) {
        ApplicationInfo info;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            info = null;
        }
        return info;
    }

    private String getEventType(UsageEvents.Event event) {
        switch(event.getEventType()) {
            case UsageEvents.Event.MOVE_TO_BACKGROUND:
                return "stop";
            case UsageEvents.Event.MOVE_TO_FOREGROUND:
                return "start";
            default:
                return null;
        }
    }

    private JSONArray serializeEvents(UsageEvents events) {
        UsageEvents.Event event = new UsageEvents.Event();
        JSONArray eventsJson = new JSONArray();
        while(true) {
            if(!events.getNextEvent(event)) {
                break;
            }

            String eventType;
            if((eventType = getEventType(event)) == null) {
                break;
            }

            ApplicationInfo info = getApplicationInfo(event.getPackageName());
            String appName = (String)packageManager.getApplicationLabel(info);
            JSONObject eventJson = new JSONObject();
            try {
                eventJson.put("packageName", info.packageName);
                eventJson.put("appName", appName);
                eventJson.put("startTime", event.getTimeStamp());
                eventJson.put("eventType", eventType);
                eventsJson.put(eventJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return eventsJson;
    }

    private boolean uploadEvents(JSONArray events) {
        String clientToken = User.getClientToken(context);

        if(clientToken == null) {
            Log.e("SyncAdapter", "Client token not found in shared preferences");
            return false;
        }

        try {
            Webb webb = Webb.create();
            Response response = webb.put(clientDataEndpoint)
                    .header("Access-Token", clientToken)
                    .useCaches(false)
                    .body(events)
                    .asJsonObject();

            if(!response.isSuccess()) {
                Log.e("SyncAdapter", "Request failed.");
                return false;
            }
        } catch (WebbException e) {
            Log.e("SyncAdapter", "Network error");
            return false;
        }

        return true;
    }

    private void showAppNotification(String title, String text) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
    }

    private void showPermissionNotification() {
        showAppNotification("Timio", "Tap to enable app usage permissions for Timio.");
    }

    private void showLoginNotification() {
        showAppNotification("Timio", "Tap to log in to Timio.");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if(User.getClientToken(context) == null) {
            showLoginNotification();
            return;
        }

        if(!Permissions.checkUsageStatsPermissions(context)) {
            showPermissionNotification();
            return;
        }

        Log.w("SyncAdapter", "Performing sync");
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000*60*60*24;

        UsageEvents events = usageStatsManager.queryEvents(startTime, endTime);
        JSONArray eventsJson = serializeEvents(events);

        if(uploadEvents(eventsJson)) {
            Log.w("onPerformSync", "Succesfully uploaded events");
        } else {
            Log.e("onPerformSync", "Failed to upload events!");
        }
    }
}