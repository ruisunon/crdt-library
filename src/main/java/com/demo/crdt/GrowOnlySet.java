package com.demo.crdt;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rui S.
 * @date 2022-11-12
 * @apiNote
 */
public class GrowOnlySet<T> implements Crdt<GrowOnlySet<T>> {


    private Set<T>  items= new LinkedHashSet<T>();
    public void add(T item) {
        items.add(item);
    }

    public Set<T> get() {
        return Collections.unmodifiableSet(items);
    }

    /**
     * Merge another set into this one
     */
    @Override
    public void merge(GrowOnlySet<T> set) {
        items.addAll(set.items);
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public void addAll(Collection<T> itemToAdd) {
        items.addAll(itemToAdd);
    }

    public GrowOnlySet<T> copy() {
        GrowOnlySet<T> copy = new GrowOnlySet<T>();
        copy.items = new LinkedHashSet<T>(items);
        return copy;
    }
}
