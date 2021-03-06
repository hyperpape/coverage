package com.justinblank.coverage;

import java.util.Arrays;

public class CoverageRecord {

    private final short[] coverage;
    private int hashCode;

    public CoverageRecord() {
        byte[] coverageCopy = Arrays.copyOf(BranchCoverage.BRANCHES, BranchCoverage.BRANCHES.length);

        var seen = 0;
        for (var i : coverageCopy) {
            if (i != 0) {
                seen++;
            }
        }
        this.coverage = new short[seen * 2];

        short coverageIdx = 0;
        for (short i = 0; i < coverageCopy.length; i++) {
            if (coverageCopy[i] != 0) {
                coverage[coverageIdx++] = i;
                coverage[coverageIdx++] = asPowOfTwo(coverageCopy[i]);
            }
        }
    }

    CoverageRecord(short[] bytes) {
        coverage = bytes;
    }

    /**
     * Return whether this record of coverage is strictly greater than the other.
     * @param other a second coverage record
     * @return true if this record is greater than the other
     */
    public boolean greaterThan(CoverageRecord other) {
        // This is a partial order: we need to know whether we've seen some branches more than the other guy, but also
        // vice versa.
        boolean hasBranchOtherLacks = false;

        // Short-circuit on the easy case
        if (coverage.length < other.coverage.length) {
            return false;
        }
        else if (coverage.length > other.coverage.length) {
            hasBranchOtherLacks = true;
        }

        // We have to walk both arrays, looking for:
        // 1. Any branch we have that the other record doesn't.
        // 2. Any branch the other has this this doesn't. Can short-circuit return.
        // 3. Any branch where this has more coverage than the other.
        // 4. Any branch where this has less coverage than the other. Can short-circuit.
        var thisIdx = 0;
        var otherIdx = 0;

        while (thisIdx < coverage.length && otherIdx < other.coverage.length) {
            var thisNextBranch = coverage[thisIdx];
            var otherNextBranch = other.coverage[otherIdx];
            if (thisNextBranch == otherNextBranch) {
                var thisBranchCount = coverage[++thisIdx];
                var otherBranchCount = other.coverage[++otherIdx];
                if (thisBranchCount > otherBranchCount ) {
                    hasBranchOtherLacks = true;
                }
                else if (thisBranchCount < otherBranchCount) {
                    return false;
                }
                thisIdx++;
                otherIdx++;
            }
            else if (thisNextBranch < otherNextBranch) {
                // If the branch is lower than the other, that means there's a branch we've seen that the other lacks
                // Advance our side, so that we can verify we have all the branches the other does
                hasBranchOtherLacks = true;
                thisIdx++;
                thisIdx++;
            }
            else {
                return false;
            }
        }
        return hasBranchOtherLacks;
    }

    public boolean differsFrom(CoverageRecord other) {
        if (coverage.length != other.coverage.length) {
            return true;
        }
        for (int i = 0; i < coverage.length; i++) {
            if (coverage[i] * 2 <= other.coverage[i] || other.coverage[i] * 2 <= coverage[i]) {
                return true;
            }
        }
        return false;
    }

    /* https://stackoverflow.com/a/15525769 */
    /* returns greatest power of 2 less than or equal to x, branch-free */
    /* Source: Hacker's Delight, First Edition. */
    // This is a slight difference from the AFL algorithm, which treats 32-127 hits at the same bucket.
    // I have no reason to believe this is better, but it doesn't require a special case there easier.
    short asPowOfTwo(int x) {
        x = x | (x>>1);
        x = x | (x>>2);
        x = x | (x>>4);
        x = x | (x>>8);
        return (short) (x - (x>>1));
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(coverage);
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CoverageRecord) {
            var other = (CoverageRecord) o;
            return Arrays.equals(this.coverage, other.coverage);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CoverageRecord{" +
                "hashCode=" + hashCode() +
                ", coverage=" + Arrays.toString(coverage) +
                '}';
    }
}

