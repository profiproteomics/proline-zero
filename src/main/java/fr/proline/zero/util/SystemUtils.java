package fr.proline.zero.util;

import fr.profi.util.system.OSInfo;
import fr.profi.util.system.OSType;
import fr.proline.zero.gui.LogFileViewer;
import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.modules.ExecutionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SystemUtils {

    private static Logger logger = LoggerFactory.getLogger(SystemUtils.class);

    static private OSType type;

    public static void end() {
        // this piece of code was originally in the ExecutionSession class but we put it here because we also need to close the splash screen and the system tray
        logger.info("Trying to close Proline Stack");
        try {
            ExecutionSession.end();
            LogFileViewer.closeAllLogFiles();
        } catch (Exception e) {
            logger.error("Proline stack cannot be stopped due to the following error: ", e);
        } finally {
            SplashScreen.stop();
            ZeroTray.stop();
        }
    }

    static public String toSystemPath(String path) {
        if (File.separatorChar == '\\') {
            // From Windows to Linux/Mac
            return path.replace('/', File.separatorChar);
        } else {
            // From Linux/Mac to Windows
            return path.replace('\\', File.separatorChar);
        }
    }

//    static public String getSystemClassPathSeparator() {
//        if (isOSUnix()) return ":";
//        return ";";
//    }

    static public String toSystemClassPath(String classpath) {
        if (isOSUnix()) {
            // From Windows to Linux/Mac
            return classpath.replace(';', ':');
        } else {
            // From Linux/Mac to Windows
            return classpath.replace(':', ';');
        }
    }

    static public boolean isOSWindows() {
        return (getOSType() == OSType.WINDOWS_AMD64) || (getOSType() == OSType.WINDOWS_X86);
    }

    static public boolean isOSLinux() {
        return (getOSType() == OSType.LINUX_AMD64) || (getOSType() == OSType.LINUX_I386);
    }

    static public boolean isOSMac() {
        return (getOSType() == OSType.MAC_AMD64) || (getOSType() == OSType.MAC_I386);
    }

    static public boolean isOSUnix() {
        return (isOSLinux() || isOSMac());
    }

    static public OSType getOSType() {
        if (type == null) {
            type = OSInfo.getOSType();
        }
        return type;
    }

    public static boolean isPortAvailable(int port) {
        try {
            // ServerSocket try to open a LOCAL port
            new ServerSocket(port).close();
            // local port can be opened, it's available
            return true;
        } catch (IOException e) {
            // local port cannot be opened, it's in use
            return false;
        }
    }

    public static String getPathEnvironmentVariable() {
        return System.getenv().get("Path");
    }

    public static String getPathEnvironmentVariable(String addValueToPath) {
        String path = getPathEnvironmentVariable();
        if (isOSUnix()) {
            path += ":" + addValueToPath;
        } else {
            path += ";" + addValueToPath;
        }
        return path;
    }

}
