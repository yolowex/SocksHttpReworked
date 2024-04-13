package com.slipkprojects.ultrasshservice.config;

public interface SettingsConstants
{
	
	// Geral
	public static final String
		AUTO_CLEAR_LOGS_KEY = "autoClearLogs",
		HIDE_LOG_KEY = "hideLog",
		MODE_DEBUG_KEY = "modeDebug",
		MODE_IS_NIGHT_KEY = "modeNight",
		BLOCK_ROOT_KEY = "blockRoot",
		LANGUAGE_KEY = "idioma",
		TETHERING_SUBNET = "tetherSubnet",
		DISABLE_DELAY_KEY = "disableDelaySSH",
		MAXIMUM_THREADS_KEY = "numberMaxThreadSocks",
		
		FILTER_APPS = "filterApps",
		FILTER_BYPASS_MODE = "filterBypassMode",
		FILTER_APPS_LIST = "filterAppsList",
		
		PROXY_IP_KEY = "proxyRemoto",
		PROXY_PORT_KEY = "proxyRemotoPorta",
		CUSTOM_PAYLOAD_KEY = "proxyPayload",
		USE_DEFAULT_PROXY_PAYLOAD = "usarDefaultPayload",
		PROXY_USE_AUTHENTICATION_KEY = "usarProxyAutenticacao"
	;
	
	public static final String
			CONFIG_PROTECT_KEY = "protegerConfig",
			CONFIG_MESSAGE_KEY = "mensagemConfig",
			CONFIG_VALIDITY_KEY = "validadeConfig",
			CONFIG_EXPORT_MESSAGE_KEY = "mensagemConfigExport",
			CONFIG_INPUT_PASSWORD_KEY = "inputPassword"
	;

	// Vpn
	public static final String
			DNS_FORWARD_KEY = "dnsForward",
		DNS_RESOLVER_KEY = "dnsResolver",
		UDP_FORWARD_KEY = "udpForward",
		UDP_RESOLVER_KEY = "udpResolver";

	// SSH
	public static final String
			SSH_SERVER_ADDRESS = "sshServer",
			SSH_SERVER_PORT = "sshPort",
			SSH_USER = "sshUser",
			SSH_PASS = "sshPass",
	KEY_PATH_KEY = "keyPath",
	LOCAL_PORT_KEY = "sshPortaLocal",
	PING_KEY = "pingerSSH";
	
	public static final String
		PAYLOAD_DEFAULT = "CONNECT [host_port] [protocol][crlf][crlf]";

	// Tunnel Type
	public static final String
			TUNNEL_TYPE_KEY = "tunnelType",
			TUNNEL_TYPE_SSH_DIRECT = "sshDirect",
			TUNNEL_TYPE_SSH_PROXY = "sshProxy",
			TUNNEL_TYPE_SSH_SSL_TUNNEL = "sshSslTunnel";

	
	public static final int
		bTUNNEL_TYPE_SSH_DIRECT = 1,
		bTUNNEL_TYPE_SSH_PROXY = 2
	;
}
