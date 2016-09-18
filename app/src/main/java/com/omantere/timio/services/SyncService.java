package com.omantere.timio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.omantere.timio.SyncAdapter;

/**
 * Created by omantere on 10/09/16.
 */
public class SyncService extends Service {
    private static SyncAdapter mSyncAdapter = null;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("SyncService", "Creating sync adapter instance.");
        if (mSyncAdapter == null) {
            mSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
