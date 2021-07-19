package fr.proline.zero.util;

public class SettingsConstant {

	public final static String JMS_PORT = "jms_server_port";
	public static int DEFAULT_POSTGRESQL_PORT = 5433;
	public static int DEFAULT_H2_PORT = 9092;
	public static int DEFAULT_JMS_PORT = 5445;
	public static int DEFAULT_JMS_BATCH_PORT = 5455;
	public static int DEFAULT_JMS_JNP_PORT = 1099;
	public static int DEFAULT_JMS_JNP_RMI_PORT = 1098;
	public final static String DEFAULT_TOTAL_MEMORY = "6G";
	public final static String DEFAULT_STUDIO_MEMORY = "1G";
	public final static String DEFAULT_SERVER_TOTAL_MEMORY = "5G";
	public final static String DEFAULT_SEQREP_MEMORY = "1G";
	public final static String DEFAULT_DATASTORE_MEMORY = "1G";
	public final static String DEFAULT_CORTEX_MEMORY = "2G";
	public final static String DEFAULT_JMS_MEMORY = "1G";

	public static boolean isBooleanTrue(String booleanValue) {
		if (booleanValue != null
				&& (booleanValue.equals("on") || booleanValue.equals("true") || booleanValue.equals("yes"))) {
			return true;
		}
		return false;
	}

	public static String booleanToString(boolean b) {
		if (b) {
			return "on";
		}
		return "off";
	}

}
