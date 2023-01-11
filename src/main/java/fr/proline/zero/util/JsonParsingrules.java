package fr.proline.zero.util;

import com.typesafe.config.*;

import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class JsonParsingrules {

    private final Logger logger = LoggerFactory.getLogger(JsonParsingrules.class);
    private static JsonParsingrules instance;
    private Config parsingRules ;


    private JsonParsingrules() {
        ConfigParseOptions options = ConfigParseOptions.defaults();
        options.setSyntax(ConfigSyntax.CONF);
        parsingRules = ConfigFactory.parseFile(ProlineFiles.PARSING_RULES_CONFIG_FILE, options);


    }

    public static JsonParsingrules getInstance() {
        if (instance == null) {
            instance = new JsonParsingrules();
        }
        return instance;
    }


    public Config getParsingConfig() {
        return parsingRules;
    }


    // add a check if is not null
    public List<String> getFastaPaths() {
        try {

            List<String> paths = null;
            if (parsingRules.hasPath("local-fasta-directories")) {

                paths = parsingRules.getStringList("local-fasta-directories");
                System.out.println(paths.toString());

            }

            return paths;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }





    public ArrayList<ParsingRule> getSetOfRules() {
        ArrayList<ParsingRule> arrayOfPrules = new ArrayList<>();

        ConfigList list = parsingRules.getList(ProlineFiles.PARSING_RULES);

        for (int i = 0; i < list.size(); i++) {
            ConfigValue configValue = list.get(i);
            Map<String, Object> parsingRule = (Map<String, Object>) configValue.unwrapped();


            String name = parsingRule.get(ParsingRulesUtils.ParsingRulesKeys.NAME.getJsonKey()).toString();


            ArrayList<String> fasta = (ArrayList<String>) parsingRule.get(ParsingRulesUtils.ParsingRulesKeys.FASTA_NAME.getJsonKey());


            String fastaV = parsingRule.get(ParsingRulesUtils.ParsingRulesKeys.FASTA_VERSION.getJsonKey()).toString();

            String protein = parsingRule.get(ParsingRulesUtils.ParsingRulesKeys.PROTEIN.getJsonKey()).toString();

            ParsingRule ps = new ParsingRule(name, fasta, fastaV, protein);

            arrayOfPrules.add(i, ps);

        }
        return arrayOfPrules;


    }

    public void updateConfigFileParseRules(ArrayList<ParsingRule> setOfRules) {

        ConfigObject toBePreserved = JsonParsingrules.getInstance().getParsingConfig().root().withoutKey(ProlineFiles.PARSING_RULES);
        Config buildLabel;
        Config buildFastaVersion;
        Config buildProtein;
        Config buildFastaName;

        Config[] configsbuild = new Config[setOfRules.size()];


        for (int i = 0; i < setOfRules.size(); i++) {

            ParsingRule pr = setOfRules.get(i);
            String name = pr.getName();
            String fastaVersion = pr.getFasta_version();
            ArrayList<String> fastaName = pr.getFasta_name();
            String protein = pr.getProtein();
            buildLabel = ConfigValueFactory.fromAnyRef(name).atKey(ParsingRulesUtils.ParsingRulesKeys.NAME.getJsonKey());

            buildFastaVersion = ConfigValueFactory.fromAnyRef(fastaVersion).atKey(ParsingRulesUtils.ParsingRulesKeys.FASTA_VERSION.getJsonKey());

            buildProtein = ConfigValueFactory.fromAnyRef(protein).atKey(ParsingRulesUtils.ParsingRulesKeys.PROTEIN.getJsonKey());

            buildFastaName = ConfigValueFactory.fromIterable(fastaName).atKey(ParsingRulesUtils.ParsingRulesKeys.FASTA_NAME.getJsonKey());

            configsbuild[i] = buildLabel.withFallback(buildFastaVersion.withFallback(buildProtein).withFallback(buildFastaName));


        }
        List<ConfigValue> cfg = new ArrayList<>();
        for (int k = 0; k < setOfRules.size(); k++) {

            cfg.add(k, configsbuild[k].root());
        }
        ConfigList cfglst = ConfigValueFactory.fromIterable(cfg);
        ConfigObject reBuiltConfig = toBePreserved.withFallback(cfglst.atKey(ProlineFiles.PARSING_RULES));

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
