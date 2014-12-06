package com.sense360.android.location.tester.notification;

import android.app.Notification;

public interface INotificationBuilder {

    Notification build(String title, String text);
}
