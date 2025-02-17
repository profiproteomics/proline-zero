package fr.proline.zero.modules;

import fr.proline.zero.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.SplashScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutionSession {

    private static Logger logger = LoggerFactory.getLogger(ExecutionSession.class);

    private static JMSServer jmsServer;
    private static Cortex cortex;
    private  static ProlineAdmin admin;
    private static SequenceRepository seqRepo;
    private static IZeroModule datastore;
    private static ProlineStudio studio;


    private static boolean isActive = false;
    private static boolean checkStudioActive = true;
    private static List<IZeroModule> activeModules = null;

    public static List<IZeroModule> getActiveModules(){
        if (activeModules == null){
            activeModules = new ArrayList<>();
            activeModules.add(getDataStore());
            activeModules.add(getJMSServer());
            activeModules.add(getProlineAdmin());
            activeModules.add(getCortex());
            if(ConfigManager.getInstance().isSeqRepActive())
                activeModules.add(getSeqRepo());
            if(ConfigManager.getInstance().isStudioActive())
                activeModules.add(getStudio());

        }
        return activeModules;
    }

    public static int getModuleCount(){
        return getActiveModules().size();
    }

    public static IZeroModule getModuleAt(int index){
        if(index <0 || index >= getActiveModules().size())
            return  null;
        return getActiveModules().get(index);
    }


    public static void initialize()  {
        logger.info("Operating system is " + SystemUtils.getOSType().name());
        logger.info("Working directory is " + ProlineFiles.WORKING_DIRECTORY.getAbsolutePath());

        // check that linux user is not root when running PostgreSQL
        if (SystemUtils.isOSUnix() && !ConfigManager.getInstance().getDatastoreType().equalsIgnoreCase("H2") && System.getProperty("user.name").equalsIgnoreCase("root")) {
            throw new RuntimeException("PostgreSQL cannot be used by user 'root'");
        }

        if (ConfigManager.getInstance().isDebugMode()) {
            logger.info("Debug mode is activated");
        }

        //Check Studio is Alive to keep Zero alive only if not in server mode
        checkStudioActive = ConfigManager.getInstance().isStudioActive();

        SplashScreen.initialize();
        isActive = true;
    }

    public static ProlineAdmin getProlineAdmin(){
        if(admin== null)
            admin = new ProlineAdmin();
        return admin;
    }

    public static JMSServer getJMSServer() {
        if (jmsServer == null) {
            jmsServer = new JMSServer();
        }
        return jmsServer;
    }

    public static Cortex getCortex() {
        if (cortex == null) {
            cortex = new Cortex();
        }
        return cortex;
    }

    public static SequenceRepository getSeqRepo() {
        if (seqRepo == null) {
            seqRepo = new SequenceRepository();
        }
        return seqRepo;
    }

    public static IZeroModule getDataStore() {
        if (datastore == null) {
            String datastoreType = ConfigManager.getInstance().getDatastoreType();

            if (datastoreType.equalsIgnoreCase("H2")) {
                datastore = new H2();
            } else {
                datastore = new PostgreSQL();
            }
        }
        return datastore;
    }

    public static ProlineStudio getStudio() {
        if (studio == null) {
            studio = new ProlineStudio();
        }
        return studio;
    }

    public synchronized static void updateConfigPort() {
        if (getDataStore().getModuleName().equals(PostgreSQL.NAME)) {
            updatePostgreSQLPortConfig();
        }
        updateJmsPortConfig();
        updateJmsBatchPortConfig();
        updateJnpPortConfig();
        updateJnpRmiPortConfig();
    }

    private static void updatePostgreSQLPortConfig() {
        int port = ConfigManager.getInstance().getAdvancedManager().getDataStorePort();
        //int defaultPort = getDataStore().getDatastoreName().equals(PostgreSQL.NAME) ? Config.DEFAULT_POSTGRESQL_PORT : Config.DEFAULT_H2_PORT;
        //force to change port even if port=defaultPort
        String newPort = "port=\"" + port + "\"";
        String regex = "port\\s*=\\s*\"(\\d{4,5})\"";  //with "" arround number
        File cortexConfigFile = ProlineFiles.CORTEX_CONFIG_FILE;
        updateProperty(cortexConfigFile, regex, newPort);
        File adminConfigFile = ProlineFiles.ADMIN_CONFIG_FILE;
        updateProperty(adminConfigFile, regex, newPort);
        File seqRepoConfigFile = ProlineFiles.SEQREPO_DATA_STORE_CONFIG_FILE;
        updateProperty(seqRepoConfigFile, regex, newPort);
    }



    private static void updateJmsPortConfig() {
        int port = ConfigManager.getInstance().getAdvancedManager().getJmsServerPort();
        //force to change port even if port=defaultPort
        String newPort = SettingsConstant.JMS_PORT + " = " + port;
        String regex = SettingsConstant.JMS_PORT + "\\s*=\\s*(\\d{4,5})"; //without "" arround number
        File cortexConfigFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
        updateProperty(cortexConfigFile, regex, newPort);
        File seqRepoConfigFile = ProlineFiles.SEQREPO_JMS_CONFIG_FILE;
        updateProperty(seqRepoConfigFile, regex, newPort);
        File hornetqConfigFile = ProlineFiles.HORNETQ_CONFIG_FILE;
        final String regexXML = "hornetq.remoting.netty.port\\s*:\\s*\\d{4,5}";
        updateProperty(hornetqConfigFile, regexXML, "hornetq.remoting.netty.port:" + port);

        File studioPrefereceFile = ProlineFiles.STUDIO_PREFERENCES_FILE;
        String regexStudio = "serverURL=localhost";
        String value = (port != SettingsConstant.DEFAULT_JMS_PORT) ? regexStudio + ":" + port : regexStudio;
        final String regexJmsUrl = regexStudio + "[:\\d]*";
        updateProperty(studioPrefereceFile, regexJmsUrl, value);

    }

    private static void updateJmsBatchPortConfig() {
        int port = ConfigManager.getInstance().getAdvancedManager().getJmsBatchServerPort();
        //if (port != Config.DEFAULT_JMS_BATCH_PORT) {
        File hornetqConfigFile = ProlineFiles.HORNETQ_CONFIG_FILE;
        final String regexXML = "hornetq.remoting.netty.batch.port\\s*:\\s*\\d{4,5}";
        updateProperty(hornetqConfigFile, regexXML, "hornetq.remoting.netty.batch.port:" + port);
        //}
    }

    private static void updateJnpRmiPortConfig() {
        int port = ConfigManager.getInstance().getAdvancedManager().getJnpRmiServerPort();
        //if (port != Config.DEFAULT_JMS_JNP_RMI_PORT) {
        File hornetqConfigFile = ProlineFiles.HORNETQ_RMI_CONFIG_FILE;
        final String regexXML = "jnp.rmiPort\\s*:\\s*\\d{4,5}";
        updateProperty(hornetqConfigFile, regexXML, "jnp.rmiPort:" + port);
        //}
    }

    private static void updateJnpPortConfig() {
        int port = ConfigManager.getInstance().getAdvancedManager().getJnpServerPort();
        //if (port != Config.DEFAULT_JMS_JNP_PORT) {
        File hornetqConfigFile = ProlineFiles.HORNETQ_RMI_CONFIG_FILE;
        final String regexXML = "jnp.port\\s*:\\s*\\d{4,5}";
        updateProperty(hornetqConfigFile, regexXML, "jnp.port:" + port);
        //}
    }

    public synchronized static void updateCortexNbParallelizableServiceRunners() {
        String nbThread = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + " = " + ConfigManager.getInstance().getAdvancedManager().getCortexNbParallelizableServiceRunners();
        File configFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
        String regex = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + "\\s*=\\s*([\\d-]+)";//possible -1
        updateProperty(configFile, regex, nbThread);
    }

    public static void updateProperty(File configFile, String regex, String value) {
        logger.info("Replace " + regex + " in file " + configFile.getPath() + " to " + value);
        try {
            List<String> lines = Files.lines(configFile.toPath()).map(l -> l.replaceAll(regex, value)).collect(Collectors.toList());
            Files.write(configFile.toPath(), lines);
        } catch (Exception e) {
            logger.error("Error replacing " + value + "in file " + configFile.getPath(), e);
        }
    }

    public static void end() throws Exception {
        for(IZeroModule m: getActiveModules()){
            if(m.isProcessAlive())
                m.stop();
        }

        isActive = false;
    }

    public static boolean isSessionToBeClose() {
        if(checkStudioActive)
            return !getStudio().isProcessAlive();
        return false;
    }

    public static boolean isSessionActive() {
        return isActive;
    }


    public static String getMzdbFolder() {
        File configFile = ProlineFiles.CORTEX_CONFIG_FILE;
        final String regex = ProlineFiles.CORTEX_MZDB_MOUNT_POINT + "\\s*=\\s*\"([\\w.\\/]+)\"";
        return getProperty(configFile, regex);
    }


    private static String getProperty(File file, String regex) {
        try {

            final Pattern pattern = Pattern.compile(regex);
            FileInputStream inputStream;
            inputStream = new FileInputStream(file);
            Scanner fileScanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
            Matcher matcher;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(file.getPath() + " FileNotFoundException", ex);
        }
        return "";
    }

}
