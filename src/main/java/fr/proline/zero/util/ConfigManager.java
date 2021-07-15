package fr.proline.zero.util;

import java.util.Objects;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import fr.proline.zero.gui.Popup;

public final class ConfigManager {

	boolean studioBeingActive;

	boolean seqRepBeingActive;

	boolean showConfigDialog;

	private static ConfigManager instance;

	private MemoryUtils memoryManager;

	private AdvancedAndServerUtils advancedManager;

	private ConfigManager() {
	}

	public void initialize() {
		memoryManager = new MemoryUtils();

		advancedManager = new AdvancedAndServerUtils();

		studioBeingActive = Config.getStudioActive();
		seqRepBeingActive = Config.getSeqRepActive();
		showConfigDialog = Config.showConfigDialog();
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
		memoryManager.setSeqRepoActive(b);
	}

	public boolean isSeqReppActive() {
		return this.seqRepBeingActive;
	}

	public void setShowConfigDialog(Boolean b) {
		showConfigDialog = b;
	}

	public boolean showConfigDialog() {
		return this.showConfigDialog;
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

	public static long getMaxTmpFolderSize() {
		return Config.getMaxTmpFolderSize();
	}

	// updates config files
	public void updateConfigFileZero() {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
					PropertiesConfiguration.class).configure(params.properties().setFileName("proline_launcher.config"));

			if (memoryManager.hasBeenChanged()) {
				updateFileMemory(builder);
			}
			// TODO faire la verif des param generaux
			if (true) {
				updateFileGeneral(builder);
			}
			if (advancedManager.hasBeenChanged()) {
				updateFileAdvanced(builder);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}


	private void updateFileMemory(FileBasedConfigurationBuilder<FileBasedConfiguration> builder) throws ConfigurationException {

		Configuration config =  builder.getConfiguration();

		config.setProperty("allocation_mode", memoryManager.getAttributionMode().toString());
		config.setProperty("total_max_memory", memoryManager.formatMemoryAsString(memoryManager.getServerTotalMemory()));
		config.setProperty("studio_memory", memoryManager.formatMemoryAsString(memoryManager.getStudioMemory()));
		config.setProperty("server_total_memory", memoryManager.formatMemoryAsString(memoryManager.getServerTotalMemory()));
		config.setProperty("seqrep_memory",memoryManager.formatMemoryAsString( memoryManager.getSeqrepMemory()));
		config.setProperty("datastore_memory", memoryManager.formatMemoryAsString(memoryManager.getDatastoreMemory()));
		config.setProperty("proline_cortex_memory", memoryManager.formatMemoryAsString(memoryManager.getProlineServerMemory()));
		config.setProperty("JMS_memory", memoryManager.formatMemoryAsString(memoryManager.getJmsMemory()));

		// writes in the file
		builder.save();
	}

	private void updateFileAdvanced(FileBasedConfigurationBuilder<FileBasedConfiguration> builder) throws ConfigurationException {
		Configuration config = builder.getConfiguration();

		config.setProperty("server_default_timeout", String.valueOf(advancedManager.getServerDefaultTimeout() / 1000));
		config.setProperty("service_thread_pool_size",
				String.valueOf(advancedManager.getCortexNbParallelizableServiceRunners()));
		config.setProperty("java_home", advancedManager.getJvmPath());
		config.setProperty("force_datastore_update", SettingsConstant.booleanToString(advancedManager.getForceDataStoreUpdate()));
		config.setProperty("datastore_port", String.valueOf(advancedManager.getDataStorePort()));
		config.setProperty("jms_server_port", String.valueOf(advancedManager.getJmsServerPort()));
		config.setProperty("jms_server_batch_port", String.valueOf(advancedManager.getJmsBatchServerPort()));
		config.setProperty("jnp_port", String.valueOf(advancedManager.getJnpServerPort()));
		config.setProperty("jnp_rmi_port", String.valueOf(advancedManager.getJnpRmiServerPort()));

		// writes in the file
		builder.save();
	}

	// TODO verifier à partir d'autre chose que le memoryManager
	private void updateFileGeneral(FileBasedConfigurationBuilder<FileBasedConfiguration> builder) throws ConfigurationException {
		Configuration config = builder.getConfiguration();
		config.setProperty("sequence_repository_active", SettingsConstant.booleanToString(memoryManager.isStudioActive()));
		config.setProperty("proline_studio_active", SettingsConstant.booleanToString(memoryManager.isSeqRepoActive()));
		config.setProperty("log_debug", SettingsConstant.booleanToString(isDebugMode()));
		config.setProperty("show_config_dialog", SettingsConstant.booleanToString(showConfigDialog()));

		// writes in the file
		builder.save();
	}

	public void restoreValues() {
		setStudioActive(Config.getStudioActive());
		setSeqRepActive(Config.getSeqRepActive());
		setShowConfigDialog(Config.showConfigDialog());
		memoryManager.restoreValues();
		advancedManager.restoreValues();
		// TODO other utils restore to their originals
	}

	public boolean verif() {

		boolean success = memoryManager.verif();
		success = success && advancedManager.verif();

		if(!success) {
			StringBuilder errorMessage = new StringBuilder();

			if (Objects.nonNull(memoryManager.getErrorMessage())) {
				errorMessage.append(memoryManager.getErrorMessage());
				errorMessage.append("\n");
			}
			if (Objects.nonNull(advancedManager.getErrorMessage())) {
				errorMessage.append(advancedManager.getErrorMessage());
				errorMessage.append("\n");
			}

			if (errorMessage.length() > 0) {
				Popup.warning(errorMessage.toString());
			}
		}
		return success;
	}

	public boolean isErrorFatal() {
		return memoryManager.isErrorFatal() || advancedManager.isErrorFatal();
	}


}
