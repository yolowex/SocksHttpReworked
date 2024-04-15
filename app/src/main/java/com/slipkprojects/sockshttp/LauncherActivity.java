package com.slipkprojects.sockshttp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author anuragdhunna
 */
public class LauncherActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(this, SocksHttpMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);

        finish();
    }
	
}
