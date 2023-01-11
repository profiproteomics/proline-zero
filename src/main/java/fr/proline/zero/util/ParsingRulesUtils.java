package fr.proline.zero.util;

import java.util.*;


public class ParsingRulesUtils {


    private ArrayList<ParsingRule> setOfRules;

    private boolean parseRulesHasBeenChanged = false;


    public ParsingRulesUtils() {
        setOfRules = getSetOfRules();
    }

    public ArrayList<ParsingRule> getSetOfRules() {
        return JsonParsingrules.getInstance().getSetOfRules();
    }

    public boolean isParseRulesHasBeenChanged() {
        return parseRulesHasBeenChanged;
    }

    public enum ParsingRulesKeys {

        NAME("name"), FASTA_NAME("fasta-name"), FASTA_VERSION("fasta-version"), PROTEIN("protein-accession");

        private final String jsonKey;

        public String getJsonKey() {
            return jsonKey;
        }

        ParsingRulesKeys(String jsonKey) {
            this.jsonKey = jsonKey;

        }


    }

    public boolean addNewRule(ParsingRule newRule) {
        // TODO check if adding is possible
        setOfRules.add(newRule);
        parseRulesHasBeenChanged = true;
        return true;
    }

    public boolean dellRule(ParsingRule ruleToBeDeleted) {
        setOfRules.remove(ruleToBeDeleted);
        parseRulesHasBeenChanged = true;
        return true;
    }
    public void restoreParseRules(){
        setOfRules=JsonParsingrules.getInstance().getSetOfRules();
    }


    public void updateConfigFileParseRules() {
        JsonParsingrules.getInstance().updateConfigFileParseRules(setOfRules);
    }

    public void test() {
        ArrayList<String> tobeadded = new ArrayList<>();
        tobeadded.add("fasta");
        tobeadded.add("fasta2");
        ParsingRule pr = new ParsingRule("toto", tobeadded, "tata", "bb");
        addNewRule(pr);
        updateConfigFileParseRules();
       // dellRule(pr);
       // updateConfigFileParseRules();
        restoreParseRules();
        updateConfigFileParseRules();
    }


}





