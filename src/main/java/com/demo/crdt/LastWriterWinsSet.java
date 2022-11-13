package com.demo.crdt;

import java.lang.annotation.ElementType;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rui S.
 * @date 2022-11-12
 * @apiNote
 */
public class LastWriterWinsSet<T>  {

    static class Timestamps {
        private final long latestAdd;
        private final long latestRemove;

        Timestamps(){
            latestAdd = 0;
            latestRemove = 0;
        }

        Timestamps(long add, long remove){
            latestAdd = add;
            latestRemove = remove;
        }

        long getLatestAdd(){
            return latestAdd;
        }

        long getLatestRemove(){
            return latestRemove;
        }
        boolean isPresent(){
            return latestAdd >= latestRemove;
        }

        Timestamps updateAdd(){
           return new Timestamps(Instant.now().getNano(), latestRemove);
        }

        Timestamps updateRemove(){
            return new Timestamps(latestAdd, Instant.now().getNano());
        }

        Timestamps merge(Timestamps other){
            if (other == null){
                return this;
            }
            return new Timestamps(Math.max(latestAdd, other.latestAdd), Math.max(latestRemove, other.latestRemove));
        }
    }

    private Map<T, Timestamps> struct = new HashMap<T, Timestamps>();

    public LastWriterWinsSet(T t){
        struct = new HashMap<T, Timestamps>();
        struct.put(t, new Timestamps().updateAdd());
    }

    public LastWriterWinsSet(Set<T> set){
        struct = new HashMap<>();
        for (T t : set){
            struct.put(t, new Timestamps().updateAdd());
        }
    }

    public LastWriterWinsSet(LastWriterWinsSet<T> first, LastWriterWinsSet<T> second){
        Function<T, Timestamps> timestampsFor = p -> {
            Timestamps firstTs = first.struct.get(p);
            Timestamps secondTs = second.struct.get(p);
            if (firstTs == null){
                return secondTs;
            }
            return firstTs.merge(secondTs);
        };
        struct = Stream.concat(first.struct.keySet().stream(), second.struct.keySet().stream())
                .distinct().collect(Collectors.toMap(p -> p, timestampsFor));
    }

    public LastWriterWinsSet<T> add(T t){
        return this.merge(new LastWriterWinsSet<T>(t));
    }

    LastWriterWinsSet(Map<T, Timestamps> struct){
        this.struct = struct;
    }

    Map<T, Timestamps> getStruct(){
        return struct;
    }


    public LastWriterWinsSet<T> remove(T e){
        Timestamps eTimestamps = struct.get(e);
        if (eTimestamps == null || !eTimestamps.isPresent()){
            return this;
        }
        Map<T, Timestamps> changeMap = new HashMap<>();
        changeMap.put(e, eTimestamps.updateRemove());
        return this.merge(new LastWriterWinsSet<>(changeMap));
    }

    public LastWriterWinsSet<T> merge(LastWriterWinsSet<T> other){
        return new LastWriterWinsSet<>(this, other);
    }

    public Set<T> value(){
        return struct.entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object obj){
        return this == obj || (obj != null && getClass() == obj.getClass() && value().equals(((LastWriterWinsSet) obj).value()));
    }

}
