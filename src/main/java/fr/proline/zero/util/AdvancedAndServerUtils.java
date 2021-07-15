package fr.proline.zero.util;

import java.io.File;

public class AdvancedAndServerUtils {
	private int dataStorePort;
	private int jmsServerPort;
	private int jmsBatchServerPort;
	private int jnpServerPort;
	private int jnpRmiServerPort;
	private int serverDefaultTimeout;
	private int serverThreadPoolSize;
	private String jvmPath;
	private Boolean forceDataStoreUpdate;

	private boolean hasBeenChanged;

	private String errorMessage;
	private boolean errorFatal;

	public AdvancedAndServerUtils() {
		dataStorePort = Config.getDataStorePort();
		jmsServerPort = Config.getJmsPort();
		jmsBatchServerPort = Config.getJmsBatchPort();
		jnpServerPort = Config.getJnpPort();
		jnpRmiServerPort = Config.getJnpRmiPort();
		serverDefaultTimeout = Config.getDefaultTimeout();
		serverThreadPoolSize = Config.getCortexNbParallelizableServiceRunners();
		jvmPath = Config.getJavaHome();
		forceDataStoreUpdate = Config.getForceUpdate();
		errorFatal = false;
	}

	public int getDataStorePort() {
		return dataStorePort;
	}

	public void setDataStorePort(int dataStorePort) {
		this.dataStorePort = dataStorePort;
	}

	public int getJmsServerPort() {
		return jmsServerPort;
	}

	public void setJmsServerPort(int jmsServerPort) {
		this.jmsServerPort = jmsServerPort;
	}

	public int getJmsBatchServerPort() {
		return jmsBatchServerPort;
	}

	public void setJmsBatchServerPort(int jmsBatchServerPort) {
		this.jmsBatchServerPort = jmsBatchServerPort;
	}

	public int getJnpServerPort() {
		return jnpServerPort;
	}

	public void setJnpServerPort(int jnpServerPort) {
		this.jnpServerPort = jnpServerPort;
	}

	public int getJnpRmiServerPort() {
		return jnpRmiServerPort;
	}

	public void setJnpRmiServerPort(int jnpRmiServerPort) {
		this.jnpRmiServerPort = jnpRmiServerPort;
	}

	/**
	 * Return the timeout to wait process to start, in millisecond
	 */
	public int getServerDefaultTimeout() {
		return serverDefaultTimeout;
	}

	public void setServerDefaultTimeout(int serverDefaultTimeout) {
		this.serverDefaultTimeout = serverDefaultTimeout;
	}

	public int getCortexNbParallelizableServiceRunners() {
		return serverThreadPoolSize;
	}

	public void setCortexNbParallelizableServiceRunners(int serverThreadPoolSize) {
		this.serverThreadPoolSize = serverThreadPoolSize;
	}

	public String getJvmPath() {
		return jvmPath;
	}

	public String getJvmExePath() {
		return new File(getJvmPath() + "/bin/java").getAbsolutePath();
	}

	public void setJvmPath(String jvmPath) {
		this.jvmPath = jvmPath;
	}

	public Boolean getForceDataStoreUpdate() {
		return forceDataStoreUpdate;
	}

	public void setForceDataStoreUpdate(Boolean forceDataStoreUpdate) {
		this.forceDataStoreUpdate = forceDataStoreUpdate;
	}

	public void setHasBeenChanged(boolean bool) {
		this.hasBeenChanged = bool;
	}

	public boolean hasBeenChanged() {
		return this.hasBeenChanged;
	}

	public void restoreValues() {
		setDataStorePort(Config.getDataStorePort());
		setJmsServerPort(Config.getJmsPort());
		setJmsBatchServerPort(Config.getJmsBatchPort());
		setJnpServerPort(Config.getJnpPort());
		setJnpRmiServerPort(Config.getJnpRmiPort());
		setServerDefaultTimeout(Config.getDefaultTimeout());
		setCortexNbParallelizableServiceRunners(Config.getCortexNbParallelizableServiceRunners());
		setJvmPath(Config.getJavaHome());
		setForceDataStoreUpdate(Config.getForceUpdate());
	}

	public void restoreAdvancedValues() {
		setJmsServerPort(Config.getJmsPort());
		setJmsBatchServerPort(Config.getJmsBatchPort());
		setJnpServerPort(Config.getJnpPort());
		setJnpRmiServerPort(Config.getJnpRmiPort());
		setServerDefaultTimeout(Config.getDefaultTimeout());
		setCortexNbParallelizableServiceRunners(Config.getCortexNbParallelizableServiceRunners());
		setJvmPath(Config.getJavaHome());
		setForceDataStoreUpdate(Config.getForceUpdate());
	}

	public boolean verif() {
		StringBuilder message = new StringBuilder();
		try {
			message.append(isJavaOk());
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.append(samePorts());
		if (!SystemUtils.isPortAvailable(getJmsServerPort())) {
			message.append("\n The specified JMS port is already in use");
			errorFatal = true;
		}
		if (!SystemUtils.isPortAvailable(getJmsBatchServerPort())) {
			message.append("\n The specified JMS batch port is already in use");
			errorFatal = true;
		}
		if (!SystemUtils.isPortAvailable(getJnpServerPort())) {
			message.append("\n The specified JNP port is already in use");
			errorFatal = true;
		}
		if (!SystemUtils.isPortAvailable(getJnpRmiServerPort())) {
			message.append("\n The specified JNP RMI batch port is already in use");
			errorFatal = true;
		}
		if (message.length() > 0) {
			errorMessage = message.toString();
			return false;
		}
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isErrorFatal() {
		return errorFatal;
	}

	public void resetVerif() {
		errorMessage = null;
		errorFatal = false;
	}

	private String isJavaOk() throws Exception {
		StringBuilder message = new StringBuilder();
		File jvmPathFile = new File(getJvmPath());
		if (!jvmPathFile.exists()) {
			message.append(
					"Java home is not configured, PostgreSQL may crash due to missing MSVC dll files.\n Trying to replace it with studio's java instead");
			String javaPath = "ProlineStudio-" + Config.getStudioVersion() + "/jre";
			jvmPath = new File(javaPath).getAbsolutePath();
		}
		return message.toString();
	}

	public String samePorts() {
		StringBuilder message = new StringBuilder();
		if (getJmsServerPort() == getJmsBatchServerPort()) {
			message.append("\nThe JMS port and the JMS Batch port are the same");
			errorFatal = true;
		}
		if (getJmsServerPort() == getJnpServerPort()) {
			message.append("\nThe JMS port and the JNP port are the same");
			errorFatal = true;
		}
		if (getJmsServerPort() == getJnpRmiServerPort()) {
			message.append("\nThe JMS port and the JNP RMI port are the same");
			errorFatal = true;
		}
		if (getJmsBatchServerPort() == getJnpServerPort()) {
			message.append("\nThe JMS Batch port and the JNP port are the same");
			errorFatal = true;
		}
		if (getJmsBatchServerPort() == getJnpRmiServerPort()) {
			message.append("\nThe JMS Batch port and the JNP RMI port are the same");
			errorFatal = true;
		}
		if (getJnpServerPort() == getJnpRmiServerPort()) {
			message.append("\nThe JNP port and the JNP RMI port are the same");
			errorFatal = true;
		}
		return message.toString();
	}
}
