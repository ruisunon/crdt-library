package com.demo.crdt;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rui S.
 * @date 2022-11-12
 * @apiNote
 */
public class TwoPhraseSet<T> implements Crdt<TwoPhraseSet<T>> {

    private GrowOnlySet<T> addedItems = new GrowOnlySet<T>();
    private GrowOnlySet<T> removedItems = new GrowOnlySet<T>();

    public void add(T item) {
        if(removedItems.contains(item))
            throw new IllegalArgumentException("Item was already removed");
        addedItems.add(item);
    }

    public void remove(T item) {
        if( addedItems.contains(item) )
            removedItems.add(item);
    }

    public Set<T> get() {
        Set<T> added = new LinkedHashSet<T>( addedItems.get() );
        added.removeAll( removedItems.get() );
        return added;
    }

    @Override
    public void merge(TwoPhraseSet<T> set) {
        addedItems.addAll( set.addedItems.get() );
        removedItems.addAll( set.removedItems.get() );
    }

    @Override
    public TwoPhraseSet<T> copy() {
        TwoPhraseSet<T> copy = new TwoPhraseSet<T>();
        copy.addedItems = addedItems.copy();
        copy.removedItems = removedItems.copy();
        return copy;
    }

}
