package fr.proline.zero;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import fr.proline.zero.util.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.proline.zero.gui.ConfigWindow;
import fr.proline.zero.gui.Popup;
import fr.proline.zero.gui.SplashScreen;
import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.modules.ExecutionSession;
import fr.proline.zero.modules.IZeroModule;

import javax.swing.*;

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
			// initialization of the singleton that will manage all of our properties
			ConfigManager.getInstance().initialize();


			// first launch
			boolean initBeforeStart = !ProlineFiles.PG_DATASTORE.exists() && !ProlineFiles.H2_DATASTORE.exists();

			// we check that all of our properties are correct and will not give errors


			// GUI  executed in EDT
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						boolean isOK = ConfigManager.getInstance().verif();
						if (!isOK) {
							if (ConfigManager.getInstance().isErrorFatal()) {
								// fatal errors
								Popup.error(ConfigManager.getInstance().getLastErrorMessage());
							} else {
								// minor errors
								Popup.warning(ConfigManager.getInstance().getLastErrorMessage());
							}
						}
						// launch, if needed, the config window (fatal error or show config_dialog = on)
						if (ConfigManager.getInstance().showConfigDialog() || ConfigManager.getInstance().isErrorFatal()) {
							// building the window and all its components here
							ConfigWindow paramWindow = ConfigWindow.getInstance();
							paramWindow.setVisible(true);
							// WAITING HERE FOR CONFIG WINDOW TO CLOSE
						} else {
							// The config window will save the properties in the file, if it's not displayed
							// we still need to save the properties because we may have changed them with
							// the verif() method
							ConfigManager.getInstance().updateConfigurationParams();
							//ConfigManager.getInstance().updateCortexConfigFile();
						}

					}
				});
			} /*catch (Exception ex) {
				// Handle exception
			}*/
			catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}

			// else we displayed the error messages


			logger.info("First launch, update port");
			ExecutionSession.updateConfigPort();

			logger.info("launch, update thread number");
			ExecutionSession.updateCortexNbParallelizableServiceRunners();

			// Load modules
			manageFolder(); // create missing folders inside Proline Zero (don't create folder elsewhere on
							// disk...)

			// Init
			ExecutionSession.initialize();
			ZeroTray.initialize();

			// add a shutdown hook that will be executed when the program ends or if the
			// user ends it with Ctrl+C
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());

			int nbrStep = initBeforeStart ? ExecutionSession.getModuleCount() * 2 : ExecutionSession.getModuleCount();
			SplashScreen.setProgressMax(nbrStep); // init, pgsql, admin, hornetq, cortex, seqrepo, studio
			IZeroModule nextModule = null;
			try {
				for (int i = 0; i < ExecutionSession.getModuleCount(); i++) {
					nextModule = ExecutionSession.getModuleAt(i);
					if (nextModule != null) {
						if (initBeforeStart) {
							SplashScreen.setProgress("Initializing " + nextModule.getModuleName());
							logger.info("Initializing module " + nextModule.getModuleName());
							nextModule.init();
							Thread.sleep(2000);

						}
						SplashScreen.setProgress("Starting " + nextModule.getModuleName());
						logger.info("Starting module " + nextModule.getModuleName());
						nextModule.start();
						Thread.sleep(2000);
						logger.info("Module +", nextModule.getModuleName() + " started");
					} else {
						Popup.error(" A Module is null, at index " + i);
					}
				}

			} catch (Exception e) {
				logger.error("Error during  initialization or starting module ", e);
				SystemUtils.end();
				System.exit(1);
			}

			SplashScreen.stop(2000);
			while (ExecutionSession.isSessionActive() && !ExecutionSession.isSessionToBeClose()) {
				Thread.sleep(2500);
			}
			// Execution stopped. Test how and close if needed
			if (ExecutionSession.isSessionToBeClose()) {
				logger.info("Session should be closed, stop all modules");
				SystemUtils.end();
				System.exit(0);
			} // else already closed...

		} catch (Throwable t) {
			// provide a msgbox to warn the user of the problem
			logger.error("Severe Error while running Proline Launcher. The application will close due to:", t);
			SystemUtils.end();
			Popup.error(t.getMessage());
			System.exit(1);
		}
	}

	private static void manageFolder() {
		File tmpFile = ProlineFiles.DATA_TMP_DIRECTORY;
		if (!tmpFile.isDirectory()) {
			boolean b;
			b = tmpFile.mkdir();
			logger.info("create folder {} successful ={}", tmpFile.getName(), b);
		}
		//VDS Should not be necessary anymore (added in zip)
		File fastaFile = ProlineFiles.DATA_SEQUENCE_REPOSITORY_DIRECTORY;
		if (!fastaFile.isDirectory()) {
			boolean b;
			b = fastaFile.mkdir();
			logger.info("create folder {} successful ={}", fastaFile.getName(), b);
		}

		//VDS Should not be necessary anymore (added in zip)
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

	private static synchronized void cleanTmpFolder(File tmpFile) {
		long folderSize = tmpFile.length();
		long maxSize = ConfigManager.getInstance().getMaxTmpFolderSize(); // in Mo
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
