package fr.proline.zero.modules;

import java.io.File;
import java.io.PrintWriter;

import fr.proline.zero.gui.ZeroTray;
import fr.proline.zero.util.Config;
import fr.proline.zero.util.ProlineFiles;
import fr.proline.zero.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

public class PostgreSQL extends DataStore {

	private static Logger logger = LoggerFactory.getLogger(PostgreSQL.class);
	private static String PG_DATA = SystemUtils.toSystemPath(ProlineFiles.PG_DATASTORE_RELATIVE_PATH);

	public String getDatastoreName() {
		return "PostgreSQL";
	}

	private String getJavaPath() throws Exception {
		File path = Config.getJavaHome();
		if(!path.exists()) {
			logger.warn("Java home is not configured, PostgreSQL may crash due to missing MSVC dll files.");
		}
		return path.getAbsolutePath()+"/bin";
	}

	public void init() throws Exception {
		if (SystemUtils.isPortAvailable(Config.getPostgreSQLPort())) {
			logger.info("Initializing PostgreSQL datastore");
			// create the temporary password file (delete it if it already exists)
			File tempPasswdFile = ProlineFiles.PG_PASSWD_FILE;
			if (tempPasswdFile.exists()) {
				boolean success = tempPasswdFile.delete();
				if (!success) {
					logger.error("Could not delete file {}, please delete it manually and restart Proline", tempPasswdFile.getAbsolutePath());
					throw new RuntimeException("Could not delete existing postres passwd temporary file");
				}
			}
			PrintWriter writer = new PrintWriter(tempPasswdFile);
			writer.println("proline");
			writer.flush();
			writer.close();
			if(!tempPasswdFile.exists()) {
				throw new RuntimeException("Could not create temporary file");
			}
			// we add the jre/bin directory to the PATH environment variable because it contains the MSVCR120.dll file and others that PostgreSQL may need
			ProcessResult result = new ProcessExecutor()
					.command("./pgsql/bin/initdb", "-Uproline", "-Apassword", "-Eutf8", "--pwfile=postgres.passwd", "-D"+PG_DATA)
					.redirectOutput(Slf4jStream.ofCaller().asInfo())
					.redirectError(Slf4jStream.ofCaller().asError())
					.environment("PATH", SystemUtils.getPathEnvironmentVariable(getJavaPath()))
					.redirectOutput(new LogOutputStream() {
						@Override
						protected void processLine(String line) {
			            	if(Config.isDebugMode()) logger.debug(line);
						}
					})
					.execute();
			int exitCode = result.getExitValue();
			if (exitCode == 0) {
				logger.info("pginit finish successfully");
			} else {
				logger.info("pginit fails with error code {}", exitCode);
				logger.info("pginit fails with output '{}'", result.outputString());
				throw new RuntimeException("Could not init postgreSQL datastore, it may be a problem with the path of PostgreSQL");
			}
			boolean success = tempPasswdFile.delete();
			if (!success) {
				logger.warn("Could not delete newly created temporary file {}", tempPasswdFile.getAbsolutePath());
			}
		} else {
			logger.error("PostgreSQL port {} is already in use. This may be caused by another process talking to this port or by an existing postgreSQL server instance already running", Config.getPostgreSQLPort());
			throw new IllegalArgumentException("PostgreSQL port " + Config.getPostgreSQLPort() + " is already in use");
		}
	}

	public void start() throws Exception {

		if (SystemUtils.isPortAvailable(Config.getPostgreSQLPort())) {
			
			//
			// Warning : pg_ctl start got a strange behavior :
			// the process stopped with error code 1 but in case of success
			// the process returns an exit code of 0 but remains running (the
			// execute().getExitValue() never returns.
			//
			StartedProcess pg = new ProcessExecutor()
					.command("./pgsql/bin/pg_ctl", "-w", "-D"+PG_DATA, "-l"+ProlineFiles.PG_LOG_FILENAME, "-o \"-p" + Config.getPostgreSQLPort() + "\"", "start")
					.redirectOutput(Slf4jStream.ofCaller().asInfo())
					.environment("PATH", SystemUtils.getPathEnvironmentVariable(getJavaPath()))
					.start();

			while (pg.getProcess().isAlive()) {
				Thread.sleep(200);
			}

			int exitCode = pg.getProcess().exitValue();
			if (exitCode == 0) {
				// TODO : verify the postmaster.pid file in databases/pg folder and retrieve the pid from that file.
                // PID cannot be retrieved from PidUtil.getPid(process.getProcess()) because pg_ctl is not the postgresql process, only the launcher
				logger.info("PostgreSQL successfully started");
				isProcessAlive = true;
			} else {
				logger.info("the process terminates with error code {}", exitCode);
				throw new RuntimeException("Could not start postgreSQL server");
			}
		} else {
			logger.error("PostgreSQL port {} is already in use. This may be caused by another process talking on this port or by an existing postgreSQL server instance already running", Config.getPostgreSQLPort());
			throw new IllegalArgumentException("PostgreSQL port "+ Config.getPostgreSQLPort() +" is already in use") ;
		}
		ZeroTray.update();

	}

	public void stop() throws Exception {
		if(isProcessAlive) {
			if ((new File(PG_DATA)).exists()) {
				int exitCode = new ProcessExecutor()
						.command("./pgsql/bin/pg_ctl", "-w", "-D" + PG_DATA, "-l " + ProlineFiles.PG_LOG_FILENAME, "stop")
						.redirectOutput(Slf4jStream.ofCaller().asInfo())
						.execute().getExitValue();
				logger.info("PostgreSQL process terminates with exit code {}", exitCode);
			} else {
				logger.info("Can't stop PostgreSQL, datastore does not exist");
			}
			isProcessAlive = false;
			ZeroTray.update();
		}
	}

}