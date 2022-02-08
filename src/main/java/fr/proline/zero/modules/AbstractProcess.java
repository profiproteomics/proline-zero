package fr.proline.zero.modules;

import fr.proline.zero.gui.ZeroTray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.process.PidUtil;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.util.concurrent.TimeUnit;

public abstract class AbstractProcess implements  IZeroModule {

    protected static Logger logger = LoggerFactory.getLogger("ZeroModule");

    protected boolean m_isProcessAlive = false;
    protected StartedProcess process;

    protected String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void init() throws Exception {

    }

    public void stop() throws Exception {
        kill(process);
    }

    protected void updateProcessStatus(String line, String keyPhrase) {
        updateProcessStatus(line, keyPhrase, true);
    }

    protected void updateProcessStatus(String line, String keyPhrase, boolean keyPhraseMustBeAtTheEnd) {
        if (keyPhraseMustBeAtTheEnd) {
            m_isProcessAlive = m_isProcessAlive || line.trim().endsWith(keyPhrase);
        } else {
            m_isProcessAlive = m_isProcessAlive || line.trim().contains(keyPhrase);
        }
    }

    public synchronized boolean isProcessAlive() {
        return m_isProcessAlive;
    }

    protected void waitForStartCompletion(long timeout) throws Exception {
        long start = System.currentTimeMillis();
        while (!isProcessAlive() && ((System.currentTimeMillis() - start) <= timeout)) {
            Thread.sleep(200);
        }

        if (!isProcessAlive()) {
            throw new RuntimeException("Could not start " + getModuleName()+ " server (process timeout after " + (timeout / 1000) + " seconds)");
        }
    }

    //    protected int getProcessPidSafely(StartedProcess process) {
    public static int getProcessPidSafely(StartedProcess process) {
        int pid = 0;
        try {
            pid = PidUtil.getPid(process.getProcess());
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return pid;
    }

    protected static boolean kill(StartedProcess process, String name, boolean isAlive) {
        int pid = getProcessPidSafely(process);
        try {
            if(process == null )
                return  false; //process invaid.

//            logger.info("Trying to stop " + name + ", pid = " + pid + " is alive: " + isAlive);
            SystemProcess sysProcess = Processes.newStandardProcess(process.getProcess(), pid);
            ProcessUtil.destroyGracefullyOrForcefullyAndWait(sysProcess, 30, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);
            isAlive = false;
            logger.info("Process " + name + " has been stopped");
        } catch(Exception e) {
            // other throwable errors are to be caught by the caller ! (StackOverflowError when killing from system tray)
            if(pid > 0) {
                logger.info("Can't stop " + name + ", process is not running or already closed");
            } else {
                logger.error("Failure while stopping " + name + ", please check pid " + pid + " to stop it yourself", e);
            }
        } finally {
            ZeroTray.update();
        }
        return isAlive;
    }

    protected void kill(StartedProcess process) {
        m_isProcessAlive = kill(process, getModuleName(), m_isProcessAlive);
    }



}
