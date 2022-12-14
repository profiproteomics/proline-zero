package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;

import java.util.*;


public class JsonAccess {
    private final Logger logger = LoggerFactory.getLogger(JsonAccess.class);

    private static JsonAccess instance;
    private Config m_cortexProlineConfig = null;

    private JsonAccess() {
        ConfigParseOptions options = ConfigParseOptions.defaults();
        options.setSyntax(ConfigSyntax.CONF);
        m_cortexProlineConfig  = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE,options);
    }

    public static JsonAccess getInstance() {
        if (instance == null) {
            instance = new JsonAccess();
        }
        return instance;
    }

    // Appel depuis constructeur ... puis refait à chaque appel ...
    public Config getCortexConfig(){
        return  m_cortexProlineConfig;
    }

    // returns a hashmap with all mount points  inside cortex application.conf
   private HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointsJson() {
        try {
                HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();
                if (m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                    Config mountPointsCfg = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                    Iterator<MountPointUtils.MountPointType> mpTypeIt = Arrays.stream(MountPointUtils.MountPointType.values()).iterator();
                    while (mpTypeIt.hasNext()){
                        MountPointUtils.MountPointType nextMp = mpTypeIt.next();
                        if (mountPointsCfg.hasPath(nextMp.getJsonKey())) {
                            HashMap<String, String> specificMountPointMap = new HashMap<>();
                            Config specificMPCfg = mountPointsCfg.getConfig(nextMp.getJsonKey());
                            Iterator<Map.Entry<String, ConfigValue>> mpEntriesIt = specificMPCfg.entrySet().iterator();
                            while (mpEntriesIt.hasNext()) {
                                Map.Entry<String, ConfigValue> entry = mpEntriesIt.next();
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

    // Return MountPoint Values read from config files.
    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointMaps()
    {
        return getMountPointsJson();
    }

    //Pour test rapide
    //
        public static void main(String[] argv){
        Config cfg = JsonAccess.getInstance().getCortexConfig();
        String data = cfg.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        try {
            FileWriter writer  = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE+"2");
            writer.write(data);
            writer.close();

//            data = cfg.root().render(ConfigRenderOptions.concise().setOriginComments(true).setFormatted(true).setJson(true));
//            writer  = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE+"3");
//            writer.write(data);
//            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
