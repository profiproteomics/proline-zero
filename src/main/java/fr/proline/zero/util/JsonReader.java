package fr.proline.zero.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import fr.proline.zero.modules.ExecutionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JsonReader {
    private static Logger logger = LoggerFactory.getLogger(JsonReader.class);

    private  static JsonReader instance;
    private Config m_cortexProlineConfig = null;

    private HashMap<MountPointUtils.MountPointType, Map<String,String>> mountPointMapJson;


    private JsonReader() {
        m_cortexProlineConfig = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE);
    }
    // singleton class ?
    public static JsonReader getInstance() {
        if (instance == null) {
            instance = new JsonReader();
        }
        return instance;
    }

    // returns a hashmap with all mount points  inside cortex application.conf
    private HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPoints() {
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
    // experimental and non secure update in cortex conf file todo regex
    public static void updateMountPoints(MountPointUtils.MountPointType mpt, String oldValue, String newValue){
        //HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointmap =ConfigManager.getInstance().getMountPointManager().getMountPointMap();
        Map<String, String> specMountPoints=ConfigManager.getInstance().getMountPointManager().getSpecMountPointMap(mpt);
        File cortexConfigFile = ProlineFiles.CORTEX_CONFIG_FILE;
        //regex
        if (specMountPoints.containsKey(oldValue)||specMountPoints.containsValue(oldValue)) {
            ExecutionSession.updateProperty(cortexConfigFile, oldValue, newValue);
        }
        else {
            logger.info("update impossible");
        }
    }

    public static void writeMountPoint(String label,String path)
    {
        File cortexConfigFile=ProlineFiles.CORTEX_CONFIG_FILE;

    }

    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointMapJson()
    {if (mountPointMapJson ==null)
        {
          mountPointMapJson =getMountPoints();
        }
        return mountPointMapJson;
    }








}
