package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

public class ProlineAdmin {

    private static Logger logger = LoggerFactory.getLogger(ProlineAdmin.class);

    public static void setUp() throws Exception {
        runCommand(new String[]{"setup"});
        runCommand(new String[]{"create_user", "-l", "proline", "-p", "proline"});
        runCommand(new String[]{"create_project", "-oid", "2", "-n", "Proline_Project", "-desc", "Proline default Project"});
    }

    public static void runCommand(String[] args) throws Exception {
        String classpath = SystemUtils.toSystemClassPath("lib/*;config");

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
                .directory(ProlineFiles.CORTEX_DIRECTORY)
                .redirectOutput(Slf4jStream.ofCaller().asInfo())
                .execute()
                .getExitValue();
        logger.info("Proline admin process " + args[0] + " finishes with exit code: " + exitValue);
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

//	public static void stop() throws Exception {
//	    if(process != null) {
//            isProcessAlive = AbstractProcess.kill(process, getProcessName(), isProcessAlive);
//            if(!isProcessAlive) process = null;
//        }
//	}
}
