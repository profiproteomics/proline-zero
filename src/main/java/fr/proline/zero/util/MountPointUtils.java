
package fr.proline.zero.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MountPointUtils {
    private boolean mountHasBeenChanged = false;

    private Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    private HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap;

    public HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }


    public Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }

    public boolean mountHasBeenChanged() {
        return mountHasBeenChanged;
    }

    private String errorMessage;
    private boolean errorFatal;


    private ArrayList<String> invalidPaths = new ArrayList<>();
    private ArrayList<String> missingMPs = new ArrayList<>();


    public MountPointUtils() {

        mountPointMap= JsonCortexAccess.getInstance().getMountPointMaps();

    }

    public enum MountPointType {
        RAW(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT, "Raw folder"),
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
    }


    public static String getMountPointDefaultPathLabel(MountPointType mpt) {

        if (mpt.equals(MountPointType.RAW)) {
            return ProlineFiles.USER_CORTEX_RAW_FILES_MOUNT_POINT;
        } else if (mpt.equals(MountPointType.RESULT)) {
            return ProlineFiles.USER_CORTEX_RESULT_FILES_POINT;
        } else {
            return ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT;
        }

    }


    public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {

        if (mountPointMap.get(mountPointType) == null&&!labelExists(value)&&!pathExists(path)) {
            Map<String, String> initialMap = new HashMap<>();
            initialMap.put(value, path);
            mountPointMap.put(mountPointType, initialMap);
            mountHasBeenChanged = true;
            return true;
        } else if (!labelExists(value) && !pathExists(path)) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            currentKValue.put(value, path);
            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged = true;
            return true;
        } else {
            return false;
        }


    }


    private boolean pathExists(String path) {
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> map = mountPointMap.get(mountPointType);
            if (map != null && map.containsValue(path)) {
                return true;
            }
        }
        return ConfigManager.getInstance().getParsingRulesManager().getFastaPaths().contains(path);
    }
    public boolean getIfPathExist(String path){
        return pathExists(path);
    }
    public boolean getIfLabelExists(String value){
        return labelExists(value);
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        for (MountPointUtils.MountPointType type : MountPointUtils.MountPointType.values()) {
            Map<String, String> mountPoints = getSpecMountPointMap(type);
            paths.addAll(mountPoints.values());
        }
        return paths;
    }



    private boolean labelExists(String value) {
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> map = mountPointMap.get(mountPointType);
            if (map != null && map.containsKey(value)) {
                return true;
            }
        }
        return false;
    }


    public boolean deleteMountPointEntry(MountPointType mountPointType, String key, Boolean forced) {
        boolean success = true;
        if (!forced) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            boolean canBeDeleted = (!key.equals(ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT)) && (!key.equals(ProlineFiles.USER_CORTEX_RESULT_FILES_POINT));
            boolean mPointExists = currentKValue.containsKey(key);
            if (canBeDeleted && mPointExists) {
                currentKValue.remove(key);
                mountPointMap.put(mountPointType, currentKValue);
                mountHasBeenChanged = true;
                success=true;
            } else {
                success = false;
            }
        }
        if (forced) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);

            currentKValue.remove(key);

            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged = true;
            success = true;

        }
        return success;
    }


    public void restoreMountPoints() {
        mountPointMap = JsonCortexAccess.getInstance().getMountPointMaps();
    }

    public void updateCortexConfigFile() {
        JsonCortexAccess.getInstance().updateCortexConfigFileJson(mountPointMap);
    }


    public boolean verif() {
        errorMessage = null;
        //boolean isValid = true;
        errorFatal = false;
        StringBuilder message = new StringBuilder();
        if (!atLeastOneMPoint()) {
            message.append("No mounting points, please add at least one \n");
            errorFatal = true;

        } else if (!allPathsExist()) {
            if (invalidPaths.size() == 1) {
                message.append("\n The following path does not exist: \n");
            } else {
                message.append("\n The following paths do not exist: \n");
            }
            for (String invalidPath : invalidPaths) {
                message.append("\n" + invalidPath + "\n");
            }
            errorFatal = true;
        }
        if (!defaultMptsExist() && atLeastOneMPoint() && allPathsExist()) {

            message.append("Minor error missing default mounting point : \n");
            for (String missingMP : missingMPs) {
                message.append(missingMP + "\n");
            }
            // no fatal error

        }
        if (message.length() > 0) {
            errorMessage = message.toString();
            // isValid = false;
        }



        return message.length() == 0;


    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isErrorFatal() {

        return errorFatal;
    }


    // check if at least one mounting point is present
    public boolean atLeastOneMPoint() {

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            if (mountPointMap.get(mountPointType) != null) {
                return true;
            }
        }
        return false;
    }
    // check if all paths inside mountpointmap exist and store wrong paths in an arraylist wrongPaths

    public boolean allPathsExist() {

        invalidPaths.clear();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> temp = mountPointMap.get(mountPointType);
            if (temp != null) {
                for (String key : temp.keySet()) {
                    Path pathToTest = Paths.get(temp.get(key));
                    boolean pathPresent = Files.exists(pathToTest);
                    if (!pathPresent) {
                        invalidPaths.add(temp.get(key));
                    }

                }
            }
        }
        return invalidPaths.isEmpty();
    }


    public boolean defaultMptsExist() {

        missingMPs.clear();

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            Map<String, String> specificMPEntries = mountPointMap.get(mountPointType);

            if (specificMPEntries == null) {
                missingMPs.add(mountPointType.getDisplayString());
            } else if (!specificMPEntries.containsKey(getMountPointDefaultPathLabel(mountPointType))) {

                missingMPs.add(mountPointType.getDisplayString());
            }

        }
        return missingMPs.isEmpty();
    }

    public List<String> getInvalidPaths() {
        return invalidPaths;
    }

    public List<String> getMissingMPs(){return missingMPs;}


}
