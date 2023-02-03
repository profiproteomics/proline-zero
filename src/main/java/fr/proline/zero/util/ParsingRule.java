package fr.proline.zero.util;

import java.util.ArrayList;

public class ParsingRule {
    private String name;
    private ArrayList<String> fastaNameRegExp;
    private String fastaVersionRegExp;
    private String proteinAccRegExp;
    private boolean isEditable;

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFastaNameRegExp() {
        return fastaNameRegExp;
    }

    public void setFastaNameRegExp(ArrayList<String> fastaNameRegExp) {
        this.fastaNameRegExp = fastaNameRegExp;
    }

    public String getFastaVersionRegExp() {
        return fastaVersionRegExp;
    }

    public void setFastaVersionRegExp(String fastaVersionRegExp) {
        this.fastaVersionRegExp = fastaVersionRegExp;
    }

    public String getProteinAccRegExp() {
        return proteinAccRegExp;
    }

    public void setProteinAccRegExp(String proteinAccRegExp) {
        this.proteinAccRegExp = proteinAccRegExp;
    }




    public ParsingRule(String name, ArrayList<String> fastaNameRegexp, String fastaVersionRegExp, String proteinAccRegExp) {
        this.name = name;
        this.fastaNameRegExp = fastaNameRegexp;
        this.fastaVersionRegExp = fastaVersionRegExp;
        this.proteinAccRegExp = proteinAccRegExp;
        // to be removed useless
        this.isEditable=false;
    }
}
