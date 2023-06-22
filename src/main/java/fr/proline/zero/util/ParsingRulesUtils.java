package fr.proline.zero.util;

import fr.proline.zero.gui.Popup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * This class handles operations on parsing rules (CRUD)
 * implements also verifications (uniqueness..)
 *
 * @see ConfigManager
 */
public class ParsingRulesUtils {


    private String defaultProteinAccRule;

    private List<ParsingRule> setOfRules;
    private List<String> fastaPaths;
    // to do rename
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
    private List<String> invalidFastaPaths;


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
        defaultProteinAccRule = JsonSeqRepoAccess.getInstance().getDefaultProtein();

    }

    public String getDefaultProteinAccRule() {
        return defaultProteinAccRule;
    }

    public List<ParsingRule> getSetOfRules() {
        return setOfRules;
    }

    public List<String> getFastaPaths() {
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


    public ParsingRule updateParsingRule(ParsingRule ruleModified, String name, String fastaVersionRegExp, String proteinAccRegExp, List<String> fastaNameRegExp) {
        ruleModified.setName(name);
        ruleModified.setFastaNameRegExp(fastaNameRegExp);
        ruleModified.setProteinAccRegExp(proteinAccRegExp);
        ruleModified.setFastaVersionRegExp(fastaVersionRegExp);
        return ruleModified;
    }

    public void updateSetOfRules(int index, ParsingRule newParsingRule) {

        setOfRules.set(index, newParsingRule);
        parseRulesAndFastaHasBeenChanged = true;

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
        // int test=setOfRules.indexOf(ruleToBeDeleted);
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
        return !pathExists && !fastaPaths.contains(path);
    }

    /**
     *
     *@param path
     * @param forced
     *
     */
    public boolean addFastaFolder(String path) {

        fastaPaths.add(path);
        parseRulesAndFastaHasBeenChanged = true;
        return true;
    }

    public boolean deleteFastaFolder(String path) {
        parseRulesAndFastaHasBeenChanged = true;
        return fastaPaths.remove(path);
    }

    /**
     * Used while editing fasta folders
     *
     * @param oldPath
     * @param newPath
     *
     */
    public boolean updateFastaFolder(String oldPath, String newPath) {
        int oldIndex = fastaPaths.indexOf(oldPath);
        fastaPaths.set(oldIndex, newPath);
        parseRulesAndFastaHasBeenChanged = true;
        return true;

    }

    public void restoreParseRulesAndFastas() {
        setOfRules = JsonSeqRepoAccess.getInstance().getSetOfRules();
        fastaPaths = JsonSeqRepoAccess.getInstance().getFastaDirectories();
        defaultProteinAccRule = JsonSeqRepoAccess.getInstance().getDefaultProtein();


    }


    public void updateConfigFileParseRulesAndFasta() {
        // JsonSeqRepoAccess.getInstance().updateConfigRulesAndFasta(fastaPaths, setOfRules);
        JsonSeqRepoAccess.getInstance().updateConfigRulesAndFasta(fastaPaths, setOfRules, defaultProteinAccRule);
    }


    public boolean verif() {
        errorMessage = null;
        errorFatal = false;
        StringBuilder message = new StringBuilder();
        // TODO implement verifications
        if (!fastaPathsAreValid()) {
            errorFatal = true;
            message.append("Fatal error inside Sequence repository file: \n");
            for (String invalidPath : invalidFastaPaths) {
                message.append("Fasta file: " + invalidPath + " is not valid");
                message.append("\n");
            }

        }
        if (noParsingRule()) {
            message.append("\n Minor error: \n");
            message.append("There is no current Parsing rule \n");
        }
        if (message.length() > 0) {
            errorMessage = message.toString();

        }
        return message.length() == 0;
    }

    private boolean fastaPathsAreValid() {

        invalidFastaPaths = new ArrayList<>();
        for (String fastaPaths : fastaPaths) {

            Path pathToTest = Paths.get(fastaPaths);
            if (!Files.exists(pathToTest)) {
                invalidFastaPaths.add(fastaPaths);
            }
            if (pathToTest.toString().equals("")) {
                invalidFastaPaths.add("a fasta folder path  is an empty string");
            }
        }
        return invalidFastaPaths.isEmpty();

    }

    public List<String> getInvalidFastaPaths() {
        return invalidFastaPaths;
    }

    private boolean noParsingRule() {
        return setOfRules.isEmpty();
    }

    public void setProteinByDefault(String newProteinByDefault) {
        defaultProteinAccRule = newProteinByDefault;
        parseRulesAndFastaHasBeenChanged = true;
    }


}





