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


    public static Object[] getMatchingParsingRule(final String fastaFileName) {
        assert (fastaFileName != null) : "getParsingRuleEntry() fastaFileName is null";
        // resultObject stores the parsing rule and the fasta name regex that match with the name of the file
        Object[] resultObject=new Object[2];


        for (ParsingRule nextPR : ConfigManager.getInstance().getParsingRulesManager().getSetOfRules()) {
            for (String fastaRegEx : nextPR.getFastaNameRegExp()) {
                final Pattern pattern = Pattern.compile(fastaRegEx, Pattern.CASE_INSENSITIVE);

                final Matcher matcher = pattern.matcher(fastaFileName);
                if (matcher.find()) {
                    resultObject[0]=nextPR;
                    resultObject[1]=fastaRegEx;
                    // loop ends as soon as a fastaRegex match with the name of the file
                    break;
                }

            } // End loop for each regex
            if (resultObject != null)
                // loop ends at first match
                break;
        }

        return resultObject;
    }
}
