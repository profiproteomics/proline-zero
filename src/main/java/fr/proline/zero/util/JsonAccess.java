package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;

import java.util.*;


public class JsonAccess {
    private Logger logger = LoggerFactory.getLogger(JsonAccess.class);

    private static JsonAccess instance;
    private Config m_cortexProlineConfig = null;

    private HashMap<MountPointUtils.MountPointType, Map<String,String>> mountPointMapJson;

    private boolean configHasBeenChanged=false;

    public boolean isConfigHasBeenChanged() {
        return configHasBeenChanged;
    }

    private JsonAccess() {
        m_cortexProlineConfig =getCortexConfig();
    }

    public static JsonAccess getInstance() {
        if (instance == null) {
            instance = new JsonAccess();
        }
        return instance;
    }
    public Config getCortexConfig(){

        ConfigParseOptions options = ConfigParseOptions.defaults();
        options.setSyntax(ConfigSyntax.CONF);
        return ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE,options);
    }
    // returns a hashmap with all mount points  inside cortex application.conf
    private HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointsJson() {
        try {
                HashMap<MountPointUtils.MountPointType, Map<String, String>> mountpointmap = new HashMap<>();
                if (m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                    Config mountPoints = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                    if (mountPoints.hasPath(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT)) {
                        HashMap<String, String> temp = new HashMap<>();
                        Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT);
                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, ConfigValue> entry = it.next();
                            String label = entry.getKey();
                            String val = resultFilesMP.getString(label);
                            temp.put(label, val);
                            mountpointmap.put(MountPointUtils.MountPointType.RESULT, temp);
                        }
                    }
                    if (mountPoints.hasPath(ProlineFiles.CORTEX_MZDB_MOUNT_POINT)) {
                        Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_MZDB_MOUNT_POINT);
                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                        HashMap<String, String> temp = new HashMap<>();
                        while (it.hasNext()) {
                            Map.Entry<String, ConfigValue> entry = it.next();
                            String label = entry.getKey();
                            String val = resultFilesMP.getString(label);
                            temp.put(label, val);
                            mountpointmap.put(MountPointUtils.MountPointType.MZDB, temp);
                        }

                    }
                    if (mountPoints.hasPath(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT)) {
                        Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT);
                        Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                        HashMap<String, String> temp = new HashMap<>();
                        while (it.hasNext()) {
                            Map.Entry<String, ConfigValue> entry = it.next();
                            String label = entry.getKey();
                            String val = resultFilesMP.getString(label);
                            temp.put(label, val);
                            mountpointmap.put(MountPointUtils.MountPointType.RAW, temp);

                        }
                    }
                }

            return mountpointmap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointMaps()
    {
        return getMountPointsJson();
    }

    // To be called after user finished to modify mountpoints in folderpanel
    // builds a config from mountpointmap and writes this config inside application.conf
    // used in folderpanel at every change in mounting points as a test

   /* public void updateFileMountPoints(HashMap<MountPointUtils.MountPointType, Map<String, String>> mpt){

        ConfigObject toBePreserved= JsonAccess.getInstance().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts=MountPointUtils.MountPointType.values().length;
        Config[] builtConfig =new Config[sizeOfMpts];
        int cpt=0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> temp = mpt.get(mountPointType);
            if(temp==null){builtConfig[cpt]=ConfigFactory.empty().atKey(mountPointType.getJsonKey());}
            else{
            builtConfig[cpt]=ConfigValueFactory.fromMap(temp).atKey(mountPointType.getJsonKey());
            cpt++;}
        }
        // merge of the different configs created above
        Config[] rebuiltConf=new Config[builtConfig.length];
        rebuiltConf[0]=builtConfig[0];
        for (int j=0;j< builtConfig.length-1;j++){
            rebuiltConf[j+1]=rebuiltConf[j].withFallback(builtConfig[j+1]);
        }

        Config finalMpts=rebuiltConf[rebuiltConf.length-1].atKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        // final merge
        Config finalConfig=finalMpts.withFallback(toBePreserved);
        String finalWrite=finalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(true).setComments(true));
        configHasBeenChanged=true;

        try {
            FileWriter writer  = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);

            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
    public void updateFileMountPointsV2(HashMap<MountPointUtils.MountPointType, Map<String, String>> mpt){

        ConfigObject toBePreserved= JsonAccess.getInstance().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts=MountPointUtils.MountPointType.values().length;
        Config[] builtConfig =new Config[sizeOfMpts];
        Config[] mergedConf=new Config[builtConfig.length];
        int cpt=0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> temp = mpt.get(mountPointType);
            if (temp==null){builtConfig[cpt]=ConfigFactory.empty().atKey(mountPointType.getJsonKey());}
            else {
            builtConfig[cpt]=ConfigValueFactory.fromMap(temp).atKey(mountPointType.getJsonKey());}
            if (cpt==0){mergedConf[cpt]=builtConfig[cpt];}
            else {
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


}
