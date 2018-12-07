package fr.proline.zero.util;

import fr.proline.zero.modules.ExecutionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import fr.proline.zero.modules.ExecutionSession;

public class ShutdownHook extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
//	private ExecutionSession executionSession;

//	public ShutdownHook(ExecutionSession configuration) {
//		executionSession = configuration;
//	}
	
	@Override
	public void run() {
		if(ExecutionSession.isSessionActive()) {
			logger.info("Launcher is being interrupted, stopping all processes");
			SystemUtils.end();
			logger.info("Exit launcher");
		}
	}
}
