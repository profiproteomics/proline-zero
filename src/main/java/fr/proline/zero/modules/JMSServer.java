package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;

public class JMSServer extends AbstractProcess {

	private static Logger logger = LoggerFactory.getLogger(JMSServer.class);
	
	private File jmsHome;
	private StartedProcess process;

	JMSServer() {
		jmsHome = ProlineFiles.HORNETQ_DIRECTORY;
		if (!jmsHome.exists() || ! jmsHome.isDirectory()) {
			throw new IllegalArgumentException("JMS server Home directory not found");
		}
	}
	
	public void start() throws Exception {

		boolean arePortAvailable = SystemUtils.isPortAvailable(1099) && SystemUtils.isPortAvailable(1098) && SystemUtils.isPortAvailable(5445);

		if (arePortAvailable) {
            String classpath = SystemUtils.toSystemClassPath("config/stand-alone/non-clustered/;schema/*;lib/*");
            logger.info("starting JMS server from path " + jmsHome.getAbsolutePath());
            List<String> command = new ArrayList<>();
            command.add(Config.getJavaExePath());
            command.add("-Djnp.port=1099");
            command.add("-Djnp.rmiPort=1098");
            command.add("-Djnp.host=localhost");
            command.add("-Dhornetq.remoting.netty.host=0.0.0.0");
            command.add("-Dhornetq.remoting.netty.port=5445");
            command.add("-XX:+UseParallelGC");
            command.add("-XX:+AggressiveOpts");
            command.add("-XX:+UseFastAccessorMethods");
            command.add(Memory.getJmsMinMemory());
            command.add(Memory.getJmsMaxMemory());
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
		                	if(Config.isDebugMode()) logger.debug(line);
		                    updateProcessStatus(line, "Server is now live");
		                }
		            })
            		.destroyOnExit()
            		.start();

            waitForStartCompletion(Config.getDefaultTimeout());
            logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getProcessName(), process.getProcess(), PidUtil.getPid(process.getProcess()), isProcessAlive);
        }  else {
            logger.error("JMS server ports (1098, 1099 or 5445) are already in use. Please make sure that these port are available before starting Proline");
            throw new IllegalArgumentException("JMS port (1098, 1099 or 5445) are already in use") ;
        }

		ZeroTray.update();
    }

	@Override
	public String getProcessName() {
		return "JMS server";
	}

	public void stop() throws Exception {
		kill(process);
	}
}
