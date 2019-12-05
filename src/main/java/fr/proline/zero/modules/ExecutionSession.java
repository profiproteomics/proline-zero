package fr.proline.zero.modules;

import fr.proline.zero.util.ProlineFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.SystemUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutionSession {

    private static Logger logger = LoggerFactory.getLogger(ExecutionSession.class);

    private static JMSServer jmsServer;
    private static Cortex cortex;
    private static SequenceRepository seqRepo;
    private static DataStore datastore;
    private static ProlineStudio studio;
    private static boolean isActive = false;

    public static void initialize() throws Exception {
        logger.info("Operating system is " + SystemUtils.getOSType().name());
        logger.info("Working directory is " + ProlineFiles.WORKING_DIRECTORY.getAbsolutePath());

        // check that linux user is not root when running PostgreSQL
        if (SystemUtils.isOSUnix() && !Config.getDatastoreType().equalsIgnoreCase("H2") && System.getProperty("user.name").equalsIgnoreCase("root")) {
            throw new RuntimeException("PostgreSQL cannot be used by user 'root'");
        }

        if (Config.isDebugMode()) {
            logger.info("Debug mode is activated");
        }

        SplashScreen.initialize();
        isActive = true;
    }

    public static JMSServer getJMSServer() {
        if (jmsServer == null) {
            int port = getJMSServerPort();
            jmsServer = new JMSServer(port);
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

    public static DataStore getDataStore() {
        if (datastore == null) {
            String datastoreType = Config.getDatastoreType();

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
        if (getDataStore().getDatastoreName().equals(PostgreSQL.NAME)) {
            updatePostgreSQLPortConfig();
        }
        updateJmsPortConfig();
        updateJmsBatchPortConfig();
        updateJnpPortConfig();
        updateJnpRmiPortConfig();
    }

    private static void updatePostgreSQLPortConfig() {
        int port = Config.getPostgreSQLPort();
        if (port != Config.DEFAULT_POSTGRESQL_PORT) {
            String newPort = "port=\"" + port + "\"";
            String regex = "port\\s*=\\s*\"(\\d+)\"";  //with "" arround number
            File cortexConfigFile = ProlineFiles.CORTEX_CONFIG_FILE;
            updateProperty(cortexConfigFile, regex, newPort);
            File adminConfigFile = ProlineFiles.ADMIN_CONFIG_FILE;
            updateProperty(adminConfigFile, regex, newPort);
            File seqRepoConfigFile = ProlineFiles.SEQREPO_DATA_STORE_CONFIG_FILE;
            updateProperty(seqRepoConfigFile, regex, newPort);
        }
    }

    private static void updateJmsPortConfig() {
        int port = Config.getJmsPort();
        if (port != -1 && port != Config.DEFAULT_JMS_PORT) {
            String newPort = Config.JMS_PORT + " = " + port;
            String regex = Config.JMS_PORT + "\\s*=\\s*([\\d-]+)"; //without "" arround number
            File cortexConfigFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
            updateProperty(cortexConfigFile, regex, newPort);
            File seqRepoConfigFile = ProlineFiles.SEQREPO_JMS_CONFIG_FILE;
            updateProperty(seqRepoConfigFile, regex, newPort);
            File hornetqConfigFile = ProlineFiles.HORNETQ_CONFIG_FILE;
            final String regexXML = "hornetq.remoting.netty.port\\s*:\\s*\\d{4}";
            updateProperty(hornetqConfigFile, regexXML, "hornetq.remoting.netty.port:" + port);
            File studioPrefereceFile = ProlineFiles.STUDIO_PREFERENCES_FILE;
            final String regexJmsUrl = "serverURL=localhost[:\\d]*";
            updateProperty(studioPrefereceFile, regexJmsUrl, "serverURL=localhost:" + port);
        }
    }

    private static void updateJmsBatchPortConfig() {
        int port = Config.getJmsBatchPort();
        if (port != -1 && port != Config.DEFAULT_JMS_BATCH_PORT) {
            File hornetqConfigFile = ProlineFiles.HORNETQ_CONFIG_FILE;
            final String regexXML = "hornetq.remoting.netty.batch.port\\s*:\\s*\\d{4}";
            updateProperty(hornetqConfigFile, regexXML, "hornetq.remoting.netty.batch.port:" + port);
        }
    }

    private static void updateJnpRmiPortConfig() {
        int port = Config.getJnpRmiPort();
        if (port != -1 && port != Config.DEFAULT_JMS_JNP_RMI_PORT) {
            File hornetqConfigFile = ProlineFiles.HORNETQ_RMI_CONFIG_FILE;
            final String regexXML = "jnp.rmiPort\\s*:\\s*\\d{4}";
            updateProperty(hornetqConfigFile, regexXML, "jnp.rmiPort:" + port);
        }
    }

    private static void updateJnpPortConfig() {
        int port = Config.getJnpPort();
        if (port != -1) {
            File hornetqConfigFile = ProlineFiles.HORNETQ_RMI_CONFIG_FILE;
            final String regexXML = "jnp.port\\s*:\\s*\\d{4}";
            updateProperty(hornetqConfigFile, regexXML, "jnp.port:" + port);
        }
    }

    public synchronized static void updateCortexNbParallelizableServiceRunners() {
        String nbThread = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + " = " + Config.getCortexNbParallelizableServiceRunners();
        File configFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
        String regex = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + "\\s*=\\s*([\\d-]+)";
        updateProperty(configFile, regex, nbThread);
    }

    private static void updateProperty(File configFile, String regex, String value) {
        logger.info("Replace " + regex + " in file " + configFile.getPath()+" "+configFile.getName() + " to " + value);
        try {
            List<String> lines = Files.lines(configFile.toPath()).map(l -> l.replaceAll(regex, value)).collect(Collectors.toList());
            Files.write(configFile.toPath(), lines, StandardOpenOption.SYNC);
        } catch (Exception e) {
            logger.error("Error replacing " + value + "in file " + configFile.getPath(), e);
        }
    }

    public static void end() throws Exception {
//        ProlineAdmin.stop();
        studio.stop();
        seqRepo.stop();
        cortex.stop();
        jmsServer.stop();
        datastore.stop();
        isActive = false;
    }

    public static boolean isSessionActive() {
        return isActive;
    }

    private static int getJMSServerPort() {
        String regex_port = ProlineFiles.CORTEX_JMS_NODE_PORT + "\\s*=\\s*([\\d]+)";
        File configFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
        String value = getProperty(configFile, regex_port);
        return Integer.parseInt(value);
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
                    String value = matcher.group(1);
                    return value;
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(file.getPath() + " FileNotFoundException", ex);
        }
        return "";
    }

}
