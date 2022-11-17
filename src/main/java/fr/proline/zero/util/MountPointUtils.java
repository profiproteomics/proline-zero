package fr.proline.zero.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class MountPointUtils {
    public static boolean mountHasBeenChanged = false;


    private Config m_cortexProlineConfig = null;

    private static Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    public static HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();


    public static HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }

    public static Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }

    public MountPointUtils() {
        m_cortexProlineConfig = ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE);
        mountPointMap = JsonReader.getInstance().getMountPointMapJson();

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


    }


    public static void addMountPointEntry(MountPointUtils.MountPointType mountPointType, String value, String path) {
        // test if mountpoint already exists
        if (!mountPointMap.get(mountPointType).containsKey(value) && !mountPointMap.get(mountPointType).containsValue(path)) {
            Map<String, String> CurrentKValue = mountPointMap.get(mountPointType);
            logger.info("initial values:   " + mountPointMap);

            CurrentKValue.put(value, path);
            mountPointMap.put(mountPointType, CurrentKValue);
            logger.info("New mount points after modification:   " + mountPointMap);


            mountHasBeenChanged = true;
        } else {
            logger.info("The Mount point could not be added because a similar key or value or both already exist");

        }
    }

    // method similar to addMountPointEntry to add a mount point (value-path) and return the new set of mount points
    public static HashMap<MountPointUtils.MountPointType, Map<String, String>> addAndGetMountPointEntry(MountPointUtils.MountPointType mpt, String value, String path) {

        if (!mountPointMap.get(mpt).containsKey(value) && !mountPointMap.get(mpt).containsValue(path)) {
            Map<String, String> currentKValue = mountPointMap.get(mpt);
            logger.info(" Initial values:   " + mountPointMap);

            currentKValue.put(value, path);
            mountPointMap.put(mpt, currentKValue);
            logger.info("After modification:   " + mountPointMap);


            mountHasBeenChanged = true;

            return mountPointMap;


        } else {
            logger.info("The Mount point could not be added because a similar key or value or both already exist");
            return null;
        }
    }



    // TODO method to delete a mount point, check if entry exists and if the mountPoint can be deleted before deletion

    public static void delMountPointEntry(MountPointType mountPointType, String key) {

        Map<String, String> currentKValue = mountPointMap.get(mountPointType);
        boolean canBeDeleted = (!key.equals("mzdb_files")) && (!key.equals("mascot_data")) && (!key.equals("raw_files"));
        boolean mPointExists = currentKValue.containsKey(key);
        if (canBeDeleted && mPointExists) {
            currentKValue.remove(key);
            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged=true;

            logger.info("map after deletion:   " + mountPointMap);

        } else {
            if (!mPointExists) {
                logger.info("the mount point you want to delete does not exist!!!");
            }
            if (!canBeDeleted) {
                logger.info("This mount point cannot be deleted");
            }
        }
    }
}
