package fr.proline.zero.util;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;

import fr.proline.zero.gui.Popup;

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

	private boolean studioActive;

	private boolean seqRepActive;

	private static final OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory
			.getOperatingSystemMXBean();
	private static final long totalMemorySize = os.getTotalPhysicalMemorySize() / (1024 * 1024);

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
		this.hasBeenChanged = false;
		this.isStudioBeingChanged = false;
	}

	// - method called for an update of all the memory values when one of the values
	// is changed
	// - depends on the mode of allocation
	public Boolean update() {
		if (attributionMode.equals(AttributionMode.AUTO)) {
			// attribution mode = auto
			for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
				if (rule.containsAuto(this.totalMemory)) {
					MemoryAllocationRule calcul = rule;
					if (studioActive) {
						this.studioMemory = calcul.getStudioMemory();
					} else {
						this.studioMemory = 0;
					}
					if (seqRepActive) {
						this.seqrepMemory = calcul.getSeqRepoMemory();
					} else {
						this.seqrepMemory = 0;
					}
					this.jmsMemory = calcul.getJmsMemory();
					long resteMemory = this.totalMemory - this.studioMemory - this.seqrepMemory - this.jmsMemory;

					this.datastoreMemory = Math.round(resteMemory * 0.35);
					if (this.datastoreMemory > calcul.getMaxPGMemory()) {
						this.datastoreMemory = (long) calcul.getMaxPGMemory();
					}
					resteMemory = resteMemory - this.datastoreMemory;
					this.prolineServerMemory = resteMemory;

					this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
							+ this.seqrepMemory;
					break;
				}
			}
		} else if (attributionMode.equals(AttributionMode.SEMIAUTO)) {
			// attribution mode = semi-auto
			if (!isStudioBeingChanged) {
				for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
					if (rule.containsSemiAuto(this.serverTotalMemory)) {
						MemoryAllocationRule calcul = rule;

						if (seqRepActive) {
							this.seqrepMemory = calcul.getSeqRepoMemory();
						} else {
							this.seqrepMemory = 0;
						}
						this.jmsMemory = calcul.getJmsMemory();

						long resteMemory = this.serverTotalMemory - this.jmsMemory - this.seqrepMemory;

						this.datastoreMemory = Math.round(resteMemory * 0.35);
						if (this.datastoreMemory > calcul.getMaxPGMemory()) {
							this.datastoreMemory = (long) calcul.getMaxPGMemory();
						}
						resteMemory = resteMemory - this.datastoreMemory;
						this.prolineServerMemory = resteMemory;
						break;
					}
				}
			}
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
		} else {
			// attribution mode = manual
			this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
					+ this.seqrepMemory;
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
		}
		verif();
		return true;
	}

	// reads the memory values in the "String" (eg : 5.5G or 700M) format and
	// returns it in long "Mo" format (eg : 5632 or 700)
	private static long parseMemoryValue(String info, String requestedMemory) {
		long memory = -1;
		try {
			String string = requestedMemory.trim().replaceAll("[mgMG]$", "");

			// extract last letter
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
		} catch (Exception e) {
			// if requested memory is unreadable, use available memory, show warning popup
			// and go on
			logger.warn(info + " allocated memory could not be read, Proline Zero will use available memory");
		}
		return memory;
	}

	// does the opposite of the previous method; takes the memory value from long
	// (Mo) format and returns them in String format (eg : 5.5G or 700M)
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

	public void setTotalMemory(long total_memory) {
		this.totalMemory = total_memory;
		this.hasBeenChanged = true;
	}

	public long getStudioMemory() {
		return studioMemory;
	}

	public void setStudioMemory(long studio_memory) {
		this.studioMemory = studio_memory;
		this.hasBeenChanged = true;
	}

	public long getServerTotalMemory() {
		return serverTotalMemory;
	}

	public void setServerTotalMemory(long serverTotalMemory) {
		this.serverTotalMemory = serverTotalMemory;
		this.hasBeenChanged = true;
	}

	public long getSeqrepMemory() {
		return seqrepMemory;
	}

	public void setSeqrepMemory(long seqrepMemory) {
		this.seqrepMemory = seqrepMemory;
		this.hasBeenChanged = true;
	}

	public long getDatastoreMemory() {
		return datastoreMemory;
	}

	public void setDatastoreMemory(long datastoreMemory) {
		this.datastoreMemory = datastoreMemory;
		this.hasBeenChanged = true;
	}

	public long getProlineServerMemory() {
		return prolineServerMemory;
	}

	public void setProlineServerMemory(long prolineServerMemory) {
		this.prolineServerMemory = prolineServerMemory;
		this.hasBeenChanged = true;
	}

	public long getJmsMemory() {
		return jmsMemory;
	}

	public void setJmsMemory(long jmsMemory) {
		this.jmsMemory = jmsMemory;
		this.hasBeenChanged = true;
	}

	public AttributionMode getAttributionMode() {
		return attributionMode;
	}

	public void setAttributionMode(AttributionMode attributionMode) {
		this.attributionMode = attributionMode;
		this.hasBeenChanged = true;
	}

	public void setStudioBeingChanged(boolean isStudioBeingChanged) {
		this.isStudioBeingChanged = isStudioBeingChanged;
	}

	// methods to get memory values in config file format (eg : 5.5G or 500M)
	public String getTotalMemoryString() {
		return toConfigFile(totalMemory);
	}

	public String getStudioMemoryString() {
		return toConfigFile(studioMemory);
	}

	public String getServerTotalMemoryString() {
		return toConfigFile(serverTotalMemory);
	}

	public String getSeqrepMemoryString() {
		return toConfigFile(seqrepMemory);
	}

	public String getDatastoreMemoryString() {
		return toConfigFile(datastoreMemory);
	}

	public String getProlineServerMemoryString() {
		return toConfigFile(prolineServerMemory);
	}

	public String getJmsMemoryString() {
		return toConfigFile(jmsMemory);
	}

	public void setHasBeenChanged(boolean bool) {
		this.hasBeenChanged = bool;
	}

	public boolean hasBeenChanged() {
		return this.hasBeenChanged;
	}

	public void setStudioActive(boolean b) {
		this.studioActive = b;
		if (!b) {
			setStudioMemory(0);
		}
		update();
	}

	public boolean getStudioActive() {
		return this.studioActive;
	}

	public void setSeqRepActive(boolean b) {
		this.seqRepActive = b;
		if (!b) {
			setSeqrepMemory(0);
		}
		update();
	}

	public boolean getSeqRepActive() {
		return this.seqRepActive;
	}

	private static AttributionMode getFromConfig(String attributionMode) {
		switch (attributionMode) {
		case "auto":
			return AttributionMode.AUTO;
		case "semi":
			return AttributionMode.SEMIAUTO;
		case "manual":
			return AttributionMode.MANUAL;
		default:
			return null;
		}
	}

	// reset the values to those in the config file
	public void restoreValues() {
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
		this.hasBeenChanged = false;
		this.isStudioBeingChanged = false;
	}

	private void verif() {
		if (totalMemory > totalMemorySize) {
			Popup.warning("The specified total memory value is greater than what is available : \n"
					+ "80% of the available memory will be allocated to Proline Zero");
			setTotalMemory((long) Math.round(totalMemorySize * 0.8));
			update();
		}
		if ((totalMemory < 4 * G) && seqRepActive) {
			Popup.warning("The specified total memory value is below 4Go : \n"
					+ "The Sequence Repository Module cannot be active ");
			setSeqRepActive(false);
		}
		if (totalMemory < 3 * G) {
			Popup.warning("The specified total memory value is below what is necessary : \n"
					+ "That value cannot go below 3Go");
			setTotalMemory(3 * G);
			update();
		}
	}
}
