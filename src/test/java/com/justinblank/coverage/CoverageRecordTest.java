package com.justinblank.coverage;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoverageRecordTest {

    @Test
    public void testEmptyRecords() {
        var record1 = new CoverageRecord(new short[0]);
        var record2 = new CoverageRecord(new short[0]);
        assertFalse(record1.greaterThan(record2));
        assertFalse(record1.differsFrom(record2));

        assertFalse(record1.greaterThan(new CoverageRecord(new short[]{1, 2})));
        assertGreater(record1, new CoverageRecord(new short[]{1, 2}));
    }

    @Test
    public void testGreaterThanDifferentOrders() {
        var record1 = new CoverageRecord(new short[]{1, 2, 3, 4});
        var record2 = new CoverageRecord(new short[]{2, 4, 4, 8});
        assertIncomparable(record1, record2);
    }

    @Test
    public void testGreaterThanAdditionalBranches() {
        var record1 = new CoverageRecord(new short[]{1, 2, 3, 4});
        var record2 = new CoverageRecord(new short[]{1, 2, 3, 4, 5, 8});
        assertGreater(record1, record2);
    }

    @Test
    public void testGreaterThanAdditionalHits() {
        var record1 = new CoverageRecord(new short[]{1, 2, 3, 4});
        var record2 = new CoverageRecord(new short[]{1, 4, 3, 4});
        assertGreater(record1, record2);
    }

    @Test
    public void testGreaterThanDifferentHits() {
        var record1 = new CoverageRecord(new short[]{1, 2, 3, 4});
        var record2 = new CoverageRecord(new short[]{1, 4, 3, 2});
        assertIncomparable(record1, record2);
    }

    @Test
    public void testGreaterThanIsAsymmetric() {
        var count = new Random().nextInt(8);
        var buffer1 = buildRandomInts(count);
        var buffer2 = buildRandomInts(count);
        if (!Arrays.equals(buffer1, buffer2)) {
            var record1 = new CoverageRecord(buffer1);
            var record2 = new CoverageRecord(buffer2);
            assertFalse(record1.greaterThan(record2) && record2.greaterThan(record1));
        }
    }

    private static short[] buildRandomInts(int count) {
        var random = new Random();
        var coverage = new short[count];
        for (var i = 0; i < count; i++) {
            coverage[i] = (short) random.nextInt(32);
        }
        return coverage;
    }

    public static void assertIncomparable(CoverageRecord record1, CoverageRecord record2) {
        assertFalse(record1.greaterThan(record2));
        assertFalse(record2.greaterThan(record1));

        assertTrue(record1.differsFrom(record2));
        assertTrue(record2.differsFrom(record1));
    }

    public static void assertGreater(CoverageRecord record1, CoverageRecord record2) {
        assertTrue(record2.greaterThan(record1));
        assertFalse(record1.greaterThan(record2));

        assertTrue(record2.differsFrom(record1));
    }
}
