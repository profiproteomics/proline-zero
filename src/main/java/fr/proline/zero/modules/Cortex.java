package fr.proline.zero.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.process.PidUtil;

public class Cortex extends AbstractProcess {

    public Cortex() {
        moduleName  = "Proline Cortex";
    }

    public void start() throws Exception {

        File cortexHome = ProlineFiles.CORTEX_DIRECTORY;
        String classpath = new StringBuilder().append(SystemUtils.toSystemClassPath("config;")).append(ProlineFiles.CORTEX_JAR_FILE.getName()).append(SystemUtils.toSystemClassPath(";lib/*")).toString();
        logger.info("starting Cortex server from path " + cortexHome.getAbsolutePath());
        List<String> command = new ArrayList<>();
        command.add(Config.getJavaExePath());
        command.add(Memory.getCortexMaxMemory(Config.isAdjustMemory()));
        command.add("-XX:+UseG1GC");
        command.add("-XX:+UseStringDeduplication");
        command.add("-XX:MinHeapFreeRatio=10");
        command.add("-XX:MaxHeapFreeRatio=30");
        command.add("-Djava.io.tmpdir=../data/tmp");
        command.add("-Dlogback.configurationFile=config/logback.xml");
        command.add("-Duser.language=en");
        command.add("-Duser.country=US");
        command.add("-classpath");
        command.add(classpath);
        command.add("fr.proline.cortex.ProcessingNode");

        process = new ProcessExecutor()
                .command(command)
                .directory(cortexHome)
                .redirectOutput(new LogOutputStream() {
                    @Override
                    protected void processLine(String line) {
                        if (Config.isDebugMode()) {
                            logger.debug(line);
                        }
                        updateProcessStatus(line, "Proline Cortex successfully started !");
                    }
                })
                .destroyOnExit()
                .start();

        waitForStartCompletion(Config.getDefaultTimeout());
        logger.info("Process {} successfully started (name = {}, pid = {}, alive = {})", getModuleName(), process.getProcess(), PidUtil.getPid(process.getProcess()), m_isProcessAlive);

        ZeroTray.update();
    }


}
