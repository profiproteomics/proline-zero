package fr.proline.zero.util;


import java.util.ArrayList;
import java.util.List;


public class ParsingRule implements Cloneable {
    private String name;
    private List<String> fastaNameRegExp;
    private String fastaVersionRegExp;
    private String proteinAccRegExp;


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

    }

    @Override
    public String toString() {
        return "ParsingRule{" +
                "name='" + name + '\'' +
                ", fastaNameRegExp=" + fastaNameRegExp +
                ", fastaVersionRegExp='" + fastaVersionRegExp + '\'' +
                ", proteinAccRegExp='" + proteinAccRegExp + '\'' +
                '}';
    }


   @Override
   public ParsingRule clone() {
       try {
           ParsingRule clone = (ParsingRule) super.clone();

           clone.fastaNameRegExp = new ArrayList<>(fastaNameRegExp);
           clone.name=name;
           clone.fastaVersionRegExp=fastaVersionRegExp;
           clone.proteinAccRegExp=proteinAccRegExp;

           return clone;
       } catch (CloneNotSupportedException e) {
           throw new AssertionError();
       }
   }

    public ParsingRule(ParsingRule parsingRule) {
        this.fastaNameRegExp = parsingRule.fastaNameRegExp;
        this.name= parsingRule.name;
        this.fastaVersionRegExp=parsingRule.fastaVersionRegExp;
        this.proteinAccRegExp=parsingRule.proteinAccRegExp;

    }
}


