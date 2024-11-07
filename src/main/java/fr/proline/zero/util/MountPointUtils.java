
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
    private boolean mountPointMapHasBeenChanged = false;

    private Logger logger = LoggerFactory.getLogger(MountPointUtils.class);

    private HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap;


    public HashMap<MountPointType, Map<String, String>> getMountPointMap() {
        return mountPointMap;
    }


    public Map<String, String> getSpecMountPointMap(MountPointType mountPointType) {
        return mountPointMap.get(mountPointType);
    }

    public boolean mountHasBeenChanged() {
        return mountPointMapHasBeenChanged;
    }

    private String errorMessage;
    private boolean errorFatal;


    private final ArrayList<String> invalidPaths = new ArrayList<>();
    private final ArrayList<String> missingMountPoints = new ArrayList<>();




    public MountPointUtils() {

        mountPointMap = JsonCortexAccess.getInstance().getMountPointMaps();

       // defaultMountPointRenamer();

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
     * @param mountPointType mountpointype of the added mounting point
     * @param value label of the mounting point
     * @param path  path of the mount point
     * @return true if the mounting point has been added
     */
    public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {


        if (mountPointMap.get(mountPointType) == null ) {
            Map<String, String> initialMap = new HashMap<>();
            initialMap.put(value, path);
            mountPointMap.put(mountPointType, initialMap);
            mountPointMapHasBeenChanged = true;
            return true;
        } else   {
            Map<String, String> currentMountPoint = mountPointMap.get(mountPointType);
            currentMountPoint.put(value, path);
            mountPointMap.put(mountPointType, currentMountPoint);
            mountPointMapHasBeenChanged = true;
            return true;
        }
        }




    /**
     * update a mountPoint, used while editing
     *@return true if the mountPoint has been updated successfully
     */

    public boolean updateMountPointEntry(MountPointType mountPointType, String newKey, String newPath, String oldKey, String oldPath) {

        Map<String, String> mapToBeUpDated = mountPointMap.get(mountPointType);
        if (mapToBeUpDated != null) {
            mapToBeUpDated.remove(oldKey, oldPath);
            mapToBeUpDated.put(newKey, newPath);
            mountPointMap.put(mountPointType, mapToBeUpDated);
            mountPointMapHasBeenChanged = true;
        } else {
            Map<String, String> mapToBeUpdated = new HashMap<>();
            mapToBeUpdated.put(newKey, newPath);
            mountPointMap.put(mountPointType, mapToBeUpdated);
            mountPointMapHasBeenChanged = true;
        }
        return true;
    }



    /**
     * First version that checks in all mountPoints plus fasta folders if path already exists
     * @param path that is to be checked against the current directory
     * @return true if path already exists
     */
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
     * Deletes a non default mountPoint
     * @param mountPointType mountpointtype of the mountPoint
     * @param key of the mount point to be deleted
     *
     * @return true if the mountPoint has been deleted successfully
     */
    public boolean deleteMountPointEntry(MountPointType mountPointType, String key) {
        boolean success = true;

            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            boolean mPointExists = currentKValue.containsKey(key);
            if ( mPointExists) {
                currentKValue.remove(key);
                mountPointMap.put(mountPointType, currentKValue);
                mountPointMapHasBeenChanged = true;

            } else {
                success = false;
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
     * verifies the validity of paths and the presence of at least one mounting point
     * checks if there are duplicate files
     * builds the error message
     *@return true if no error in mounting points
     * @see ConfigManager
     */

    public boolean verif() {
        errorMessage = null;
        errorFatal = false;

        StringBuilder message = new StringBuilder();
        if (noMountingPoints()) {
            message.append("No mounting points found \n");
            errorFatal = true;
        }

        if (!allPathsExist()) {
            message.append("Fatal error inside mounting points:\n");
            for (String invalidPath : invalidPaths) {
                message.append("The path: ").append(invalidPath).append(" is not valid").append("\n");
            }
            errorFatal = true;

        }
       // Checks if some mounting points share the same label
        ArrayList<String> nonUniqueLabel = getNonUniqueLabels(mountPointMap);
        if (nonUniqueLabel.size() != 0) {
            errorFatal = true;
            for (String label : nonUniqueLabel) {
                message.append("The label: ").append(label).append(" is not unique\n");
            }
        }

       // Builds the warning message
        ArrayList<String> listOfDuplicates = getDuplicatePaths();
        if (listOfDuplicates.size() != 0) {
            message.append("------- Warning ---------\n");
            message.append("Some mounting points share the same path: \n");
            for (String path : listOfDuplicates) {
                message.append(path).append("\n");
            }
        }

       // Checks if the default mounting points are present

        if (!defaultMountPointsExist() && !noMountingPoints()) {
            message.append("Minor error missing default mounting point : \n");
            for (String missingMP : missingMountPoints) {
                message.append(missingMP).append("\n");
            }
        }

        if (message.length() > 0) {
            errorMessage = message.toString();

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
     *@return true if no mountPoints present
     */

    public boolean noMountingPoints() {

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            // skips raw
            if (!mountPointType.equals(MountPointType.RAW)) {
                if (mountPointMap.get(mountPointType) != null) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * check if all paths inside mountpointmap exist and store wrong paths in an arraylist invalidPaths
     *
     * @return true if all paths are valid
     */

    public boolean allPathsExist() {

        invalidPaths.clear();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> mountPointMap = this.mountPointMap.get(mountPointType);
            if (mountPointMap != null) {
                for (String key : mountPointMap.keySet()) {
                    Path pathToTest = Paths.get(mountPointMap.get(key));

                    if (!pathToTest.isAbsolute()) {
                        Path cortexPath = ProlineFiles.CORTEX_DIRECTORY.toPath();
                        pathToTest = cortexPath.resolve(pathToTest);
                    }

                    boolean pathPresent = Files.exists(pathToTest);
                    boolean pathIsEmptyString = pathToTest.toString().equals("");
                    if (!pathPresent) {
                        invalidPaths.add(mountPointMap.get(key));
                    } else if (pathIsEmptyString) {
                        invalidPaths.add("the path corresponding to label: "+key+" is an empty string");

                    }
                }
            }
        }
        return invalidPaths.isEmpty();
    }


    // Does not check raw mountpoints
    public boolean defaultMountPointsExist() {

        missingMountPoints.clear();

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            Map<String, String> specificMPEntries = mountPointMap.get(mountPointType);
            //Raw mount Points are not evaluated for now
            if (!mountPointType.equals(MountPointType.RAW)) {
                if (specificMPEntries == null) {
                    missingMountPoints.add(mountPointType.getDisplayString());
                } else if (!specificMPEntries.containsKey(getMountPointDefaultPathLabel(mountPointType))) {

                    missingMountPoints.add(mountPointType.getDisplayString());
                }

            }
        }
        return missingMountPoints.isEmpty();
    }

    public List<String> getInvalidPaths() {
        return invalidPaths;
    }


    /**
     * Builds an array with all paths inside mountpoints + fasta folders
     *
     */
    public ArrayList<String> filesPaths() {
        ArrayList<String> pathsPresent = new ArrayList<>();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> MountPoints = mountPointMap.get(mountPointType);
            if (MountPoints != null) {
                Set<Map.Entry<String, String>> test = MountPoints.entrySet();
                for (Map.Entry<String, String> entries : test) {
                    String path = entries.getValue();
                    pathsPresent.add(path);
                }
            }
        }
        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        if (fastaToBeDisplayed != null) {
            pathsPresent.addAll(fastaToBeDisplayed);
        }
        return pathsPresent;
    }




    /**
     * @param paths a particular list of paths
     *@return duplicate paths among the list
     *
     */
    private ArrayList<String> duplicateFilesBuilder(ArrayList<String> paths) {
        ArrayList<String> duplicates = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            String pathI = paths.get(i);
            if (pathI != null) {
                for (int j = i + 1; j < paths.size(); j++) {
                    String pathJ = paths.get(j);
                    if (pathJ != null) {
                        if (pathI.equals(pathJ)) {
                            if (!duplicates.contains(pathI)) {
                                duplicates.add(pathI);
                            }
                        }
                    }
                }
            }
        }
        return duplicates;
    }


    public ArrayList<String> getDuplicatePaths() {
        ArrayList<String> paths = filesPaths();
        return duplicateFilesBuilder(paths);
    }


    public ArrayList<String> getNonUniqueLabels(HashMap<MountPointUtils.MountPointType, Map<String, String>> hashMap) {
        ArrayList<String> nonUniqueValues = new ArrayList<>();
        Map<String, Integer> keyCounts = new HashMap<>();

        // Counts the number of occurrences of labels
        for (Map<String, String> innerMap : hashMap.values()) {
            for (String key : innerMap.keySet()) {
                keyCounts.put(key, keyCounts.getOrDefault(key, 0) + 1);
            }
        }

        // Select labels when a label appears twice or more
        for (Map.Entry<MountPointUtils.MountPointType, Map<String, String>> entry : hashMap.entrySet()) {

            Map<String, String> innerMap = entry.getValue();

            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                String key = innerEntry.getKey();

                if (keyCounts.get(key) > 1) {
                    if (!nonUniqueValues.contains(key)) {
                        nonUniqueValues.add(key);
                    }
                }
            }
        }

        return nonUniqueValues;
    }

    /**
     * Patch that handles case where inside same mount point, the 2 default labels are present
     * (mzdb+msacot) update the labels by adding a character
     * @implNote To be decided if should be used or not?
     *
     */
    private void defaultMountPointRenamer() {

        for (Map.Entry<MountPointUtils.MountPointType, Map<String, String>> entry : mountPointMap.entrySet()) {
            MountPointUtils.MountPointType mountPointType = entry.getKey();
            Map<String, String> innerMap = entry.getValue();

            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                String key = innerEntry.getKey();
                boolean case1 = (key.equals(ProlineFiles.USER_CORTEX_RESULT_FILES_POINT) && mountPointType.equals(MountPointType.MZDB));
                boolean case2 = (key.equals(ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT) && mountPointType.equals(MountPointType.RESULT));
                if (case1 || case2) {

                    Map<String, String> currentKValue = mountPointMap.get(mountPointType);
                    String path = currentKValue.get(key);
                    currentKValue.remove(key);
                    String newKey = key + "2";
                    currentKValue.put(newKey, path);

                    mountPointMap.put(mountPointType, currentKValue);
                    mountPointMapHasBeenChanged = true;
                    break;
                }

            }
        }


    }






}


