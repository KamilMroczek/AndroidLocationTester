package com.kamil.android_location.com.kamil.android_location.notification;

import android.app.Notification;

public interface INotificationBuilder {

    Notification build(String title, String text);
}
