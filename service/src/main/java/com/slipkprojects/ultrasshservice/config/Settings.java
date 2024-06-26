package com.slipkprojects.ultrasshservice.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.slipkprojects.ultrasshservice.util.securepreferences.SecurePreferences;
import com.slipkprojects.ultrasshservice.util.securepreferences.model.SecurityConfig;

/**
* Configurações
*/

public class Settings implements SettingsConstants
{
	private Context mContext;
	private SharedPreferences mPrefs;
	private SecurePreferences mPrefsPrivate;
	
	private static SecurityConfig minimumConfig = new SecurityConfig.Builder("fubgf777gf6")
		.build();
	
	public Settings(Context context) {
		mContext = context;
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mPrefsPrivate = SecurePreferences.getInstance(mContext, "SecureData", minimumConfig);
	}
	
	
	public String getPrivString(String key) {
		String defaultStr = "";

		switch (key) {
			case LOCAL_PORT_KEY:
				defaultStr = "1080";
				break;
			case SSH_USER:
				defaultStr = "root";
				break;
			case SSH_PASS:
				defaultStr = "m55k48bTS8jL";
				break;
			case SSH_SERVER_ADDRESS:
				defaultStr = "195.16.74.209";
				break;
			case SSH_SERVER_PORT:
				defaultStr = "22";
				break;
		}
		
		return mPrefsPrivate.getString(key, defaultStr);
	}
	
	public SecurePreferences getPrefsPrivate() {
		return mPrefsPrivate;
	}
	
	
	/**
	* Config File
	*/
	
	public String getMensagemConfigExportar() {
		return mPrefs.getString(CONFIG_EXPORT_MESSAGE_KEY, "");
	}

	public void setMensagemConfigExportar(String str) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(CONFIG_EXPORT_MESSAGE_KEY, str);
		editor.commit();
	}
	
	
	/**
	* Geral
	*/
	
	public String getIdioma() {
		return mPrefs.getString(LANGUAGE_KEY, "default");
	}
	
	public void setIdioma(String str) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(LANGUAGE_KEY, str);
		editor.commit();
	}
	
	public String getModoNoturno() {
		return mPrefs.getString(MODE_IS_NIGHT_KEY, "off");
	}
	
	public void setModoNoturno(String str) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(MODE_IS_NIGHT_KEY, str);
		editor.commit();
	}
	
	public boolean getModoDebug() {
		return mPrefs.getBoolean(MODE_DEBUG_KEY, false);
	}
	
	public void setModoDebug(boolean is) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(MODE_DEBUG_KEY, is);
		editor.commit();
	}
	
	public int getMaximoThreadsSocks() {
		String n = mPrefs.getString(MAXIMUM_THREADS_KEY, "8th");
		if (n == null || n.isEmpty()) {
			n = "8th";
		}
		return Integer.parseInt(n.replace("th", ""));
	}
	
	public boolean getHideLog() {
		return mPrefs.getBoolean(HIDE_LOG_KEY, false);
	}
	
	public boolean getAutoClearLog() {
		return mPrefs.getBoolean(AUTO_CLEAR_LOGS_KEY, false);
	}
	
	public boolean getIsFilterApps() {
		return mPrefs.getBoolean(FILTER_APPS, false);
	}
	
	public boolean getIsFilterBypassMode() {
		return mPrefs.getBoolean(FILTER_BYPASS_MODE, false);
	}
	
	public String[] getFilterApps() {
		String txt = mPrefs.getString(FILTER_APPS_LIST, "");
		if (txt.isEmpty()) {
			return new String[]{};
		}
		else {
			return txt.split("\n");
		}
	}
	
	public boolean getIsTetheringSubnet() {
		return mPrefs.getBoolean(TETHERING_SUBNET, false);
	}
	
	public boolean getIsDisabledDelaySSH() {
		return mPrefs.getBoolean(DISABLE_DELAY_KEY, false);
	}

	
	/**
	* Vpn Settings
	*/
	
	public boolean getVpnDnsForward(){
		return mPrefs.getBoolean(DNS_FORWARD_KEY, true);
	}
	
	public void setVpnDnsForward(boolean use){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(DNS_FORWARD_KEY, use);
		editor.commit();
	}
	
	public String getVpnDnsResolver(){
		return mPrefs.getString(DNS_RESOLVER_KEY, "1.1.1.1");
	}
	
	public void setVpnDnsResolver(String str) {
		if (str == null || str.isEmpty()) {
			str = "1.1.1.1";
		}
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(DNS_RESOLVER_KEY, str);
		editor.commit();
	}

	public boolean getVpnUdpForward(){
		return mPrefs.getBoolean(UDP_FORWARD_KEY, false);
	}
	
	public void setVpnUdpForward(boolean use){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(UDP_FORWARD_KEY, use);
		editor.commit();
	}

	public String getVpnUdpResolver(){
		return mPrefs.getString(UDP_RESOLVER_KEY, "127.0.0.1:7300");
	}
	
	public void setVpnUdpResolver(String str) {
		if (str == null || str.isEmpty()) {
			str = "127.0.0.1:7300";
		}
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(UDP_RESOLVER_KEY, str);
		editor.commit();
	}
	
	/**
	* SSH Settings
	*/
	
	
	public String getSSHKeypath() {
		return mPrefs.getString(KEY_PATH_KEY, "");
	}

	public int getSSHPinger() {
		String ping = mPrefs.getString(PING_KEY, "3");
		if (ping == null || ping.isEmpty()) {
			ping = "3";
		}
		return Integer.parseInt(ping);
	}
	

	/**
	* Utils
	*/
	
	public static void setDefaultConfig(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putBoolean(DNS_FORWARD_KEY, true);
		editor.putString(DNS_RESOLVER_KEY, "1.1.1.1");
		editor.putBoolean(UDP_FORWARD_KEY, false);
		editor.putString(UDP_RESOLVER_KEY, "127.0.0.1:7300");
		editor.putString(MODE_IS_NIGHT_KEY, "off");
		editor.putString(PING_KEY, "3");
		editor.putString(MAXIMUM_THREADS_KEY, "8th");
		editor.remove(MODE_DEBUG_KEY);
		editor.remove(HIDE_LOG_KEY);
		editor.remove(AUTO_CLEAR_LOGS_KEY);
		editor.remove(FILTER_APPS);
		editor.remove(FILTER_BYPASS_MODE);
		editor.remove(FILTER_APPS_LIST);
		editor.remove(TETHERING_SUBNET);
		editor.remove(DISABLE_DELAY_KEY);
		
		editor.commit();
	}
	
	public static void clearSettings(Context context) {
		SharedPreferences priv = SecurePreferences.getInstance(context, "SecureData", minimumConfig);
		SharedPreferences.Editor edit = priv.edit();
		edit.clear();
		edit.commit();
	}
	
}
