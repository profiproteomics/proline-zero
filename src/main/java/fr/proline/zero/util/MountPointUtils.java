package fr.proline.zero.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MountPointUtils {
    public boolean MountHasBeenChanged=false;
    private  static MountPointUtils instance;

    private Config m_cortexProlineConfig = null;

    private static Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    public HashMap<MountPointUtils.MountPointType, Map<String,String>> MountPointMap = new HashMap<>();


    public static MountPointUtils getInstance() {
        if (instance == null) {
            instance = new MountPointUtils();
        }
        return instance;
    }
    public MountPointUtils() {
        m_cortexProlineConfig = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE);
        // resultFilesPath = new ArrayList<>();


    }




    public enum MountPointType {


        RAW {
            public String toString() {
                return "raw_files";
            }
        },
        MZDB {
            public String toString() {
                return "mzdb_files";
            }
        },
        RESULT {
            public String toString() {
                return "result_files";
            }
        }
        // add attribute? see documentation

    }




   //returns all key-values in a map of strings for a specific MountPoint
    public HashMap<String,String> getSpecMountPoints(MountPointUtils.MountPointType MPT){
        try {
            HashMap<String,String> temp =new HashMap<>();
            if(m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                com.typesafe.config.Config mountPoints = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                if (MPT== MountPointUtils.MountPointType.RESULT){

                    com.typesafe.config.Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);


                    }
                    logger.info("list of results files:  "+temp);

                }
                if (MPT== MountPointUtils.MountPointType.MZDB){
                    com.typesafe.config.Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_MZDB_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();


                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);

                        //logger.info(" For type "+ProlineFiles.CORTEX_MZDB_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);




                    }
                    logger.info("list of mzdb files:     "+temp);


                }
                if (MPT== MountPointUtils.MountPointType.RAW){
                    Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();

                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);


                    }
                    logger.info("list of raw files   "+temp);

                }

            }
            return temp;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getAllMountPoints(){
        try {
            if(m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                Config mountPoints = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                if (mountPoints.hasPath(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT)){
                    HashMap<String,String> temp =new HashMap<>();
                    Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.RESULT,temp);




                    }
                }
                if (mountPoints.hasPath(ProlineFiles.CORTEX_MZDB_MOUNT_POINT)){
                    Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_MZDB_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    HashMap<String,String> temp =new HashMap<>();

                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);

                        //logger.info(" For type "+ProlineFiles.CORTEX_MZDB_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.MZDB,temp);



                    }

                }
                if (mountPoints.hasPath(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT)){
                    Config resultFilesMP = mountPoints.getConfig(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT);
                    Iterator<Map.Entry<String, ConfigValue>> it = resultFilesMP.entrySet().iterator();
                    HashMap<String,String> temp =new HashMap<>();
                    while (it.hasNext()){
                        Map.Entry<String, ConfigValue> entry = it.next();
                        String label = entry.getKey();
                        String val = resultFilesMP.getString(label);
                        //logger.info(" For type "+ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT+" entry "+label +" => "+val);

                        temp.put(label,val);
                        MountPointMap.put(MountPointUtils.MountPointType.RAW,temp);

                    }
                }
            }
            logger.info("list of all mount points inside application.conf "+ MountPointMap);
            return MountPointMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public void addMountPointEntry(MountPointUtils.MountPointType MPT,String value,String path)
    {
        HashMap<MountPointUtils.MountPointType, Map<String,String>> CurrentMPTs= getAllMountPoints();
        Map<String,String> CurrentKValue = getSpecMountPoints(MPT);
        logger.info("initial values:   "+CurrentMPTs);

        CurrentKValue.put(value,path);
        CurrentMPTs.put(MPT,CurrentKValue);
        logger.info("New mount points after modification:   "+CurrentMPTs);
        // TODO add the new key-value in application.conf Cortex
        MountHasBeenChanged=true;



    }
    // method similar to addMountPointEntry to add a mount point (value path) and return the new set of mount points
    public HashMap<MountPointUtils.MountPointType, Map<String,String>>  addAndGetMountPointEntry(MountPointUtils.MountPointType MPT,String value,String path)
    {
        HashMap<MountPointUtils.MountPointType, Map<String,String>> CurrentMPTs= getAllMountPoints();
        Map<String,String> CurrentKValue = getSpecMountPoints(MPT);
        logger.info(" Initial values:   "+CurrentMPTs);

        CurrentKValue.put(value,path);
        CurrentMPTs.put(MPT,CurrentKValue);
        logger.info("After modification:   "+CurrentMPTs);
        // TODO add value-path  in application.conf Cortex
        MountHasBeenChanged=true;
        return CurrentMPTs;


    }





}
