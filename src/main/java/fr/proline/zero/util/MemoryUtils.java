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

	private boolean hasBeenChanged;

	public enum AttributionMode {
		AUTO {
			public String toString() {
				return "auto";
			}
		},
		SEMIAUTO {
			public String toString() {
				return "semi";
			}
		},
		MANUAL {
			public String toString() {
				return "manual";
			}
		};

	};

	private AttributionMode attributionMode;

	// TODO : changer le constructeur pour lire le fichier
	public MemoryUtils() {
		switch (Config.getAllocationMode()) {
		case "auto":
			setAttributionMode(AttributionMode.AUTO);
			setTotalMemory(parseMemoryValue("Total memory value ", Config.getTotalMemory()));
			update();
			break;
		case "semi":
			setAttributionMode(AttributionMode.SEMIAUTO);
			setStudioMemory(parseMemoryValue("Studio memory value ", Config.getStudioMemory()));
			setServerTotalMemory(parseMemoryValue("Total server memory value ", Config.getServerTotalMemory()));
			update();
			break;
		case "manual":
			setAttributionMode(AttributionMode.MANUAL);
			setStudioMemory(parseMemoryValue("Studio memory value ", Config.getStudioMemory()));
			setSeqrepMemory(parseMemoryValue("SeqRep memory value ", Config.getSeqRepMemory()));
			setDatastoreMemory(parseMemoryValue("PG memory value ", Config.getDatastoreMemory()));
			setProlineServerMemory(parseMemoryValue("Cortex memory value ", Config.getCortexMemory()));
			setJmsMemory(parseMemoryValue("JMS memory value ", Config.getJMSMemory()));
			update();
			break;
		}
		;
		hasBeenChanged = false;
		this.isStudioBeingChanged = false;
	}

	// - method called for an update of all the memory values when one of the values
	// is changed
	// - depends on the mode of allocation
	public Boolean update() {
		if (attributionMode.equals(AttributionMode.AUTO)) {
			for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
				if (rule.containsAuto(this.totalMemory)) {
					MemoryAllocationRule calcul = rule;

					this.studioMemory = calcul.getStudioMemory();
					this.seqrepMemory = calcul.getSeqRepoMemory();
					this.jmsMemory = calcul.getJmsMemory();
					long resteMemory = this.totalMemory - this.studioMemory - this.seqrepMemory - this.jmsMemory;

					this.datastoreMemory = Math.round(resteMemory * 0.35);
					if (this.datastoreMemory > 5120) {
						this.datastoreMemory = 5120;
					}
					resteMemory = resteMemory - this.datastoreMemory;
					this.prolineServerMemory = resteMemory;

					this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
							+ this.seqrepMemory;
					break;
				}
			}
		} else if (attributionMode.equals(AttributionMode.SEMIAUTO)) {
			if (!isStudioBeingChanged) {
				for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
					if (rule.containsSemiAuto(this.serverTotalMemory)) {
						MemoryAllocationRule calcul = rule;

						this.seqrepMemory = calcul.getSeqRepoMemory();
						this.jmsMemory = calcul.getJmsMemory();

						long resteMemory = this.serverTotalMemory - this.jmsMemory - this.seqrepMemory;

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
			String string = requestedMemory.trim().replaceAll("[kmgtKMGT]$", "");
			String unit = requestedMemory.trim().replaceAll("[^A-Za-z ]", "");
			if (unit.equals("M")) {
				int iMemory = Integer.parseInt(string);
				memory = iMemory * M;
			} else if (unit.equals("G")) {
				double iMemory = Double.parseDouble(string);
				memory = Math.round((iMemory) * G);
			} else if (unit.equals("")) {
				int iMemory = Integer.parseInt(string);
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

	private static String toConfigFile(long value) {
		if (value == 0) {
			return "0";
		}
		if (value < G) {
			return value + "M";
		}
		return String.valueOf(value / (float) G) + "G";
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public String getTotalMemoryString() {
		return toConfigFile(totalMemory);
	}

	public void setTotalMemory(long total_memory) {
		this.totalMemory = total_memory;
		hasBeenChanged = true;
	}

	public long getStudioMemory() {
		return studioMemory;
	}

	public String getStudioMemoryString() {
		return toConfigFile(studioMemory);
	}

	public void setStudioMemory(long studio_memory) {
		this.studioMemory = studio_memory;
		hasBeenChanged = true;
	}

	public long getServerTotalMemory() {
		return serverTotalMemory;
	}

	public String getServerTotalMemoryString() {
		return toConfigFile(serverTotalMemory);
	}

	public void setServerTotalMemory(long serverTotalMemory) {
		this.serverTotalMemory = serverTotalMemory;
		hasBeenChanged = true;
	}

	public long getSeqrepMemory() {
		return seqrepMemory;
	}

	public String getSeqrepMemoryString() {
		return toConfigFile(seqrepMemory);
	}

	public void setSeqrepMemory(long seqrepMemory) {
		this.seqrepMemory = seqrepMemory;
		hasBeenChanged = true;
	}

	public long getDatastoreMemory() {
		return datastoreMemory;
	}

	public String getDatastoreMemoryString() {
		return toConfigFile(datastoreMemory);
	}

	public void setDatastoreMemory(long datastoreMemory) {
		this.datastoreMemory = datastoreMemory;
		hasBeenChanged = true;
	}

	public long getProlineServerMemory() {
		return prolineServerMemory;
	}

	public String getProlineServerMemoryString() {
		return toConfigFile(prolineServerMemory);
	}

	public void setProlineServerMemory(long prolineServerMemory) {
		this.prolineServerMemory = prolineServerMemory;
		hasBeenChanged = true;
	}

	public long getJmsMemory() {
		return jmsMemory;
	}

	public String getJmsMemoryString() {
		return toConfigFile(jmsMemory);
	}

	public void setJmsMemory(long jmsMemory) {
		this.jmsMemory = jmsMemory;
		hasBeenChanged = true;
	}

	public AttributionMode getAttributionMode() {
		return attributionMode;
	}

	public void setAttributionMode(AttributionMode attributionMode) {
		this.attributionMode = attributionMode;
		hasBeenChanged = true;
	}

	public void setStudioBeingChanged(boolean isStudioBeingChanged) {
		this.isStudioBeingChanged = isStudioBeingChanged;
	}

	public void restoreValues() {
		setTotalMemory(parseMemoryValue("total memory", Config.getTotalMemory()));
		setStudioMemory(parseMemoryValue("studio memory", Config.getStudioMemory()));
		setServerTotalMemory(parseMemoryValue("total server memory", Config.getServerTotalMemory()));
		setSeqrepMemory(parseMemoryValue("seqrep memory", Config.getSeqRepMemory()));
		setDatastoreMemory(parseMemoryValue("datastore memory", Config.getDatastoreMemory()));
		setProlineServerMemory(parseMemoryValue("cortex memory", Config.getCortexMemory()));
		setJmsMemory(parseMemoryValue("jms memory", Config.getJMSMemory()));
		hasBeenChanged = false;
	}
}
