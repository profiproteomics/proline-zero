package fr.proline.zero.util;

import fr.proline.zero.gui.Popup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


public class ParsingRulesUtils {

    private String defaultProteinAccRule = null;

    private List<ParsingRule> setOfRules;
    private List<String> fastaPaths;

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
        if (!fastaPathsAreValid()) {
            errorFatal = true;
            if (invalidFastaPaths.size() > 1) {
                message.append("Some paths inside seq repo configuration file are not valid: \n");


                for (String invalidPath : invalidFastaPaths) {
                    message.append(invalidPath);
                    message.append("\n");
                }
            } else {
                message.append("One path is not valid: \n");
                message.append(invalidFastaPaths.get(0));
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
        if (Config.getSeqRepActive())
        {  invalidFastaPaths = new ArrayList<>();
        for (String fastaPaths : fastaPaths) {

            Path pathToTest = Paths.get(fastaPaths);
            if (!Files.exists(pathToTest)) {
                invalidFastaPaths.add(fastaPaths);
            }
            if (pathToTest.toString().equals("")) {
                invalidFastaPaths.add("a fasta folder path  is an empty string");
            }
        }
        return invalidFastaPaths.isEmpty();}
        else {
            return true;
        }
    }

    public List<String> getInvalidFastaPaths() {
        return invalidFastaPaths;
    }

    private boolean noParsingRule() {
        return setOfRules.isEmpty();
    }


}





