package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SystemUtils;

/*
 * Note: 
 * While H2 seems to be functional, it is not working with Sequence Repository.
 * The problem is that it is not possible to create the SeqDb external db due to a relative dbName.
 * The dbName used is "seq_db" instead of "./data/databases/h2/seq_db"
 * This can be fixed in PM-SequenceRepository but not in the launcher.
 */
public class H2 implements IZeroModule {

    private  boolean isProcessAlive = false;
    private static final String relativeDatastorePath = "data/databases/h2";
    private static Logger logger = LoggerFactory.getLogger(H2.class);
    private StartedProcess process;
    private boolean datastoreIsRunning;

    public boolean isProcessAlive(){
        return isProcessAlive;
    }

    public String getModuleName() {
        return "H2";
    }

//	public H2(File workingDirectory) {
//		super(workingDirectory, relativeDatastorePath);
//	}
    public void init() throws Exception {
        File datastoreDirectory = new File(relativeDatastorePath);
        if (!datastoreDirectory.exists() || !datastoreDirectory.isDirectory()) {
            datastoreDirectory.mkdir();
        }
    }

    public void start() throws Exception {
        datastoreIsRunning = false;
        int dataStorePort = ConfigManager.getInstance().getAdvancedManager().getDataStorePort();
        int JmsServerPort = ConfigManager.getInstance().getAdvancedManager().getJmsServerPort();
        if (SystemUtils.isPortAvailable(dataStorePort) && SystemUtils.isPortAvailable(JmsServerPort)) {

            logger.info("Initializing H2 datastore");
            // start H2
            String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("Proline-Cortex-")).append(ConfigManager.getInstance().getCortexVersion()).append(SystemUtils.toSystemClassPath("/lib/*")).toString();
            List<String> command = new ArrayList<>();
            command.add(ConfigManager.getInstance().getAdvancedManager().getJvmExePath());
            command.add("-cp");
            command.add(classpath);
            command.add("org.h2.tools.Console");
            command.add("-tcp");
            command.add("-pg");
//			logger.info("ABU running command: "+command.toString());
            process = new ProcessExecutor()
                    .command(command)
                    .redirectOutput(new LogOutputStream() {
                        @Override
                        protected void processLine(String line) {
                            if (ConfigManager.getInstance().isDebugMode()) {
                                logger.debug(line);
                            }
                            if (line.contains("PG server running at")) {
                                datastoreIsRunning = true;
                            }
                        }
                    })
                    .start();

            long start = System.currentTimeMillis();
            while (!datastoreIsRunning && ((System.currentTimeMillis() - start) <= ConfigManager.getInstance().getAdvancedManager().getServerDefaultTimeout())) {
                Thread.sleep(200);
            }
            if (datastoreIsRunning) {
                logger.info("H2 successfully started");
                isProcessAlive = true;
            } else {
                throw new RuntimeException("Could not start H2 server");
            }
        } else {
            logger.error("H2 ports {} and/or JMS port {} is already in use. "
                    + "This may be caused by another process talking on this port or by an existing postgreSQL server instance already running",
                    dataStorePort, JmsServerPort);
            throw new IllegalArgumentException("H2 port " + dataStorePort + " and/or JMS port " + JmsServerPort + " are already in use");

        }

    }

    public void stop() throws Exception {
        // if running, stop it
        if (process != null) {
            boolean isAlive = false;
            // if previous processes could not be started, the current process will not exist
            if (process.getProcess() != null) {
                isAlive = process.getProcess().isAlive();
                // TODO put the lines below in this condition ?
            }
            logger.info("Trying to stop Cortex Process " + process.getProcess() + " , pid = " + PidUtil.getPid(process.getProcess()) + " is alive: " + isAlive);
            SystemProcess sysProcess = Processes.newStandardProcess(process.getProcess());

            ProcessUtil.destroyGracefullyOrForcefullyAndWait(sysProcess, 30, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);
            isProcessAlive = false;
        } else {
            logger.info("Can't stop H2, process is not running");
        }
    }

}
