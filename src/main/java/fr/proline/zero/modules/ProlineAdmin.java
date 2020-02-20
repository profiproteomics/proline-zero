package fr.proline.zero.modules;

import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.SplashScreen;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;

public class ProlineAdmin {

    private static Logger logger = LoggerFactory.getLogger(ProlineAdmin.class);

    public static void setUp() throws Exception {
        //OutputStream logStream = Slf4jStream.ofCaller().asInfo();
        OutputStream logStream = null;
        runCommand(new String[]{"setup"}, logStream);
        runCommand(new String[]{"create_user", "-l", "proline", "-p", "proline"}, logStream);
        runCommand(new String[]{"create_project", "-oid", "2", "-n", "Proline_Project", "-desc", "Proline default Project"}, logStream);
    }

    public static void runCommand(String[] args, OutputStream output) throws Exception {
        String classpath = SystemUtils.toSystemClassPath("lib/*;config;" + ProlineFiles.ADMIN_JAR_FILE.getName());

        List<String> command = new ArrayList<>();
        command.add(Config.getJavaExePath());
        command.add("-Xmx1024m");

        command.add("-Djava.io.tmpdir=../data/tmp");
        command.add("-Dlogback.configurationFile=config/logback.xml");

        command.add("-classpath");
        command.add(classpath);
        command.add("fr.proline.admin.RunCommand");
        for (String argument : args) {
            command.add(argument);
        }
        int exitValue = new ProcessExecutor()
                .command(command)
                .directory(ProlineFiles.ADMIN_DIRECTORY)
                .redirectOutput(output)
                .execute()
                .getExitValue();
        logger.info("Proline admin process " + args[0] + " finishes with exit code: " + exitValue);
    }

    public static void checkUpdate(String pred) throws Exception {
        //SplashScreen
        if (pred == null) {
            pred = "";
        }
        String msg;
        if (pred.isEmpty()) {
            msg = " Datastore check update";
        } else {
            msg = pred + "... Check update";
        }
        SplashScreen.setMessage(msg);
        runCommand(new String[]{"check_for_updates"}, new checkUpdateLOStream());
    }

    private static class checkUpdateLOStream extends LogOutputStream {

        private StartedProcess process;

        @Override
        protected void processLine(String line) {

            if (line.contains("Need Update Migration:")) {
                logger.info(line.trim());
                if (line.endsWith("NO")) {
                    doMigration(NO_NEED_UPDATE);
                } else {
                    doMigration(NEED_UPDATE);
                }
            }
        }
    }
    private static boolean NEED_UPDATE = true;
    private static boolean NO_NEED_UPDATE = false;

    /**
     *
     * @param need , true = must upgrade all database
     */
    public static void doMigration(boolean need) {
        boolean yes = false;
        if (need == NEED_UPDATE) {
            String[] options = {"Do upgrade", "No"};
            yes = Popup.optionYesNO("\"Update Databse Necessary\" detected", options);
        } else {
            boolean isForeceUpdate = Config.getForceUpdate();
            logger.debug("isForeceUpdate={}", isForeceUpdate);
            if (isForeceUpdate) {
                String[] options = {"Force an upgrade", "No"};
                yes = Popup.optionYesNO("\"Need Update Databse\" no detected, do you force an upgrade? "
                        + "\n If you don't need, you can disable the force_datastore_update option in proline_launcher.config to avoid this prompt at each launche", options);
            }
        }
        if (yes) {
            try {
                logger.info("Update databse");
                Popup.info("This will take several minutes, please don't kill/stop this process");
                SplashScreen.setMessage("Initializing Proline databases...upgrading...");
                runCommand(new String[]{"upgrade_dbs"}, null);
            } catch (Exception ex) {
                logger.info("Update databse Exception :" + ex.getCause() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
            }
        } else {
            if (need == NO_NEED_UPDATE) {
                //if (need == NEED_UPDATE) {// only for test
                return;   // continue to launch proline Zero
            } else {
                logger.info("Exit without database update.");
                Popup.info("Without database update, Proline Zero will be stopped...");
                SystemUtils.end();
                System.exit(0);
            }
        }
    }
    //	private static StartedProcess process;
    //    private static String getProcessName() { return "Proline Admin"; }
    //    private static boolean isProcessAlive = false;

    public static void start() throws Exception {
//		if(process == null) {
        File adminHome = ProlineFiles.ADMIN_DIRECTORY;
        String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("config;lib/*;")).append(ProlineFiles.ADMIN_JAR_FILE.getName()).toString();
        logger.info("starting Proline Admin from path " + adminHome.getAbsolutePath());
        List<String> command = new ArrayList<>();
        command.add(Config.getJavaExePath());
        command.add(Memory.getAdminMaxMemory());
        command.add("-classpath");
        command.add(classpath);
        command.add("-Dlogback.configurationFile=config/logback.xml");
        command.add("fr.proline.admin.gui.Main"); // TODO replace Main with Monitor when RemPsdb branch is merged to trunk
//            logger.info("ABU cd '"+adminHome+"'");
//			logger.info("ABU '"+StringUtils.join(command, "' '")+"'");

//			process = new ProcessExecutor()
        new ProcessExecutor()
                .command(command)
                .directory(adminHome)
                .destroyOnExit()
                .execute();
//                    .getExitValue();
//					.destroyOnExit()
//					.start();
//
//			logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getProcessName(), process.getProcess(), AbstractProcess.getProcessPidSafely(process), isProcessAlive);
//            while(process.getProcess().isAlive()) {
//                Thread.sleep(2500);
//            }
//			stop();
//        }
    }

//    @Override
//    public void stop() throws Exception {
//        if (process != null) {
//            if (!isProcessAlive) {
//                process = null;
//            }
//        }
//    }
}
