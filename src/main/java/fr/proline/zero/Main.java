package fr.proline.zero;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.modules.DataStore;
import fr.proline.zero.modules.ExecutionSession;
import fr.proline.zero.modules.PostgreSQL;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.Memory;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;

public class Main {

	/*
	 * TODO [ ] Check that postgresql port has not been updated [ ] Remake the log
	 * file viewer with javafx [ ] add a javafx window to set java memory per
	 * component [ ] add a server mode: when set, open proline monitor, do not open
	 * studio, add an Exit button [ ] change Proline Admin version to include
	 * Proline Monitor (instead of ProlineAdmin.Main) [ ] test Proline Zero when
	 * SeqRepo is missing [ ]
	 */
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		logger.info("Starting Proline Zero");
		try {
			ConfigWindow test = new ConfigWindow();
//			manageFolder();
//			ExecutionSession.initialize();
//			ZeroTray.initialize();
//			// add a shutdown hook that will be executed when the program ends or if the
//			// user ends it with Ctrl+C
//			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
//			if (!ProlineFiles.PG_DATASTORE.exists() && !ProlineFiles.H2_DATASTORE.exists()) {// first launch
//				logger.info("First launch, update port");
//				ExecutionSession.updateConfigPort();// can be change only 1 time at the first time
//				logger.info("launch, update thread number");
//				ExecutionSession.updateCortexNbParallelizableServiceRunners();
//				SplashScreen.setProgressMax(7); // init, pgsql, admin, hornetq, cortex, seqrepo, studio
//				SplashScreen.setProgress("Initializing Datastore");
//				logger.info("Datastore folder is empty : Proline Datastore must be initialized");
//				DataStore dStore = ExecutionSession.getDataStore();
//				try {
//					dStore.init();
//				} catch (Exception e) {
//					logger.error("Error during datastore initialization", e);
//					SystemUtils.end();
//					System.exit(1);
//				}
//				startDataStore(dStore);
//				SplashScreen.setProgress("Initializing Proline databases");
//				ProlineAdmin.setUp();
//				ProlineAdmin.checkUpdate("Initializing Proline databases");
//			} else {
//				logger.info("launch, update thread number");
//				SplashScreen.setProgressMax(5); // pgsql, hornetq, cortex, seqrepo, studio
//				ExecutionSession.updateCortexNbParallelizableServiceRunners();// each time, we can change
//				startDataStore(ExecutionSession.getDataStore());
//				ProlineAdmin.checkUpdate("");
//			}
//			logger.info("Starting Proline");
//			SplashScreen.setProgress("Starting JMS Server");
//			ExecutionSession.getJMSServer().start();
//			logger.info("JMS Server started");
//			SplashScreen.setProgress("Starting Cortex Server");
//			ExecutionSession.getCortex().start();
//			logger.info("Cortex Server started");
//			// allow SeqRepo to be missing (if not packaged with Proline Zero, or if
//			// launcher is used on a server)
//			// TODO test this case !
//			if (Config.isSeqRepoEnabled() && ProlineFiles.SEQREPO_DIRECTORY.exists()) {
//				SplashScreen.setProgress("Starting Sequence Repository");
//				ExecutionSession.getSeqRepo().start();
//				logger.info("Sequence Repository started");
//			} else {
//				SplashScreen.setProgress("Skipping Sequence Repository");
//				logger.info("Skipping Sequence Repository (as requested in config file)");
//			}
//			// allow Proline Studio to be missing (or if launcher is in server mode)
//			// TODO make a completely different method for server mode, to allow a different
//			// behaviour and an easier way to test each modes
//			if (!Config.isServerMode()) {
//				SplashScreen.setProgress("Starting Proline Studio");
//				ExecutionSession.getStudio().start(); // TODO rename start to startAndWait ? or maybe do the waiting
//														// here ?
//				logger.info("Studio closed, stop Cortex & JMS");
//				// stop JMS, Cortex and SeqRepo (should be useless because it will be done in
//				// the Shutdown Hook class)
//				SystemUtils.end();
//			} else {
//				SplashScreen.setProgress("Skipping Proline Studio");
//				logger.info("Skipping Proline Studio (as requested in config file)");
//				SplashScreen.stop();
////                // wait until user stops Proline through the system tray
////                // TODO open Proline Monitor instead (but closing Monitor must not close Proline Zero !)
//				while (ExecutionSession.isSessionActive()) {
//					Thread.sleep(2500);
//				}
//			}
		} catch (Throwable t) {
			// provide a msgbox to warn the user of the problem
			logger.error("Severe Error while running Proline Launcher. The application will close due to:", t);
			SystemUtils.end();
			Popup.error(t.getMessage());
		}
	}

	private static void manageFolder() {
		File tmpFile = ProlineFiles.DATA_TMP_DIRECTORY;
		if (!tmpFile.isDirectory()) {
			boolean b;
			b = tmpFile.mkdir();
			logger.info("create folder {} successful ={}", tmpFile.getName(), b);
		}
		File fastaFile = ProlineFiles.DATA_SEQUENCE_REPOSITORY_DIRECTORY;
		if (!fastaFile.isDirectory()) {
			boolean b;
			b = fastaFile.mkdir();
			logger.info("create folder {} successful ={}", fastaFile.getName(), b);
		}
		String dataMzdbPath = ExecutionSession.getMzdbFolder().replace("..", ".");// relative under working directory,
																					// not relative to cortex directory
		File mzdbFile = new File(dataMzdbPath);
		if (!mzdbFile.isDirectory()) {
			boolean b;
			b = mzdbFile.mkdir();
			logger.info("create folder {} successful={}", mzdbFile.getName(), b);
			mzdbFile.mkdir();
		}
		cleanTmpFolder(tmpFile);
	}

	private static void startDataStore(DataStore dataStore) throws Exception {
		SplashScreen.setProgress("Starting Datastore");
		if (dataStore.getDatastoreName().equals(PostgreSQL.NAME)) {
			((PostgreSQL) dataStore).verifyVersion();
		}
		if (Config.isAdjustMemory()) {// VDS: POurquoi adjust memory ici.... en amont ?
			Memory.adjustMemory(Config.getTotalMemory());
		} else {
			if (dataStore.getDatastoreName().equals(PostgreSQL.NAME)) {
				Memory.restorePostgreSQLDefaultConfig();
			}
		}
		// start datastore
		ExecutionSession.getDataStore().start();

	}

	private static synchronized void cleanTmpFolder(File tmpFile) {
		long folderSize = tmpFile.length();
		long maxSize = Config.getMaxTmpFolderSize(); // in Mo
		if (maxSize < 0) {
			return;
		}
		if (folderSize > maxSize * 1024) {// to bytes
			try {
				boolean confirm = Popup
						.okCancel("Temporary ./data/tmp folder size now is " + folderSize + "byte,\n more than "
								+ maxSize + "byte defined in configuration, Do you confirm to clean the folder?");
				if (confirm) {
					FileUtils.cleanDirectory(tmpFile);
				}
			} catch (IOException ex) {
				logger.info("clean folder ./data/tmp exception" + ex.getCause() + " " + ex.getMessage() + " "
						+ ex.getLocalizedMessage());
			}
		}
	}
}
