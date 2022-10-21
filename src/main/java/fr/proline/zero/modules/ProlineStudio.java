package fr.proline.zero.modules;

import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProlineStudio extends AbstractProcess {

    private static String studioUserDir = "/.prolinestudio";

    public ProlineStudio() {
        moduleName = "Proline Studio";
    }

    /**
     * in order to delete the file
     * "../data/.prolinestudio/var/cache/all-installer.dat
     */
    private void cleanCache() {
        String cacheFolder = ProlineFiles.WORKING_DATA_DIRECTORY + studioUserDir + "/var/cache";
        File cacheFolderFile = new File(cacheFolder);
        if (cacheFolderFile.isDirectory() && cacheFolderFile.exists()) {
            try {
                logger.info("clean folder: " + cacheFolderFile.getAbsolutePath());
                FileUtils.cleanDirectory(cacheFolderFile);
            } catch (IOException ex) {
                logger.info("clean folder" + cacheFolder + ex.getCause() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
            }
        }
    }

    @Override
    public void start() throws Exception {
        String classpath = new StringBuilder().append(ProlineFiles.STUDIO_JAR_FILE.getName()).append(SystemUtils.toSystemClassPath(";lib/*")).toString();
        logger.info("starting Studio  from path " + ProlineFiles.STUDIO_DIRECTORY.getAbsolutePath());

        List<String> command = new ArrayList<>();
        command.add(ConfigManager.getInstance().getAdvancedManager().getJvmExePath());
        command.add("-Xmx"+ConfigManager.getInstance().getMemoryManager().getStudioMemory()+"M");

//        command.add("-XX:+UseG1GC");
//        command.add("-XX:+UseStringDeduplication");
//        command.add("-XX:MinHeapFreeRatio=10");
//        command.add("-XX:MaxHeapFreeRatio=30");
//        command.add("-Duser.language=en");
//        command.add("-Duser.country=US");
      //  command.add("-Dlogback.configurationFile=config/logback.xml");
        command.add("-classpath");
        command.add(classpath);
        command.add("fr.proline.studio.main.Main");
        command.add("--userdir");
        command.add("../data" + studioUserDir);

        logger.info("starting Studio using " +command.toString());
        process = new ProcessExecutor()
                .command(command)
                .directory(ProlineFiles.STUDIO_DIRECTORY)
                .destroyOnExit()
                .start();

        // wait a moment before closing the splash screen, because Studio  is only displayed a moment after the process is started
        SplashScreen.stop(2000);
//		logger.info("Proline Studio is running...");
        this.m_isProcessAlive = true;
        logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getModuleName(), process.getProcess(), getProcessPidSafely(process), m_isProcessAlive);
        ZeroTray.update();
        // check every 2.5 seconds that Studio is still alive
        // the ending of Proline Zero is invisible to the user, so it does not matter if it happens directly when Studio is stopped
        while (process.getProcess().isAlive()) {
            // update system tray
            ZeroTray.update();
            Thread.sleep(25000);
        }
        stop();
    }

    public void startNetbeans() throws Exception {
        cleanCache();
        List<String> command = new ArrayList<>();
//		command.add(Config.getJavaExePath());
//		command.add(Memory.getStudioMinMemory());
//		command.add(Memory.getStudioMaxMemory());
//		command.add("-XX:MaxPermSize=128m");
//		command.add("-Djava.library.path=./sqlite4java");
//		command.add("-Dnetbeans.slow.system.clipboard.hack=false");
//		command.add("-Dnetbeans.user.dir=../data/.prolinestudio");
//		command.add("-Djdk.home=./jre");

        if (SystemUtils.isOSUnix()) {
            command.add("./bin/prolinestudio");
        } else {
            command.add("ProlineStudio-" + ConfigManager.getInstance().getStudioVersion() + "/bin/prolinestudio");
        }
        command.add("--userdir");
        command.add("../data" + studioUserDir);//"../data/.prolinestudio", relative directory
        command.add("--nosplash"); // there's already a splash screen, no need to have another one !
        // Warning : it seems that starting studio with --console suppress option is not compatible with redirectOutput option
        // this means that start failure won't be visible in the launcher output. An option to test is to start the process with another
        // option such as --fork-java ? Dont known in this case how java forked process output are redirected ??
        // Corrections: the redirectOutput only works without --console suppress and when the launcher is started with a console

        process = new ProcessExecutor().command(command).directory(ProlineFiles.STUDIO_DIRECTORY).destroyOnExit().start();
        // wait a moment before closing the splash screen, because Studio  is only displayed a moment after the process is started
        SplashScreen.stop(2000);
//		logger.info("Proline Studio is running...");
        m_isProcessAlive = true;
        logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getModuleName(), process.getProcess(), getProcessPidSafely(process), m_isProcessAlive);
        ZeroTray.update();

        //VDS : NO MORE NEEDED. To be removed once tests will be done. ExecutionSession manage stop
        // check every 2.5 seconds that Studio is still alive
        // the ending of Proline Zero is invisible to the user, so it does not matter if it happens directly when Studio is stopped
//        while (process.getProcess().isAlive()) {
//            // update system tray
//            ZeroTray.update();
//            Thread.sleep(2500);
//        }
//        stop();
    }

    public boolean isProcessAlive() {
        m_isProcessAlive = (process!=null && process.getProcess().isAlive());
        return m_isProcessAlive;
    }



//    @Override
//    public void stop() throws Exception {
//        // FIXME when called from system tray, it only closes the ProlineStudio.exe process but not the java process (which is the real process)
//        // Proline Studio should stop by itself when user closes Studio
//        // This method will be called if the user uses the the system tray to close Studio (or if a Proline Zero method one day wants to end Studio)
//        if (isProcessAlive) {
//            kill(process);
//        }
//    }

}
