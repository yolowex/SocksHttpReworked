package com.slipkprojects.sockshttp;

import android.os.Bundle;
import android.content.Intent;
import com.slipkprojects.ultrasshservice.logger.SkStatus;
import android.app.Activity;
import com.slipkprojects.ultrasshservice.tunnel.TunnelManagerHelper;
import com.slipkprojects.ultrasshservice.LaunchVpn;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;


public class SocksHttpMainActivity extends Activity
{
	private static final String TAG = SocksHttpMainActivity.class.getSimpleName();

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity);
	}



	public void onTrigger(View view) {
		Activity activity = this;
		if (SkStatus.isTunnelActive()) {
			Log.d(TAG, "Stopping vpn");

			TunnelManagerHelper.stopSocksHttp(activity);
		}
		else{
			Log.d(TAG, "Starting vpn");

			Intent intent = new Intent(activity, LaunchVpn.class);
			intent.setAction(Intent.ACTION_MAIN);
			activity.startActivity(intent);
		}

	}
}

