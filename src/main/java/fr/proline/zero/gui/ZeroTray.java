package fr.proline.zero.gui;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import dorkbox.util.SwingUtil;
import fr.proline.zero.modules.ExecutionSession;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ZeroTray {

    private static Logger logger = LoggerFactory.getLogger(ZeroTray.class);

    private static SystemTray systemTray;
    private static Menu menuProcesses, menuDatastore, menuHornetQ, menuCortex, menuSeqRepo, menuStudio;
    /*
     * TODO
     * [x] Add the Proline Zero config file somewhere
     * [x] The process status should be updated regularly, or only when opened (cortex, jms, pgsql, seqrepo, studio)
     * [x] The log files should be checked at opening (this would only be useful when Proline stays open on two consecutive days)
     * [x] Log files should be opened in "tail -f" mode in a specific jwindow
     * [x] Get rid of the StackOverflowError when restarting a process (or avoid restarting processes)
     * [x] Exit button does not close Proline Studio completely (fix it or remove it)
     * [x] Add Proline Admin monitor
     * [ ] Add a "Server mode" (in Main/config): if true, display proline Monitor, add Exit button and do not run Studio (otherwise, no adminGUI, no exit button and run Studio)
     * [ ] consider Config.isSeqRepoEnabled when dealing with SeqRepo
     * [ ] Add a GUI to let the user set memory himself ? (good idea both for server and client modes)
     *
     */

    public static void initialize() throws RuntimeException {
        logger.info("Start system tray");

//        SystemTray.DEBUG = true; // for test apps, we always want to run in debug mode
//        CacheUtil.clear(); // for test apps, make sure the cache is always reset. You should never do this in production.

        SwingUtil.setLookAndFeel(null); // set Native L&F (this is the System L&F instead of CrossPlatform L&F)

        logger.info("System tray initialization");
        systemTray = SystemTray.get();
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        logger.info("System tray definition");
        systemTray.setTooltip("Proline Zero");
        systemTray.setImage(ProlineFiles.PROFI_ICON);

        Menu mainMenu = systemTray.getMenu();
        mainMenu.add(_prolineZeroMenu());
        // add these menu in a submenu (processes)
        menuProcesses = new Menu("Proline stack", ProlineFiles.PROGRESS_ICON);
        menuProcesses.add(_pgsqlMenu());
        menuProcesses.add(_hornetqMenu());
        menuProcesses.add(_cortexMenu());
        menuProcesses.add(_seqRepoMenu());
        menuProcesses.add(getStudioMenu());
        mainMenu.add(menuProcesses);

        // open Proline monitor
//        mainMenu.add(new MenuItem("Administration", ProlineFiles.EDIT_ICON, e -> {
//            try {
//                ProlineAdmin.start();
//            } catch(Throwable t) {
//                Popup.error(t);
//            }
//        }));
        // link to the packaged documentation file
        mainMenu.add(new MenuItem("Help", ProlineFiles.HELP_ICON, e -> {
            try {
                Desktop.getDesktop().browse(ProlineFiles.getProlineDocumentationFile().toURI());
            } catch(Throwable t) {
                Popup.error(t);
            }
        }));
        // link to profi website
        mainMenu.add(new MenuItem("ProFI Web site", e -> {
            try {
                Desktop.getDesktop().browse(new URL(ProlineFiles.PROFI_WEBSITE).toURI());
            } catch (Throwable t) {
                Popup.error(t);
            }
        }));
        if (Config.isServerMode()) {
            // exit button
            mainMenu.add(new MenuItem("Exit", ProlineFiles.QUIT_ICON, e -> {
                // FIXME this command Kills Studio and Proline Zero, but not the Studio child process that provides the GUI (so user don't feel like it's close)
                // solution is to remove Exit button when Proline Zero is not in server mode (client just need to close Proline Studio)
                logger.info("User requested to close Proline Zero");
                try {
//                ExecutionSession.getStudio().stop();
                    SystemUtils.end();
                } catch (Throwable t) {
                    logger.error("Error while killing studio", t);
                    Popup.error(t);
                }
            }));
        }

    }

    public static void stop() {
        if (systemTray != null) {
            systemTray.shutdown();
            systemTray = null;
        }
    }

    private static void updateStatus(Menu menu, boolean isAlive) {
        menu.setImage(isAlive ? ProlineFiles.STATUS_RUNNING_ICON : ProlineFiles.STATUS_STOPPED_ICON);
    }

    public static void update() {
        if (systemTray != null) {
            // check processes status
            ZeroTray.updateStatus(menuDatastore, ExecutionSession.getDataStore().isProcessAlive());
            ZeroTray.updateStatus(menuHornetQ, ExecutionSession.getJMSServer().isProcessAlive());
            ZeroTray.updateStatus(menuCortex, ExecutionSession.getCortex().isProcessAlive());
            ZeroTray.updateStatus(menuSeqRepo, ExecutionSession.getSeqRepo().isProcessAlive());
            ZeroTray.updateStatus(menuStudio, ExecutionSession.getStudio().isProcessAlive());
            // parent menu should either be:
            if (ExecutionSession.getDataStore().isProcessAlive() && ExecutionSession.getJMSServer().isProcessAlive() && ExecutionSession.getCortex().isProcessAlive() && ExecutionSession.getSeqRepo().isProcessAlive() && ExecutionSession.getStudio().isProcessAlive()) {
                // if all processes are running, status is "running"
                menuProcesses.setImage(ProlineFiles.STATUS_RUNNING_ICON);
            } else if (!ExecutionSession.getDataStore().isProcessAlive() && !ExecutionSession.getJMSServer().isProcessAlive() && !ExecutionSession.getCortex().isProcessAlive() && !ExecutionSession.getSeqRepo().isProcessAlive() && !ExecutionSession.getStudio().isProcessAlive()) {
                // if all processes are stopped, status is "stopped"
                menuProcesses.setImage(ProlineFiles.STATUS_STOPPED_ICON);
            } else {
                // if some processes are running but not all, status is "progress"
                menuProcesses.setImage(ProlineFiles.PROGRESS_ICON);
            }
        }
    }

    private static void editFile(File file) {
        Popup.info("Be careful while changing configuration file.\nConfiguration changes will not be effective until you restart Proline Zero");
        try {
            if (SystemUtils.isOSWindows()) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
                Runtime.getRuntime().exec(cmd);
            } else {
                Desktop.getDesktop().edit(file);
            }
        } catch (IOException ioe) {
            Popup.error("File '" + file.getAbsolutePath() + "' failed to be opened in the default file editor, open it manually to change configuration.", ioe);
        }
    }

    private static Menu _prolineZeroMenu() {
        Menu menu = new Menu("Proline Zero", ProlineFiles.PROFI_ICON);
        menu.add(new MenuItem("Edit launcher settings", ProlineFiles.EDIT_ICON, e -> ZeroTray.editFile(ProlineFiles.PROLINE_ZERO_CONFIG_FILE)), 0);
        menu.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openProlineZeroLog()));
        return menu;
    }

    private static Menu _pgsqlMenu() {
        if (menuDatastore == null) {
            menuDatastore = new Menu(ExecutionSession.getDataStore().getDatastoreName(), ProlineFiles.PROGRESS_ICON);

            menuDatastore.add(new MenuItem("Edit configuration", ProlineFiles.EDIT_ICON, e -> ZeroTray.editFile(ProlineFiles.PG_CONFIG_FILE)), 0);
            menuDatastore.add(new MenuItem("Restore default configuration", ProlineFiles.DEFAULT_ICON, e -> {
                // ask if user is certain
                if (Popup.okCancel("Restore file '" + ProlineFiles.PG_DEFAULT_CONFIG_FILE.getName() + "' ?")) {
                    Memory.restorePostgreSQLDefaultConfig();
                }
            }), 1);
            menuDatastore.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openPostgreSqlLog()), 2);
        }
        return menuDatastore;
    }

    private static Menu _hornetqMenu() {
        if (menuHornetQ == null) {
            menuHornetQ = new Menu(ExecutionSession.getJMSServer().getProcessName() + " " + Config.getHornetQVersion(), ProlineFiles.PROGRESS_ICON);
            menuHornetQ.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openHornetQLog()));
        }
        return menuHornetQ;
    }

    private static Menu _cortexMenu() {
        if (menuCortex == null) {
            menuCortex = new Menu(ExecutionSession.getCortex().getProcessName() + " " + Config.getCortexVersion(), ProlineFiles.PROGRESS_ICON);
//            menuCortex.add(new MenuItem("Edit configuration", ProlineFiles.EDIT_ICON, e -> ZeroTray.editFile(ProlineFiles.CORTEX_CONFIG_FILE)));
            menuCortex.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openCortexLog()));
            menuCortex.add(new MenuItem("mzDB log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openCortexMzdbLog()));
        }
        return menuCortex;
    }

    private static Menu _seqRepoMenu() {
        if (menuSeqRepo == null) {
            menuSeqRepo = new Menu(ExecutionSession.getSeqRepo().getProcessName() + " " + Config.getSeqRepoVersion(), ProlineFiles.PROGRESS_ICON);
            menuSeqRepo.add(new MenuItem("Edit parse rules", ProlineFiles.EDIT_ICON, e -> ZeroTray.editFile(ProlineFiles.SEQREPO_PARSE_RULES_CONFIG_FILE)));
            menuSeqRepo.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openSeqRepoLog()));
        }
        return menuSeqRepo;
    }

    private static Menu getStudioMenu() {
        if (menuStudio == null) {
            menuStudio = new Menu(ExecutionSession.getStudio().getProcessName() + " " + Config.getStudioVersion(), ProlineFiles.PROGRESS_ICON);
//            menuStudio.add(new MenuItem("Edit configuration", ProlineFiles.EDIT_ICON, e -> ZeroTray.editFile(ProlineFiles.STUDIO_CONFIG_FILE)));
            menuStudio.add(new MenuItem("Log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openStudioLog()));
            menuStudio.add(new MenuItem("Error log file", ProlineFiles.DOCUMENT_ICON, e -> LogFileViewer.openStudioErrorLog()));
        }
        return menuStudio;
    }

}
