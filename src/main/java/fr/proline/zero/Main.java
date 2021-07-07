package fr.proline.zero;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.modules.ExecutionSession;
import fr.proline.zero.util.Config;
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

			// VDS TODO Add load config and first verif here !!

			// if specified in config load IHM
			ConfigWindow test = new ConfigWindow();

			// VDS TODO: test and save new config here !!

//			//Load modules
//			manageFolder(); //create missing folders inside Proline Zero (don't create folder elsewhere on disk...)
//
//			//Init
//			ExecutionSession.initialize();
//			ZeroTray.initialize();
//
//			// add a shutdown hook that will be executed when the program ends or if the
//			// user ends it with Ctrl+C
//			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
//			boolean initBeforeStart = !ProlineFiles.PG_DATASTORE.exists() && !ProlineFiles.H2_DATASTORE.exists(); // first launch
//
//			//VDS TODO --  To be moved in config part above
//			logger.info("First launch, update port");
//			ExecutionSession.updateConfigPort();// can be change only 1 time at the first time
//			logger.info("launch, update thread number");
//			ExecutionSession.updateCortexNbParallelizableServiceRunners();
//			ExecutionSession.updateCortexNbParallelizableServiceRunners();// each time, we can change
//			//VDS TODO --  To be moved in config part above END
//
//
//			int nbrStep = initBeforeStart ? ExecutionSession.getModuleCount() * 2 : ExecutionSession.getModuleCount() ;
//			SplashScreen.setProgressMax(nbrStep); // init, pgsql, admin, hornetq, cortex, seqrepo, studio
//			IZeroModule nextModule = null;
//			try {
//					for (int i = 0; i < ExecutionSession.getModuleCount(); i++) {
//						nextModule = ExecutionSession.getModuleAt(i);
//						if(initBeforeStart) {
//							SplashScreen.setProgress("Initializing " + nextModule.getModuleName());
//							logger.info("Initializing module +", nextModule.getModuleName());
//							nextModule.init();
//						}
//						SplashScreen.setProgress("Starting " + nextModule.getModuleName());
//						logger.info("Starting module +", nextModule.getModuleName());
//						nextModule.start();
//						logger.info("Module +", nextModule.getModuleName()+" started");
//					}
//
//			} catch (Exception e) {
//				logger.error("Error during  initialization or starting module ", e);
//				SystemUtils.end();
//				System.exit(1);
//			}
//
//			SplashScreen.stop(2000);
//			while (ExecutionSession.isSessionActive() && !ExecutionSession.isSessionToBeClose()) {
//				Thread.sleep(2500);
//			}
//			// Execution stopped. Test how and close if needed
//			if(ExecutionSession.isSessionToBeClose()){
//				logger.info("Session should be closed, stop all modules");
//				SystemUtils.end();
//			} //else already closed...

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

//	private static void startDataStore(DataStore dataStore) throws Exception {
//		SplashScreen.setProgress("Starting Datastore");
//		if (dataStore.getDatastoreName().equals(PostgreSQL.NAME)) {
//			((PostgreSQL) dataStore).verifyVersion();
//		}
//		if (Config.isAdjustMemory()) {// VDS: POurquoi adjust memory ici.... en amont ?
//			Memory.adjustMemory(Config.getTotalMemory());
//		} else {
//			if (dataStore.getDatastoreName().equals(PostgreSQL.NAME)) {
//				Memory.restorePostgreSQLDefaultConfig();
//			}
//		}
//		// start datastore
//		ExecutionSession.getDataStore().start();
//
//	}

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