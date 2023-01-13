package fr.proline.zero.util;

import java.util.*;


public class ParsingRulesUtils {


    private ArrayList<ParsingRule> setOfRules;
    private ArrayList<String> fastaPaths;

    private boolean parseRulesHasBeenChanged = false;
    private boolean fastaDirectoriesHasBeenchanged=false;




    public ParsingRulesUtils() {
        setOfRules = SuperJson.getInstanceParseRules().getSetOfRules();
        fastaPaths=  SuperJson.getInstanceParseRules().getFastaPaths();
    }

    public ArrayList<ParsingRule> getSetOfRules() {
        return setOfRules;
    }

    public ArrayList<String> getFastaPaths() {
        return fastaPaths;
    }

    public boolean isParseRulesHasBeenChanged() {
        return parseRulesHasBeenChanged;
    }
    public boolean isFastaDirectoriesHasBeenchanged() {
        return fastaDirectoriesHasBeenchanged;
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

        parseRulesHasBeenChanged = true;
        return setOfRules.add(newRule);
    }

    public boolean dellRule(ParsingRule ruleToBeDeleted) {

        parseRulesHasBeenChanged=true;
        return setOfRules.remove(ruleToBeDeleted);
    }
    public boolean addFastaFolder(String path){

        fastaDirectoriesHasBeenchanged=true;
        return fastaPaths.add(path);
    }
    public boolean dellFastaFolder(String path){
        fastaDirectoriesHasBeenchanged=true;
        return fastaPaths.remove(path);
    }

    public void restoreParseRules() {
        setOfRules =SuperJson.getInstanceParseRules().getSetOfRules();
    }

    public void restoreFastaDirectories(){
        fastaPaths=  SuperJson.getInstanceParseRules().getFastaPaths();
    }


    public void updateConfigFileParseRules() {

        SuperJson.getInstanceParseRules().updateConfigFileParseRules(setOfRules);
    }

    public void updateConfigFileFastaDirectories(){
        SuperJson.getInstanceParseRules().updateConfigFastaDirectories(fastaPaths);
    }


   /* public ArrayList<ArrayList<String>> getFastaNames(ArrayList<ParsingRule> setOfRules){
        ArrayList<ArrayList<String>> fastaNames=new ArrayList<>();
        for (int i=0;i< setOfRules.size();i++){
            ParsingRule pr=setOfRules.get(i);
            fastaNames.add(i,pr.getFasta_name());
        }
        return fastaNames;

    }*/

    public void test() {
        ArrayList<String> tobeadded = new ArrayList<>();
        tobeadded.add("fasta");
        tobeadded.add("fasta2");
        ParsingRule pr = new ParsingRule("toto", tobeadded, "tata", "bb");
        addNewRule(pr);
        //updateConfigFileParseRules();

        // dellRule(pr);
        // updateConfigFileParseRules();
       // restoreParseRules();
       // updateConfigFileParseRules();
    }


}





