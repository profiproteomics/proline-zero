package fr.proline.zero.util;

import java.util.ArrayList;

public class MemoryUtils {

	private long total_memory;
	private long studio_memory;
	private long server_total_memory;
	private long seqrep_memory;
	private long datastore_memory;
	private long proline_server_memory;
	private long jms_memory;
	ArrayList<Long> memory = new ArrayList<Long>();

	public MemoryUtils(long total_memory, long proline_studio_memory, long server_total_memory, long seqrep_memory,
			long datastore_memory, long proline_server_memory, long jms_memory) {
		this.total_memory = total_memory;
		this.studio_memory = proline_studio_memory;
		this.server_total_memory = server_total_memory;
		this.seqrep_memory = seqrep_memory;
		this.datastore_memory = datastore_memory;
		this.proline_server_memory = proline_server_memory;
		this.jms_memory = jms_memory;
		this.memory = new ArrayList<Long>();

		memory.add(this.total_memory);
		memory.add(this.studio_memory);
		memory.add(this.server_total_memory);
		memory.add(this.seqrep_memory);
		memory.add(this.datastore_memory);
		memory.add(this.proline_server_memory);
		memory.add(this.jms_memory);
	}

	// public ArrayList<Long> updateAuto() {
	//
	// }

	public ArrayList<Long> updateManual(Long studioMem, Long jmsMem, Long seqrepMem, Long datastoreMem,
			Long cortexMem) {
		this.studio_memory = studioMem;
		this.seqrep_memory = seqrepMem;
		this.datastore_memory = datastoreMem;
		this.proline_server_memory = cortexMem;
		this.jms_memory = jmsMem;
		this.server_total_memory = this.datastore_memory + this.jms_memory + this.proline_server_memory
				+ this.seqrep_memory;
		this.total_memory = this.studio_memory + this.server_total_memory;

		return this.memory;
	}

	public long getTotal_memory() {
		return total_memory;
	}

	public void setTotal_memory(long total_memory) {
		this.total_memory = total_memory;
	}

	public long getStudio_memory() {
		return studio_memory;
	}

	public void setStudio_memory(long studio_memory) {
		this.studio_memory = studio_memory;
	}

	public long getServer_total_memory() {
		return server_total_memory;
	}

	public void setServer_total_memory(long server_total_memory) {
		this.server_total_memory = server_total_memory;
	}

	public long getSeqrep_memory() {
		return seqrep_memory;
	}

	public void setSeqrep_memory(long seqrep_memory) {
		this.seqrep_memory = seqrep_memory;
	}

	public long getDatastore_memory() {
		return datastore_memory;
	}

	public void setDatastore_memory(long datastore_memory) {
		this.datastore_memory = datastore_memory;
	}

	public long getProline_server_memory() {
		return proline_server_memory;
	}

	public void setProline_server_memory(long proline_server_memory) {
		this.proline_server_memory = proline_server_memory;
	}

	public long getJms_memory() {
		return jms_memory;
	}

	public void setJms_memory(long jms_memory) {
		this.jms_memory = jms_memory;
	}
}
