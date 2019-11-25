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

    public static void updateConfigurationFiles() {
        String datastoreType = Config.getDatastoreType();

        if (datastoreType.equalsIgnoreCase("H2")) {
            // nothing to configure
        } else {
            updatePostgreSQLPortConfig();
            updateCortexNbParallelizableServiceRunners();
        }
    }

    private static void updatePostgreSQLPortConfig() {
        File cortexConfigFile = ProlineFiles.CORTEX_CONFIG_FILE;
        updatePostgreSQLPortConfig(cortexConfigFile);
        File adminConfigFile = ProlineFiles.ADMIN_CONFIG_FILE;
        updatePostgreSQLPortConfig(adminConfigFile);
        File seqRepoConfigFile = ProlineFiles.SEQREPO_DATA_STORE_CONFIG_FILE;
        updatePostgreSQLPortConfig(seqRepoConfigFile);
    }

    private static void updatePostgreSQLPortConfig(File configFile) {
        String newPort = "port=\"" + Config.getPostgreSQLPort() + "\"";
        logger.info("Replace PG port in file " + configFile.getAbsolutePath() + " to " + newPort);
        try {
            List<String> lines = Files.lines(configFile.toPath()).map(l -> l.replaceAll("port\\s*=\\s*\"(\\d+)\"", newPort)).collect(Collectors.toList());
            Files.write(configFile.toPath(), lines);
        } catch (Exception e) {
            logger.error("Error replacing database port in file " + configFile.getAbsolutePath(), e);
        }
    }

    private static void updateCortexNbParallelizableServiceRunners() {
        String nbThread = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + " = " + Config.getCortexNbParallelizableServiceRunners();
        File configFile = ProlineFiles.CORTEX_JMS_CONFIG_FILE;
        logger.info("Replace " + ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + " in file " + configFile.getAbsolutePath() + " to " + nbThread);
        String regex = ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE + "\\s*=\\s*([\\d-]+)";
        try {
            List<String> lines = Files.lines(configFile.toPath()).map(l -> l.replaceAll(regex, nbThread)).collect(Collectors.toList());
            Files.write(configFile.toPath(), lines);
        } catch (Exception e) {
            logger.error("Error replacing " + nbThread + "in file " + configFile.getAbsolutePath(), e);
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
