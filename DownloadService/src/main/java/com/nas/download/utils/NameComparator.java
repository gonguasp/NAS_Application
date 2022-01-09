package com.nas.download.utils;

import java.io.File;
import java.util.Comparator;

public class NameComparator implements Comparator<File> {
    @Override
    public int compare(File n1, File n2) {
        return n1.getName().compareTo(n2.getName());
    }
}
