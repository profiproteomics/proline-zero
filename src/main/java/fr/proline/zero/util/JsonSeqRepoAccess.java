package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class JsonSeqRepoAccess {

    private final Logger logger = LoggerFactory.getLogger(JsonSeqRepoAccess.class);
    private static JsonSeqRepoAccess instance;
    private Config parsingRules ;


    private JsonSeqRepoAccess() {
        ConfigParseOptions options = ConfigParseOptions.defaults();
        options.setSyntax(ConfigSyntax.CONF);
        parsingRules = ConfigFactory.parseFile(ProlineFiles.PARSING_RULES_CONFIG_FILE, options);


    }

    public static JsonSeqRepoAccess getInstance() {
        if (instance == null) {
            instance = new JsonSeqRepoAccess();
        }
        return instance;
    }


    public Config getParsingConfig() {
        return parsingRules;
    }
    public ArrayList<String> getFastaDirectories() {
        try {

           // List<String> paths = null;
            ArrayList<String> array = null;
            if (parsingRules.hasPath(ProlineFiles.SEQREPO_FASTA_DIRECTORIES)) {

               List<String> paths = parsingRules.getStringList(ProlineFiles.SEQREPO_FASTA_DIRECTORIES);
               array = new ArrayList<>(paths);


            }

            return array;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public String getDefaultProtein(){
        String proteinAccess="";
        try  {
            if (parsingRules.hasPath(ProlineFiles.PROTEIN_DEFAULT_REGEX)){
                proteinAccess=parsingRules.getString(ProlineFiles.PROTEIN_DEFAULT_REGEX);
            }
            return proteinAccess;

        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public ArrayList<ParsingRule> getSetOfRules() {
        ArrayList<ParsingRule> arrayOfPrules = new ArrayList<>();

        ConfigList list = parsingRules.getList(ProlineFiles.SEQREPO_PARSING_RULE_KEY);

        for (int i = 0; i < list.size(); i++) {
            ConfigValue configValue = list.get(i);
            Map<String, Object> parsingRule = (Map<String, Object>) configValue.unwrapped();


            String name = parsingRule.get(ProlineFiles.NAME).toString();


            ArrayList<String> fasta = (ArrayList<String>) parsingRule.get(ProlineFiles.FASTA_NAME);


            String fastaV = parsingRule.get(ProlineFiles.FASTA_VERSION).toString();

            String protein = parsingRule.get(ProlineFiles.PROTEIN).toString();

            ParsingRule ps = new ParsingRule(name, fasta, fastaV, protein);

            arrayOfPrules.add(i, ps);

        }
        return arrayOfPrules;


    }

    public void updateConfigRulesAndFasta(ArrayList<String> fastaPaths, List<ParsingRule> setOfRules){
        // first updates fasta directories
        ConfigObject toBePreserved = JsonSeqRepoAccess.getInstance().getParsingConfig().root().withoutKey(ProlineFiles.SEQREPO_FASTA_DIRECTORIES);

        ConfigList cfg = ConfigValueFactory.fromIterable(fastaPaths);
        ConfigObject finalbuild = toBePreserved.withFallback(cfg.atKey(ProlineFiles.SEQREPO_FASTA_DIRECTORIES));
        // then  updates parsing rules
        ConfigObject toBePreserved2 = finalbuild.withoutKey(ProlineFiles.SEQREPO_PARSING_RULE_KEY);
        Config buildLabel;
        Config buildFastaVersion;
        Config buildProtein;
        Config buildFastaName;

        Config[] configsbuild = new Config[setOfRules.size()];
        List<ConfigValue> cfg2 = new ArrayList<>();


        for (int i = 0; i < setOfRules.size(); i++) {

            ParsingRule pr = setOfRules.get(i);
            String name = pr.getName();
            String fastaVersion = pr.getFastaVersionRegExp();
            ArrayList<String> fastaName = pr.getFastaNameRegExp();
            String protein = pr.getProteinAccRegExp();
            buildLabel = ConfigValueFactory.fromAnyRef(name).atKey(ProlineFiles.NAME);

            buildFastaVersion = ConfigValueFactory.fromAnyRef(fastaVersion).atKey(ProlineFiles.FASTA_VERSION);

            buildProtein = ConfigValueFactory.fromAnyRef(protein).atKey(ProlineFiles.PROTEIN);

            buildFastaName = ConfigValueFactory.fromIterable(fastaName).atKey(ProlineFiles.FASTA_NAME);

            configsbuild[i] = buildLabel.withFallback(buildFastaVersion.withFallback(buildProtein).withFallback(buildFastaName));
            cfg2.add(i, configsbuild[i].root());

        }

        ConfigList cfglst = ConfigValueFactory.fromIterable(cfg2);
        ConfigObject reBuiltConfig = toBePreserved2.withFallback(cfglst.atKey(ProlineFiles.SEQREPO_PARSING_RULE_KEY));

        String finalWrite = reBuiltConfig.render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        try {
            FileWriter writer = new FileWriter(ProlineFiles.PARSING_RULES_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }












}
