package fr.proline.zero.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.Main;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.modules.PostgreSQL;

class Config {

	private static Logger logger = LoggerFactory.getLogger(Config.class);
	private static Properties properties;
	private static Pattern memoryPattern = Pattern.compile("[0-9]+((\\.[0-9]+)?)[GM]");

	private static void initialize() {
		if (properties == null) {
			try {
				properties = new Properties();
				File configFile = new File("proline_launcher.config");
				properties.load(new FileInputStream(configFile));
			} catch (Throwable t) {
				logger.error("Error while reading configuration file, using default configuration instead", t);
				InputStream is = Main.class.getClassLoader().getResourceAsStream("fr/proline/zero/proline_launcher.config");
				if(is==null)
					throw new RuntimeException("Error while reading configuration file. No default file found");

				try {
					properties.load(is);
				} catch (Exception ioe) {
					logger.error("Error reading default configuration file", ioe);
					throw new RuntimeException(ioe);
				}
			}
		}
	}

	public static String getDatastoreType() {
		Config.initialize();
		return properties.getProperty("datastore").trim();
	}

	public static int getDefaultTimeout() {
		Config.initialize();
		int timeout;
		try {
			timeout = Integer.parseInt(properties.getProperty("server_default_timeout").trim());
		} catch (Throwable t) {
			timeout = 60;
			logger.warn("No timeout has been defined or timeout is not readable, using default timeout (" + timeout
					+ " seconds).");
		}
		// multiply timeout by 1000 to have it in milliseconds
		return timeout * 1000;
	}

	public static long getMaxTmpFolderSize() {
		Config.initialize();
		String value = properties.getProperty("max_tmp_folder_size").trim();
		return (value == null) ? -1 : Long.parseLong(value);
	}

	public static int getDataStorePort() {
		Config.initialize();
		String value = properties.getProperty("datastore_port").trim();
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
		String value = properties.getProperty(SettingsConstant.JMS_PORT).trim();
		if (value == null) {
			Popup.warning("The JMS server port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJmsBatchPort() {
		Config.initialize();
		String value = properties.getProperty("jms_server_batch_port").trim();
		if (value == null) {
			Popup.warning("The JMS Batch port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_BATCH_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJnpPort() {
		Config.initialize();
		String value = properties.getProperty("jnp_port").trim();
		if (value == null) {
			Popup.warning("The JNP server port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_JNP_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getJnpRmiPort() {
		Config.initialize();
		String value = properties.getProperty("jnp_rmi_port").trim();
		if (value == null) {
			Popup.warning("The JNP RMI port could not be read, using the default one instead");
			return SettingsConstant.DEFAULT_JMS_JNP_RMI_PORT;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static int getCortexNbParallelizableServiceRunners() {
		Config.initialize();
		return Integer.parseInt(properties.getProperty(ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE).trim());
	}

	public static String getHornetQVersion() {
		Config.initialize();
		return properties.getProperty("hornetq_version").trim();
	}

	public static String getCortexVersion() {
		Config.initialize();
		return properties.getProperty("cortex_version").trim();
	}

	public static String getSeqRepoVersion() {
		Config.initialize();
		return properties.getProperty("seqrepo_version").trim();
	}

	public static String getJavaHome() {
		Config.initialize();
		// if null try to find studio's jdk, if still null return current jdk
		String javaPath = properties.getProperty("java_home").trim();
		if (javaPath == null) {
			Popup.warning("The JDK path could not be read, trying to find studio's jdk");
			javaPath = "ProlineStudio-" + Config.getStudioVersion() + "/jdk";
		}
		return new File(javaPath).getAbsolutePath();
	}

	public static String getAllocationMode() {
		Config.initialize();
		String allocationMode = properties.getProperty("allocation_mode").trim();
		if (allocationMode == null) {
			Popup.warning("The JDK path could not be read, trying to find studio's jdk");
			allocationMode = "auto";
		}
		return allocationMode;
	}

	public static String getTotalMemory() {
		Config.initialize();
		String totalMemory = properties.getProperty("total_max_memory").trim();
		Matcher m = memoryPattern.matcher(totalMemory);
		if (totalMemory == null || (!totalMemory.equals("0") && !m.matches())) {
			Popup.warning("The total memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_TOTAL_MEMORY);
			totalMemory = SettingsConstant.DEFAULT_TOTAL_MEMORY;
		}
		return totalMemory;
	}

	public static String getStudioMemory() {
		Config.initialize();
		String studioMemory = properties.getProperty("studio_memory").trim();
		Matcher m = memoryPattern.matcher(studioMemory);
		if (studioMemory == null || (!studioMemory.equals("0") && !m.matches())) {
			Popup.warning("The studio memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_STUDIO_MEMORY);
			studioMemory = SettingsConstant.DEFAULT_STUDIO_MEMORY;
		}
		return studioMemory;
	}

	public static String getServerTotalMemory() {
		Config.initialize();
		String serverTotalMemory = properties.getProperty("server_total_memory").trim();
		Matcher m = memoryPattern.matcher(serverTotalMemory);
		if (serverTotalMemory == null || (!serverTotalMemory.equals("0") && !m.matches())) {
			Popup.warning("The total server memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_SERVER_TOTAL_MEMORY);
			serverTotalMemory = SettingsConstant.DEFAULT_SERVER_TOTAL_MEMORY;
		}
		return serverTotalMemory;
	}

	public static String getSeqRepMemory() {
		Config.initialize();
		String SeqRepMemory = properties.getProperty("seqrep_memory").trim();
		Matcher m = memoryPattern.matcher(SeqRepMemory);
		if (SeqRepMemory == null || (!SeqRepMemory.equals("0") && !m.matches())) {
			Popup.warning("The sequence repository memory value could not be read \n set to default value : "
					+ SettingsConstant.DEFAULT_SEQREP_MEMORY);
			SeqRepMemory = SettingsConstant.DEFAULT_SEQREP_MEMORY;
		}
		return SeqRepMemory;
	}

	public static String getDatastoreMemory() {
		Config.initialize();
		String DatastoreMemory = properties.getProperty("datastore_memory").trim();
		Matcher m = memoryPattern.matcher(DatastoreMemory);
		if (DatastoreMemory == null || (!DatastoreMemory.equals("0") && !m.matches())) {
			Popup.warning("The datastore memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_DATASTORE_MEMORY);
			DatastoreMemory = SettingsConstant.DEFAULT_DATASTORE_MEMORY;
		}
		return DatastoreMemory;
	}

	public static String getCortexMemory() {
		Config.initialize();
		String CortexMemory = properties.getProperty("proline_cortex_memory").trim();
		Matcher m = memoryPattern.matcher(CortexMemory);
		if (CortexMemory == null || (!CortexMemory.equals("0") && !m.matches())) {
			Popup.warning("The server (cortex) memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_CORTEX_MEMORY);
			CortexMemory = SettingsConstant.DEFAULT_CORTEX_MEMORY;
		}
		return CortexMemory;
	}

	public static String getJMSMemory() {
		Config.initialize();
		String JMSMemory = properties.getProperty("JMS_memory").trim();
		Matcher m = memoryPattern.matcher(JMSMemory);
		if (JMSMemory == null || (!JMSMemory.equals("0") && !m.matches())) {
			Popup.warning("The JMS server memory value could not be read : \n set to default value : "
					+ SettingsConstant.DEFAULT_JMS_MEMORY);
			JMSMemory = SettingsConstant.DEFAULT_JMS_MEMORY;
		}
		return JMSMemory;
	}

	public static String getAdminVersion() {
		Config.initialize();
		return properties.getProperty("admin_version").trim();
	}

	public static String getStudioVersion() {
		Config.initialize();
		return properties.getProperty("studio_version").trim();
	}

	public static boolean isDebugMode() {
		Config.initialize();
		String debug = properties.getProperty("log_debug").trim();
		return SettingsConstant.isBooleanTrue(debug);
	}

	public static boolean getForceUpdate() {
		Config.initialize();
		String databaseUpdate = properties.getProperty("force_datastore_update").trim();
		if (databaseUpdate == null) {
			Popup.warning("The Force update setting could not be read : \n set to on");
			databaseUpdate = "on";
		}
		return SettingsConstant.isBooleanTrue(databaseUpdate);
	}

	public static Boolean getStudioActive() {
		Config.initialize();
		String StudioActive = properties.getProperty("proline_studio_active").trim();
		if (StudioActive == null) {
			Popup.warning("The Studio active setting could not be read : \n set to on");
			StudioActive = "on";
		}
		return SettingsConstant.isBooleanTrue(StudioActive);
	}

	public static Boolean getSeqRepActive() {
		Config.initialize();
		String seqRepActive = properties.getProperty("sequence_repository_active").trim();
		if (seqRepActive == null) {
			Popup.warning("The sequence repository active setting could not be read : \n set to on");
			seqRepActive = "on";
		}
		return SettingsConstant.isBooleanTrue(seqRepActive);
	}

	public static Boolean showConfigDialog() {
		Config.initialize();
		String showConfigDialog = properties.getProperty("show_config_dialog").trim();
		if (showConfigDialog == null) {
			Popup.warning("Show config dialog setting could not be read : \n set to on");
			showConfigDialog = "on";
		}
		return SettingsConstant.isBooleanTrue(showConfigDialog);
	}

}
