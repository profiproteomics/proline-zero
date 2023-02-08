package fr.proline.zero.util;

import fr.proline.zero.gui.Popup;

import java.util.*;


public class ParsingRulesUtils {


    private ArrayList<ParsingRule> setOfRules;
    private ArrayList<String> fastaPaths;

    private boolean parseRulesAndFastaHasBeenChanged = false;

    public boolean isLabelExists() {
        return labelExists;
    }

    public void setLabelExists(boolean labelExists) {
        this.labelExists = labelExists;
    }

    private boolean labelExists;
    private String errorMessage;
    private boolean errorFatal;

    public boolean isErrorFatal() {

        return errorFatal;
    }

    public void setErrorFatal(boolean errorFatal) {
        this.errorFatal = errorFatal;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ParsingRulesUtils() {
        setOfRules = JsonSeqRepoAccess.getInstance().getSetOfRules();
        fastaPaths = JsonSeqRepoAccess.getInstance().getFastaDirectories();
    }

    public ArrayList<ParsingRule> getSetOfRules() {
        return setOfRules;
    }

    public ArrayList<String> getFastaPaths() {
        return fastaPaths;
    }


    public boolean isParseRulesAndFastaHasBeenChanged() {
        return parseRulesAndFastaHasBeenChanged;
    }


    public boolean addNewRule(ParsingRule newRule) {
        // TODO add other verifications?
        String label = newRule.getName();
        if (!labelExists(label)) {
            setLabelExists(false);
            setOfRules.add(newRule);
            parseRulesAndFastaHasBeenChanged = true;
            return true;

        } else {

            setLabelExists(true);
            Popup.warning("label already exists! please choose another label");
            return false;
        }

    }

    public boolean labelExistsOLD(String labelToBeAdded) {
        boolean labelExists = false;
        for (int k = 0; k < setOfRules.size(); k++) {
            String label = setOfRules.get(k).getName();
            if (label.equals(labelToBeAdded)) {
                labelExists = true;
                break;
            }
        }
        return labelExists;
    }
    public boolean labelExists(String labelToBeAdded) {
        for (ParsingRule parsingRule : setOfRules) {
            if (parsingRule.getName().equals(labelToBeAdded)) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteRule(ParsingRule ruleToBeDeleted) {
        parseRulesAndFastaHasBeenChanged = true;
        return setOfRules.remove(ruleToBeDeleted);
    }

    //used to check if path is not already in fasta directories or in mounting points
    public boolean canBeAdded(String path) {
        boolean pathExists = false;
        // check inside mounting points
        HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointHashMap = ConfigManager.getInstance().getMountPointManager().getMountPointMap();
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            if ((mountPointHashMap.get(mountPointType) != null) && (mountPointHashMap.get(mountPointType).containsValue(path))) {
                pathExists = true;
                break;
            }
        }
        // check inside fasta directories
        for (int k = 0; k < fastaPaths.size(); k++) {
            if (fastaPaths.get(k).equals(path)) {
                pathExists = true;
                break;
            }
        }

        return !pathExists;
    }


    public boolean addFastaFolder(String path) {
        boolean addOk = false;
        if (canBeAdded(path)) {
            fastaPaths.add(path);
            parseRulesAndFastaHasBeenChanged = true;
            addOk = true;
        }
        return addOk;
    }

    public boolean deleteFastaFolder(String path) {
        parseRulesAndFastaHasBeenChanged = true;
        return fastaPaths.remove(path);
    }

    // To be deleted?
    public void restoreParseRules() {
        setOfRules = JsonSeqRepoAccess.getInstance().getSetOfRules();
    }

    // To be deleted?
    public void restoreFastaDirectories() {
        fastaPaths = JsonSeqRepoAccess.getInstance().getFastaDirectories();
    }

    public void restoreParseRulesAndFastas() {
        setOfRules = JsonSeqRepoAccess.getInstance().getSetOfRules();
        fastaPaths = JsonSeqRepoAccess.getInstance().getFastaDirectories();


    }

    public void updateConfigFileParseRulesAndFasta() {
        JsonSeqRepoAccess.getInstance().updateConfigRulesAndFasta(fastaPaths, setOfRules);
    }

    public boolean verif() {
        errorMessage = null;
        errorFatal = false;
        StringBuilder message = new StringBuilder();
        // TODO implement verifications
        //message.append(errorMessage);
        return message.length() == 0;
    }


}





