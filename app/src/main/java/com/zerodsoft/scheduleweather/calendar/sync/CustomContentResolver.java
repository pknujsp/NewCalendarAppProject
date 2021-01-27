package com.zerodsoft.scheduleweather.calendar.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;

public class CustomContentResolver extends ContentResolver
{
    public CustomContentResolver(Context context)
    {
        super(context);
    }


}
