package com.nas.download.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

class FolderComparatorTest {

    @Test
    void compareTest() {
        new FolderComparator().compare(new File(""), new File(" "));
    }
}
