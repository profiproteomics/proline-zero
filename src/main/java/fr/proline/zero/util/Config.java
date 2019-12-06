package fr.proline.zero.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.Main;

public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private static Properties properties;
    public static String JMS_PORT = "jms_server_port";
    public static String POSTGRESQL_PORT = "postgresql_port";
    public static int DEFAULT_POSTGRESQL_PORT = 5433;
    public static int DEFAULT_JMS_PORT = 5445;
    public static int DEFAULT_JMS_BATCH_PORT = 5455;
    public static int DEFAULT_JMS_JNP_PORT = 1099;
    public static int DEFAULT_JMS_JNP_RMI_PORT = 1098;

    private static void initialize() {
        if (properties == null) {
            try {
                properties = new Properties();
                File configFile = new File("proline_launcher.config");
                properties.load(new FileInputStream(configFile));
            } catch (Throwable t) {
                logger.error("Error while reading configuration file, using default configuration instead", t);
                InputStream is = Main.class.getClassLoader().getResourceAsStream("fr/proline/zero/proline_launcher.config");
                try {
                    properties.load(is);
                } catch (IOException ioe) {
                    logger.error("Error reading default configuration file", ioe);
                }
            }
        }
    }

    public static String getDatastoreType() {
        Config.initialize();
        return properties.getProperty("datastore");
    }

    public static String getDataFolder() {
        Config.initialize();
        return properties.getProperty("data_folder");
    }

    public static String getDataTmpFolder() {
        Config.initialize();
        return properties.getProperty("data_folder");
    }

    public static int getDefaultTimeout() {
        Config.initialize();
        int timeout;
        try {
            timeout = Integer.parseInt(properties.getProperty("server_default_timeout"));
        } catch (Throwable t) {
            timeout = 60;
            logger.warn("No timeout has been defined or timeout is not readable, using default timeout (" + timeout + " seconds).");
        }
        // multiply timeout by 1000 to have it in milliseconds
        return timeout * 1000;
    }

    public static int getPostgreSQLPort() {
        Config.initialize();
        String value = properties.getProperty(POSTGRESQL_PORT);
        if (value == null) {
            return DEFAULT_POSTGRESQL_PORT;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static int getH2Port() {
        Config.initialize();
        return Integer.parseInt(properties.getProperty(POSTGRESQL_PORT));
    }

    public static int getJmsPort() {
        Config.initialize();
        String value = properties.getProperty(JMS_PORT);
        if (value == null) {
            return DEFAULT_JMS_PORT;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static int getJmsBatchPort() {
        Config.initialize();
        String value = properties.getProperty("jms_server_batch_port");
        if (value == null) {
            return DEFAULT_JMS_BATCH_PORT;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static int getJnpPort() {
        Config.initialize();
        String value = properties.getProperty("jnp_port");
        if (value == null) {
            return DEFAULT_JMS_JNP_PORT;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static int getJnpRmiPort() {
        Config.initialize();
        String value = properties.getProperty("jnp_rmi_port");
        if (value == null) {
            return DEFAULT_JMS_JNP_RMI_PORT;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static int getCortexNbParallelizableServiceRunners() {
        Config.initialize();
        return Integer.parseInt(properties.getProperty(ProlineFiles.CORTEX_JMS_NODE_NB_RUNSERVICE));
    }

    public static String getHornetQVersion() {
        Config.initialize();
        return properties.getProperty("hornetq_version");
    }

    public static String getCortexVersion() {
        Config.initialize();
        return properties.getProperty("cortex_version");
    }

    public static String getSeqRepoVersion() {
        Config.initialize();
        return properties.getProperty("seqrepo_version");
    }

    public static File getJavaHome() {
        Config.initialize();
        // if null try to find studio's jre, if still null return current jre
        String javaPath = properties.getProperty("java_home");
        if (javaPath == null) {
            javaPath = "ProlineStudio-" + Config.getStudioVersion() + "/jre";
        }
        return new File(javaPath);
    }

    public static String getJavaExePath() {
        Config.initialize();
        return new File(Config.getJavaHome() + "/bin/java").getAbsolutePath();
    }

    public static String getWorkingMemory() {
        Config.initialize();
        return properties.getProperty("server_max_memory");
    }

    public static String getAdminVersion() {
        Config.initialize();
        return properties.getProperty("admin_version");
    }

    public static String getStudioVersion() {
        Config.initialize();
        return properties.getProperty("studio_version");
    }

    private static boolean isBooleanTrue(String booleanValue) {
        if (booleanValue != null && (booleanValue.equals("on") || booleanValue.equals("true") || booleanValue.equals("yes"))) {
            return true;
        }
        return false;
    }

    public static boolean isServerMode() {
        Config.initialize();
        String debug = properties.getProperty("server_mode");
        return Config.isBooleanTrue(debug);
    }

    public static boolean isDebugMode() {
        Config.initialize();
        String debug = properties.getProperty("debug_mode");
        return Config.isBooleanTrue(debug);
    }

    public static boolean isSeqRepoEnabled() {
        Config.initialize();
        String seqRepoDisabled = properties.getProperty("disable_sequence_repository");
        return !Config.isBooleanTrue(seqRepoDisabled);
    }

}
