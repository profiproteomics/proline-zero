package fr.proline.zero.modules;

public abstract class DataStore {

	protected boolean isProcessAlive = false;
	public boolean isProcessAlive() {
		return isProcessAlive;
	}
	
	public abstract void init() throws Exception;

	public abstract void start() throws Exception;
	
	public abstract void stop() throws Exception;

	public abstract String getDatastoreName();

}
