/**
 * Extracted and modified from QuickTheories. Copyright notice from
 * QuickTheories included below.
 * 
 * Based on http://code.google.com/p/javacoveragent/ by
 * "alex.mq0" and "dmitry.kandalov"
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.quicktheories.coverage;

import com.justinblank.coverage.BranchCoverageAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CoverageClassVisitor extends ClassVisitor {

  public CoverageClassVisitor(final ClassWriter writer) {
    super(Opcodes.ASM6, writer);
  }

  @Override
  public final MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions) {
    final MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc,
        signature, exceptions);
    return new BranchCoverageAdapter(this.api, methodVisitor, access, name, desc);
  }
}
