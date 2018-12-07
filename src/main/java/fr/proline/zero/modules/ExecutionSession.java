package fr.proline.zero.modules;

import fr.proline.zero.util.ProlineFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.SystemUtils;

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
        if (datastore == null) {
            String datastoreType = Config.getDatastoreType();

            if (datastoreType.equalsIgnoreCase("H2")) {
                // nothing to configure
            } else {
                getCortex().updatePostgreSQLPortConfig();
                getSeqRepo().updatePostgreSQLPortConfig();
            }
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

}
