
package fr.proline.zero.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class MountPointUtils {
    private   boolean mountHasBeenChanged = false;

    private  Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    private  HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap;


    public HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }

    public  Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }

    public boolean isMountHasBeenChanged() {
        return mountHasBeenChanged;
    }

    public MountPointUtils() {
        mountPointMap = JsonAccess.getInstance().getMountPointMaps();
    }

    public enum MountPointType {

        RAW(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT, "Raw folders"),
        MZDB(ProlineFiles.CORTEX_MZDB_MOUNT_POINT, "Mzdb folder"),
        RESULT(ProlineFiles.CORTEX_RESULT_FILES_MOUNT_POINT, "Result folder");
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

        public static MountPointType getMPTypeForDisplayString(String display){
            for(MountPointType type : MountPointType.values()){
                if(type.getDisplayString().equals(display))
                    return type;
            }
            return null;
        }
    }

    public static String getMountPointDefaultPathLabel(MountPointType mpt) {
        switch (mpt){
            case RESULT ->  {
                return ProlineFiles.USER_CORTEX_RESULT_FILES_POINT;
            }
            case MZDB -> {
                return ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT;
            }
            case RAW -> {
                return ProlineFiles.USER_CORTEX_RAW_FILES_MOUNT_POINT;
            }
            default -> {
                return "";
            }
        }
    }

        public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {

            if (!labelExists(value) && !pathExists(path)) {
                //String keymap= mountPointType.getJsonKey();
                Map<String, String> currentKValue = mountPointMap.get(mountPointType);
                currentKValue.put(value, path);
                mountPointMap.put(mountPointType, currentKValue);
                mountHasBeenChanged=true;
                return true;
            } else {
                // a warning message will be displayed in IHM

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
            if (mountPointMap.containsKey(MountPointType.RAW) && mountPointMap.get(MountPointType.RAW).containsValue(path)) {
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
            if (mountPointMap.containsKey(MountPointType.RAW) && mountPointMap.get(MountPointType.RAW).containsKey(value)) {
                labelExists = true;
            }
            return labelExists;
        }


        public boolean delMountPointEntry(MountPointType mountPointType, String key) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            boolean canBeDeleted = (!key.equals(ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT)) && (!key.equals(ProlineFiles.USER_CORTEX_RESULT_FILES_POINT));
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

        public void restoreMountPoints(){
            mountPointMap=JsonAccess.getInstance().getMountPointMaps();
        }



}
