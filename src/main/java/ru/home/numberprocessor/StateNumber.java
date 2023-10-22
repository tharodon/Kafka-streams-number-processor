package ru.home.numberprocessor;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class StateNumber {

    private final Map<Integer, List<List<Integer>>> aggregatedByTen = new HashMap<>();

    private Integer lastNumber;

    public StateNumber process(Integer value) {
        int ten = value / 10;
        aggregatedByTen.merge(ten, createFirstInstance(value), this::mergeFunction);
        lastNumber = value;
        return this;
    }

    private List<List<Integer>> createFirstInstance(Integer value) {
        List<Integer> integers = new LinkedList<>();
        integers.add(value);
        List<List<Integer>> linkedLists = new LinkedList<>();
        linkedLists.add(integers);
        return linkedLists;
    }

    private List<List<Integer>> mergeFunction(List<List<Integer>> existsLists, List<List<Integer>> lists) {
        Integer value = lists.get(0).get(0);
        if (isEqualsTen(value)) {
            existsLists.get(0).add(value);
        } else {
            existsLists.add(0, lists.get(0));
        }
        return existsLists;
    }

    private boolean isEqualsTen(Integer value) {
        return value / 10 == lastNumber / 10;
    }
}
