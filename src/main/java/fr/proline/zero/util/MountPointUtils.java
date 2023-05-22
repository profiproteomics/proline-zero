
package fr.proline.zero.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Contains utilities to manipulate the set of mounting points
 * implements verifications and ensures consistency (uniqueness or validity of paths)
 *
 * @see ConfigManager
 * @see JsonCortexAccess
 */


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

    private ArrayList<String> duplicatePaths = new ArrayList<>();


    public MountPointUtils() {

        mountPointMap = JsonCortexAccess.getInstance().getMountPointMaps();

    }

    /**
     * Enum, represents three different type of mounting points
     */

    public enum MountPointType {
        RAW(ProlineFiles.CORTEX_RAW_FILES_MOUNT_POINT, "Raw folder"),
        MZDB(ProlineFiles.CORTEX_MZDB_MOUNT_POINT, "mzDB folder"),
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

    /**
     * Add a mounting point
     *
     * @param mountPointType
     * @param value
     * @param path
     * @return true if the mounting point has been added
     */
    public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {

        if (mountPointMap.get(mountPointType) == null && !labelExists(value) && !pathExists(path)) {
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
        boolean pathExistsInMountpoints = false;
        boolean pathExistsInFastas = false;
        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();


        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> map = mountPointMap.get(mountPointType);
            if (map != null && map.containsValue(path)) {
                pathExistsInMountpoints = true;
                break;
            }
        }
        if (fastaToBeDisplayed != null) {
            pathExistsInFastas = fastaToBeDisplayed.contains(path);
        }


        return pathExistsInFastas || pathExistsInMountpoints;

    }

    public boolean getIfPathExist(String path) {
        return pathExists(path);
    }

    public boolean getIfLabelExists(String value) {
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

    /**
     * @param mountPointType
     * @param key
     * @param forced         set forced to true to delete a default mounting point
     * @return
     */
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
                success = true;
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


    /**
     * verifies the validity of paths and the presence of at least one mounting point check if there are duplicate files
     * builds the error message
     *
     * @return true if no error in mounting points
     * @see ConfigManager
     */

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
        /// added 15/05
        if (duplicateFileInside(filesPaths())) {
            errorFatal = true;
            message.append("Some paths in the mount points are identical, please check your mount points \n");
            for (String duplicatePaths : duplicatePaths) {
                message.append(duplicatePaths + "\n");
            }
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

    /**
     * check if at least one mounting point is present
     * does not check raw mountPointType
     *
     * @return true if at least one mounting point is present
     */
    public boolean atLeastOneMPoint() {

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            if (!mountPointType.equals(MountPointType.RAW)) {
                if (mountPointMap.get(mountPointType) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean atLeastOneMPointV2() {

        return mountPointMap.size() != 0;
    }

    /**
     * check if all paths inside mountpointmap exist and store wrong paths in an arraylist invalidPaths
     *
     * @return true if all paths exist
     */

    public boolean allPathsExist() {

        invalidPaths.clear();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> temp = mountPointMap.get(mountPointType);
            if (temp != null) {
                for (String key : temp.keySet()) {
                    Path pathToTest = Paths.get(temp.get(key));

                    boolean pathPresent = Files.exists(pathToTest); // returns true even if path is empty string???
                    boolean pathIsEmptyString = pathToTest.toString().equals("");
                    if (!pathPresent) {
                        invalidPaths.add(temp.get(key));
                    } else if (pathIsEmptyString) {
                        invalidPaths.add(key + " : the path corresponding to that label is an empty string");

                    }

                }
            }
        }
        return invalidPaths.isEmpty();
    }


    // Does not check raw mountpoints
    public boolean defaultMptsExist() {

        missingMPs.clear();

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            Map<String, String> specificMPEntries = mountPointMap.get(mountPointType);
            // CD   Raw mount Points are not evaluated
            if (!mountPointType.equals(MountPointType.RAW)) {
                if (specificMPEntries == null) {
                    missingMPs.add(mountPointType.getDisplayString());
                } else if (!specificMPEntries.containsKey(getMountPointDefaultPathLabel(mountPointType))) {

                    missingMPs.add(mountPointType.getDisplayString());
                }

            }
        }
        return missingMPs.isEmpty();
    }

    public List<String> getInvalidPaths() {
        return invalidPaths;
    }

    public List<String> getMissingMPs() {
        return missingMPs;
    }

    public ArrayList<String> filesPaths() {
        ArrayList<String> pathsPresent = new ArrayList<>();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> Mpentries = mountPointMap.get(mountPointType);
            if (Mpentries != null) {
                Set<Map.Entry<String, String>> test = Mpentries.entrySet();
                for (Map.Entry<String, String> entries : test) {
                    String path = entries.getValue();
                    pathsPresent.add(path);
                }
            }
        }
        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        if (fastaToBeDisplayed != null) {
            for (int k = 0; k < fastaToBeDisplayed.size(); k++) {
                String path = fastaToBeDisplayed.get(k);
                pathsPresent.add(path);
            }
        }


        return pathsPresent;
    }

    public boolean duplicateFileInside(ArrayList<String> paths) {
        duplicatePaths.clear();

        boolean duplicateFile = false;
        for (int i = 0; i < paths.size(); i++) {
            String pathi = paths.get(i);
            for (int j = i + 1; j < paths.size(); j++) {

                String pathj = paths.get(j);

                if (pathi.equals(pathj)) {
                    duplicatePaths.add(pathi);

                }
            }

        }
        return !duplicatePaths.isEmpty();
    }
    public ArrayList<String> duplicateFilesBuilder(ArrayList<String> paths) {
        ArrayList<String>  duplicates=new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            String pathi = paths.get(i);
            for (int j = i + 1; j < paths.size(); j++) {

                String pathj = paths.get(j);

                if (pathi.equals(pathj)) {
                    duplicates.add(pathi);

                }
            }

        }
        return duplicates;
    }

    public ArrayList<String> getDuplicatePaths() {
        ArrayList<String>  paths=filesPaths();
        ArrayList<String> duplicates= duplicateFilesBuilder(paths);

        return duplicates;
    }


}
