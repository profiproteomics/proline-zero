package fr.proline.zero.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.management.OperatingSystemMXBean;

import fr.proline.zero.gui.Popup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class is the memory dealer
 * It gives the memory for each component
 * It should allow the following scenarios:
 * 1) user has set a max value in the config file
 * -> check that it is enough
 * -> dispatch this value among pgsql, jms, cortex, seqrepo and studio
 * 2) user has not set a max value in the config file 
 * -> get the memory available
 * -> dispatch this value among pgsql, jms, cortex, seqrepo and studio
 */
public class Memory {
	
	private static Logger logger = LoggerFactory.getLogger(Memory.class);

	// units
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	// default values in MB
	private static final long jmsXms = 512;
	private static final long jmsXmx = 1024;
	private static final long cortexXms = 1024;
	private static long cortexXmx = 4096; // to be adjusted to the available memory
	private static final long seqRepoXms = 512;
	private static final long seqRepoXmx = 1024;
	private static final long studioXms = 128;
	private static final long studioXmx = 1024;
	private static final long adminXmx = 1024;
	
	// physical memory (minimum should be 8G)
	private static final OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	private static final long totalMemorySize = os.getTotalPhysicalMemorySize();

	public static void adjustMemory(String requestedMemory) {
		final long availableMemory = Memory.os.getFreePhysicalMemorySize();
		// check that the current computer has at least 4GB (allow a bit less just in case)
		if(totalMemorySize < 3.5 * G) {
			throw new RuntimeException("This computer does not have enough RAM to run Proline Zero (4GB required, 8GB recommanded)");
		}
		long memory = availableMemory;
		if(requestedMemory != null) {
			// take the given memory parameter from the config file and convert it to real values
			try {
				int iMemory = Integer.parseInt(requestedMemory.trim().replaceAll("[kmgtKMGT]$", ""));
				String unit = requestedMemory.trim().replaceAll("\\d", "");
				if(unit.equals("K")) memory = iMemory * K;
				else if(unit.equals("M")) memory = iMemory * M;
				else if(unit.equals("G")) memory = iMemory * G;
				else if(unit.equals("T")) memory = iMemory * T;
				else if(unit.equals("")) memory = iMemory;
				// extract last letter
			} catch (Exception e) {
				// if requested memory is unreadable, use available memory, show warning popup and go on
				logger.warn("Allocated memory could not be read, Proline Zero will use available memory");
			}
		}

		logger.info("Proline Zero will use "+Math.floorDiv(memory, G)+"GB of RAM");
		// define settings according to the memory requested by the user
		if(memory < 4 * G) {
			Popup.warning("Your computer does not have enough free memory, Proline Zero will likely be running slowly");
			// if selected memory is low, only adjust the cortex memory and use postgresql default config
			cortexXmx = Math.floorDiv(memory, M);
			if(ProlineFiles.PG_DATASTORE != null) {
				restorePortgreSQLDefaultConfig();
			}
		} else {
			// split the selected memory: 40% for cortex, 40% for postgresql, 20% for hornetq, seqrepo and studio
			long memory40 = (long)(memory * 0.4);
			cortexXmx = Math.floorDiv(memory40, M);
			if(ProlineFiles.PG_DATASTORE != null) {
				adjustPostgreSQLMemory(memory40);
			}
		}
	}
	
	private static String toPgUnit(long value) {
		if(value < K) return value + "B";
		if(value < M) return (value / K) + "kB";
		if(value < G) return (value / M) + "MB";
		return (value / G) + "GB";
	}
	
	public static void restorePortgreSQLDefaultConfig() {
		// get config files
		File pgConfig = ProlineFiles.PG_CONFIG_FILE;
		File pgConfigDefault = ProlineFiles.PG_DEFAULT_CONFIG_FILE;
		File pgConfigBackup = ProlineFiles.PG_PREVIOUS_CONFIG_FILE;

		// if the default config file does not exist, then there is nothing to do
		if(!pgConfigDefault.exists()) {
			// good, we already use the default config
		} else {
			// save the config file
			try {
				Files.copy(pgConfig.toPath(), pgConfigBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch(IOException ioe) {
				logger.error("Failed to make a copy of the PostgreSQL configuration file, it will probably be lost", ioe);
			}
			// restore the default config file
			try {
				Files.copy(pgConfigDefault.toPath(), pgConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch(IOException ioe) {
				logger.error("Failed to restore the default PostgreSQL configuration file", ioe);
			}
		}
	}
	
	private static void adjustPostgreSQLMemory(long memory) {
		/*
		 * PostgreSQL settings to be changed:
		 * | parameter            | default | opt                | max                |
		 * | shared_buffers       | 128M    | physical/4         | physical/2         |
		 * | checkpoint_segments  | 3       | shared_buffers/16M | shared_buffers/8M  |
		 * | temp_buffers         | 8M      | physical/32        | physical/8         |
		 * | maintenance_work_mem | 64M     | physical/16        | physical/8         |
		 * | effective_cache_size | 4G      | physical/2         | physical*3/4       |
		 * 
		 * */
		
		// compute postgresql settings according to the given value
		long sharedBuffersOptimizedValue = memory / 4;
		if(sharedBuffersOptimizedValue > 4*G) sharedBuffersOptimizedValue = 4 * G;
		HashMap<String, String> pgParams = new HashMap<String, String>();
		pgParams.put("shared_buffers", "shared_buffers = " + toPgUnit(sharedBuffersOptimizedValue));
		pgParams.put("checkpoint_segments", "checkpoint_segments = " + Math.floorDiv(sharedBuffersOptimizedValue, (16 * M)));
		pgParams.put("temp_buffers", "temp_buffers = " + toPgUnit(memory / 32));
		pgParams.put("work_mem", "work_mem = 8MB");
		pgParams.put("maintenance_work_mem", "maintenance_work_mem = " + toPgUnit(memory / 16));
		pgParams.put("effective_cache_size", "effective_cache_size = " + toPgUnit(memory / 2));
		
		// get a fresh copy of the postgresql file
		File pgConfig = ProlineFiles.PG_CONFIG_FILE;
		File pgConfigDefault = ProlineFiles.PG_DEFAULT_CONFIG_FILE;
		if(!pgConfig.exists()) {
			throw new RuntimeException("The PostgreSQL configuration file is missing");
		}
		// if the default config file does not exist, create it from the existing file, it should correspond to the first launch
		if(!pgConfigDefault.exists()) {
			try {
				Files.copy(pgConfig.toPath(), pgConfigDefault.toPath());
			} catch(IOException ioe) {
				logger.error("Failed to make a copy of the default PostgreSQL configuration file", ioe);
			}
		}
		try {
			// open the default file and change the values
			ArrayList<String> lines = new ArrayList<String>();
			// read each line
			Files.lines(pgConfigDefault.toPath()).forEach(line -> {
				boolean lineAdded = false;
				// except for comments and blank lines
				if(!line.trim().startsWith("#") && !line.isEmpty()) {
					// check each parameter
					for(String key: pgParams.keySet()) {
						if(line.contains(key) && !pgParams.get(key).equals("")) {
							// put the new setting in the file
							lines.add(pgParams.get(key));
							// remove the setting from the map
							pgParams.put(key, "");
							// set to true so we dont have the same setting twice
							lineAdded = true;
						}
					}
				}
				if(!lineAdded) lines.add(line);
			});
			// add the remaining parameters at the end of the list of lines
			pgParams.forEach((key, value) -> {
				if(!pgParams.get(key).equals("")) {
					lines.add(value);
				}
			});
			// write the updated content to the regular config file
			Files.write(pgConfig.toPath(), lines);
		} catch(IOException ioe) {
			logger.error("Failed to make a copy of the default PostgreSQL configuration file", ioe);
		} catch (Exception e) {
			logger.error("Error writing file "+pgConfig.getAbsolutePath(), e);
		}
	}
	
	// memory getters
	public static String getJmsMinMemory() {
		return "-Xms"+jmsXms+"M";
	}
	public static String getJmsMaxMemory() { return "-Xmx"+jmsXmx+"M"; }
	public static String getCortexMinMemory() {
		return "-Xms"+cortexXms+"M";
	}
	public static String getCortexMaxMemory(boolean debugMode) { return !debugMode ? "-Xmx"+cortexXmx+"M" : "-Xmx2G"; }
//	public static String getCortexMaxMemory() { return getCortexMaxMemory(false); }
	public static String getSeqRepoMinMemory() {
		return "-Xms"+seqRepoXms+"M";
	}
	public static String getSeqRepoMaxMemory() { return "-Xmx"+seqRepoXmx+"M"; }
//	public static String getStudioMinMemory() { return "-Xms"+studioXms+"M"; }
//	public static String getStudioMaxMemory() { return "-Xmx"+studioXmx+"M"; }
	public static String getAdminMaxMemory() { return "-Xmx"+adminXmx+"M"; }
}
