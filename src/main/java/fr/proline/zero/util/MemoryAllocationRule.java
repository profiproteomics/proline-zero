package fr.proline.zero.util;

import org.apache.commons.lang.math.LongRange;

public enum MemoryAllocationRule {
	// ici toutes les valeurs de mémoire sont attendues en Mo

	MIN(3072, 4097, 1024, 512, 0, 5120), RULETWO(4097, 5120, 1024, 512, 512, 5120),
	RULETHREE(5121, 6000, 1024, 512, 1024, 5120), RULEFOUR(6001, 10240, 1024, 1024, 1024, 5120),
	RULEMAX(10240, Long.MAX_VALUE, 2024, 1024, 1024, 5120);

	private LongRange range;
	private long studioMemory;
	private long jmsMemory;
	private long seqRepoMemory;
	private long maxPGMemory;

	private MemoryAllocationRule(long rangeLow, long rangeHigh, long studioMemory, long jmsMemory, long seqRepoMemory,
			long maxPGMemory) {
		this.range = new LongRange(rangeLow, rangeHigh);
		this.studioMemory = studioMemory;
		this.jmsMemory = jmsMemory;
		this.seqRepoMemory = seqRepoMemory;
		this.maxPGMemory = maxPGMemory;
	}

	public LongRange getRange() {
		return range;
	}

	public long getStudioMemory() {
		return studioMemory;
	}

	public long getJmsMemory() {
		return jmsMemory;
	}

	public long getSeqRepMemory() {
		return seqRepoMemory;
	}

	public double getMaxPGMemory() {
		return maxPGMemory;
	}

	public boolean containsAuto(long number) {
		return range.containsLong(number);
	}

	public boolean containsSemiAuto(long number) {
		long numberSemiAuto = number + studioMemory;
		return range.containsLong(numberSemiAuto);
	}
}
