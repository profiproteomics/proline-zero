package fr.proline.zero.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class MountPointUtils {
    public static boolean mountHasBeenChanged = false;

    private static Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    private static HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();


    public static HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }

    public static Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }
    public static boolean addSucces=true;
    public static boolean delSucces=true;

    public MountPointUtils() {

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
        
        if (!labelExists(value)&&!pathExists(path)) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            currentKValue.put(value, path);
            mountPointMap.put(mountPointType, currentKValue);
            addSucces=true;
        }
        else {
            // a warning message will be displayed
             addSucces=false;
        }
    }
    public static boolean pathExists(String path){
        boolean pathExists=false;
        if (mountPointMap.get(MountPointType.RESULT).containsValue(path)){pathExists=true;}
        if (mountPointMap.get(MountPointType.MZDB).containsValue(path)){pathExists=true;}
        if (mountPointMap.get(MountPointType.RAW).containsValue(path)){pathExists=true;}
        return pathExists;
    }
    public static boolean labelExists(String value){
        boolean labelExists=false;
        if (mountPointMap.get(MountPointType.RESULT).containsKey(value)){labelExists=true;}
        if (mountPointMap.get(MountPointType.MZDB).containsKey(value)){labelExists=true;}
        if (mountPointMap.get(MountPointType.RAW).containsKey(value)){labelExists=true;}
        return labelExists;
    }

    // method similar to addMountPointEntry to add a mount point (value-path) and return the new set of mount points
    // can be deleted, not used
    public static HashMap<MountPointUtils.MountPointType, Map<String, String>> addAndGetMountPointEntry(MountPointUtils.MountPointType mpt, String value, String path) {

        if (!mountPointMap.get(mpt).containsKey(value) && !mountPointMap.get(mpt).containsValue(path)) {
            Map<String, String> currentKValue = mountPointMap.get(mpt);
            currentKValue.put(value, path);
            mountPointMap.put(mpt, currentKValue);
            mountHasBeenChanged = true;
            addSucces=true;

            return mountPointMap;

        } else {

            addSucces=false;
            //TODO Warning Map is not null but returns null!!!
            return null;
        }
    }
    // TODO method to delete a mount point, check if entry exists and if the mountPoint can be deleted before deletion

    public static void delMountPointEntry(MountPointType mountPointType, String key) {
        Map<String, String> currentKValue = mountPointMap.get(mountPointType);
        boolean canBeDeleted = (!key.equals("mzdb_files")) && (!key.equals("mascot_data"));
        boolean mPointExists = currentKValue.containsKey(key);
        if (canBeDeleted && mPointExists) {
            currentKValue.remove(key);
            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged=true;
            delSucces=true;

        } else {
            if (!mPointExists) {
                delSucces=false;
            }
            if (!canBeDeleted) {
                delSucces=false;
            }
        }
    }


}
