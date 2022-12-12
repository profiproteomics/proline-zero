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

    private HashMap<MountPointUtils.MountPointType, Map<String,String>> mountPointMapJson;

    // VDS --> A supprimer. Pas ici... c'est MountPointUtil qui gère. Ici que acces au config Json
    private boolean configHasBeenChanged=false;

    public boolean isConfigHasBeenChanged() {
        return configHasBeenChanged;
    }

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

//        ConfigParseOptions options = ConfigParseOptions.defaults();
//        options.setSyntax(ConfigSyntax.CONF);
//        return ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE,options);
        return  m_cortexProlineConfig;
    }

    // returns a hashmap with all mount points  inside cortex application.conf
    private HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointsJson() {
        try {
                HashMap<MountPointUtils.MountPointType, Map<String, String>> mountpointmap = new HashMap<>();
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
                                mountpointmap.put(nextMp, specificMountPointMap);
                            }
                        }
                    }
                }



//                    if (mountPointsCfg.hasPath(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT)) {
//                        HashMap<String, String> specificMountPointMap = new HashMap<>();
//                        Config resultFilesMP = mountPointsCfg.getConfig(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT);
//                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
//                        while (it.hasNext()) {
//                            Map.Entry<String, ConfigValue> entry = it.next();
//                            String label = entry.getKey();
//                            String val = resultFilesMP.getString(label);
//                            specificMountPointMap.put(label, val);
//                            mountpointmap.put(MountPointUtils.MountPointType.RESULT, specificMountPointMap);
//                        }
//                    }
//                    if (mountPointsCfg.hasPath(ProlineFiles.CORTEX_MZDB_MOUNT_POINT)) {
//                        Config resultFilesMP = mountPointsCfg.getConfig(ProlineFiles.CORTEX_MZDB_MOUNT_POINT);
//                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
//                        HashMap<String, String> specificMountPointMap = new HashMap<>();
//                        while (it.hasNext()) {
//                            Map.Entry<String, ConfigValue> entry = it.next();
//                            String label = entry.getKey();
//                            String val = resultFilesMP.getString(label);
//                            specificMountPointMap.put(label, val);
//                            mountpointmap.put(MountPointUtils.MountPointType.MZDB, specificMountPointMap);
//                        }
//
//                    }
//                    if (mountPointsCfg.hasPath(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT)) {
//                        Config resultFilesMP = mountPointsCfg.getConfig(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT);
//                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
//                        HashMap<String, String> temp = new HashMap<>();
//                        while (it.hasNext()) {
//                            Map.Entry<String, ConfigValue> entry = it.next();
//                            String label = entry.getKey();
//                            String val = resultFilesMP.getString(label);
//                            temp.put(label, val);
//                            mountpointmap.put(MountPointUtils.MountPointType.RAW, temp);
//
//                        }
//                    }
//                }

            return mountpointmap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    // Return MountPoint Values read from config files.
    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointMaps()
    {
        return getMountPointsJson();
    }


    public void updateConfgFileMountPoints(HashMap<MountPointUtils.MountPointType, Map<String, String>> mpt){

        ConfigObject toBePreserved= JsonAccess.getInstance().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts=MountPointUtils.MountPointType.values().length;
        Config[] builtConfig =new Config[sizeOfMpts];
        Config[] mergedConf=new Config[builtConfig.length];
        int cpt=0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> specificMPEntries = mpt.get(mountPointType);
            if (specificMPEntries==null){
                builtConfig[cpt]=ConfigFactory.empty().atKey(mountPointType.getJsonKey());
            } else {
                builtConfig[cpt]=ConfigValueFactory.fromMap(specificMPEntries).atKey(mountPointType.getJsonKey());
            }
            if (cpt==0){
                mergedConf[cpt]=builtConfig[cpt];
            } else {
                mergedConf[cpt]=mergedConf[cpt-1].withFallback(builtConfig[cpt]);
            }
            cpt++;
        }

        Config finalMpts=mergedConf[cpt-1].atKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        // final merge
        Config finalConfig=finalMpts.withFallback(toBePreserved);
        String finalWrite=finalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        configHasBeenChanged=true;

        try {
            FileWriter writer  = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // VDS --> Pas écriture mais lecture !
//    public void restoreValues(){
//        Config initialConfig=m_cortexProlineConfig;
//        String initialConfigWrite=initialConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
//        try {
//            FileWriter writer  = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);
//            writer.write(initialConfigWrite);
//            writer.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
    //Pour test rapide
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
