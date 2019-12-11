package fr.proline.zero.gui;

import fr.proline.zero.util.ProlineFiles;

public class LogFileViewer {

    private static LogFile zeroLog;
    private static LogFile pgLog;
    private static LogFile hornetQLog;
    private static LogFile cortexLog;
    private static LogFile cortexMzdbLog;
    private static LogFile seqRepoLog;
    private static LogFile studioLog;
    private static LogFile studioErrorLog;

    public static void openProlineZeroLog() {
        if (zeroLog == null) {
            zeroLog = new LogFile(ProlineFiles.getProlineZeroCurrentLogFile());
        } else {
            zeroLog.focus();
        }
    }

    public static void closeProlineZeroLog() {
        if (zeroLog != null) {
            zeroLog.close();
            zeroLog = null;
        }
    }

    public static void openPostgreSqlLog() {
        if (pgLog == null) {
            pgLog = new LogFile(ProlineFiles.PG_LOG_FILE);
        } else {
            pgLog.focus();
        }
    }

    public static void closePostgreSqlLog() {
        if (pgLog != null) {
            pgLog.close();
            pgLog = null;
        }
    }

    public static void openHornetQLog() {
        if (hornetQLog == null) {
            hornetQLog = new LogFile(ProlineFiles.HORNETQ_LOG_FILE);
        } else {
            hornetQLog.focus();
        }
    }

    public static void closeHornetQLog() {
        if (hornetQLog != null) {
            hornetQLog.close();
            hornetQLog = null;
        }
    }

    public static void openCortexLog() {
        if (cortexLog == null) {
            cortexLog = new LogFile(ProlineFiles.getCortexCurrentDebugLogFile());
        } else {
            cortexLog.focus();
        }
    }

    public static void closeCortexLog() {
        if (cortexLog != null) {
            cortexLog.close();
            cortexLog = null;
        }
    }

    public static void openCortexMzdbLog() {
        if (cortexMzdbLog == null) {
            cortexMzdbLog = new LogFile(ProlineFiles.getCortexCurrentMzdbLogFile());
        } else {
            cortexMzdbLog.focus();
        }
    }

    public static void closeCortexMzdbLog() {
        if (cortexMzdbLog != null) {
            cortexMzdbLog.close();
            cortexMzdbLog = null;
        }
    }

    public static void openSeqRepoLog() {
        if (seqRepoLog == null) {
            seqRepoLog = new LogFile(ProlineFiles.getSeqrepoCurrentLogFile());
        } else {
            seqRepoLog.focus();
        }
    }

    public static void closeSeqRepoLog() {
        if (seqRepoLog != null) {
            seqRepoLog.close();
            seqRepoLog = null;
        }
    }

    public static void openStudioLog() {
        if (studioLog == null) {
            studioLog = new LogFile(ProlineFiles.STUDIO_LOG_FILE);
        } else {
            studioLog.focus();
        }
    }

    public static void closeStudioLog() {
        if (studioLog != null) {
            studioLog.close();
            studioLog = null;
        }
    }

    public static void openStudioErrorLog() {
        if (studioErrorLog == null) {
            studioErrorLog = new LogFile(ProlineFiles.STUDIO_ERROR_LOG_FILE);
        } else {
            studioErrorLog.focus();
        }
    }

    public static void closeStudioErrorLog() {
        if (studioErrorLog != null) {
            studioErrorLog.close();
            studioErrorLog = null;
        }
    }

    public static void closeAllLogFiles() {
        closeProlineZeroLog();
        closePostgreSqlLog();
        closeHornetQLog();
        closeCortexLog();
        closeCortexMzdbLog();
        closeSeqRepoLog();
        closeStudioLog();
        closeStudioErrorLog();
    }
}
