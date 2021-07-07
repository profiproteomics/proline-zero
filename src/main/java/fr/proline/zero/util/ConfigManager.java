package fr.proline.zero.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public final class ConfigManager {

	private static ConfigManager instance;

	private MemoryUtils memoryManager;

	private ConfigManager() {
		memoryManager = new MemoryUtils();
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public MemoryUtils getMemoryManager(String value) {
		return memoryManager;
	}

	// update config files
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

		builder.save();
	}

	// methode test d'ecriture dans un fichier
	public static void replaceLines() {
		try {
			// input the (modified) file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader(ProlineFiles.PROLINE_ZERO_CONFIG_FILE));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				line = line;
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(ProlineFiles.PROLINE_ZERO_CONFIG_FILE);
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

}
