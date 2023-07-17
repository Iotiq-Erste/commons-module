package com.iotiq.commons.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.StringJoiner;

@Data
@EqualsAndHashCode(callSuper = true)
public class Codes extends LinkedList<String> {
    private transient StringJoiner joiner = new StringJoiner(".");

    @NonNull
    public String[] toStringArray() {
        return toArray(new String[0]);
    }

    @Override
    public boolean add(String s) {
        joiner.add(s);
        return super.add(joiner.toString());
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        for (String s : c) {
            add(s);
        }
        return true;
    }
}
