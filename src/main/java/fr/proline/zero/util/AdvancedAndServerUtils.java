package fr.proline.zero.util;

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

	public AdvancedAndServerUtils() {
		setDataStorePort(Config.getDataStorePort());
		setJmsServerPort(Config.getJmsPort());
		setJmsBatchServerPort(Config.getJmsBatchPort());
		setJnpServerPort(Config.getJnpPort());
		setJnpRmiServerPort(Config.getJnpRmiPort());
		setServerDefaultTimeout(Config.getDefaultTimeoutSeconds());
		setServerThreadPoolSize(Config.getCortexNbParallelizableServiceRunners());
		setJvmPath(Config.getJavaExePath());
		setForceDataStoreUpdate(Config.getForceUpdate());
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

	public int getServerDefaultTimeout() {
		return serverDefaultTimeout;
	}

	public void setServerDefaultTimeout(int serverDefaultTimeout) {
		this.serverDefaultTimeout = serverDefaultTimeout;
	}

	public int getServerThreadPoolSize() {
		return serverThreadPoolSize;
	}

	public void setServerThreadPoolSize(int serverThreadPoolSize) {
		this.serverThreadPoolSize = serverThreadPoolSize;
	}

	public String getJvmPath() {
		return jvmPath;
	}

	public void setJvmPath(String jvmPath) {
		this.jvmPath = jvmPath;
	}

	public Boolean getForceDataStoreUpdate() {
		return forceDataStoreUpdate;
	}

	public String getForceDataStoreUpdateString() {
		if (forceDataStoreUpdate) {
			return "on";
		} else {
			return "off";
		}
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
		setServerThreadPoolSize(Config.getCortexNbParallelizableServiceRunners());
		setJvmPath(Config.getJavaExePath());
		setForceDataStoreUpdate(Config.getForceUpdate());
	}

	public void restoreAdvancedValues() {
		setJmsServerPort(Config.getJmsPort());
		setJmsBatchServerPort(Config.getJmsBatchPort());
		setJnpServerPort(Config.getJnpPort());
		setJnpRmiServerPort(Config.getJnpRmiPort());
		setServerDefaultTimeout(Config.getDefaultTimeout());
		setServerThreadPoolSize(Config.getCortexNbParallelizableServiceRunners());
		setJvmPath(Config.getJavaExePath());
		setForceDataStoreUpdate(Config.getForceUpdate());
	}

}
