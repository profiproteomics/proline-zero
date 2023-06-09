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
import java.util.stream.Stream;

import static fr.proline.module.seq.Constants.LATIN_1_CHARSET;

/**
 * This class contains all the methods used to do tests on parsing rules
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


        List<String> fastaDirectories = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();

        Map<String, List<File>> fastaPaths = retrieveFastaFiles(fastaDirectories);

        if (fastaPaths.isEmpty()) {
            Popup.warning("No fasta files found, you might check fasta directories inside folder panel");
            return;
        }
        Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
        // resultStore contains parsing rule selected if any plus fasta name regex plus name of file
        ArrayList<Object[]> resultStore = new ArrayList<>();

        // lineResults contains lines from fasta and protein extracted from lines if any
        ArrayList<Map<String, String>> lineResults = new ArrayList<>();
        String protByDefault = ConfigManager.getInstance().getParsingRulesManager().getDefaultProteinAccRule();
        boolean proteinByDefaultIsValid = isRegexProtValid(protByDefault);
        // test won't be executed if regex by default is not valid
        if (proteinByDefaultIsValid) {

            int successMatching = 0;
            int numberOfFilesWithoutProteinExtracted = 0;
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
                        boolean regularExpressionIsValid = isRegexValid(rule.getProteinAccRegExp());
                        if (regularExpressionIsValid) {
                            protRegex = rule.getProteinAccRegExp();
                            successMatching++;

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
                            if (trimmedLine.startsWith(">")) { //Found an entry
                                countEntry++;
                                String foundEntry = getMatchingString(trimmedLine, protRegex);
                                if (foundEntry != null) {
                                    proteinHasBeenFound = true;
                                    linePlusProteinName.put(trimmedLine, foundEntry);

                                } else {
                                    linePlusProteinName.put(trimmedLine, "No protein accession extracted");
                                }
                            } // End entryFound
                            rawLine = br.readLine();
                        }
                        if (!proteinHasBeenFound) {
                            numberOfFilesWithoutProteinExtracted++;
                        }
                        if (countEntry == 0) {
                            //linePlusProteinName.put("Could not extract any line from file  " + fastaName, "No protein extracted");
                            linePlusProteinName.put("Could not extract any line from this file", "No line no protein accession extracted");
                            // numberOfFilesWithoutProteinExtracted++;
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

            ResultOfGlobalTestDialog resultDialog = new ResultOfGlobalTestDialog(ConfigWindow.getInstance(), resultStore, lineResults, successMatching, numberOfFilesWithoutProteinExtracted);
            resultDialog.setSize(930, 700);
            resultDialog.centerToWindow(ConfigWindow.getInstance());
            resultDialog.setVisible(true);
        }


    }
    // Method from seqrepo works but might be heavy??

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
     * will retrieve all fasta files, key is name of file, values are Files path with name equal to key
     * recursive method
     *
     * @param folderPaths
     * @return a Map
     * @author Christophe Delapierre
     */


    public static Map<String, List<File>> retrieveFastaFiles(List<String> folderPaths) {
        Map<String, List<File>> fastaFilesByName = new HashMap<>();

        for (String folderPath : folderPaths) {
            Path folder = Paths.get(folderPath);
            if (Files.isDirectory(folder)) {
                try (Stream<Path> filesStream = Files.find(folder, Integer.MAX_VALUE, (filePath, fileAttr) ->
                        fileAttr.isRegularFile() && filePath.toString().endsWith(".fasta"))
                ) {
                    filesStream.forEach(filePath -> {
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
     *
     * @param sourceText
     * @param protRegEx
     * @return String
     * @throws PatternSyntaxException
     * @author Christophe Delapierre
     */

    private static String getMatchingString(final String sourceText, final String protRegEx) {
        if (sourceText == null || protRegEx == null)
            return null;
        String matchingProtein = null;

        try {
            Pattern textPattern = Pattern.compile(protRegEx, Pattern.CASE_INSENSITIVE);


            final Matcher matcher = textPattern.matcher(sourceText);

            if (matcher.find()) {

                if (matcher.groupCount() >= 1)
                    matchingProtein = matcher.group(1).trim();
            }


        } catch (PatternSyntaxException patternSyntaxException) {
            // shall never happen in global test  because protRegex has already been checked
            patternSyntaxException.printStackTrace();
            // could happen in local test
            Popup.warning("Protein Regex is not valid please modify it");
            return "regex not valid, no protein extracted";

        }
        return matchingProtein;
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

    public static boolean isRegexProInsideDialog(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {

            return false;
        }
    }

    public static boolean isRegexValid(String regex) {
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
     *
     * @param fastaFileName
     * @return Object[]
     * @throws PatternSyntaxException
     * @author Christophe Delapierre
     */

    //
    public static Object[] getMatchingParsingRule(final String fastaFileName) {
        assert (fastaFileName != null) : "getParsingRuleEntry() fastaFileName is null";
        // resultObject stores the parsing rule and the fasta name regex that match with the name of the file
        Object[] resultObject = new Object[4];

        for (ParsingRule parsingRule : ConfigManager.getInstance().getParsingRulesManager().getSetOfRules()) {
            for (String fastaRegEx : parsingRule.getFastaNameRegExp()) {
                try {

                    final Pattern pattern = Pattern.compile(fastaRegEx, Pattern.CASE_INSENSITIVE);

                    final Matcher matcher = pattern.matcher(fastaFileName);
                    if (matcher.find()) {
                        resultObject[0] = parsingRule;
                        resultObject[1] = fastaRegEx;
                        resultObject[3] = true;
                        // loop ends as soon as a fastaRegex match with the name of the file
                        break;

                    }
                } catch (PatternSyntaxException patternSyntaxException) {

                    resultObject[3] = false;
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
     *  Tests the validity of fasta name regex associated with a parsingRule
     * will return false if at least one regex is not valid
     *
     * @param parsingRule
     * @return boolean
     */
    public static boolean parsingRuleFastaRegexisNotValid(ParsingRule parsingRule) {

        boolean atleastOneFastaIsNotValid = false;
        List<String> fastaNameRegex = parsingRule.getFastaNameRegExp();
        for (String nameRegex : fastaNameRegex) {

            if (!isRegexValid(nameRegex)) {
                atleastOneFastaIsNotValid = true;
                break;

            }
        }
        return atleastOneFastaIsNotValid;
    }

    /**
     * Tests if all fasta names regex are valid.
     *
     * @param fastaList
     * @return
     */
    public static ArrayList<Boolean> testFastaList(List<String> fastaList) {
        ArrayList<Boolean> fastaValid = new ArrayList<>();
        int sizeOfFastaRegex = fastaList.size();
        for (int j = 0; j < sizeOfFastaRegex; j++) {

            if (isRegexValid(fastaList.get(j))) {
                fastaValid.add(j, true);

            } else fastaValid.add(false);
        }
        return fastaValid;

    }

    public static boolean testFastaListInsideDialog(List<String> fastaList) {
        int sizeOfFastaRegex = fastaList.size();
        boolean fastaRegexAreValid = true;
        for (int j = 0; j < sizeOfFastaRegex; j++) {

            if (!isRegexValid(fastaList.get(j))) {
                fastaRegexAreValid = false;
                break;

            }
        }
        return fastaRegexAreValid;

    }


}
