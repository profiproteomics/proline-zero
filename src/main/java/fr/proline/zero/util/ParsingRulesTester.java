package fr.proline.zero.util;

import fr.proline.module.seq.service.FastaPathsScanner;
import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.ResultOfGlobalTestDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.proline.module.seq.Constants.LATIN_1_CHARSET;

public class ParsingRulesTester {

    public ParsingRulesTester() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRulesTester.class);


    public void globalTest() throws Exception {

        System.out.println("test pressed");
        Map<String, List<File>> fastaPaths = getFastaFilesMap();
        Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
        // resultStore contains parsing rule selected if any plus fasta name regex plus name of file
        ArrayList<Object[]> resultStore = new ArrayList<>();
        // lineresults contains lines from fasta and protein extracted if any
        ArrayList<Map<String, String>> lineResults = new ArrayList<>();

        boolean noResult = true;
        for (Map.Entry<String, List<File>> entry : entries) {
            String fastaName = entry.getKey();
            List<File> FastaFiles = entry.getValue();
            //Read 3 entries in fasta file using ParsingRuleEntry regEx
            for (File nextFile : FastaFiles) {

                Map<String, String> linePlusProteinName = new HashMap<>();
                Object[] resultsOfTest = getMatchingParsingRule(fastaName);
                resultStore.add(resultsOfTest);
                ParsingRule rule = (ParsingRule) resultsOfTest[0];
                String protRegex;
                boolean defaultProtein;
                if (rule != null) {
                    protRegex = rule.getProteinAccRegExp();
                    defaultProtein = false;
                } else {

                    protRegex = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
                    defaultProtein = true;
                }

                BufferedReader br = null;

                try {
                    InputStream is = new FileInputStream(nextFile);
                    br = new BufferedReader(new InputStreamReader(is, LATIN_1_CHARSET));

                    String rawLine = br.readLine();
                    int countEntry = 0;

                    boolean proteinHasBeenFound = false;

                    while (countEntry < 3 && rawLine != null) {

                        final String trimmedLine = rawLine.trim();
                        if (!trimmedLine.isEmpty() && trimmedLine.startsWith(">")) { //Found an entry
                            countEntry++;
                            String foundEntry = getMatchingString(trimmedLine, protRegex);
                            if (foundEntry != null) {
                                proteinHasBeenFound = true;
                                linePlusProteinName.put(trimmedLine, foundEntry);
                                noResult = false;
                            }
                        } // End entryFound
                        rawLine = br.readLine();

                    }
                    if (!proteinHasBeenFound) {
                        linePlusProteinName.put("Could not extract any line with a matching protein ", "no protein");
                    }

                    lineResults.add(linePlusProteinName);

                    // End read some entries
                } finally {

                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException exClose) {
                            LOG.error("Error closing [" + nextFile + ']', exClose);
                        }
                    }

                }
            }
            //End go through associated fasta files
        }
        // launch a dialog once test is completed
        ResultOfGlobalTestDialog resultDialog = new ResultOfGlobalTestDialog(ConfigWindow.getInstance(), resultStore, lineResults);
        resultDialog.centerToScreen();
        resultDialog.setSize(800, 500);
        resultDialog.setVisible(true);


    }

    private Map<String, List<File>> getFastaFilesMap() throws Exception {
        Map<String, List<File>> fastaFiles = null;
        List<String> localFASTAPaths = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        if (localFASTAPaths != null && !localFASTAPaths.isEmpty()) {
            fastaFiles = FastaPathsScanner.scanPaths(new FastaPathsScanner(), localFASTAPaths);

        } else {
            LOG.error("No valid localFASTAPaths configured");
        }
        return fastaFiles;

    }

    public static String extractProteinNameWithRegEx(String line, String protRegex) {
        return getMatchingString(line, protRegex);
    }

    private static String getMatchingString(final String sourceText, final String searchStrRegEx) {
        if (sourceText == null || searchStrRegEx == null)
            return null;

        Pattern textPattern = Pattern.compile(searchStrRegEx, Pattern.CASE_INSENSITIVE);

        String result = null;
        if (textPattern != null) {
            final Matcher matcher = textPattern.matcher(sourceText);

            if (matcher.find()) {

                if (matcher.groupCount() >= 1)
                    result = matcher.group(1).trim();
            }
        }
        return result;
    }

    // Retrieves first parsingrule that contains a fasta name regex that matches with name of file passed
    public static Object[] getMatchingParsingRule(final String fastaFileName) {
        assert (fastaFileName != null) : "getParsingRuleEntry() fastaFileName is null";
        // resultObject stores the parsing rule and the fasta name regex that match with the name of the file
        Object[] resultObject = new Object[3];
        resultObject[2] = fastaFileName;

        for (ParsingRule nextPR : ConfigManager.getInstance().getParsingRulesManager().getSetOfRules()) {
            for (String fastaRegEx : nextPR.getFastaNameRegExp()) {
                final Pattern pattern = Pattern.compile(fastaRegEx, Pattern.CASE_INSENSITIVE);

                final Matcher matcher = pattern.matcher(fastaFileName);
                if (matcher.find()) {
                    resultObject[0] = nextPR;
                    resultObject[1] = fastaRegEx;
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


    /**
     * Global test that creates an infoDialog with a string builder inside
     */

    /*   public void globalTest() throws Exception {

           System.out.println("test pressed");
           StringBuilder stringBuilder = new StringBuilder();
           Map<String, List<File>> fastaPaths = getFastaFilesMap();
           Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
           ArrayList<Object[]> resultStore=new ArrayList<>();



           boolean noResult = true;
           for (Map.Entry<String, List<File>> entry : entries) {
               String fastaName = entry.getKey();
               List<File> FastaFiles = entry.getValue();
               Object[] resultsOfTest = ParsingRule.getMatchingParsingRule(fastaName);
               resultStore.add(resultsOfTest);
               ParsingRule rule = (ParsingRule) resultsOfTest[0];
               String protRegex;
               if (rule != null) {

                   protRegex = rule.getProteinAccRegExp();
               } else {
                   //TODO use current value or the one inside config file?
                   protRegex = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
               }
               //Read 3 entries in fasta files using ParsingRuleEntry regEx
               for (File nextFile : FastaFiles) {
                   //stringBuilder.append("fasta file:  " + nextFile.getAbsolutePath());
                   stringBuilder.append("Fasta file: " + nextFile.getName());
                   stringBuilder.append("\n");

                   if (resultsOfTest[1] == null) {
                       stringBuilder.append("No regular expression that match with the name of the file \n");
                   } else {
                       stringBuilder.append("Parsing rule selected: " + ((ParsingRule) resultsOfTest[0]).getName() + "\n");
                       stringBuilder.append("Fasta Name RegEx:  " + resultsOfTest[1] + "\n");
                   }
                   // Warning curent or inside config file
                   boolean defaultProtein = protRegex.equals(ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule());
                   if (defaultProtein) {
                       stringBuilder.append("Default regular expression selected: " + protRegex + "\n");
                   } else {
                       stringBuilder.append("Regular expression selected to do the test: " + protRegex + "\n");
                   }

                   BufferedReader br = null;
                   ArrayList<Map<String,String>> linePlusProteinName=new ArrayList<>();
                   try {
                       InputStream is = new FileInputStream(nextFile);
                       br = new BufferedReader(new InputStreamReader(is, LATIN_1_CHARSET));

                       String rawLine = br.readLine();
                       int countEntry = 0;
                       // int numberOfNoFoundEntry = 0;
                       boolean proteinHasBeenFound = false;
                       while (countEntry < 3 && rawLine != null) {

                           final String trimmedLine = rawLine.trim();
                           if (!trimmedLine.isEmpty() && trimmedLine.startsWith(">")) { //Found an entry
                               countEntry++;


                               String foundEntry = ParsingRulesUtils.getMatchingString(trimmedLine, protRegex);
                               if (foundEntry != null) {
                                   proteinHasBeenFound = true;
                                   stringBuilder.append(trimmedLine + "\n");
                                   stringBuilder.append("protein name:  ");
                                   stringBuilder.append(foundEntry);
                                   stringBuilder.append("\n");
                                   Map<String, String> lineAndProt=new HashMap<>();
                                   lineAndProt.put(trimmedLine,foundEntry);
                                   linePlusProteinName.add(lineAndProt);
                                   noResult = false;

                               }

                           } // End entryFound
                           rawLine = br.readLine();

                       }


                       if (!proteinHasBeenFound) {
                           stringBuilder.append("No protein found inside the file");
                           stringBuilder.append("\n");
                       }
                       stringBuilder.append("------------------------------------------------------------\n");


                       // End read some entries

                   } finally {

                       if (br != null) {
                           try {
                               br.close();
                           } catch (IOException exClose) {
                               LOG.error("Error closing [" + nextFile + ']', exClose);
                           }
                       }

                   }
               } //End go through associated fasta files


           }
           if (noResult) {
               stringBuilder.append("The test did not return any results you might check fasta files inside the folder panel");
           }

           //  -----------------------first version with an INFO Dialog----------------------------//

           *//*InfoDialog infoDialog = new InfoDialog(ConfigWindow.getInstance(), InfoDialog.InfoType.NO_ICON, "Test results", stringBuilder.toString(), false);
        infoDialog.setButtonVisible(BUTTON_OK, false);
        infoDialog.setButtonName(BUTTON_CANCEL, "close");
        infoDialog.setSize(1200, 500);
        infoDialog.centerToScreen();
        infoDialog.setVisible(true);*//*
        ResultOfGlobalTestDialog resultDialog=new ResultOfGlobalTestDialog(ConfigWindow.getInstance(),resultStore);
        resultDialog.centerToScreen();
        resultDialog.setSize(1200,500);
        resultDialog.setVisible(true);


    }*/


}
