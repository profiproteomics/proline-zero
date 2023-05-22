package fr.proline.zero.util;

import fr.proline.module.seq.service.FastaPathsScanner;
import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.ResultOfGlobalTestDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static fr.proline.module.seq.Constants.LATIN_1_CHARSET;

/**
 * This class contains all the methods used to do tests on parsing rules
 *
 */

public class ParsingRulesTester {

    public ParsingRulesTester() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRulesTester.class);


    /**
     * globalTest will test all fasta files, for each of these files it will attempt to find a parsingrule that matches with name of file.
     * Then it will test three lines of the file with protein accession rule of the parsing rule selected. protein name are then extracted.
     * if no parsing rule is found, the protein rule by default is used to parse the file.
     * Once results of tests are produced a ResultOfGlobalTestDialog will open
     *
     * @throws Exception
     */


    public static void globalTest() throws Exception {

        System.out.println("test pressed");
        List<String> localFASTAPaths = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        // Map<String, List<File>> fastaPaths = getFastaFilesMap();
        Map<String, List<File>> fastaPaths = retrieveFastaFiles(localFASTAPaths);

        if (fastaPaths == null) {
            Popup.warning("No fasta files found, you might check fasta directories inside folder panel");
            return;
        }
        Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
        // resultStore contains parsing rule selected if any plus fasta name regex plus name of file
        ArrayList<Object[]> resultStore = new ArrayList<>();
        // lineresults contains lines from fasta and protein extracted from lines if any
        ArrayList<Map<String, String>> lineResults = new ArrayList<>();
        String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        boolean proteinByDefaultIsValid = isRegexProtValid(protByDefault);
        // test won't be executed if regex by default is not valid
        if (proteinByDefaultIsValid) {

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
                    if (rule != null) {
                        boolean regularExpressionIsValid = isRegexFastaNameValid(rule.getProteinAccRegExp());
                        if (regularExpressionIsValid) {
                            protRegex = rule.getProteinAccRegExp();

                        } else {
                            // if protein accession rule is not valid will use protein by default
                            protRegex = protByDefault;
                        }
                    } else {
                        protRegex = protByDefault;
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
                                } else {
                                    linePlusProteinName.put(trimmedLine, "No protein name extracted");
                                }
                            } // End entryFound
                            rawLine = br.readLine();
                        }
                        if (!proteinHasBeenFound && countEntry == 0) {
                            linePlusProteinName.put("Could not extract any line from file:  " + fastaName, "No protein found");
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


    }
    // method imported from seqrepo using FastaPathsScanner works but not adapted to context ?
    //
    private static Map<String, List<File>> getFastaFilesMap() throws Exception {
        Map<String, List<File>> fastaFiles = null;
        List<String> localFASTAPaths = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        if (localFASTAPaths != null && !localFASTAPaths.isEmpty()) {
            fastaFiles = FastaPathsScanner.scanPaths(new FastaPathsScanner(), localFASTAPaths);

        } else {
            LOG.error("No valid localFASTAPaths configured");
        }
        return fastaFiles;

    }

    /**
     * will retrieve all fasta files, key is name of file, values are Files with name equal to key
     * recursive method
     * @param folderPaths
     * @return  a Map
     * @author Christophe Delapierre
     *
     */

    public static Map<String, List<File>> retrieveFastaFiles(List<String> folderPaths) {
        Map<String, List<File>> fastaFilesByName = new HashMap<>();

        for (String folderPath : folderPaths) {
            Path folder = Paths.get(folderPath);
            if (Files.isDirectory(folder)) {
                try {
                    Files.find(folder, Integer.MAX_VALUE, (filePath, fileAttr) ->
                            fileAttr.isRegularFile() && filePath.toString().endsWith(".fasta")
                    ).forEach(filePath -> {
                        String fileName = filePath.getFileName().toString();
                        File file = filePath.toFile();
                        if (!fastaFilesByName.containsKey(fileName)) {
                            fastaFilesByName.put(fileName, new ArrayList<>());
                        }
                        List<File> filesWithName = fastaFilesByName.get(fileName);
                        if (!filesWithName.contains(file)) {
                            filesWithName.add(file);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fastaFilesByName;
    }


    public static String extractProteinNameWithRegEx(String line, String protRegex) {
        return getMatchingString(line, protRegex);
    }

    /**
     * method that takes two strings as input parameters: sourceText and protRegEx. It attempts to find a matching substring
     * within sourceText based on the regular expression protRegEx.
     * @param sourceText
     * @param protRegEx
     * @return String
     * @author Christophe Delapierre
     * @throws PatternSyntaxException
     */

    private static String getMatchingString(final String sourceText, final String protRegEx) {
        if (sourceText == null || protRegEx == null)
            return null;
        String result = null;

        try {
            Pattern textPattern = Pattern.compile(protRegEx, Pattern.CASE_INSENSITIVE);

            if (textPattern != null) {
                final Matcher matcher = textPattern.matcher(sourceText);

                if (matcher.find()) {

                    if (matcher.groupCount() >= 1)
                        result = matcher.group(1).trim();
                }
            }
        } catch (PatternSyntaxException patternSyntaxException) {
            // shall never happen in global test  because protRegex has already been checked
            patternSyntaxException.printStackTrace();
            // could happen in local test
            Popup.warning("Protein Regex is not valid please modify it");

            return "regex not valid, no protein extracted";

        }
        return result;
    }

    public static boolean isRegexProtValid(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            Popup.warning("Default regular expression is not valid please modify it");
            return false;
        }
    }

    public static boolean isRegexFastaNameValid(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {

            return false;
        }
    }

    /**
     * Retrieves first parsingrule that contains a fasta name regex that matches with name of file passed
     * return null if no parsing rule matches
     * @param fastaFileName
     * @return Object[]
     * @author Christophe Delapierre
     * @throws PatternSyntaxException
     */

    //
    public static Object[] getMatchingParsingRule(final String fastaFileName) {
        assert (fastaFileName != null) : "getParsingRuleEntry() fastaFileName is null";
        // resultObject stores the parsing rule and the fasta name regex that match with the name of the file
        Object[] resultObject = new Object[4];

        for (ParsingRule nextPR : ConfigManager.getInstance().getParsingRulesManager().getSetOfRules()) {
            for (String fastaRegEx : nextPR.getFastaNameRegExp()) {
                try {

                    final Pattern pattern = Pattern.compile(fastaRegEx, Pattern.CASE_INSENSITIVE);

                    final Matcher matcher = pattern.matcher(fastaFileName);
                    if (matcher.find()) {
                        resultObject[0] = nextPR;
                        resultObject[1] = fastaRegEx;
                        // loop ends as soon as a fastaRegex match with the name of the file
                        break;
                    }
                } catch (PatternSyntaxException patternSyntaxException) {

                    System.out.println("not valid fasta regex: " + fastaRegEx);

                    resultObject[3]=true;
                    break;
                }
            }
            // End loop for each regex
            if (resultObject[1] != null)
                // loop ends at first match
                break;
        }
        resultObject[2] = fastaFileName;
        return resultObject;
    }

    /**
     * Will test the validity of fasta name regex associated with a parsingRule
     * will return false if at least one regex is not valid
     *
     * @param parsingRule
     * @return boolean
     */
    public static boolean parsingRuleFastaRegexisNotValid(ParsingRule parsingRule) {
        boolean atleastOneFastaIsNotValid = false;
        List<String> fastaNameRegex = parsingRule.getFastaNameRegExp();
        int sizeOfFastaRegex = fastaNameRegex.size();
        for (int j = 0; j < sizeOfFastaRegex; j++) {

            if (!isRegexFastaNameValid(fastaNameRegex.get(j))) {
                atleastOneFastaIsNotValid = true;
                break;

            }
        }
        return atleastOneFastaIsNotValid;
    }
    public static ArrayList<Boolean> testFastaList(List<String> fastaList){
        ArrayList<Boolean> fastaValid=new ArrayList<>();
        int sizeOfFastaRegex = fastaList.size();
        for (int j = 0; j < sizeOfFastaRegex; j++) {

            if (isRegexFastaNameValid(fastaList.get(j))) {
                fastaValid.add(j,true);


            }
            else fastaValid.add(false);
        }
        return fastaValid;

    }


    // Global test that returns an InfoDialog functionnal

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
