package fr.proline.zero.util;

import fr.proline.module.seq.service.FastaPathsScanner;
import fr.proline.studio.gui.InfoDialog;
import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.ParsingRulesPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.proline.module.seq.Constants.LATIN_1_CHARSET;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_CANCEL;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_OK;

public class ParsingRulesTester {

    public ParsingRulesTester() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ParsingRulesTester.class);

    public void globalTest() throws Exception {

        System.out.println("test pressed");
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, List<File>> fastaPaths = getFastaFilesMap();
        Set<Map.Entry<String, List<File>>> entries = fastaPaths.entrySet();
        boolean noResult = true;
        for (Map.Entry<String, List<File>> entry : entries) {
            String fastaName = entry.getKey();
            List<File> FastaFiles = entry.getValue();
            Object[] resultsOfTest = ParsingRule.getMatchingParsingRule(fastaName);
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

        System.out.println(stringBuilder);
        InfoDialog infoDialog = new InfoDialog(ConfigWindow.getInstance(), InfoDialog.InfoType.NO_ICON, "Test results", stringBuilder.toString(), false);
        infoDialog.setButtonVisible(BUTTON_OK, false);
        infoDialog.setButtonName(BUTTON_CANCEL, "close");
        infoDialog.setSize(1200, 500);
        infoDialog.centerToScreen();
        infoDialog.setVisible(true);


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




}
