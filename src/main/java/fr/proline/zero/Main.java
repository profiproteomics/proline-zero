package fr.proline.zero;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.modules.ExecutionSession;
import fr.proline.zero.modules.ProlineAdmin;

public class Main {

    /*
     * TODO
     * [ ] Check that postgresql port has not been updated
     * [ ] Remake the log file viewer with javafx
     * [ ] add a javafx window to set java memory per component
     * [ ] add a server mode: when set, open proline monitor, do not open studio, add an Exit button
     * [ ] change Proline Admin version to include Proline Monitor (instead of ProlineAdmin.Main)
     * [ ] test Proline Zero when SeqRepo is missing
     * [ ]
     */

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		logger.info("Starting Proline Zero");

		try {
			ExecutionSession.initialize();
			ZeroTray.initialize();
			// add a shutdown hook that will be executed when the program ends or if the user ends it with Ctrl+C
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());

			SplashScreen.setProgressMax(5); // pgsql, hornetq, cortex, seqrepo, studio
			if(!ProlineFiles.PG_DATASTORE.exists() && !ProlineFiles.H2_DATASTORE.exists()) {
				SplashScreen.setProgressMax(7); // init, pgsql, admin, hornetq, cortex, seqrepo, studio
				try {
					SplashScreen.setProgress("Initializing Datastore");
					logger.info("Datastore folder is empty : Proline Datastore must be initialized");

					ExecutionSession.getDataStore().init();
					SplashScreen.setProgress("Starting Datastore");
					// adjust memory before starting datastore
					if(!Config.isDebugMode()) {
						Memory.adjustMemory(Config.getWorkingMemory());
					} else {
						Memory.restorePostgreSQLDefaultConfig();
					}
					// start datastore
					ExecutionSession.getDataStore().start();

				} catch (Exception e) {
					logger.error("Error during datastore initialization", e);
					SystemUtils.end();
					System.exit(1);
				}

				SplashScreen.setProgress("Initializing Proline databases");
				ExecutionSession.updateConfigurationFiles();
				ProlineAdmin.setUp();

			} else {
				SplashScreen.setProgress("Starting Datastore");
				// TODO: check that application.conf PG port == proline_launcher.config PG port because
        // PG port can be updated only once since Proline store the database JDBC URL
				// adjust memory before starting datastore
				if(!Config.isDebugMode()) {
					Memory.adjustMemory(Config.getWorkingMemory());
				} else {
					Memory.restorePostgreSQLDefaultConfig();
				}
				// start datastore
				ExecutionSession.getDataStore().start();
			}

			logger.info("Starting Proline");
			SplashScreen.setProgress("Starting JMS Server");
			ExecutionSession.getJMSServer().start();
			logger.info("JMS Server started");
			SplashScreen.setProgress("Starting Cortex Server");
			ExecutionSession.getCortex().start();
			logger.info("Cortex Server started");
			// allow SeqRepo to be missing (if not packaged with Proline Zero, or if launcher is used on a server)
			// TODO test this case !
			if(Config.isSeqRepoEnabled() && ProlineFiles.SEQREPO_DIRECTORY.exists()) {
				SplashScreen.setProgress("Starting Sequence Repository");
				ExecutionSession.getSeqRepo().start();
				logger.info("Sequence Repository started");
			} else {
				SplashScreen.setProgress("Skipping Sequence Repository");
				logger.info("Skipping Sequence Repository (as requested in config file)");
			}
            // allow Proline Studio to be missing (or if launcher is in server mode)
            // TODO make a completely different method for server mode, to allow a different behaviour and an easier way to test each modes
//            if(!Config.isServerMode()) {
                SplashScreen.setProgress("Starting Proline Studio");
                ExecutionSession.getStudio().start(); // TODO rename start to startAndWait ? or maybe do the waiting here ?
                logger.info("Studio closed, stop Cortex & JMS");
                // stop JMS, Cortex and SeqRepo (should be useless because it will be done in the Shutdown Hook class)
                SystemUtils.end();
//            } else {
//                // wait until user stops Proline through the system tray
//                // TODO open Proline Monitor instead (but closing Monitor must not close Proline Zero !)
//                while(ExecutionSession.isSessionActive()) {
//                    Thread.sleep(2500);
//                }
//            }
		} catch (Throwable t) {
			// provide a msgbox to warn the user of the problem
			logger.error("Severe Error while running Proline Launcher. The application will close due to:", t);
			SystemUtils.end();
			Popup.error(t.getMessage());
		}
	}

}
