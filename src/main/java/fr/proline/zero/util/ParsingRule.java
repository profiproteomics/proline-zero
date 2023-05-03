package fr.proline.zero.util;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingRule {
    private String name;
    private List<String> fastaNameRegExp;
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

    public List<String> getFastaNameRegExp() {
        return fastaNameRegExp;
    }

    public void setFastaNameRegExp(List<String> fastaNameRegExp) {
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


    public ParsingRule(String name, List<String> fastaNameRegexp, String fastaVersionRegExp, String proteinAccRegExp) {
        this.name = name;
        this.fastaNameRegExp = fastaNameRegexp;
        this.fastaVersionRegExp = fastaVersionRegExp;
        this.proteinAccRegExp = proteinAccRegExp;
        // to be removed useless;
        this.isEditable = false;
    }
}


