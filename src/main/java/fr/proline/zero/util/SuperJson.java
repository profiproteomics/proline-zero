package fr.proline.zero.util;


import com.typesafe.config.*;

import com.typesafe.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

public class SuperJson {
    private final Logger logger = LoggerFactory.getLogger(SuperJson.class);
    private static SuperJson instanceMountPoints;
    private static SuperJson instanceParseRules;
    private Config m_cortexProlineConfig = null;
    private Config parsingRules=null;

    private SuperJson(){
        ConfigParseOptions options=ConfigParseOptions.defaults();
        options.setSyntax(ConfigSyntax.CONF);
        m_cortexProlineConfig=ConfigFactory.parseFile(ProlineFiles.CORTEX_CONFIG_FILE,options);
        parsingRules = ConfigFactory.parseFile(ProlineFiles.PARSING_RULES_CONFIG_FILE, options);
    }
    public static SuperJson getInstanceMountPoints(){
        if (instanceMountPoints==null){
            instanceMountPoints=new SuperJson();
        }
        return instanceMountPoints;
    }
    public static SuperJson getInstanceParseRules(){
        if (instanceParseRules==null){
            instanceParseRules=new SuperJson();
        }
        return instanceParseRules;
    }
    public Config getCortexConfig(){
        return  m_cortexProlineConfig;
    }
    public Config getParsingConfig() {
        return parsingRules;
    }

    private HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointsJson() {
        try {
            HashMap<MountPointUtils.MountPointType, Map<String, String>> mountPointMap = new HashMap<>();
            if (m_cortexProlineConfig.hasPath(ProlineFiles.CORTEX_MOUNT_POINTS_KEY)) {
                Config mountPointsCfg = m_cortexProlineConfig.getConfig(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
                Iterator<MountPointUtils.MountPointType> mpTypeIt = Arrays.stream(MountPointUtils.MountPointType.values()).iterator();
                while (mpTypeIt.hasNext()){
                    MountPointUtils.MountPointType nextMp = mpTypeIt.next();
                    if (mountPointsCfg.hasPath(nextMp.getJsonKey())) {
                        HashMap<String, String> specificMountPointMap = new HashMap<>();
                        Config specificMPCfg = mountPointsCfg.getConfig(nextMp.getJsonKey());
                        Iterator<Map.Entry<String, ConfigValue>> mpEntriesIt = specificMPCfg.entrySet().iterator();
                        while (mpEntriesIt.hasNext()) {
                            Map.Entry<String, ConfigValue> entry = mpEntriesIt.next();
                            String label = entry.getKey();
                            String val = specificMPCfg.getString(label);
                            specificMountPointMap.put(label, val);
                            mountPointMap.put(nextMp, specificMountPointMap);
                        }
                    }
                }
            }

            return mountPointMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void updateCortexConfigFileJson(HashMap<MountPointUtils.MountPointType, Map<String, String>> mpt) {

        ConfigObject toBePreserved = SuperJson.getInstanceMountPoints().getCortexConfig().root().withoutKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        int sizeOfMpts = MountPointUtils.MountPointType.values().length;
        com.typesafe.config.Config[] builtConfig = new com.typesafe.config.Config[sizeOfMpts];
        com.typesafe.config.Config[] mergedConf = new com.typesafe.config.Config[builtConfig.length];
        int cpt = 0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> specificMPEntries = mpt.get(mountPointType);
            if (specificMPEntries == null) {
                builtConfig[cpt] = ConfigFactory.empty().atKey(mountPointType.getJsonKey());
            } else {
                builtConfig[cpt] = ConfigValueFactory.fromMap(specificMPEntries).atKey(mountPointType.getJsonKey());
            }
            if (cpt == 0) {
                mergedConf[cpt] = builtConfig[cpt];
            } else {
                mergedConf[cpt] = mergedConf[cpt - 1].withFallback(builtConfig[cpt]);
            }
            cpt++;
        }

        com.typesafe.config.Config finalMpts = mergedConf[cpt - 1].atKey(ProlineFiles.CORTEX_MOUNT_POINTS_KEY);
        // final merge
        Config finalConfig = finalMpts.withFallback(toBePreserved);
        String finalWrite = finalConfig.root().render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        // configHasBeenChanged=true;

        try {
            FileWriter writer = new FileWriter(ProlineFiles.CORTEX_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Return MountPoint Values read from config files.
    public HashMap<MountPointUtils.MountPointType, Map<String,String>> getMountPointMaps()
    {
        return getMountPointsJson();
    }


    public ArrayList<String> getFastaPaths() {
        try {

            List<String> paths = null;
            ArrayList<String> array = null;
            if (parsingRules.hasPath("local-fasta-directories")) {

                paths = parsingRules.getStringList("local-fasta-directories");
                array = new ArrayList<String>(paths);


            }

            return array;
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
    public void updateConfigFastaDirectories(ArrayList<String> fastaPaths){

        ConfigObject toBePreserved=SuperJson.getInstanceParseRules().getParsingConfig().root().withoutKey(ProlineFiles.FASTA_DIRECTORIES);

        ConfigList cfg=ConfigValueFactory.fromIterable(fastaPaths);
        ConfigObject finalbuild=toBePreserved.withFallback(cfg.atKey(ProlineFiles.FASTA_DIRECTORIES));
        String finalWrite = finalbuild.render(ConfigRenderOptions.concise().setFormatted(true).setJson(false).setComments(true));
        try {
            FileWriter writer = new FileWriter(ProlineFiles.PARSING_RULES_CONFIG_FILE);
            writer.write(finalWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
