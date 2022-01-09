package com.nas.download.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

class NameComparatorTest {

    @Test
    void compareTest() {
        new NameComparator().compare(new File(""), new File(" "));
    }
}
