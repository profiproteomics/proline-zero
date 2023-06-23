package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import fr.proline.zero.gui.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Read and Write Sequence Repository config file.
 * Handles fasta directories as well as  parsing rules
 *
 * @see ConfigManager
 */

public class JsonSeqRepoAccess {

    private static JsonSeqRepoAccess instance;
    private Config parsingRules;


    private JsonSeqRepoAccess() throws FileNotFoundException {


        ConfigParseOptions options = ConfigParseOptions.defaults();
        options = options.setSyntax(ConfigSyntax.CONF);
        File configFile = ProlineFiles.PARSING_RULES_CONFIG_FILE;
        try {

            if (configFile.exists()) {
                parsingRules = ConfigFactory.parseFile(ProlineFiles.PARSING_RULES_CONFIG_FILE, options);
            } else {
                parsingRules = ConfigFactory.empty();
                throw new FileNotFoundException("Seq Repo config file parsing-rules.conf not found");
            }
        } catch (FileNotFoundException exception) {

            Popup.warning("No configuration file found for sequence repository \n" +
                    "sequence repository will be deactivated");
            ConfigManager.getInstance().setSeqRepActive(false);
            ProlineFiles.setSeqRepoConfigFileNotFound(true);

            Logger logger = LoggerFactory.getLogger(JsonSeqRepoAccess.class);
            logger.warn("exception occurred: " + exception.getMessage());
        }


    }

    public static JsonSeqRepoAccess getInstance() {
        if (instance == null) {
            try {
                instance = new JsonSeqRepoAccess();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }


    public Config getParsingConfig() {
        return parsingRules;
    }

    public List<String> getFastaDirectories() {

        try {

            List<String> array = null;
            if (parsingRules.hasPath(ProlineFiles.SEQREPO_FASTA_DIRECTORIES)) {
                array = parsingRules.getStringList(ProlineFiles.SEQREPO_FASTA_DIRECTORIES);
            }

            return array;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDefaultProtein() {
        String proteinAccess = "";
        try {
            if (parsingRules.hasPath(ProlineFiles.SEQREPO_PROTEIN_DEFAULT_REGEX)) {
                proteinAccess = parsingRules.getString(ProlineFiles.SEQREPO_PROTEIN_DEFAULT_REGEX);
            }
            return proteinAccess;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    public List<ParsingRule> getSetOfRules() {
        List<ParsingRule> arrayOfParsingRules = new ArrayList<>();

        try {
            if (parsingRules.hasPath(ProlineFiles.SEQREPO_PARSING_RULE_KEY)) {
                ConfigList list = parsingRules.getList(ProlineFiles.SEQREPO_PARSING_RULE_KEY);

                for (int i = 0; i < list.size(); i++) {
                    ConfigValue configValue = list.get(i);
                    Object unwrappedValue = configValue.unwrapped();

                    if (unwrappedValue instanceof Map<?, ?> parsingRule) {
                        String name = parsingRule.get(ProlineFiles.PARSING_RULE_NAME).toString();

                        @SuppressWarnings("unchecked")
                        List<String> fastaList = (List<String>) parsingRule.get(ProlineFiles.PARSING_RULE_FASTA_NAME);
                        ArrayList<String> fastaNameRegEx = new ArrayList<>(fastaList);

                        String fastaVersion = parsingRule.get(ProlineFiles.PARSING_RULE_FASTA_VERSION).toString();
                        String protein = parsingRule.get(ProlineFiles.PARSING_RULE_PROTEIN).toString();

                        ParsingRule parsingRuleBuilded = new ParsingRule(name, fastaNameRegEx, fastaVersion, protein);

                        arrayOfParsingRules.add(i, parsingRuleBuilded);
                    }
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return arrayOfParsingRules;
    }


    /**
     * Saves config file parsing-rule.conf.
     */


    public void updateConfigRulesAndFasta(List<String> fastaPaths, List<ParsingRule> setOfRules, String defaultProteinAccessionRule) {

        // first updates fasta directories
        ConfigObject nonModifiedConfig = JsonSeqRepoAccess.getInstance().getParsingConfig().root().withoutKey(ProlineFiles.SEQREPO_FASTA_DIRECTORIES);

        ConfigList configListFastas = ConfigValueFactory.fromIterable(fastaPaths);
        ConfigObject finalBuild = nonModifiedConfig.withFallback(configListFastas.atKey(ProlineFiles.SEQREPO_FASTA_DIRECTORIES));

        // then  updates parsing rules
        ConfigObject nonModifiedConfig2 = finalBuild.withoutKey(ProlineFiles.SEQREPO_PARSING_RULE_KEY);
        Config labelConfig;
        Config fastaVersionConfig;
        Config proteinConfig;
        Config fastaNameConfig;

        Config[] configBuild = new Config[setOfRules.size()];
        List<ConfigValue> parsingRulesConfig = new ArrayList<>();


        for (int i = 0; i < setOfRules.size(); i++) {

            ParsingRule pr = setOfRules.get(i);
            String name = pr.getName();
            String fastaVersion = pr.getFastaVersionRegExp();
            List<String> fastaName = pr.getFastaNameRegExp();
            String protein = pr.getProteinAccRegExp();
            labelConfig = ConfigValueFactory.fromAnyRef(name).atKey(ProlineFiles.PARSING_RULE_NAME);

            fastaVersionConfig = ConfigValueFactory.fromAnyRef(fastaVersion).atKey(ProlineFiles.PARSING_RULE_FASTA_VERSION);

            proteinConfig = ConfigValueFactory.fromAnyRef(protein).atKey(ProlineFiles.PARSING_RULE_PROTEIN);

            fastaNameConfig = ConfigValueFactory.fromIterable(fastaName).atKey(ProlineFiles.PARSING_RULE_FASTA_NAME);

            configBuild[i] = labelConfig.withFallback(fastaVersionConfig.withFallback(proteinConfig).withFallback(fastaNameConfig));
            parsingRulesConfig.add(i, configBuild[i].root());

        }

        ConfigList configList = ConfigValueFactory.fromIterable(parsingRulesConfig);
        ConfigObject reBuiltConfig = nonModifiedConfig2.withFallback(configList.atKey(ProlineFiles.SEQREPO_PARSING_RULE_KEY));
        // then updates default protein accession rule
        ConfigObject nonModifiedConfig3 = reBuiltConfig.withoutKey(ProlineFiles.SEQREPO_PROTEIN_DEFAULT_REGEX);
        Config protByDefault = ConfigValueFactory.fromAnyRef(defaultProteinAccessionRule).atKey(ProlineFiles.SEQREPO_PROTEIN_DEFAULT_REGEX);
        ConfigObject finalConfig = nonModifiedConfig3.withFallback(protByDefault);

        // edit config file
        String finalWrite = finalConfig.render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        try {
            FileWriter writer = new FileWriter(ProlineFiles.PARSING_RULES_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
