package fr.proline.zero.modules;

import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProlineAdmin  implements  IZeroModule {

    protected boolean isProcessAlive = false;
    private static Logger logger = LoggerFactory.getLogger("ZeroModule");

    protected String moduleName;
    public ProlineAdmin() {
        moduleName = "Proline Admin";
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    public boolean isProcessAlive() {
        return isProcessAlive;
    }

    public void init() throws Exception {
        OutputStream logStream = null;
        runCommand(new String[]{"setup"}, logStream);
        runCommand(new String[]{"create_user", "-l", "proline", "-p", "proline"}, logStream);
        runCommand(new String[]{"create_project", "-oid", "2", "-n", "Proline_Project", "-desc", "Proline default Project"}, logStream);
    }

    public static void runCommand(String[] args, OutputStream output) throws Exception {
        String classpath = SystemUtils.toSystemClassPath("lib/*;config;" + ProlineFiles.ADMIN_JAR_FILE.getName());

        List<String> command = new ArrayList<>();
        command.add(ConfigManager.getInstance().getAdvancedManager().getJvmExePath());
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
     * @param migrationNeeded , true = must upgrade all database
     */
    public static void doMigration(boolean migrationNeeded) {
        boolean updateConfirmed = false;

        //--- Verify if migration should be done
        if (migrationNeeded == NEED_UPDATE) {
            String[] options = {"Do upgrade", "No"};
            updateConfirmed = Popup.optionYesNO("A database schema update is necessary (new version detected)", options);
        } else {
            boolean isForeceUpdate = ConfigManager.getInstance().getAdvancedManager().getForceDataStoreUpdate();
            logger.debug("isForeceUpdate={}", isForeceUpdate);
            if (isForeceUpdate) {
                String[] options = {"Do update", "No"};
                updateConfirmed = Popup.optionYesNO(" No database schema update has been detected but the force update option is set.\nA upgrade may be necessary for data update. Do you want to run update ? "
                        , options);
            }
        }

        //--- Run migration
        if (updateConfirmed) {
            try {
                logger.info("Update database");
                Popup.info("This may take several minutes, please don't kill/stop this process");
                SplashScreen.setMessage("Initializing Proline databases...upgrading...");
                runCommand(new String[]{"upgrade_dbs"}, null);
            } catch (Exception ex) {
                logger.info("Update database Exception :" + ex.getCause() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
            }
        } else if (migrationNeeded == NEED_UPDATE) {
            // Not run but necessary. exit Proline
            logger.info("Exit without database update.");
            Popup.info("Proline Zero can't be launch Without a database update. Will exit...");
//            try {
//                Thread.sleep(3000); // to see message
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            SystemUtils.end();
            System.exit(0);
        }

    }

    public void start() throws Exception {
        try {
            isProcessAlive = true;
            checkUpdate("");
        } finally {
            isProcessAlive = false;
        }
    }

    public void stop() throws Exception {

    }

    public static void startGui() throws Exception {
//		if(process == null) {
        File adminHome = ProlineFiles.ADMIN_DIRECTORY;
        String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("config;lib/*;")).append(ProlineFiles.ADMIN_JAR_FILE.getName()).toString();
        logger.info("starting Proline Admin from path " + adminHome.getAbsolutePath());
        List<String> command = new ArrayList<>();
        command.add(ConfigManager.getInstance().getAdvancedManager().getJvmExePath());
        command.add("-Xmx"+ConfigManager.getInstance().getMemoryManager().getStudioMemory()+"M"); // VDS TODO : Run with Studio Mem ... Or create other... how admin in launch in any case !
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

}
