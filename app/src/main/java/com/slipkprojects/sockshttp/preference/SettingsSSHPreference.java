package com.slipkprojects.sockshttp.preference;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.EditTextPreference;
import android.content.SharedPreferences;
import android.content.Intent;

import com.slipkprojects.sockshttp.R;
import com.slipkprojects.ultrasshservice.logger.SkStatus;
import com.slipkprojects.ultrasshservice.config.SettingsConstants;
import com.slipkprojects.ultrasshservice.config.Settings;
import com.slipkprojects.ultrasshservice.logger.ConnectionStatus;
import android.os.Handler;
import android.util.Log;

public class SettingsSSHPreference extends PreferenceFragmentCompat
	implements SettingsConstants, SkStatus.StateListener
{
	private static final String TAG = SettingsSSHPreference.class.getSimpleName();
	
	private Handler mHandler;
	private Settings mConfig;
	private SharedPreferences mSecurePrefs;
	private SharedPreferences mInsecurePrefs;
	
	protected String[] listEdit_keysProteger = {
			SSH_SERVER_ADDRESS,
			SSH_SERVER_PORT,
            SSH_USER,
			SSH_PASS
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler();
		mConfig = new Settings(getContext());
	
		mInsecurePrefs = getPreferenceManager()
			.getDefaultSharedPreferences(getContext());
		mSecurePrefs = mConfig.getPrefsPrivate();
	}

	@Override
    public void onCreatePreferences(Bundle bundle, String s)
	{
        // Load the Preferences from the XML file
        setPreferencesFromResource(R.xml.sshtunnel_preferences, s);
		
		// update views
		getPreferenceScreen().setEnabled(!SkStatus.isTunnelActive());
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		SkStatus.addStateListener(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		SkStatus.removeStateListener(this);
	}

	@Override
	public void updateState(String state, String logMessage, int localizedResId, ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				getPreferenceScreen().setEnabled(!SkStatus.isTunnelActive());
			}
		});
	}

	@Override
    public void onStart() {
        super.onStart();
		
		for (String key : listEdit_keysProteger) {
			if (mSecurePrefs.contains(key)) {
				((EditTextPreference)findPreference(key))
					.setText(mSecurePrefs.getString(key, null));
			}
			
			if (mSecurePrefs.getBoolean(Settings.CONFIG_PROTECT_KEY, false)) {
				if ((key.equals(SSH_USER) || key.equals(SSH_PASS)) &&
					mSecurePrefs.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
					continue;
				}
				
				Preference pref = findPreference(key);
				
				pref.setEnabled(false);
				pref.setSummary(R.string.blocked);
			}
		}
		
		String key = Settings.LOCAL_PORT_KEY;
		if (mSecurePrefs.contains(key)) {
			((EditTextPreference)findPreference(key))
				.setText(mSecurePrefs.getString(key, null));
		}
    }
	
	@Override
    public void onStop() {
        super.onStop();

        //because the standard PreferenceActivity deals with unencrpyted prefs, we get them and replace with encrypted version when the activity is stopped
        final SharedPreferences.Editor insecureEditor = mInsecurePrefs.edit();
        final SharedPreferences.Editor secureEditor = mSecurePrefs.edit();
        
		for (String key : listEdit_keysProteger) {
			if (mInsecurePrefs.contains(key)) {
				Log.d(TAG, "match found for " + key + " adding encrypted copy to secure prefs");
				//add the enc versions to the secure prefs
				secureEditor.putString(key, mInsecurePrefs.getString(key, null));
				//remove entry from the default/insecure prefs
				insecureEditor.remove(key);
			}
		}
		
        String key = Settings.LOCAL_PORT_KEY;
        if (mInsecurePrefs.contains(key)) {
            Log.d(TAG, "match found for " + key + " adding encrypted copy to secure prefs");
            secureEditor.putString(key, mInsecurePrefs.getString(key, null));
            insecureEditor.remove(key);
        }
        
		insecureEditor.commit();
        secureEditor.commit();
    }
}
