package com.nas.download.utils;

import java.io.File;
import java.util.Comparator;

public class FolderComparator implements Comparator<File> {
    @Override
    public int compare(File n1, File n2) {
        return ((Boolean) n2.isDirectory()).compareTo(n1.isDirectory());
    }
}
