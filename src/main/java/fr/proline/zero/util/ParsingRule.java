package fr.proline.zero.util;

import java.util.ArrayList;

public class ParsingRule {
    private String name;
    private ArrayList<String> fasta_name;
    private String fasta_version;
    private String protein;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFasta_name() {
        return fasta_name;
    }

    public void setFasta_name(ArrayList<String> fasta_name) {
        this.fasta_name = fasta_name;
    }

    public String getFasta_version() {
        return fasta_version;
    }

    public void setFasta_version(String fasta_version) {
        this.fasta_version = fasta_version;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }




    public ParsingRule(String name, ArrayList<String> fasta_name, String fasta_version, String protein) {
        this.name = name;
        this.fasta_name = fasta_name;
        this.fasta_version = fasta_version;
        this.protein = protein;
    }
}
