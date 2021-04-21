package com.justinblank.coverage;

import java.util.Arrays;

// Copy of the AFL coverage instrumentation (https://lcamtuf.coredump.cx/afl/technical_details.txt)
public class BranchCoverage {

    public static int previousLocation;

    public static final int BRANCH_TRACKING_SIZE = Short.MAX_VALUE; // TODO: figure out best way to handle non-short value
    public static byte[] BRANCHES = new byte[BRANCH_TRACKING_SIZE];

    public static void clearBranches() {
        Arrays.fill(BRANCHES, (byte) 0);
    }

}
