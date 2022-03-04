package com.mytechden.flyway_csv.utils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MigrationInfoHelperTest {
    @Test
    public void isValidUuid(){
        assertTrue(MigrationInfoHelper.isValidUUID("009692ee-f930-4a74-bbd0-63b8baa5a927"));
    }
    @Test
    public void isNotValidUuid(){
        assertTrue(!MigrationInfoHelper.isValidUUID(null));
        assertTrue(!MigrationInfoHelper.isValidUUID(""));
        assertTrue(!MigrationInfoHelper.isValidUUID("test-ss-ss-ss-s"));
        assertTrue(!MigrationInfoHelper.isValidUUID("009692ee-f9309-4a74-bbd0-63b8baa5a927"));
        assertTrue(!MigrationInfoHelper.isValidUUID("1-1-1-1"));
    }
}
