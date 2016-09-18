package com.omantere.timio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.omantere.timio.lib.User;
import com.omantere.timio.lib.Permissions;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerUIHandlers();
        syncManager = new SyncManager(this);
        checkLogin();
        syncManager.createSyncAccount();
        checkUsageStatsPermissionsAndSync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
        syncManager.setupPeriodicSync();
    }

    private void registerUIHandlers() {
        Button button = (Button) findViewById(R.id.logout_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User.removeClientToken(context);
                showLoginScreen();
            }
        });

    }

    private void checkUsageStatsPermissionsAndSync() {
        if(Permissions.checkUsageStatsPermissions(context))
            syncManager.setupPeriodicSync();
        else
            Permissions.requestUsageStatsPermissions(context);
    }

    private void checkLogin() {
        if(User.getClientToken(context) == null)
            showLoginScreen();
    }

    private void showLoginScreen() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }
}
