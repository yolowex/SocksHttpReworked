package com.slipkprojects.ultrasshservice.config;

import android.content.Context;

import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;

import android.content.pm.PackageInfo;
import java.util.Calendar;

import android.content.pm.PackageManager;
import com.slipkprojects.ultrasshservice.logger.SkStatus;
import com.slipkprojects.ultrasshservice.util.FileUtils;
import java.io.InputStream;
import com.slipkprojects.ultrasshservice.R;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;

//import com.kimchangyoun.rootbeerFresh.RootBeer;
import com.slipkprojects.ultrasshservice.util.securepreferences.crypto.Cryptor;
import com.slipkprojects.ultrasshservice.util.securepreferences.model.SecurityConfig;
import com.slipkprojects.ultrasshservice.util.Cripto;

/**
* @author SlipkHunter
*/
public class ConfigParser
{
	private static final String TAG = ConfigParser.class.getSimpleName();
	public static final String CONVERTED_PROFILE = "converted Profile";
	
	public static final String FILE_EXTENSAO = "sks";
	private static final String KEY_PASSWORD = "cinbdf665$4";
	
	private static final String
		SETTING_VERSION = "file.appVersionCode",
		SETTING_VALIDADE = "file.validade",
		SETTING_PROTEGER = "file.proteger",
		SETTING_AUTOR_MSG = "file.msg";
	
	
	public static boolean convertInputAndSave(InputStream input, Context mContext)
			throws IOException {
		Properties mConfigFile = new Properties();
		
		Settings settings = new Settings(mContext);
		SharedPreferences.Editor prefsEdit = settings.getPrefsPrivate()
			.edit();
		
		try {
			
			InputStream decodedInput = decodeInput(input);
			
			try {
				mConfigFile.loadFromXML(decodedInput);
			} catch(FileNotFoundException e) {
				throw new IOException("File Not Found");
			} catch(IOException e) {
				throw new Exception("Error Unknown", e);
			}

			// versão check
			int versionCode = Integer.parseInt(mConfigFile.getProperty(SETTING_VERSION));

			if (versionCode > getBuildId(mContext)) {
				throw new IOException(mContext.getString(R.string.alert_update_app));
			}

			// validade check
			String msg = mConfigFile.getProperty(SETTING_AUTOR_MSG);
			boolean mIsProteger = mConfigFile.getProperty(SETTING_PROTEGER).equals("1") ? true : false;
			long mValidade = 0;
			
			try {
				mValidade = Long.parseLong(mConfigFile.getProperty(SETTING_VALIDADE));
			} catch(Exception e) {
				throw new IOException(mContext.getString(R.string.alert_update_app));
			}

			if (!mIsProteger || mValidade < 0) {
				mValidade = 0;
			}
			else if (mValidade > 0 && isValidadeExpirou(mValidade)){
				throw new IOException(mContext.getString(R.string.error_settings_expired));
			}
			
			// bloqueia root
			boolean isBloquearRoot = false;
			String _blockRoot = mConfigFile.getProperty("bloquearRoot");
			if (_blockRoot != null) {
				isBloquearRoot = _blockRoot.equals("1") ? true : false;
				if (isBloquearRoot) {
					if (isDeviceRooted(mContext)) {
						throw new IOException(mContext.getString(R.string.error_root_detected));
					}
				} 
			}
			

			try {
				String mServidor = mConfigFile.getProperty(Settings.SSH_SERVER_ADDRESS);
				String mServidorPorta = mConfigFile.getProperty(Settings.SSH_SERVER_PORT);
				String mUsuario = mConfigFile.getProperty(Settings.SSH_USER);
				String mSenha = mConfigFile.getProperty(Settings.SSH_PASS);
				int mPortaLocal = Integer.parseInt(mConfigFile.getProperty(Settings.LOCAL_PORT_KEY));
				int mTunnelType = Settings.bTUNNEL_TYPE_SSH_DIRECT;
				
				String _tunnelType = mConfigFile.getProperty(Settings.TUNNEL_TYPE_KEY);
				if (!_tunnelType.isEmpty()) {
					/**
					* Mantêm compatibilidade
					*/
					if (_tunnelType.equals(Settings.TUNNEL_TYPE_SSH_PROXY)) {
						mTunnelType = Settings.bTUNNEL_TYPE_SSH_PROXY;
					}
					else if (!_tunnelType.equals(Settings.TUNNEL_TYPE_SSH_DIRECT)) {
						mTunnelType = Integer.parseInt(_tunnelType);
					}
				}
				
				if (mServidor == null) {
					throw new Exception();
				}

				String _proxyIp = mConfigFile.getProperty(Settings.PROXY_IP_KEY);
				String _proxyPort = mConfigFile.getProperty(Settings.PROXY_PORT_KEY);
				prefsEdit.putString(Settings.PROXY_IP_KEY, _proxyIp != null ? _proxyIp : "");
				prefsEdit.putString(Settings.PROXY_PORT_KEY, _proxyPort != null ? _proxyPort : "");

				prefsEdit.putBoolean(Settings.USE_DEFAULT_PROXY_PAYLOAD, !mConfigFile.getProperty(Settings.USE_DEFAULT_PROXY_PAYLOAD).equals("1") ? false : true);
				
				String _customPayload = mConfigFile.getProperty(Settings.CUSTOM_PAYLOAD_KEY);
				prefsEdit.putString(Settings.CUSTOM_PAYLOAD_KEY, _customPayload != null ? _customPayload : "");
				
				if (mIsProteger) {
					prefsEdit.putString(Settings.CONFIG_MESSAGE_KEY, msg != null ? msg : "");
					
					new Settings(mContext)
						.setModoDebug(false);

					String pedirLogin = mConfigFile.getProperty("file.pedirLogin");
					if (pedirLogin != null)
						prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, pedirLogin.equals("1") ? true : false);
					else
						prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false);
				}
				else {
					prefsEdit.putString(Settings.CONFIG_MESSAGE_KEY, "");
					prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false);
				}
				
				prefsEdit.putString(Settings.SSH_SERVER_ADDRESS, mServidor);
				prefsEdit.putString(Settings.SSH_SERVER_PORT, mServidorPorta);
				prefsEdit.putString(Settings.SSH_USER, mUsuario);
				prefsEdit.putString(Settings.SSH_PASS, mSenha);
				prefsEdit.putString(Settings.LOCAL_PORT_KEY, Integer.toString(mPortaLocal));

				prefsEdit.putInt(Settings.TUNNEL_TYPE_KEY, mTunnelType);
				prefsEdit.putBoolean(Settings.CONFIG_PROTECT_KEY, mIsProteger);
				prefsEdit.putLong(Settings.CONFIG_VALIDITY_KEY, mValidade);
				prefsEdit.putBoolean(Settings.BLOCK_ROOT_KEY, isBloquearRoot);
				
				String _isDnsForward = mConfigFile.getProperty(Settings.DNS_FORWARD_KEY);
				boolean isDnsForward = _isDnsForward != null && _isDnsForward.equals("0") ? false : true;
				String dnsResolver = mConfigFile.getProperty(Settings.DNS_RESOLVER_KEY);
				settings.setVpnDnsForward(isDnsForward);
				settings.setVpnDnsResolver(dnsResolver);
				
				String _isUdpForward = mConfigFile.getProperty(Settings.UDP_FORWARD_KEY);
				boolean isUdpForward = _isUdpForward != null && _isUdpForward.equals("1") ? true : false;
				String udpResolver = mConfigFile.getProperty(Settings.UDP_RESOLVER_KEY);
				settings.setVpnUdpForward(isUdpForward);
				settings.setVpnUdpResolver(udpResolver);
				
			} catch(Exception e) {
				if (settings.getModoDebug()) {
					SkStatus.logException("Error Settings", e);
				}
				throw new IOException(mContext.getString(R.string.error_file_settings_invalid));
			}
			
			return prefsEdit.commit();
		
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw new IOException(mContext.getString(R.string.error_file_invalid), e);
		} catch (Throwable e) {
			throw new IOException(mContext.getString(R.string.error_file_invalid));
		}
	}
	
	public static void convertDataToFile(OutputStream fileOut, Context mContext,
			boolean mIsProteger, boolean mPedirSenha, boolean isBloquearRoot, String mMensagem, long mValidade)
				throws IOException {
		
		Properties mConfigFile = new Properties();
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		
		Settings settings = new Settings(mContext);
		SharedPreferences prefs = settings.getPrefsPrivate();
		
		try {
			int targerId = getBuildId(mContext);
			// para versões betas
			targerId = 24;
			
			mConfigFile.setProperty(SETTING_VERSION, Integer.toString(targerId));

			mConfigFile.setProperty(SETTING_AUTOR_MSG, mMensagem);
			mConfigFile.setProperty(SETTING_PROTEGER, mIsProteger ? "1" : "0");
			mConfigFile.setProperty("bloquearRoot", isBloquearRoot ? "1" : "0");
						
			mConfigFile.setProperty(SETTING_VALIDADE, Long.toString(mValidade));
			mConfigFile.setProperty("file.pedirLogin", mPedirSenha ? "1" : "0");

			String server = prefs.getString(Settings.SSH_SERVER_ADDRESS, "");
			String server_port = prefs.getString(Settings.SSH_SERVER_PORT, "");
			
			if (mIsProteger && (server.isEmpty() || server_port.isEmpty())) {
				throw new Exception();
			}
						
			mConfigFile.setProperty(Settings.SSH_SERVER_ADDRESS, server);
			mConfigFile.setProperty(Settings.SSH_SERVER_PORT, server_port);
			mConfigFile.setProperty(Settings.SSH_USER, prefs.getString(Settings.SSH_USER, ""));
			mConfigFile.setProperty(Settings.SSH_PASS, prefs.getString(Settings.SSH_PASS, ""));
			mConfigFile.setProperty(Settings.LOCAL_PORT_KEY, prefs.getString(Settings.LOCAL_PORT_KEY, "1080"));

			mConfigFile.setProperty(Settings.TUNNEL_TYPE_KEY, Integer.toString(prefs.getInt(Settings.TUNNEL_TYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT)));
			
			mConfigFile.setProperty(Settings.DNS_FORWARD_KEY, settings.getVpnDnsForward() ? "1" : "0");
			mConfigFile.setProperty(Settings.DNS_RESOLVER_KEY, settings.getVpnDnsResolver());
			
			mConfigFile.setProperty(Settings.UDP_FORWARD_KEY, settings.getVpnUdpForward() ? "1" : "0");
			mConfigFile.setProperty(Settings.UDP_RESOLVER_KEY, settings.getVpnUdpResolver());
			
			mConfigFile.setProperty(Settings.PROXY_IP_KEY, prefs.getString(Settings.PROXY_IP_KEY, ""));
			mConfigFile.setProperty(Settings.PROXY_PORT_KEY, prefs.getString(Settings.PROXY_PORT_KEY, ""));

			String isDefaultPayload = prefs.getBoolean(Settings.USE_DEFAULT_PROXY_PAYLOAD, true) ? "1" : "0";
			String customPayload = prefs.getString(Settings.CUSTOM_PAYLOAD_KEY, "");
						
			if (mIsProteger && isDefaultPayload.equals("0") && customPayload.isEmpty()) {
				throw new IOException();
			}
			
			mConfigFile.setProperty(Settings.USE_DEFAULT_PROXY_PAYLOAD, isDefaultPayload);
			mConfigFile.setProperty(Settings.CUSTOM_PAYLOAD_KEY, customPayload);

		} catch(Exception e) {
			throw new IOException(mContext.getString(R.string.error_file_settings_invalid));
		}

		try {
			mConfigFile.storeToXML(tempOut,
				"Arquivo de Configuração");
		} catch(FileNotFoundException e) {
			throw new IOException("File Not Found");
		} catch(IOException e) {
			throw new IOException("Error Unknown", e);
		}
		
		try {
			InputStream input_encoded = encodeInput(
				new ByteArrayInputStream(tempOut.toByteArray()));
			
			FileUtils.copiarArquivo(input_encoded, fileOut);
		} catch(Throwable e) {
			throw new IOException(mContext.getString(R.string.error_save_settings));
		}
	}
	
	
	/**
	* Criptografia
	*/
	
	private static Cryptor mCrypto;
	
	static {
		mCrypto = Cryptor.initWithSecurityConfig(
			new SecurityConfig.Builder("fubvx788b46v").build());
	}
	
	private static InputStream encodeInput(InputStream in) throws Throwable {
		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		String strBase64 = mCrypto.encryptToBase64(getBytesArrayInputStream(in)
			.toByteArray());
		
		//Cripto.encrypt(KEY_PASSWORD, in, out);
		
		return new ByteArrayInputStream(strBase64.getBytes());
	}
	
	private static InputStream decodeInput(InputStream in) throws Throwable {
		byte[] byteDecript;
		
		ByteArrayOutputStream byteArrayOut = getBytesArrayInputStream(in);
		
		try {
			byteDecript = mCrypto.decryptFromBase64(byteArrayOut.toString());
		} catch (IllegalArgumentException e) {
			// decodifica confg antigas
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Cripto.decrypt(KEY_PASSWORD, new ByteArrayInputStream(byteArrayOut.toByteArray()), out);
			byteDecript = out.toByteArray();
		}
		
		return new ByteArrayInputStream(byteDecript);
	}
	
	public static ByteArrayOutputStream getBytesArrayInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		
		return buffer;
	}

	
	/**
	* Utils
	*/
	
	public static boolean isValidadeExpirou(long validadeDateMillis) {
		if (validadeDateMillis == 0) {
			return false;
		}
		
		// Get Current Date
		long date_atual = Calendar.getInstance()
			.getTime().getTime();
		
		if (date_atual >= validadeDateMillis) {
			return true;
		}
		
		return false;
	}
	
	public static int getBuildId(Context context) throws IOException {
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pinfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new IOException("Build ID not found");
		}
	}
	
	public static boolean isDeviceRooted(Context context) {
        /*for (String pathDir : System.getenv("PATH").split(":")){
			if (new File(pathDir, "su").exists()) {
				return true;
			}
		}
		
		Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }*/
		return false;
//		RootBeer rootBeer = new RootBeer(context);
//
//		boolean simpleTests = rootBeer.detectRootManagementApps() || rootBeer.detectPotentiallyDangerousApps() || rootBeer.checkForBinary("su")
//			|| rootBeer.checkForDangerousProps() || rootBeer.checkForRWPaths()
//			|| rootBeer.detectTestKeys() || rootBeer.checkSuExists() || rootBeer.checkForRootNative() || rootBeer.checkForMagiskBinary();
//		//boolean experiementalTests = rootBeer.checkForMagiskNative();
//
//		return simpleTests;
	}

}
