package dev.lugami.practice.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeduplicatingArrayList<T> extends ArrayList<T> {

    @Override
    public boolean add(T element) {
        // Only deduplicate if the element is not already present
        if (!this.contains(element)) {
            super.add(element);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return super.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return super.stream();
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // Use LinkedHashSet to deduplicate and preserve order
        LinkedHashSet<T> set = new LinkedHashSet<>(this);
        set.addAll(c);
        super.clear();
        return super.addAll(set);
    }
}
