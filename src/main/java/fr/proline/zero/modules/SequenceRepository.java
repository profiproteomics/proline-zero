package fr.proline.zero.modules;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.ProlineFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;

import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.SystemUtils;

public class SequenceRepository extends AbstractProcess {

	private static Logger logger = LoggerFactory.getLogger(SequenceRepository.class);
	private StartedProcess process;

	@Override
	public String getProcessName() {
		return "Sequence Repository";
	}

	@Override
	public void start() throws Exception {
		File seqRepoHome = ProlineFiles.SEQREPO_DIRECTORY;
		String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("config;")).append(ProlineFiles.SEQREPO_JAR_FILE).append(SystemUtils.toSystemClassPath(";lib/*")).toString();
		logger.info("starting Sequence Repository from path " + seqRepoHome.getAbsolutePath());
		List<String> command = new ArrayList<>();
		command.add(Config.getJavaExePath());
		command.add(Memory.getSeqRepoMaxMemory());
		command.add("-XX:+UseG1GC");
		command.add("-XX:+UseStringDeduplication");
		command.add("-XX:MinHeapFreeRatio=10");
		command.add("-XX:MaxHeapFreeRatio=30");
		command.add("-cp");
		command.add(classpath);
		command.add("fr.proline.module.seq.jms.RunNode");
		
		process = new ProcessExecutor().command(command).directory(seqRepoHome).redirectOutput(new LogOutputStream() {
			@Override
			protected void processLine(String line) {
            	if(Config.isDebugMode()) logger.debug(line);
				updateProcessStatus(line, "Entering Consumer receive loop", false);
			}
		}).destroyOnExit().start();
		waitForStartCompletion(Config.getDefaultTimeout());
		logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getProcessName(),
				process.getProcess(), PidUtil.getPid(process.getProcess()), isProcessAlive);
		ZeroTray.update();
	}

	@Override
	public synchronized void stop() throws Exception {
		if(process != null) kill(process);
	}
	
	public void updatePostgreSQLPortConfig() {
		String newPort = "port = \""+ Config.getPostgreSQLPort()+"\"";
		File configFile = ProlineFiles.SEQREPO_CONFIG_FILE;
		logger.info("Replace PG port in file "+configFile.getAbsolutePath()+" to "+newPort);
		try {
			List<String> lines = Files.lines(configFile.toPath()).map(l -> l.replaceAll("port\\s*=\\s*\"(\\d+)\"", newPort)).collect(Collectors.toList());
			Files.write(configFile.toPath(), lines);
		} catch (Exception e) {
			logger.error("Error replacing database port in file "+configFile.getAbsolutePath(), e);
		}
	}

}
