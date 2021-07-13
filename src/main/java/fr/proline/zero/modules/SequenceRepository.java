package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.*;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;

public class SequenceRepository extends AbstractProcess {

    public SequenceRepository() {
        moduleName= "Sequence Repository";
    }

    @Override
    public void start() throws Exception {
        File seqRepoHome = ProlineFiles.SEQREPO_DIRECTORY;
        String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("config;")).append(ProlineFiles.SEQREPO_JAR_FILE).append(SystemUtils.toSystemClassPath(";lib/*")).toString();
        logger.info("starting Sequence Repository from path " + seqRepoHome.getAbsolutePath());
        List<String> command = new ArrayList<>();
        command.add(ConfigManager.getInstance().getAdvancedManager().getJvmExePath());
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
                if (ConfigManager.getInstance().isDebugMode()) {
                    logger.debug(line);
                }
                updateProcessStatus(line, "Entering Consumer receive loop", false);
            }
        }).destroyOnExit().start();
        waitForStartCompletion(ConfigManager.getInstance().getAdvancedManager().getServerDefaultTimeout());
        logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getModuleName(),
                process.getProcess(), PidUtil.getPid(process.getProcess()), m_isProcessAlive);
        ZeroTray.update();
    }


}
