package fr.proline.zero.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class MountPointUtils {
    public static boolean mountHasBeenChanged = false;

    private static Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    private static HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();


    public HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }

    public Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }

    public MountPointUtils() {

        mountPointMap = JsonReader.getInstance().getMountPointMaps();
    }

    public enum MountPointType {

        RAW("raw_files", "Raw folders"),
        MZDB("mzdb_files", "Mzdb folder"),
        RESULT("result_files", "Result folder");
        private final String jsonKey;
        private final String displayStr;

        MountPointType(String key, String displayStr) {
            this.jsonKey = key;
            this.displayStr = displayStr;
        }

        public String getDisplayString() {
            return displayStr;
        }

        public String getJsonKey() {
            return jsonKey;
        }
    }


        public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {

            if (!labelExists(value) && !pathExists(path)) {
                //String keymap= mountPointType.getJsonKey();
                Map<String, String> currentKValue = mountPointMap.get(mountPointType);
                currentKValue.put(value, path);
                mountPointMap.put(mountPointType, currentKValue);
                return true;
            } else {
                // a warning message will be displayed in IHM
                logger.info(String.valueOf(mountPointMap));
                return false;
            }
        }

        public boolean pathExists(String path) {
            boolean pathExists = false;
            if (mountPointMap.get(MountPointType.RESULT).containsValue(path)) {
                pathExists = true;
            }
            if (mountPointMap.get(MountPointType.MZDB).containsValue(path)) {
                pathExists = true;
            }
            if (mountPointMap.get(MountPointType.RAW).containsValue(path)) {
                pathExists = true;
            }
            return pathExists;
        }

        public boolean labelExists(String value) {
            boolean labelExists = false;
            if (mountPointMap.get(MountPointType.RESULT).containsKey(value)) {
                labelExists = true;
            }
            if (mountPointMap.get(MountPointType.MZDB).containsKey(value)) {
                labelExists = true;
            }
            if (mountPointMap.get(MountPointType.RAW).containsKey(value)) {
                labelExists = true;
            }
            return labelExists;
        }


        public boolean delMountPointEntry(MountPointType mountPointType, String key) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            boolean canBeDeleted = (!key.equals("mzdb_files")) && (!key.equals("mascot_data"));
            boolean mPointExists = currentKValue.containsKey(key);
            if (canBeDeleted && mPointExists) {
                currentKValue.remove(key);
                mountPointMap.put(mountPointType, currentKValue);
                mountHasBeenChanged = true;
                return true;

            } else {
                return false;
            }
        }



}