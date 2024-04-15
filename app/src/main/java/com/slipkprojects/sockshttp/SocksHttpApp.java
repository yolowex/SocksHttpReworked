package com.slipkprojects.sockshttp;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import com.slipkprojects.ultrasshservice.util.SkProtect;
import android.content.Context;
import com.slipkprojects.ultrasshservice.SocksHttpCore;
import com.slipkprojects.ultrasshservice.config.Settings;
import android.content.res.Configuration;

/**
* App
*/
public class SocksHttpApp extends Application
{
	private static final String TAG = SocksHttpApp.class.getSimpleName();
	
	private static SocksHttpApp mApp;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mApp = this;
		SocksHttpCore.init(this);
		SkProtect.init(this);
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public static SocksHttpApp getApp() {
		return mApp;
	}
}
