package fr.proline.zero.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LogBuffer {
    private final int limit;
    private final String[] data;
    private int counter = 0;

    public LogBuffer(int limit) {
        this.limit = limit;
        this.data = new String[limit];
    }

    public void collect(String line) {
        data[counter++ % limit] = line;
    }

    public List<String> contents() {
        return IntStream.range(counter < limit ? 0 : counter - limit, counter)
                .mapToObj(index -> data[index % limit])
                .collect(Collectors.toList());
    }
}
