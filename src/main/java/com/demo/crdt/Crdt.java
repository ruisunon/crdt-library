package com.demo.crdt;

import java.io.Serializable;

/**
 * @author Rui S.
 * @date 2022-11-12
 * @apiNote
 */
public interface Crdt <T extends Crdt<T>> extends Serializable {
    void merge(T other);
    T copy();
}
