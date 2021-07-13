package fr.proline.zero.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.Main;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.modules.PostgreSQL;

class Config {

	private static Logger logger = LoggerFactory.getLogger(Config.class);
	private static Properties properties;

	public static String DATA_STORE_PORT = "data_store_port";

	private static void initialize() {
		if (properties == null) {
			try {
				properties = new Properties();
				File configFile = new File("proline_launcher.config");
				properties.load(new FileInputStream(configFile));
			} catch (Throwable t) {
				logger.error("Error while reading configuration file, using default configuration instead", t);
				InputStream is = Main.class.getClassLoader()
						.getResourceAsStream("fr/proline/zero/proline_launcher.config");
				try {
					properties.load(is);
				} catch (IOException ioe) {
					logger.error("Error reading default configuration file", ioe);
				}
			}
		}
	}

	public static String getDatastoreType() {
		Config.initialize();
		return properties.getProperty("datastore");
	}

	public static int getDefaultTimeout() {
		Config.initialize();
		int timeout;
		try {
			timeout = Integer.parseInt(properties.getProperty("server_default_timeout"));
		} catch (Throwable t) {
			timeout = 60;
			logger.warn("No timeout has been defined or timeout is not readable, using default timeout (" + timeout
					+ " seconds).");
		}
		// multiply timeout by 1000 to have it in milliseconds
		return timeout * 1000;
	}

	public static int getDefaultTimeoutSeconds() {
		Config.initialize();
		int timeout;
		try {
			timeout = Integer.parseInt(properties.getProperty("server_default_timeout"));
		} catch (Throwable t) {
			timeout = 60;
			logger.warn("No timeout has been defined or timeout is not readable, using default timeout (" + timeout
					+ " seconds).");
		}
		return timeout;
	}

	public static long getMaxTmpFolderSize() {
		Config.initialize();
		String value = properties.getProperty("max_tmp_folder_size");
		return (value == null) ? -1 : Long.parseLong(value);
	}

	public static int getDataStorePort() {
		Config.initialize();
		String value = properties.getProperty(DATA_STORE_PORT);
		if (value == null) {
			Popup.warning("The datastore port could not be read, using the default one instead");
			if (getDatastoreType().equalsIgnoreCase(PostgreSQL.NAME)) {
				return SettingsConstant.DEFAULT_POSTGRESQL_PORT;
			} else {
				return SettingsConstant.DEFAULT_H2_PORT;
			}
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJmsPort() {
		Config.initialize();
		String value = properties.getProperty(SettingsConstant.JMS_PORT);
		if (value == null) {
			Popup.warning("The JMS server port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJmsBatchPort() {
		Config.initialize();
		String value = properties.getProperty("jms_server_batch_port");
		if (value == null) {
			Popup.warning("The JMS Batch port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_BATCH_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJnpPort() {
		Config.initialize();
		String value = properties.getProperty("jnp_port");
		if (value == null) {
			Popup.warning("The JNP server port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_JNP_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJnpRmiPort() {
		Config.initialize();
		String value = properties.getProperty("jnp_rmi_port");
		if (value == null) {
			Popup.warning("The JNP RMI port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_JNP_RMI_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getCortexNbParallelizableServiceRunners() {
		Config.initialize();
		return Integer.parseInt(properties.getProperty(ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE));
	}

	public static String getHornetQVersion() {
		Config.initialize();
		return properties.getProperty("hornetq_version");
	}

	public static String getCortexVersion() {
		Config.initialize();
		return properties.getProperty("cortex_version");
	}

	public static String getSeqRepoVersion() {
		Config.initialize();
		return properties.getProperty("seqrepo_version");
	}

	public static File getJavaHomeFile() {
		Config.initialize();
		// if null try to find studio's jre, if still null return current jre
		String javaPath = properties.getProperty("java_home");
		if (javaPath == null) {
			Popup.warning("The JRE path could not be read, trying to find studio's jre");
			javaPath = "ProlineStudio-" + Config.getStudioVersion() + "/jre";
		}
		return new File(javaPath);
	}

	public static String getJavaHome() {
		Config.initialize();
		// if null try to find studio's jre, if still null return current jre
		String javaPath = properties.getProperty("java_home");
		if (javaPath == null) {
			Popup.warning("The JRE path could not be read, trying to find studio's jre");
			javaPath = "ProlineStudio-" + Config.getStudioVersion() + "/jre";
		}
		return new File(javaPath).getAbsolutePath();
	}

	public static String getJavaExePath() {
		Config.initialize();
		return new File(Config.getJavaHome() + "/bin/java").getAbsolutePath();
	}

	public static String getAllocationMode() {
		Config.initialize();
		String allocationMode = properties.getProperty("allocation_mode");
		if (allocationMode == null) {
			Popup.warning("The JRE path could not be read, trying to find studio's jre");
			allocationMode = "auto";
		}
		return allocationMode;
	}

	public static String getTotalMemory() {
		Config.initialize();
		String totalMemory = properties.getProperty("total_max_memory");
		if (totalMemory == null) {
			Popup.warning("The total memory value could not be read : \n set to 6Go");
			totalMemory = "6G";
		}
		return totalMemory;
	}

	public static String getStudioMemory() {
		Config.initialize();
		String studioMemory = properties.getProperty("studio_memory");
		if (studioMemory == null) {
			Popup.warning("The studio memory value could not be read : \n set to 1Go");
			studioMemory = "1G";
		}
		return studioMemory;
	}

	public static String getServerTotalMemory() {
		Config.initialize();
		String serverTotalMemory = properties.getProperty("server_total_memory");
		if (serverTotalMemory == null) {
			Popup.warning("The total server memory value could not be read : \n set to 2Go");
			serverTotalMemory = "5G";
		}
		return serverTotalMemory;
	}

	public static String getSeqRepMemory() {
		Config.initialize();
		String SeqRepMemory = properties.getProperty("seqrep_memory");
		if (SeqRepMemory == null) {
			Popup.warning("The sequence repository memory value could not be read : \n set to 1Go");
			SeqRepMemory = "1G";
		}
		return SeqRepMemory;
	}

	public static String getDatastoreMemory() {
		Config.initialize();
		String DatastoreMemory = properties.getProperty("datastore_memory");
		if (DatastoreMemory == null) {
			Popup.warning("The datastore memory value could not be read : \n set to 1Go");
			DatastoreMemory = "1G";
		}
		return DatastoreMemory;
	}

	public static String getCortexMemory() {
		Config.initialize();
		String CortexMemory = properties.getProperty("proline_cortex_memory");
		if (CortexMemory == null) {
			Popup.warning("The server (cortex) memory value could not be read : \n set to 2Go");
			CortexMemory = "2G";
		}
		return CortexMemory;
	}

	public static String getJMSMemory() {
		Config.initialize();
		String JMSMemory = properties.getProperty("JMS_memory");
		if (JMSMemory == null) {
			Popup.warning("The JMS server memory value could not be read : \n set to 1Go");
			JMSMemory = "1G";
		}
		return JMSMemory;
	}

	public static String getAdminVersion() {
		Config.initialize();
		return properties.getProperty("admin_version");
	}

	public static String getStudioVersion() {
		Config.initialize();
		return properties.getProperty("studio_version");
	}

	private static boolean isBooleanTrue(String booleanValue) {
		if (booleanValue != null
				&& (booleanValue.equals("on") || booleanValue.equals("true") || booleanValue.equals("yes"))) {
			return true;
		}
		return false;
	}

	public static boolean isServerMode() {
		Config.initialize();
		String debug = properties.getProperty("server_mode");
		return Config.isBooleanTrue(debug);
	}

	public static boolean isDebugMode() {
		Config.initialize();
		String debug = properties.getProperty("debug_mode");
		return Config.isBooleanTrue(debug);
	}

	public static boolean isAdjustMemory() {
		Config.initialize();
		String adjustMemo = properties.getProperty("adjust_memory");
		if (adjustMemo == null)
			return true;
		return Config.isBooleanTrue(adjustMemo);
	}

	public static boolean isSeqRepoEnabled() {
		Config.initialize();
		String seqRepoDisabled = properties.getProperty("disable_sequence_repository");
		return !Config.isBooleanTrue(seqRepoDisabled);
	}

	public static boolean getForceUpdate() {
		Config.initialize();
		String databaseUpdate = properties.getProperty("force_datastore_update");
		if (databaseUpdate == null) {
			Popup.warning("The Force update setting could not be read : \n set to on");
			databaseUpdate = "on";
		}
		return Config.isBooleanTrue(databaseUpdate);
	}

	public static Boolean getStudioActive() {
		Config.initialize();
		String StudioActive = properties.getProperty("proline_studio_active");
		if (StudioActive == null) {
			Popup.warning("The Studio active setting could not be read : \n set to on");
			StudioActive = "on";
		}
		return Config.isBooleanTrue(StudioActive);
	}

	public static Boolean getSeqRepActive() {
		Config.initialize();
		String seqRepActive = properties.getProperty("sequence_repository_active");
		if (seqRepActive == null) {
			Popup.warning("The sequence repository active setting could not be read : \n set to on");
			seqRepActive = "on";
		}
		return Config.isBooleanTrue(seqRepActive);
	}

	public static Boolean getHideConfigDialog() {
		Config.initialize();
		String hideConfigDialog = properties.getProperty("hide_config_dialog");
		if (hideConfigDialog == null) {
			Popup.warning("The hide config dialog setting could not be read : \n set to on");
			hideConfigDialog = "on";
		}
		return Config.isBooleanTrue(hideConfigDialog);
	}

}
