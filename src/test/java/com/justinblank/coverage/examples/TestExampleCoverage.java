package com.justinblank.coverage.examples;

import com.justinblank.coverage.BranchCoverage;
import com.justinblank.coverage.CoverageRecord;
import com.justinblank.examples.Example;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestExampleCoverage {

    @Test
    public void testExampleTest() {
        Loader.init();
        // Make sure classes are loaded to avoid contaminating coverage
        var throwawayRecord = new CoverageRecord();
        Example.exampleTest(true, true, true, true, true, true, true, true);
        BranchCoverage.clearBranches();

        Example.exampleTest(true, true, true, true, true, true, true, true);
        var firstRecord = new CoverageRecord();

        BranchCoverage.clearBranches();
        Example.exampleTest(true, true, true, false, true, true, true, true);
        var secondRecord = new CoverageRecord();
        assertTrue(firstRecord.differsFrom(secondRecord));

        // Are we intentionally not clearing branches?
        Example.exampleTest(true, true, true, true, true, true, true, true);
        var thirdRecord = new CoverageRecord();
        assertTrue(thirdRecord.greaterThan(firstRecord));
        assertTrue(thirdRecord.greaterThan(secondRecord));
        assertFalse(firstRecord.greaterThan(thirdRecord));
        assertFalse(secondRecord.greaterThan(thirdRecord));
    }

    @Test
    public void testCollatzCoverage() {
        Loader.init(); // for side effects
        Example.collatz(1);
        var throwaway = new CoverageRecord();
        BranchCoverage.clearBranches();

        Example.collatz(1);
        var firstRecord = new CoverageRecord();

        BranchCoverage.clearBranches();
        Example.collatz(9);
        var secondRecord = new CoverageRecord();
        assertTrue(firstRecord.differsFrom(secondRecord));
        assertTrue(secondRecord.greaterThan(firstRecord));
    }


}
