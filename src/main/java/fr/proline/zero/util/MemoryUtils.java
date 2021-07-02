package fr.proline.zero.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryUtils {

	private static Logger logger = LoggerFactory.getLogger(Memory.class);

	// units
	private static final long M = 1;
	private static final long G = 1024;

	// values
	private long totalMemory;
	private long studioMemory;
	private long serverTotalMemory;
	private long seqrepMemory;
	private long datastoreMemory;
	private long prolineServerMemory;
	private long jmsMemory;

	private boolean isStudioBeingChanged;

	public enum AttributionMode {
		AUTO, SEMIAUTO, MANUAL
	};

	private AttributionMode attributionMode;

	// TODO : changer le constructeur pour lire le fichier
	public MemoryUtils() {
		switch (Config.getAllocationMode()) {
		case "auto":
			setAttributionMode(AttributionMode.AUTO);
			this.totalMemory = parseMemoryValue("Total memory value", Config.getTotalMemory());
			update(totalMemory);
			break;
		case "semi":
			setAttributionMode(AttributionMode.SEMIAUTO);
			this.studioMemory = parseMemoryValue("Total memory value", Config.getStudioMemory());
			this.serverTotalMemory = parseMemoryValue("Total memory value", Config.getServerTotalMemory());
			update(serverTotalMemory);
			break;
		case "manual":
			setAttributionMode(AttributionMode.MANUAL);
			break;
		}
		;
		this.isStudioBeingChanged = false;
	}

	// - method called for an update of all the memory values when one of the values
	// is changed
	// - depends on the mode of allocation
	// - refValue is the value of the spinner from Memorypannel that changed and
	// called for an update
	// - refValue is either :
	// - total memory value (Automatic mode)
	// - server total memory value (Semi automatic mode)
	// - studio memory value (Semi automatic mode)
	// we check which one it is with the boolean attribute isStudioBeingChanged to
	// check if we need to
	// update server modules values
	// - a particular server module value (Manual mode)
	// it is then useless because no processign will be done with it
	//
	public Boolean update(long refValue) {
		if (attributionMode.equals(AttributionMode.AUTO)) {
			for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
				if (rule.containsAuto(refValue)) {
					MemoryAllocationRule calcul = rule;

					this.studioMemory = calcul.getStudioMemory();
					this.seqrepMemory = calcul.getSeqRepoMemory();
					this.jmsMemory = calcul.getJmsMemory();
					long resteMemory = this.totalMemory - this.studioMemory - this.seqrepMemory - this.jmsMemory;

					this.prolineServerMemory = Math.round(resteMemory * 0.65);
					resteMemory = resteMemory - this.prolineServerMemory;
					this.datastoreMemory = resteMemory;

					this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
							+ this.seqrepMemory;
					break;
				}
			}
		} else if (attributionMode.equals(AttributionMode.SEMIAUTO)) {
			if (!isStudioBeingChanged) {
				for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
					if (rule.containsSemiAuto(refValue)) {
						MemoryAllocationRule calcul = rule;

						this.seqrepMemory = calcul.getSeqRepoMemory();
						this.jmsMemory = calcul.getJmsMemory();

						long resteMemory = refValue - this.jmsMemory - this.seqrepMemory;

						this.prolineServerMemory = Math.round(resteMemory * 0.65);
						resteMemory = resteMemory - this.prolineServerMemory;
						this.datastoreMemory = resteMemory;
						break;
					}
				}
			}
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
		} else {
			this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
					+ this.seqrepMemory;
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
		}
		return true;
	}

	private static long parseMemoryValue(String info, String requestedMemory) {
		long memory = -1;
		try {
			int iMemory = Integer.parseInt(requestedMemory.trim().replaceAll("[kmgtKMGT]$", ""));
			String unit = requestedMemory.trim().replaceAll("\\d", "");
			if (unit.equals("M")) {
				memory = iMemory * M;
			} else if (unit.equals("G")) {
				memory = iMemory * G;
			} else if (unit.equals("")) {
				memory = iMemory;
			}
			// extract last letter
		} catch (Exception e) {
			// if requested memory is unreadable, use available memory, show warning popup
			// and go on
			logger.warn(info + " allocated memory could not be read, Proline Zero will use available memory");
		}
		return memory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long total_memory) {
		this.totalMemory = total_memory;
	}

	public long getStudioMemory() {
		return studioMemory;
	}

	public void setStudioMemory(long studio_memory) {
		this.studioMemory = studio_memory;
	}

	public long getServerTotalMemory() {
		return serverTotalMemory;
	}

	public void setServerTotalMemory(long serverTotalMemory) {
		this.serverTotalMemory = serverTotalMemory;
	}

	public long getSeqrepMemory() {
		return seqrepMemory;
	}

	public void setSeqrepMemory(long seqrepMemory) {
		this.seqrepMemory = seqrepMemory;
	}

	public long getDatastoreMemory() {
		return datastoreMemory;
	}

	public void setDatastoreMemory(long datastoreMemory) {
		this.datastoreMemory = datastoreMemory;
	}

	public long getProlineServerMemory() {
		return prolineServerMemory;
	}

	public void setProlineServerMemory(long prolineServerMemory) {
		this.prolineServerMemory = prolineServerMemory;
	}

	public long getJmsMemory() {
		return jmsMemory;
	}

	public void setJmsMemory(long jmsMemory) {
		this.jmsMemory = jmsMemory;
	}

	public AttributionMode getAttributionMode() {
		return attributionMode;
	}

	public void setAttributionMode(AttributionMode attributionMode) {
		this.attributionMode = attributionMode;
	}

	public void setStudioBeingChanged(boolean isStudioBeingChanged) {
		this.isStudioBeingChanged = isStudioBeingChanged;
	}
}
