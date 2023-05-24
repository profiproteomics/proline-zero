package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import fr.proline.zero.gui.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

/**
 * read and write inside Cortex configfile application.conf
 */

public class JsonCortexAccess {

    private static JsonCortexAccess instance;
    private Config m_cortexProlineConfig;

    private JsonCortexAccess() {
        ConfigParseOptions options = ConfigParseOptions.defaults();
        options = options.setSyntax(ConfigSyntax.CONF);
        File cortexConfigFile = ProlineFiles.CORTEX_CONFIG_FILE;
        try {

            if (cortexConfigFile.exists()) {

                m_cortexProlineConfig = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE, options);

            } else {

                throw new FileNotFoundException("Cortex config file not found");

            }
        } catch (FileNotFoundException exception) {



            Popup.error("No configuration file found for cortex  ");
            m_cortexProlineConfig = ConfigFactory.empty();

            Logger logger = LoggerFactory.getLogger(JsonCortexAccess.class);
            logger.warn("exception occurred: " + exception.getMessage());

            System.exit(1);
        }


    }


    public static JsonCortexAccess getInstance() {
        if (instance == null) {
            instance = new JsonCortexAccess();
        }
        return instance;
    }

    public Config getCortexConfig() {
        return m_cortexProlineConfig;
    }


    /**
     * @return  returns a hashmap with all mount points found inside cortex application.conf
     */
    private HashMap<MountPointUtils.MountPointType, Map<String, String>> getMountPointsJson() {
        try {
            HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();
            if (m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                Config mountPointsCfg = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                Iterator<MountPointUtils.MountPointType> mpTypeIt = Arrays.stream(MountPointUtils.MountPointType.values()).iterator();
                while (mpTypeIt.hasNext()) {
                    MountPointUtils.MountPointType nextMp = mpTypeIt.next();
                    if (mountPointsCfg.hasPath(nextMp.getJsonKey())) {
                        HashMap<String, String> specificMountPointMap = new HashMap<>();
                        Config specificMPCfg = mountPointsCfg.getConfig(nextMp.getJsonKey());

                        for (Map.Entry<String, ConfigValue> entry : specificMPCfg.entrySet()) {
                            String label = entry.getKey();
                            String val = specificMPCfg.getString(label);
                            specificMountPointMap.put(label, val);
                            mountPointMap.put(nextMp, specificMountPointMap);
                        }
                    }
                }
            }

            return mountPointMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Saves mountpointmaps inside cortex application.conf
     *
     */
    public void updateCortexConfigFileJson(HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPoints) {

        ConfigObject toBePreserved = JsonCortexAccess.getInstance().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts = MountPointUtils.MountPointType.values().length;
        com.typesafe.config.Config[] builtConfig = new com.typesafe.config.Config[sizeOfMpts];
        com.typesafe.config.Config[] mergedConf = new com.typesafe.config.Config[builtConfig.length];
        int cpt = 0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> specificMPEntries = mountPoints.get(mountPointType);
            if (specificMPEntries == null) {
                builtConfig[cpt] = ConfigFactory.empty().atKey(mountPointType.getJsonKey());
            } else {
                builtConfig[cpt] = ConfigValueFactory.fromMap(specificMPEntries).atKey(mountPointType.getJsonKey());
            }
            if (cpt == 0) {
                mergedConf[cpt] = builtConfig[cpt];
            } else {
                mergedConf[cpt] = mergedConf[cpt - 1].withFallback(builtConfig[cpt]);
            }
            cpt++;
        }

        com.typesafe.config.Config finalMpts = mergedConf[cpt - 1].atKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        // final merge
        Config finalConfig = finalMpts.withFallback(toBePreserved);
        String finalWrite = finalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));


        try {
            FileWriter writer = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Return MountPoint Values read from config files.
    public HashMap<MountPointUtils.MountPointType, Map<String, String>> getMountPointMaps() {
        return getMountPointsJson();
    }


}
