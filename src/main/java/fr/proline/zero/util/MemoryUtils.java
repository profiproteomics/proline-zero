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

	public ArrayList<Long> updateManual(long studioMem, long jmsMem, long seqrepMem, long datastoreMem,
			long cortexMem) {
		System.out.println("test boucle");
		this.studio_memory = studioMem;
		this.seqrep_memory = seqrepMem;
		this.datastore_memory = datastoreMem;
		this.proline_server_memory = cortexMem;
		this.jms_memory = jmsMem;
		this.server_total_memory = this.datastore_memory + this.jms_memory;
		this.total_memory = this.studio_memory + this.server_total_memory;

		return this.memory;
	}
}
