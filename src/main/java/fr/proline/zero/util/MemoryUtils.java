package fr.proline.zero.util;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;

public class MemoryUtils {

	private static Logger logger = LoggerFactory.getLogger(MemoryUtils.class);

	// units
	private static final long M = 1;
	private static final long G = 1024;

	// values
	private long totalMemory;
	private long studioMemory;
	private long serverTotalMemory;
	public long seqrepMemory;
	private long datastoreMemory;
	private long prolineServerMemory;
	private long jmsMemory;

	private boolean isStudioBeingChanged;

	private boolean hasBeenChanged;

	private MemoryAllocationRule m_currentMemRule = null;

	private static final OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory
			.getOperatingSystemMXBean();
	private static final long totalMemorySize = os.getTotalPhysicalMemorySize() / (G * G);

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

	private String errorMessage;
	private boolean errorFatal;

	public MemoryUtils() {
		initializeValues();
	}

	// - method called for an update of all the memory values when one of the values
	// is changed
	// - depends on the mode of allocation
	public void update() {
		AttributionMode currentAttMode = attributionMode;
		if (attributionMode.equals(AttributionMode.AUTO) && !ConfigManager.getInstance().isStudioActive()) {
			currentAttMode = AttributionMode.SEMIAUTO;
			studioMemory = 0;
			serverTotalMemory = totalMemory;
		}
		switch (currentAttMode) {
		case AUTO:
			for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
				if (rule.containsAuto(this.totalMemory)) {
					m_currentMemRule = rule;
					this.studioMemory = m_currentMemRule.getStudioMemory();
					if (ConfigManager.getInstance().seqRepActive) {
						this.seqrepMemory = m_currentMemRule.getSeqRepMemory();
//						if (seqrepMemory == 0) {
//							ConfigManager.getInstance().setSeqRepActive(false);
//						}
					} else {
						this.seqrepMemory = 0;
					}

					this.jmsMemory = m_currentMemRule.getJmsMemory();
					long resteMemory = this.totalMemory - this.studioMemory - this.seqrepMemory - this.jmsMemory;

					this.datastoreMemory = Math.round(resteMemory * 0.35);
					if (this.datastoreMemory > m_currentMemRule.getMaxPGMemory()) {
						this.datastoreMemory = (long) m_currentMemRule.getMaxPGMemory();
					}
					resteMemory = resteMemory - this.datastoreMemory;
					this.prolineServerMemory = resteMemory;

					this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
							+ this.seqrepMemory;
					break;
				}
			}
			break;
		case SEMIAUTO:
			if (!isStudioBeingChanged) {
				for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
					if (rule.containsSemiAuto(this.serverTotalMemory)) {
						m_currentMemRule = rule;

						if (ConfigManager.getInstance().seqRepActive) {
							this.seqrepMemory = m_currentMemRule.getSeqRepMemory();
						} else {
							this.seqrepMemory = 0;
						}

						this.jmsMemory = m_currentMemRule.getJmsMemory();

						long resteMemory = this.serverTotalMemory - this.jmsMemory - this.seqrepMemory;

						this.datastoreMemory = Math.round(resteMemory * 0.35);
						if (this.datastoreMemory > m_currentMemRule.getMaxPGMemory()) {
							this.datastoreMemory = (long) m_currentMemRule.getMaxPGMemory();
						}
						resteMemory = resteMemory - this.datastoreMemory;
						this.prolineServerMemory = resteMemory;
						break;
					}
				}
			}
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
			break;
		case MANUAL:
			m_currentMemRule = null;
			this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
					+ this.seqrepMemory;
			this.totalMemory = this.studioMemory + this.serverTotalMemory;
			break;
		}

//	this.totalMemory = this.studioMemory + this.serverTotalMemory;
//		if (attributionMode.equals(AttributionMode.AUTO)) {
//			// attribution mode = auto
//			long memoryToCheck = this.totalMemory;
//
//			if (!studioActive) {
//				memoryToCheck = memoryToCheck + (1 * G);
//			}
//			for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
//				if (rule.containsAuto(memoryToCheck)) {
//					MemoryAllocationRule calcul = rule;
//					if (studioActive) {
//						this.studioMemory = calcul.getStudioMemory();
//					} else {
//						this.studioMemory = 0;
//					}
//					if (seqRepActive) {
//						this.seqrepMemory = calcul.getSeqRepoMemory();
//					} else {
//						this.seqrepMemory = 0;
//					}
//
//					this.jmsMemory = calcul.getJmsMemory();
//					long resteMemory = this.totalMemory - this.studioMemory - this.seqrepMemory - this.jmsMemory;
//
//					this.datastoreMemory = Math.round(resteMemory * 0.35);
//					if (this.datastoreMemory > calcul.getMaxPGMemory()) {
//						this.datastoreMemory = (long) calcul.getMaxPGMemory();
//					}
//					resteMemory = resteMemory - this.datastoreMemory;
//					this.prolineServerMemory = resteMemory;
//
//					this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
//							+ this.seqrepMemory;
//
//					ConfigWindow.setSeqRep(this.serverTotalMemory >= 3 * G);
//					break;
//				}
//			}
//		} else if (attributionMode.equals(AttributionMode.SEMIAUTO)) {
//			// attribution mode = semi-auto
//			if (!isStudioBeingChanged) {
//				for (MemoryAllocationRule rule : MemoryAllocationRule.values()) {
//					if (rule.containsSemiAuto(this.serverTotalMemory)) {
//						MemoryAllocationRule calcul = rule;
//
//						if (seqRepActive) {
//							this.seqrepMemory = calcul.getSeqRepoMemory();
//						} else {
//							this.seqrepMemory = 0;
//						}
//
//						this.jmsMemory = calcul.getJmsMemory();
//
//						long resteMemory = this.serverTotalMemory - this.jmsMemory - this.seqrepMemory;
//
//						this.datastoreMemory = Math.round(resteMemory * 0.35);
//						if (this.datastoreMemory > calcul.getMaxPGMemory()) {
//							this.datastoreMemory = (long) calcul.getMaxPGMemory();
//						}
//						resteMemory = resteMemory - this.datastoreMemory;
//						this.prolineServerMemory = resteMemory;
//						ConfigWindow.setSeqRep(this.serverTotalMemory >= 3 * G);
//						break;
//					}
//				}
//			}
//			this.totalMemory = this.studioMemory + this.serverTotalMemory;
//		} else {
//			// attribution mode = manual
//			this.serverTotalMemory = this.datastoreMemory + this.jmsMemory + this.prolineServerMemory
//					+ this.seqrepMemory;
//			this.totalMemory = this.studioMemory + this.serverTotalMemory;
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
	// does the opposite of the previous method; takes the memory value from long
	// (Mo) format and returns them in String format (eg : 5.5G or 700M)
	protected static String formatMemoryAsString(long value) {
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setHasBeenChanged(boolean bool) {
		this.hasBeenChanged = bool;
	}

	public boolean hasBeenChanged() {
		return this.hasBeenChanged;
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
		initializeValues();
	}

	private void initializeValues() {

		switch (Config.getAllocationMode()) {
		case "auto":
			attributionMode = AttributionMode.AUTO;
			totalMemory = parseMemoryValue("Total memory value ", Config.getTotalMemory());
			update();
			break;
		case "semi":
			attributionMode = AttributionMode.SEMIAUTO;
			studioMemory = parseMemoryValue("Studio memory value ", Config.getStudioMemory());
			setServerTotalMemory(parseMemoryValue("Total server memory value ", Config.getServerTotalMemory()));
			update();
			break;
		case "manual":
			attributionMode = AttributionMode.MANUAL;
			studioMemory = parseMemoryValue("Studio memory value ", Config.getStudioMemory());
			seqrepMemory = parseMemoryValue("SeqRep memory value ", Config.getSeqRepMemory());
			datastoreMemory = parseMemoryValue("PG memory value ", Config.getDatastoreMemory());
			prolineServerMemory = parseMemoryValue("Cortex memory value ", Config.getCortexMemory());
			jmsMemory = parseMemoryValue("JMS memory value ", Config.getJMSMemory());
			update();
			break;
		}
		hasBeenChanged = false;
		isStudioBeingChanged = false;
		errorFatal = false;
	}

	public boolean verif() {
		errorMessage = null;
		errorFatal = false;
		StringBuilder message = new StringBuilder();
		if (totalMemory > totalMemorySize) {
			message.append(
					"- The specified total memory value is greater than available Memory : \n Proline Zero won't be able to start\n");
			errorFatal = true;
		}

		if (totalMemory < MemoryAllocationRule.MIN.getRange().getMinimumInteger()) {
			message.append("The specified total memory value is below minimum required ("
					+ MemoryAllocationRule.MIN.getRange().getMinimumInteger() + ")");
			errorFatal = true;
//			ConfigManager.getInstance().setSeqRepActive(!(this.seqrepMemory == 0));
		} else if (((serverTotalMemory + (1 * G)) < MemoryAllocationRule.MIN.getRange().getMaximumInteger())
				&& ConfigManager.getInstance().isSeqRepActive()
				&& !(getAttributionMode().equals(AttributionMode.MANUAL))) {
			if (message.length() > 0)
				message.append("\n");
			message.append(
					"- The specified memory values are below what is needed to \n start the Sequence Repository Module \n ");
			ConfigManager.getInstance().setSeqRepActive(false);
		}
		// VDS TO TEST .. SHould not occur
		if (ConfigManager.getInstance().isSeqRepActive() && this.seqrepMemory == 0) {
			logger.error(" ***************** SEQ REPO ACTIVE && seqrepMemory == 0 !!!!! ");
		}

		if (message.length() > 0) {
			errorMessage = message.toString();
			return false;
		}
		return true;
	}

	public boolean canEnableSeqRepo() {
		if (m_currentMemRule == null)// We are in Manual Mode
			return true;

		long seqRepoMem = m_currentMemRule.getSeqRepMemory();
		return (seqRepoMem > 0);
	}

	public boolean isErrorFatal() {
		return errorFatal;
	}

}
