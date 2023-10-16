package com.cegeka.batch.throttling;

import com.google.common.collect.Lists;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ThrottlingBatchPartitioner<T> implements Partitioner {

    private int partitionSize;

    public ThrottlingBatchPartitioner(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<T> items = findItems();

        return createPartitions(items, gridSize);
    }

    private List<T> findItems() {
        List<T> persons = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            persons.add((T) new DummyPerson());
        }
        return persons;
    }

    private Map<String, ExecutionContext> createPartitions(List<T> values, int gridSize) {
        List<List<T>> sublists = splitList(values);

        Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);
        IntStream.range(0, sublists.size())
            .forEach(i -> partitions.put(partitionKey(i + 1), createContext(sublists.get(i))));

        return partitions;
    }

    private List<List<T>> splitList(List<T> values) {
        return Lists.partition(values, partitionSize)
            .stream()
            .map(ArrayList::new)
            .collect(toList());
    }

    private ExecutionContext createContext(List<T> sublist) {
        ExecutionContext context = new ExecutionContext();
        context.put("partitionList", sublist);
        return context;
    }

    protected String partitionKey(int partitionNumber) {
        return "partition_" + partitionNumber;
    }

}
