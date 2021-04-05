package com.justinblank.coverage;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Random;

public class BranchCoverageAdapter extends AdviceAdapter {

    // Should be thread-safe, as it's only used inside of the class-loader
    private static final Random BLOCK_RANDOM_SOURCE = new Random();

    private final MethodVisitor methodVisitor;

    public BranchCoverageAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        this.methodVisitor = methodVisitor;
    }

    void addCoverage() {
        // cur_location = COMPILE_TIME_RANDOM
        // shared_mem[cur_location ^ prev_location]++
        // prev_location = cur_location >> 1;

        final int branchRandomValue = BLOCK_RANDOM_SOURCE.nextInt(BranchCoverage.BRANCH_TRACKING_SIZE);
        final String className = "com/justinblank/coverage/BranchCoverage";

        // Update hit count
        mv.visitFieldInsn(GETSTATIC, className, "BRANCHES", "[B");
        mv.visitFieldInsn(GETSTATIC, className, "previousLocation", "I");
        mv.visitLdcInsn(branchRandomValue);
        mv.visitInsn(IXOR);
        mv.visitFieldInsn(GETSTATIC, className, "BRANCHES", "[B");
        mv.visitFieldInsn(GETSTATIC, className, "previousLocation", "I");
        mv.visitLdcInsn(branchRandomValue);
        mv.visitInsn(IXOR);
        mv.visitInsn(BALOAD);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitInsn(I2B);
        mv.visitInsn(BASTORE);

        // set current location
        pushPositiveConstants(branchRandomValue);
        this.methodVisitor.visitInsn(ICONST_1);
        this.methodVisitor.visitInsn(IUSHR);
        this.methodVisitor.visitFieldInsn(PUTSTATIC, className,"previousLocation", "I");
    }

    protected void pushPositiveConstants(final int value) {
        switch (value) {
            case 0:
                this.mv.visitInsn(ICONST_0);
                break;
            case 1:
                this.mv.visitInsn(ICONST_1);
                break;
            case 2:
                this.mv.visitInsn(ICONST_2);
                break;
            case 3:
                this.mv.visitInsn(ICONST_3);
                break;
            case 4:
                this.mv.visitInsn(ICONST_4);
                break;
            case 5:
                this.mv.visitInsn(ICONST_5);
                break;
            default:
                if (value <= Byte.MAX_VALUE) {
                    this.mv.visitIntInsn(Opcodes.BIPUSH, value);
                } else if (value <= Short.MAX_VALUE) {
                    this.mv.visitIntInsn(Opcodes.SIPUSH, value);
                } else {
                    this.mv.visitLdcInsn(value);
                }
        }
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        addCoverage();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(final Label label) {
        super.visitLabel(label);
        addCoverage();
    }


    @Override
    public void visitTableSwitchInsn(final int min, final int max,
                                     final Label dflt, final Label... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        addCoverage();
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
                                      final Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        addCoverage();
    }
}
