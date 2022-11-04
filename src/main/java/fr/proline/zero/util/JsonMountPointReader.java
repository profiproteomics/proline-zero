package fr.proline.zero.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonMountPointReader {

    private  static JsonMountPointReader instance;
    //private Config m_cortexProlineConfig = null;
    private Config GeneralConfig=null;
    private static Logger logger = LoggerFactory.getLogger(JsonMountPointReader.class);
    public HashMap<MountPointUtils.MountPointType, Map<String,String>> MountPointMap = new HashMap<>();

    //public JsonMountPointReader() {
        //m_cortexProlineConfig = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE);
        // resultFilesPath = new ArrayList<>();


    //}
    public static JsonMountPointReader getInstance() {
        if (instance == null) {
            instance = new JsonMountPointReader();
        }
        return instance;
    }

    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointsByFile(File ConfFile){
        try {
            GeneralConfig=ConfigFactory.parseFile(ConfFile);
            if(GeneralConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                com.typesafe.config.Config mountPoints = GeneralConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                if (mountPoints.hasPath(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT)){
                    HashMap<String,String> TempMapResult =new HashMap<>();
                    com.typesafe.config.Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        TempMapResult.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.RESULT,TempMapResult);




                    }
                }
                if (mountPoints.hasPath(ProlineFiles.CORTEX_MZDB_MOUNT_POINT)){
                    com.typesafe.config.Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_MZDB_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    HashMap<String,String> TempMapMzdb =new HashMap<>();

                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);

                        //logger.info(" For type "+ProlineFiles.CORTEX_MZDB_MOUNT_POINT+" entry "+label +" => "+val);

                        TempMapMzdb.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.MZDB,TempMapMzdb);



                    }

                }
                if (mountPoints.hasPath(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT)){
                    Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    HashMap<String,String> TempMapRaw =new HashMap<>();
                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        TempMapRaw.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.RAW,TempMapRaw);

                    }
                }
            }
            logger.info("list of the mount points inside cortex application.conf:   "+ MountPointMap);
            return MountPointMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
