package com.omantere.timio;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.omantere.timio.lib.Helpers;

/**
 * Created by omantere on 18/09/16.
 */
public class SyncManager {

    private String ACCOUNT_TYPE;
    private String ACCOUNT_NAME;
    private String SYNC_AUTHORITY;
    private Account syncAccount;
    private Context context;

    public SyncManager(Context context) {
        ACCOUNT_TYPE = context.getString(R.string.sync_account_type);
        ACCOUNT_NAME = context.getString(R.string.sync_account);
        SYNC_AUTHORITY = context.getString(R.string.sync_authority);
        this.context = context;
    }

    public void createSyncAccount() {
        Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        if(!accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.w("SyncManager", "Couldn't add account, might already exist.");
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            if(accounts.length != 1)
                Log.wtf("SyncManager", "Wrong number of accounts exist for this application, should be only one!");
            syncAccount = accounts[0];
        }
        ContentResolver.setSyncAutomatically(newAccount, SYNC_AUTHORITY, true);
        ContentResolver.setMasterSyncAutomatically(true);
        syncAccount = newAccount;
    }

    public void setupPeriodicSync() {
        Log.w("SyncManager", "Periodic sync setup");
        context.getContentResolver();
        ContentResolver.requestSync(syncAccount, SYNC_AUTHORITY, Bundle.EMPTY);
        ContentResolver.addPeriodicSync(syncAccount, SYNC_AUTHORITY, Bundle.EMPTY, 3600);
        Helpers.writeToSharedPreferences(context, "sync_setup", "true");
    }
}
