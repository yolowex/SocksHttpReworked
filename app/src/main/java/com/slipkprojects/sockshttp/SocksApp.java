package com.slipkprojects.sockshttp;

import android.app.Application;
import android.content.Intent;
import com.slipkprojects.ultrasshservice.logger.SkStatus;
import android.app.Activity;
import com.slipkprojects.ultrasshservice.tunnel.TunnelManagerHelper;
import com.slipkprojects.ultrasshservice.LaunchVpn;

public class SocksApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startMainActivity();
    }

    // Method to start the main activity
    private void startMainActivity() {
        Intent intent = new Intent(this, SocksHttpMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}