package fr.proline.zero.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProlineFiles {

    private static Logger logger = LoggerFactory.getLogger(ProlineFiles.class);

    public final static File WORKING_DIRECTORY = new File(".").getAbsoluteFile();
    public final static File WORKING_DATA_DIRECTORY = new File(WORKING_DIRECTORY + "/data");

    private static String getFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public final static File PROLINE_ZERO_CONFIG_FILE = new File(WORKING_DIRECTORY + "/proline_launcher.config");

    public static File getProlineZeroCurrentLogFile() {
        return new File(WORKING_DIRECTORY + "/proline_launcher_log." + getFormattedDate() + ".txt");
    }

    public static File getProlineDocumentationFile() {
        File documentationFolder = new File(STUDIO_DIRECTORY + "/documentation");
        File[] files = documentationFolder.listFiles((file) -> (file.getName().endsWith(".pdf") || file.getName().endsWith(".docx.html")));
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }
    public final static String PROFI_WEBSITE = "http://www.profiproteomics.fr/";

    /**
     * data directory
     */
    public final static File DATA_TMP_DIRECTORY = new File(WORKING_DATA_DIRECTORY + "/tmp");
    public final static File DATA_SEQUENCE_REPOSITORY_DIRECTORY = new File(WORKING_DATA_DIRECTORY + "/fasta");

    /*
     * Proline Admin files
     */
    public final static File ADMIN_DIRECTORY = new File(WORKING_DIRECTORY + "/Proline-Admin-GUI-" + Config.getAdminVersion());
    public final static File ADMIN_JAR_FILE = new File(ADMIN_DIRECTORY + "/Proline-Admin-GUI-" + Config.getAdminVersion() + ".jar");
    public final static File ADMIN_LOG_FILE = new File(ADMIN_DIRECTORY + "/logs/proline_admin_gui_log.txt");
    public final static File ADMIN_CONFIG_FILE = new File(ADMIN_DIRECTORY + "/config/application.conf");

    /*
     * Proline Cortex files
     */
    public final static File CORTEX_DIRECTORY = new File(WORKING_DIRECTORY + "/Proline-Cortex-" + Config.getCortexVersion());
    public final static File CORTEX_JAR_FILE = new File(CORTEX_DIRECTORY + "/proline-cortex-" + Config.getCortexVersion() + ".jar");
    public final static File CORTEX_CONFIG_FILE = new File(CORTEX_DIRECTORY + "/config/application.conf");
    public final static File CORTEX_JMS_CONFIG_FILE = new File(CORTEX_DIRECTORY + "/config/jms-node.conf");
    public static String CORTEX_JMS_NODE_NB_RUNSERVICE = "service_thread_pool_size";

    public static String CORTEX_JMS_NODE_PORT = "jms_server_port";
    public static String CORTEX_MOUNT_POINTS_KEY ="mount_points";
    public static String CORTEX_MZDB_MOUNT_POINT = "mzdb_files";
    public static String CORTEX_RESULT_FILES_MOUNT_POINT="result_files";
    public static String CORTEX_RAW_FILES_MOUNT_POINT="raw_files";


    public static String USER_CORTEX_MZDB_MOUNT_POINT="mzdb_files";

    public static String USER_CORTEX_RESULT_FILES_Point="mascot_data";

    public static String USER_CORTEX_RAW_FILES_MOUNT_POINT="raw_files";




    public static File getCortexCurrentDebugLogFile() {
        return new File(CORTEX_DIRECTORY + "/logs/proline_cortex_log." + getFormattedDate() + ".txt");
    }

    public static File getCortexCurrentMzdbLogFile() {
        int i = 0;
        return new File(CORTEX_DIRECTORY + "/logs/proline_mzdb_log." + getFormattedDate() + "." + i + ".txt");
    }

    /*
     * JMS/HornetQ files
     */
    public final static File HORNETQ_DIRECTORY = new File(WORKING_DIRECTORY + "/Proline-Cortex-" + Config.getCortexVersion() + "/hornetq_light-" + Config.getHornetQVersion());
    public final static File HORNETQ_CONFIG_DIRECTORY = new File(HORNETQ_DIRECTORY + "/config/stand-alone/non-clustered");
    public final static File HORNETQ_CONFIG_FILE = new File(HORNETQ_CONFIG_DIRECTORY + "/hornetq-configuration.xml");
    public final static File HORNETQ_RMI_CONFIG_FILE = new File(HORNETQ_CONFIG_DIRECTORY + "/hornetq-beans.xml");
    public final static File HORNETQ_LOG_FILE = new File(HORNETQ_DIRECTORY + "/logs/hornetq.log");

    /*
     * Sequence Repository files
     */
    public final static File SEQREPO_DIRECTORY = new File(WORKING_DIRECTORY + "/PM-SequenceRepository-" + Config.getSeqRepoVersion());
    public final static File SEQREPO_JAR_FILE = new File(SEQREPO_DIRECTORY + "/pm-sequence-repository-" + Config.getSeqRepoVersion() + ".jar");
    public final static File SEQREPO_PARSE_RULES_CONFIG_FILE = new File(SEQREPO_DIRECTORY + "/config/parsing-rules.conf");
    public final static File SEQREPO_DATA_STORE_CONFIG_FILE = new File(SEQREPO_DIRECTORY + "/config/application.conf");
    public final static File SEQREPO_JMS_CONFIG_FILE = new File(SEQREPO_DIRECTORY + "/config/jms-node.conf");

    public static File getSeqrepoCurrentLogFile() {
        return new File(SEQREPO_DIRECTORY + "/sequence_repository_debug_" + getFormattedDate() + ".txt");
    }

    /*
     * Proline Studio files
     */
    public final static File STUDIO_DIRECTORY = new File(WORKING_DIRECTORY + "/ProlineStudio-" + Config.getStudioVersion());
    public final static File STUDIO_JAR_FILE = new File(STUDIO_DIRECTORY + "/prolinestudio-resultexplorer-" + Config.getStudioVersion() + ".jar");


    public final static File STUDIO_CONFIG_FILE = new File(STUDIO_DIRECTORY + "/etc/prolinestudio.conf");
    public final static File STUDIO_LOG_FILE = new File(STUDIO_DIRECTORY + "/Proline_Studio-" + System.getProperty("user.name") + ".log");
    public final static File STUDIO_ERROR_LOG_FILE = new File(STUDIO_DIRECTORY + "/Proline_Studio-Error-" + System.getProperty("user.name") + ".log");
    public final static File STUDIO_PREFERENCES_FILE = new File(WORKING_DATA_DIRECTORY + "/.prolinestudio/Preferences.properties");
    /*
     * PostgreSQL files
     */
    public final static String PG_DATASTORE_RELATIVE_PATH = WORKING_DATA_DIRECTORY.getName() + "/databases/pg";
    public final static File PG_DATASTORE = new File(WORKING_DIRECTORY + "/" + PG_DATASTORE_RELATIVE_PATH);
    public final static File PG_CONFIG_FILE = new File(PG_DATASTORE, "postgresql.conf");
    public final static File PG_DEFAULT_CONFIG_FILE = new File(PG_DATASTORE, "postgresql.conf.default");
    public final static File PG_PREVIOUS_CONFIG_FILE = new File(PG_DATASTORE, "postgresql.conf.previous");
    public final static File PG_PASSWD_FILE = new File(WORKING_DIRECTORY, "postgres.passwd");
    public final static String PG_LOG_FILENAME = "pgsql.log";
    public final static File PG_LOG_FILE = new File(WORKING_DIRECTORY, "pgsql.log");

    /*
     * H2 files
     */
    public final static String H2_DATASTORE_RELATIVE_PATH = WORKING_DATA_DIRECTORY.getName() + "/databases/h2";
    public final static File H2_DATASTORE = new File(WORKING_DIRECTORY + "/" + H2_DATASTORE_RELATIVE_PATH);

    /*
     * Images
     */
    private static URL getImage(String name) {
        // TODO put images in img/ ?
//        return getRessource("img/"+name);
        return getRessource(name);
    }

    private static URL getRessource(String name) {
        URL url = ProlineFiles.class.getClassLoader().getResource(name);
        if (url == null) {
            logger.warn("Ressource '" + name + "' is not found");
        }
        return url;
    }

    public final static URL SPLASHSCREEN_IMAGE = getImage("profi.png");
    public final static URL PROFI_ICON = getImage("logo32x32.png");
    public final static URL QUIT_ICON = getImage("cross.png");
    public final static URL DEFAULT_ICON = getImage("eraser.png");
    public final static URL DOCUMENT_ICON = getImage("document.png");
    public final static URL EDIT_ICON = getImage("settings.png");
    public final static URL STATUS_RUNNING_ICON = getImage("tick.png");
    public final static URL STATUS_STOPPED_ICON = getImage("stop.png");
    public final static URL PROGRESS_ICON = getImage("hourGlass.png");
    public final static URL HELP_ICON = getImage("question.png");

}
