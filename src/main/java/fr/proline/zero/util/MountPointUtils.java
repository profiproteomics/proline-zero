
package fr.proline.zero.util;


import com.typesafe.config.*;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
        mountPointMap = JsonAccess.getInstance().getMountPointMaps();
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

   /* public static String getMountPointDefaultPathLabel2(MountPointType mpt) {
        String valueToReturn =null;
        if (mpt.equals(MountPointType.RAW)) {
            valueToReturn = ProlineFiles.USER_CORTEX_RAW_FILES_MOUNT_POINT;
        }
        if (mpt.equals(MountPointType.RESULT)) {
            valueToReturn = ProlineFiles.USER_CORTEX_RESULT_FILES_POINT;
        }
        if (mpt.equals(MountPointType.MZDB)) {
            valueToReturn = ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT;
        }
        return valueToReturn;
    }*/
    public static String getMountPointDefaultPathLabel(MountPointType mpt) {
        //String valueToReturn =null;
        if (mpt.equals(MountPointType.RAW)) {
            return ProlineFiles.USER_CORTEX_RAW_FILES_MOUNT_POINT;
        }
       else if (mpt.equals(MountPointType.RESULT)) {
            return ProlineFiles.USER_CORTEX_RESULT_FILES_POINT;
        }

        else {
            return ProlineFiles.USER_CORTEX_MZDB_MOUNT_POINT;
        }

    }

    /*public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {
        boolean succes = false;
        if (mountPointMap.get(mountPointType) == null) {
            Map<String, String> initialMap = new HashMap<>();
            initialMap.put(value, path);
            mountPointMap.put(mountPointType, initialMap);
            mountHasBeenChanged = true;
            succes = true;
        } else if (!labelExists(value) && !pathExists(path)) {
            Map<String, String> currentKValue = mountPointMap.get(mountPointType);
            currentKValue.put(value, path);
            mountPointMap.put(mountPointType, currentKValue);
            mountHasBeenChanged = true;
            succes = true;
        }

        return succes;
    }*/
    public boolean addMountPointEntry(MountPointType mountPointType, String value, String path) {

        if (mountPointMap.get(mountPointType) == null) {
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
        }
        else {
            return false;
        }


    }

    private boolean pathExists(String path) {
        boolean pathExists = false;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            if ((mountPointMap.get(mountPointType) != null) && (mountPointMap.get(mountPointType).containsValue(path))) {
                pathExists = true;
                break;
            }
        }
        return pathExists;
    }

    private boolean labelExists(String value) {
        boolean exists = false;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            if ((mountPointMap.get(mountPointType) != null) && (mountPointMap.get(mountPointType).containsKey(value))) {
                exists = true;
                break;
            }
        }
        return exists;
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

    // called to delete a default mounting point when the path is invalid
    public boolean delMountPointEntryForced(MountPointType mountPointType, String key) {

        Map<String, String> currentKValue = mountPointMap.get(mountPointType);
        logger.info(currentKValue.toString());
        currentKValue.remove(key);
        logger.info(currentKValue.toString());
        mountPointMap.put(mountPointType, currentKValue);
        mountHasBeenChanged = true;

        return true;

    }

    public void restoreMountPoints() {

        mountPointMap = JsonAccess.getInstance().getMountPointMaps();
    }

    public void updateCortexConfigFile(HashMap<MountPointUtils.MountPointType, Map<String, String>> mpt) {

        ConfigObject toBePreserved = JsonAccess.getInstance().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts = MountPointUtils.MountPointType.values().length;
        com.typesafe.config.Config[] builtConfig = new com.typesafe.config.Config[sizeOfMpts];
        com.typesafe.config.Config[] mergedConf = new com.typesafe.config.Config[builtConfig.length];
        int cpt = 0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> specificMPEntries = mpt.get(mountPointType);
            if (specificMPEntries == null) {
                builtConfig[cpt] = ConfigFactory.empty().atKey(mountPointType.getJsonKey());
            } else {
                builtConfig[cpt] = ConfigValueFactory.fromMap(specificMPEntries).atKey(mountPointType.getJsonKey());
            }
            if (cpt == 0) {
                mergedConf[cpt] = builtConfig[cpt];
            } else {
                mergedConf[cpt] = mergedConf[cpt - 1].withFallback(builtConfig[cpt]);
            }
            cpt++;
        }

        com.typesafe.config.Config finalMpts = mergedConf[cpt - 1].atKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        // final merge
        Config finalConfig = finalMpts.withFallback(toBePreserved);
        String finalWrite = finalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        // configHasBeenChanged=true;

        try {
            FileWriter writer = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean verif() {
        errorMessage = null;
        //boolean isValid = true;
        errorFatal = false;
        StringBuilder message = new StringBuilder();
        if (!atLeastOneMpoint()) {
            message.append("No mounting points, please add at least one \n");
            errorFatal = true;

        } else if (!allPathsExist()) {
            if (invalidPaths.size() == 1) {
                message.append("\n The following path does not exist: \n");
            } else {
                message.append("\n The following paths do not exist: \n");
            }
            for (int i = 0; i < invalidPaths.size(); i++) {
                message.append("\n" + invalidPaths.get(i) + "\n");
            }
            errorFatal = true;
        }
        if (!defaultMptsExist() && atLeastOneMpoint() && allPathsExist()) {

            message.append("Minor error missing default mounting point : \n");
            for (int i = 0; i < missingMPs.size(); i++) {
                message.append(missingMPs.get(i) + "\n");
            }
            // no fatal error

        }
        if (message.length() > 0) {
            errorMessage = message.toString();
           // isValid = false;
        }

        System.out.println(message.length());

        return message.length()==0;


    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isErrorFatal() {

        return errorFatal;
    }


    // check if at least one mounting point is present
    public boolean atLeastOneMpoint() {
        ArrayList<String> mountPointsPresent = new ArrayList<>();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            if (mountPointMap.get(mountPointType) != null) {
                mountPointsPresent.add(mountPointType.getDisplayString());
            }
        }
        return !mountPointsPresent.isEmpty();
    }
    // check if all paths inside mountpointmap exist and store wrong paths in an arraylist wrongPaths

    public boolean allPathsExist() {

        invalidPaths.clear();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> temp = mountPointMap.get(mountPointType);
            if (temp != null) {
                for (String key : temp.keySet()) {
                    Path pathToTest = Paths.get(temp.get(key));
                    Boolean pathPresent = Files.exists(pathToTest);
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

    public ArrayList<String> getInvalidPaths() {
        return invalidPaths;
    }


}
