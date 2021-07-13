package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;

public class JMSServer extends AbstractProcess {

    private File jmsHome;

    public JMSServer() {
        moduleName  = "JMS server";
        jmsHome = ProlineFiles.HORNETQ_DIRECTORY;
        //VDS FOR TEST
//        if (!jmsHome.exists() || !jmsHome.isDirectory()) {
//            throw new IllegalArgumentException("JMS server Home directory not found");
//        }
    }

    public void start() throws Exception {

        int jmsPort = ConfigManager.getInstance().getAdvancedManager().getJmsServerPort();
        int jmsBatchPort = ConfigManager.getInstance().getAdvancedManager().getJmsBatchServerPort();
        int jnpPort = ConfigManager.getInstance().getAdvancedManager().getJnpServerPort();
        int jnpRmiPort = ConfigManager.getInstance().getAdvancedManager().getJnpRmiServerPort();
        boolean isPortAvailable = SystemUtils.isPortAvailable(jmsPort)
                && SystemUtils.isPortAvailable(jmsBatchPort)
                && SystemUtils.isPortAvailable(jnpPort)
                && SystemUtils.isPortAvailable(jnpRmiPort);
        if (isPortAvailable) {
            String classpath = SystemUtils.toSystemClassPath("config/stand-alone/non-clustered/;schema/*;lib/*");
            logger.info("starting JMS server from path " + jmsHome.getAbsolutePath());
            List<String> command = new ArrayList<>();
            command.add(ConfigManager.getInstance().getAdvancedManager().getJvmPath());
            command.add("-Dhornetq.remoting.netty.host=0.0.0.0");
            command.add("-Dhornetq.remoting.netty.port=" + jmsPort);
            command.add("-XX:+UseParallelGC");
            command.add("-XX:+AggressiveOpts");
            command.add("-XX:+UseFastAccessorMethods");
            command.add("-Xmx"+ConfigManager.getInstance().getMemoryManager().getJmsMemory()+"M");
            command.add("-Dhornetq.config.dir=config/stand-alone/non-clustered");

            command.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");
            command.add("-Djava.util.logging.config.file=config/stand-alone/non-clustered/logging.properties");
            command.add("-Djava.library.path=bin");

            command.add("-classpath");
            command.add(classpath);
            command.add("org.hornetq.integration.bootstrap.HornetQBootstrapServer");
            command.add("hornetq-beans.xml");

            process = new ProcessExecutor()
                    .command(command)
                    .environment("HORNETQ_HOME", jmsHome.getAbsolutePath())
                    .directory(jmsHome)
                    .redirectOutput(new LogOutputStream() {
                        @Override
                        protected void processLine(String line) {
                            if (ConfigManager.getInstance().isDebugMode()) {
                                logger.debug(line);
                            }
                            updateProcessStatus(line, "Server is now live");
                        }
                    })
                    .destroyOnExit()
                    .start();
            waitForStartCompletion(ConfigManager.getInstance().getAdvancedManager().getServerDefaultTimeout());
            logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getModuleName(), process.getProcess(), PidUtil.getPid(process.getProcess()), m_isProcessAlive);
        } else {
            m_isProcessAlive = false;
            logger.error("JMS server ports " + jmsPort
                    + "JMS server batch ports " + jmsBatchPort
                    + "JMS server JNP ports " + jnpPort
                    + "JMS server JNP RMI ports " + jnpRmiPort
                    + " are already in use. Please make sure that these port are available before starting Proline");
            throw new IllegalArgumentException("JMS server ports " + jmsPort
                    + "JMS server batch ports " + jmsBatchPort
                    + "JMS server JNP ports " + jnpPort
                    + "JMS server JNP RMI ports " + jnpRmiPort
                    + " are already in use");
        }

        ZeroTray.update();
    }

}
