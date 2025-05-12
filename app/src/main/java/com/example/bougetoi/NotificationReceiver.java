package com.example.bougetoi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.sendNotification(context, "N'oubliez pas de rentrer votre poids");
    }
}
