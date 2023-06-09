
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
    private HashMap<MountPointUtils.MountPointType, Map<String, List<String>>> mountPointVerif;

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
    private ArrayList<String> missingMountPoints = new ArrayList<>();

    private ArrayList<String> duplicatePaths = new ArrayList<>();

    private ArrayList<String> duplicateInsideSameMountPoint = new ArrayList<>();


    public MountPointUtils() {

        mountPointMap = JsonCortexAccess.getInstance().getMountPointMaps();

        defaultMountPointRenamer(mountPointMap);

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

        if (mountPointMap.get(mountPointType) == null && !labelExists(value)) {
            Map<String, String> initialMap = new HashMap<>();
            initialMap.put(value, path);
            mountPointMap.put(mountPointType, initialMap);
            mountHasBeenChanged = true;
            return true;
        } else if (!labelExists(value) && !pathExistsV2(path, mountPointType)) {
            Map<String, String> currentMountPoint = mountPointMap.get(mountPointType);
            currentMountPoint.put(value, path);
            mountPointMap.put(mountPointType, currentMountPoint);
            mountHasBeenChanged = true;
            return true;
        } else {
            return false;
        }


    }

    /**
     * First version that checks in all mountPoints plus fasta folders if path already exists
     * used only when adding a fasta
     *
     * @param path
     * @return
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

    /**
     * New version that checks only in the same mountPointType plus inside fasta folder
     *
     * @param path
     * @param mountPointType
     * @return
     */
    private boolean pathExistsV2(String path, MountPointType mountPointType) {
        boolean pathExistsInMountpoints = false;
        boolean pathExistsInFastas = false;
        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();


        Map<String, String> map = mountPointMap.get(mountPointType);
        if (map != null && map.containsValue(path)) {
            pathExistsInMountpoints = true;

        }

        if (fastaToBeDisplayed != null) {
            pathExistsInFastas = fastaToBeDisplayed.contains(path);
        }


        return pathExistsInFastas || pathExistsInMountpoints;

    }

    public boolean getIfPathExist(String path) {
        return pathExists(path);
    }

    public boolean getIfPathExistInAMountPoint(String path, MountPointType mountPointType) {
        return pathExistsV2(path, mountPointType);
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

            } else {
                success = false;
            }
        }
        if (forced) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);

            currentKValue.remove(key);

            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged = true;


        }
        return success;
    }
    public void renameMountPointEntry(MountPointType mountPointType, String key,String newKey) {
        boolean success = true;

        Map<String, String> currentKValue = mountPointMap.get(mountPointType);
        String path=currentKValue.get(key);
        currentKValue.remove(key);

        currentKValue.put(newKey, path);

        mountPointMap.put(mountPointType, currentKValue);
        mountHasBeenChanged = true;

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
        errorFatal = false;

        StringBuilder message = new StringBuilder();
        if (noMountingPoints()) {
            message.append("No mounting points, please add at least one \n");
            errorFatal = true;

        }
        /**
         * Checks if some mounting points have same labels
         */
        ArrayList<String> nonUniqueLabel=getNonUniqueLabels(mountPointMap);
        if (nonUniqueLabel.size()!=0) {
            errorFatal=true;
            message.append("Fatal error :\n");
            for (String label: nonUniqueLabel){
                message.append("The label: ").append(label).append(" is not unique\n");
            }

        }


        if (!allPathsExist()) {
            if (invalidPaths.size() == 1) {
                message.append("The following path does not exist: \n");
            } else {
                message.append("The following paths do not exist: \n");
            }
            for (String invalidPath : invalidPaths) {
                message.append(invalidPath).append("\n");
            }
            errorFatal = true;

        }
        /**
         * Check if some path are identical among same mount points
         */
        if (getDuplicatesInsideMountPoint().size() != 0) {

            errorFatal = true;
            message.append("---- ERROR -----\n");
            message.append("Some paths are identical inside same mountPoint:\n");
            for (String duplicates : duplicateInsideSameMountPoint) {
                message.append(duplicates).append("\n");
            }
        }

        /**
         * Checks everywhere if paths are identical among different mount
         * points
         * not a fatal error
         */
        ArrayList<String> listOfDuplicateFilesExterior= getDuplicatesExteriorToMountPoints();

        if (listOfDuplicateFilesExterior.size()!=0){
            message.append("------- Warning ---------\n");
            message.append("Some paths in different mount points are identical\n");
            for (String paths: listOfDuplicateFilesExterior){
                message.append(paths).append("\n");

            }


        }
        /**
         * Checks if a mountPoint by default is missing
         * not a fatal error
         */
        if (!defaultMountPointsExist() && !errorFatal) {
            message.append("Minor error missing default mounting point : \n");
            for (String missingMP : missingMountPoints) {
                message.append(missingMP).append("\n");
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

    public boolean noMountingPoints() {

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            if (!mountPointType.equals(MountPointType.RAW)) {
                if (mountPointMap.get(mountPointType) != null) {
                    return false;
                }
            }
        }
        return true;
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
    public boolean defaultMountPointsExist() {

        missingMountPoints.clear();

        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {

            Map<String, String> specificMPEntries = mountPointMap.get(mountPointType);
            // CD   Raw mount Points are not evaluated
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

    public List<String> getMissingMountPoints() {
        return missingMountPoints;
    }

    /**
     * Builds an array with all paths inside mountpoints + fasta folders
     *
     * @return
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

    public ArrayList<String> buildFilesPaths(MountPointType mountPointType) {
        ArrayList<String> pathsPresent = new ArrayList<>();

        Map<String, String> MountPoints = mountPointMap.get(mountPointType);
        if (MountPoints != null) {
            Set<Map.Entry<String, String>> test = MountPoints.entrySet();
            for (Map.Entry<String, String> entries : test) {
                String path = entries.getValue();
                pathsPresent.add(path);
            }
        }

        return pathsPresent;
    }





    public boolean duplicatePathInsideArray(ArrayList<String> listOfPath) {
        duplicatePaths.clear();


        for (int i = 0; i < listOfPath.size(); i++) {
            String pathi = listOfPath.get(i);
            for (int j = i + 1; j < listOfPath.size(); j++) {

                String pathj = listOfPath.get(j);

                if (pathi.equals(pathj)) {
                    if (!duplicatePaths.contains(pathi)) {
                        duplicatePaths.add(pathi);
                    }

                }
            }

        }
        return !duplicatePaths.isEmpty();
    }


    /**
     * Takes in argument a list of paths and returns duplicate paths among the list
     *
     * @param paths
     * @return
     */
    private ArrayList<String> duplicateFilesBuilder(ArrayList<String> paths) {
        ArrayList<String> duplicates = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            String pathI = paths.get(i);
            for (int j = i + 1; j < paths.size(); j++) {

                String pathJ = paths.get(j);

                if (pathI.equals(pathJ)) {
                    if (!duplicates.contains(pathI)) {
                        duplicates.add(pathI);
                    }

                }
            }
        }
        return duplicates;
    }

    /**
     * check among all mountpoints if any duplicate path
     *
     * @return
     */
    public ArrayList<String> getDuplicatePaths() {
        ArrayList<String> paths = filesPaths();
        return duplicateFilesBuilder(paths);
    }

    public ArrayList<String> getDuplicatePathsPerMountPoint(MountPointType mountPointType) {
        ArrayList<String> paths = buildFilesPaths(mountPointType);
        return duplicateFilesBuilder(paths);
    }
    public ArrayList<String> getDuplicateExteriorToMountPoints(){
        return getDuplicatesExteriorToMountPoints();
    }

    public ArrayList<String> getDuplicateAmongFastas() {
        List<String> fastaFiles = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        return duplicateFilesBuilder((ArrayList<String>) fastaFiles);
    }

    /**
     * Simply add duplicate paths from different mount points plus fasta
     * @return
     */
    private ArrayList<String> getDuplicatesInsideMountPoint() {

        duplicateInsideSameMountPoint.clear();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            ArrayList<String> duplicateInAMountPointType = getDuplicatePathsPerMountPoint(mountPointType);
            for (String paths : duplicateInAMountPointType) {
                duplicateInsideSameMountPoint.add(paths);
            }
        }
        ArrayList<String> fastaDuplicates = getDuplicateAmongFastas();
        for (String fastaPaths : fastaDuplicates) {
            duplicateInsideSameMountPoint.add(fastaPaths);
        }

        return duplicateInsideSameMountPoint;

    }

    /**
     * Gets the paths identical across different mont points
     * @return
     */
    private ArrayList<String> getDuplicatesExteriorToMountPoints(){

        ArrayList<String> globalDuplicates=getDuplicatePaths();
        ArrayList<String> duplicateInsideMountPoints= getDuplicatesInsideMountPoint();
        ArrayList<String> finalDuplicates=new ArrayList<>();
        for (String path : globalDuplicates){
            if (!duplicateInsideMountPoints.contains(path)){
                if (!finalDuplicates.contains(path)){
                    finalDuplicates.add(path);
                }
            }
        }
        return finalDuplicates;

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
           MountPointUtils.MountPointType mountPointType = entry.getKey();
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
     * (mzdb+msacot) rename them directly
     *
     * @param hashMap
     */
    public  void defaultMountPointRenamer(HashMap<MountPointUtils.MountPointType, Map<String, String>> hashMap) {

        for (Map.Entry<MountPointUtils.MountPointType, Map<String, String>> entry : hashMap.entrySet()) {
            MountPointUtils.MountPointType mountPointType = entry.getKey();
            Map<String, String> innerMap = entry.getValue();

            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                String key = innerEntry.getKey();
                boolean name1=(key.equals(ProlineFiles.USER_CORTEX_RESULT_FILES_POINT)&&mountPointType.equals(MountPointType.MZDB));
                boolean name2=(key.equals(ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT)&&mountPointType.equals(MountPointType.RESULT));
                if (name1||name2) {

                    Map<String, String> currentKValue = mountPointMap.get(mountPointType);
                    String path=currentKValue.get(key);
                    currentKValue.remove(key);
                    String newKey=key+"2";
                    currentKValue.put(newKey, path);

                    mountPointMap.put(mountPointType, currentKValue);
                    mountHasBeenChanged = true;
                    break;
                }

            }
        }


    }



    /**
     * Same as above but retrieves paths corresponding to dupicate labels
     * @param hashMap
     * @return
     */
    public  ArrayList<String> getPathsWithNonUniqueLabels(HashMap<MountPointUtils.MountPointType, Map<String, String>> hashMap) {
        ArrayList<String> nonUniqueValues = new ArrayList<>();
        Map<String, Integer> keyCounts = new HashMap<>();

        // Counts the number of occurences of labels
        for (Map<String, String> innerMap : hashMap.values()) {
            for (String key : innerMap.keySet()) {
                keyCounts.put(key, keyCounts.getOrDefault(key, 0) + 1);
            }
        }

        // select path when label appear twice or more
        for (Map<String, String> innerMap : hashMap.values()) {
            for (Map.Entry<String, String> entry : innerMap.entrySet()) {
                String key = entry.getKey();
                if (keyCounts.get(key) > 1) {
                    if (!nonUniqueValues.contains(key))
                        nonUniqueValues.add(entry.getValue());
                }
            }
        }

        return nonUniqueValues;
    }



}


