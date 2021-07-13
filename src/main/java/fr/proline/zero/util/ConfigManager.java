package fr.proline.zero.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public final class ConfigManager {

	boolean studioBeingActive;

	boolean seqRepBeingActive;

	boolean hideConfigDialog;

	private static ConfigManager instance;

	private MemoryUtils memoryManager;

	private AdvancedAndServerUtils advancedManager;

	private ConfigManager() {
	}

	public void initialize() {
		memoryManager = new MemoryUtils();
		advancedManager = new AdvancedAndServerUtils();
		setStudioActive(Config.getStudioActive());
		setSeqRepActive(Config.getSeqRepActive());
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public MemoryUtils getMemoryManager() {
		return memoryManager;
	}

	public AdvancedAndServerUtils getAdvancedManager() {
		return advancedManager;
	}

	public void setStudioActive(boolean b) {
		studioBeingActive = b;
		memoryManager.setStudioActive(b);
	}

	public boolean isStudioActive() {
		return this.studioBeingActive;
	}

	public void setSeqRepActive(boolean b) {
		seqRepBeingActive = b;
		memoryManager.setSeqRepActive(b);
	}

	public boolean isSeqReppActive() {
		return this.seqRepBeingActive;
	}

	public void setHideConfigDialog(Boolean b) {
		hideConfigDialog = b;
	}

	public boolean getHideConfigDialog() {
		return this.hideConfigDialog;
	}

	// updates config files
	public void updateFileZero() {
		try {
			if (memoryManager.hasBeenChanged()) {
				updateFileMemory();
			}
			// TODO faire la verif des param generaux
			if (true) {
				updateFileGeneral();
			}
			if (advancedManager.hasBeenChanged()) {
				updateFileAdvanced();
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void updateFileMemory() throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.properties().setFileName("proline_launcher.config"));
		Configuration config = builder.getConfiguration();

		config.setProperty("allocation_mode", memoryManager.getAttributionMode().toString());
		config.setProperty("total_max_memory", memoryManager.getServerTotalMemoryString());
		config.setProperty("studio_memory", memoryManager.getStudioMemoryString());
		config.setProperty("server_total_memory", memoryManager.getServerTotalMemoryString());
		config.setProperty("seqrep_memory", memoryManager.getSeqrepMemoryString());
		config.setProperty("datastore_memory", memoryManager.getDatastoreMemoryString());
		config.setProperty("proline_cortex_memory", memoryManager.getProlineServerMemoryString());
		config.setProperty("JMS_memory", memoryManager.getJmsMemoryString());

		// writes in the file
		builder.save();
	}

	// Access to Config values
	public static boolean isDebugMode() {
		return Config.isDebugMode();
	}
	public static String getDatastoreType() {
		return Config.getDatastoreType();
	}
	public static String getStudioVersion() {
		return Config.getStudioVersion();
	}
	public static String getHornetQVersion() {
		return Config.getHornetQVersion();
	}
	public static String getCortexVersion() {
		return Config.getCortexVersion();
	}
	public static String getSeqRepoVersion() {
		return Config.getSeqRepoVersion();
	}
	public static long getMaxTmpFolderSize(){
		return Config.getMaxTmpFolderSize();
	}

	public void updateFileAdvanced() throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.properties().setFileName("proline_launcher.config"));
		Configuration config = builder.getConfiguration();

		config.setProperty("server_default_timeout", String.valueOf(advancedManager.getServerDefaultTimeout()));
		config.setProperty("service_thread_pool_size", String.valueOf(advancedManager.getCortexNbParallelizableServiceRunners()));
		config.setProperty("java_home", advancedManager.getJvmPath());
		config.setProperty("force_datastore_update", advancedManager.getForceDataStoreUpdateString());
		config.setProperty("data_store_port", String.valueOf(advancedManager.getDataStorePort()));
		config.setProperty("jms_server_port", String.valueOf(advancedManager.getJmsServerPort()));
		config.setProperty("jms_server_batch_port", String.valueOf(advancedManager.getJmsBatchServerPort()));
		config.setProperty("jnp_port", String.valueOf(advancedManager.getJnpServerPort()));
		config.setProperty("jnp_rmi_port", String.valueOf(advancedManager.getJnpRmiServerPort()));

		// writes in the file
		builder.save();
	}

	// TODO verifier à partir d'autre chose que le memoryManager
	private void updateFileGeneral() throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.properties().setFileName("proline_launcher.config"));
		Configuration config = builder.getConfiguration();
		if (memoryManager.getStudioActive()) {
			config.setProperty("sequence_repository_active", "on");
		} else {
			config.setProperty("sequence_repository_active", "off");
		}
		if (memoryManager.getSeqRepActive()) {
			config.setProperty("proline_studio_active", "on");
		} else {
			config.setProperty("proline_studio_active", "off");
		}

		// writes in the file
		builder.save();
	}

	public void restoreValues() {
		setStudioActive(Config.getStudioActive());
		setSeqRepActive(Config.getSeqRepActive());
		setHideConfigDialog(Config.getHideConfigDialog());
		memoryManager.restoreValues();
		advancedManager.restoreValues();
		// TODO other utils restore to their originals
	}
}
